package com.framework.gft.api.apiTemplate.router;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.pool.PoolStats;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.framework.gft.api.apiTemplate.json.JsonSerializer;
import com.framework.gft.api.apiTemplate.response.DataRouterHttpResponse;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpRequest.HttpRequestMethod;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpConnectionAbortedException;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpException;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpRequestInterruptedException;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpResponseException;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpRuntimeException;
import com.framework.gft.api.apiTemplate.security.DefaultCsrfValidator;
import com.framework.gft.api.apiTemplate.security.DefaultSignatureValidator;
import com.framework.gft.api.apiTemplate.security.SecurityParameters;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpClient {

	final static Logger logger = Logger.getLogger(DataRouterHttpClient.class);

	private static final Duration LOG_SLOW_REQUEST_THRESHOLD = Duration.ofSeconds(10);

	private final CloseableHttpClient httpClient;
	private final JsonSerializer jsonSerializer;
	private final DefaultSignatureValidator signatureValidator;
	private final DefaultCsrfValidator csrfValidator;
	private final Supplier<String> apiKeySupplier;
	private final DataRouterHttpClientConfig config;
	private final PoolingHttpClientConnectionManager connectionManager;

	DataRouterHttpClient(CloseableHttpClient httpClient, JsonSerializer jsonSerializer, DefaultSignatureValidator signatureValidator, DefaultCsrfValidator csrfValidator, Supplier<String> apiKeySupplier,
			DataRouterHttpClientConfig config, PoolingHttpClientConnectionManager connectionManager) {
		this.httpClient = httpClient;
		this.jsonSerializer = jsonSerializer;
		this.signatureValidator = signatureValidator;
		this.csrfValidator = csrfValidator;
		this.apiKeySupplier = apiKeySupplier;
		this.config = config;
		this.connectionManager = connectionManager;
	}

	public DataRouterHttpResponse execute(DataRouterHttpRequest request) {
		try {
			return executeChecked(request);
		} catch (DataRouterHttpException e) {
			throw new DataRouterHttpRuntimeException(e);
		}
	}

	public DataRouterHttpResponse execute(DataRouterHttpRequest request, Consumer<HttpEntity> httpEntityConsumer) {
		try {
			return executeChecked(request, httpEntityConsumer);
		} catch (DataRouterHttpException e) {
			throw new DataRouterHttpRuntimeException(e);
		}
	}

	public <E> E execute(DataRouterHttpRequest request, Class deserializeToType) {
		try {
			return executeChecked(request, deserializeToType);
		} catch (Exception e) {
			throw new DataRouterHttpRuntimeException(e);
		}
	}

	public <E> E executeChecked(DataRouterHttpRequest request, Class deserializeToType) throws DataRouterHttpException, JsonParseException, JsonMappingException, IOException, ParseException, JSONException {
		String entity = executeChecked(request).getEntity();
		return (E) jsonSerializer.jsonStringToObject(entity, deserializeToType);
	}

	public DataRouterHttpResponse executeChecked(DataRouterHttpRequest request) throws DataRouterHttpException {
		return executeChecked(request, (Consumer<HttpEntity>) null);
	}

	public DataRouterHttpResponse executeChecked(DataRouterHttpRequest request, Consumer<HttpEntity> httpEntityConsumer) throws DataRouterHttpException {
		setSecurityProperties(request);

		HttpClientContext context = new HttpClientContext();
		context.setAttribute(DataRouterHttpRetryHandler.RETRY_SAFE_ATTRIBUTE, request.getRetrySafe());
		CookieStore cookieStore = new BasicCookieStore();
		for (BasicClientCookie cookie : request.getCookies()) {
			cookieStore.addCookie(cookie);
		}
		context.setCookieStore(cookieStore);

		DataRouterHttpException ex;
		HttpRequestBase internalHttpRequest = null;
		long requestStartTimeMs = System.currentTimeMillis();
		try {
			internalHttpRequest = request.getRequest();
			requestStartTimeMs = System.currentTimeMillis();
			HttpResponse httpResponse = httpClient.execute(internalHttpRequest, context);
			Duration duration = Duration.ofMillis(System.currentTimeMillis() - requestStartTimeMs);
			if (duration.compareTo(LOG_SLOW_REQUEST_THRESHOLD) > 0) {
				logger.warn("Slow request target= " + request.getPath() + " duration= " + duration);
			}
			DataRouterHttpResponse response = new DataRouterHttpResponse(httpResponse, context, httpEntityConsumer);
			if (response.getStatusCode() >= HttpStatus.SC_BAD_REQUEST) {
				throw new DataRouterHttpResponseException(response, duration);
			}
			return response;
		} catch (IOException e) {
			ex = new DataRouterHttpConnectionAbortedException(e, requestStartTimeMs);
		} catch (CancellationException e) {
			ex = new DataRouterHttpRequestInterruptedException(e, requestStartTimeMs);
		}
		if (internalHttpRequest != null) {
			forceAbortRequestUnchecked(internalHttpRequest);
		}
		throw ex;
	}

	private void setSecurityProperties(DataRouterHttpRequest request) {
		Map<String, String> params = new HashMap<>();
		if (csrfValidator != null) {
			String csrfIv = DefaultCsrfValidator.generateCsrfIv();
			params.put(SecurityParameters.CSRF_IV, csrfIv);
			params.put(SecurityParameters.CSRF_TOKEN, csrfValidator.generateCsrfToken(csrfIv));
		}
		if (apiKeySupplier != null) {
			params.put(SecurityParameters.API_KEY, apiKeySupplier.get());
		}
		if (request.canHaveEntity() && request.getEntity() == null) {
			params = request.addPostParams(params).getFirstPostParams();
			if (signatureValidator != null && !params.isEmpty()) {
				String signature = signatureValidator.getHexSignature(request.getFirstPostParams());
				request.addPostParam(SecurityParameters.SIGNATURE, signature);
			}
			request.setEntity(request.getFirstPostParams());
		} else if (request.getMethod() == HttpRequestMethod.GET) {
			params = request.addGetParams(params).getFirstGetParams();
			if (signatureValidator != null && !params.isEmpty()) {
				String signature = signatureValidator.getHexSignature(request.getFirstGetParams());
				request.addGetParam(SecurityParameters.SIGNATURE, signature);
			}
		} else {
			request.addHeaders(params);
			if (signatureValidator != null && request.getEntity() != null) {
				String signature = signatureValidator.getHexSignature(request.getFirstGetParams(), request.getEntity());
				request.addHeader(SecurityParameters.SIGNATURE, signature);
			}
		}
	}

	public void shutdown() {
		try {
			httpClient.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void forceAbortRequestUnchecked(HttpRequestBase internalHttpRequest) {
		try {
			internalHttpRequest.abort();
		} catch (Exception e) {
			logger.error("aborting internal http request failed", e);
		}
	}

	public DataRouterHttpClient addDtoToPayload(DataRouterHttpRequest request, Object dto, String dtoType) throws JsonGenerationException, JsonMappingException, IOException {
		String serializedDto = jsonSerializer.serializeAsJsonString(dto);
		String dtoTypeNullSafe = dtoType;
		if (dtoType == null) {
			if (dto instanceof Iterable) {
				Iterable<?> dtos = (Iterable<?>) dto;
				dtoTypeNullSafe = dtos.iterator().hasNext() ? dtos.iterator().next().getClass().getCanonicalName() : "";
			} else {
				dtoTypeNullSafe = dto.getClass().getCanonicalName();
			}
		}
		DataRouterHttpClientConfig requestConfig = request.getRequestConfig(config);
		Map<String, String> params = new HashMap<>();
		params.put(requestConfig.getDtoParameterName(), serializedDto);
		params.put(requestConfig.getDtoTypeParameterName(), dtoTypeNullSafe);
		request.addPostParams(params);
		return this;
	}

	public DataRouterHttpClient setEntityDto(DataRouterHttpRequest request, Object dto) throws JsonGenerationException, JsonMappingException, IOException {
		String serializedDto = jsonSerializer.serializeAsJsonString(dto);
		request.setEntity(serializedDto, ContentType.APPLICATION_JSON);
		return this;
	}

	public PoolStats getPoolStats() {
		return connectionManager.getTotalStats();
	}

	public CloseableHttpClient getApacheHttpClient() {
		return httpClient;
	}

}
