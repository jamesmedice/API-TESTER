package com.framework.gft.api.apiTemplate.router;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;

import com.framework.gft.api.apiTemplate.json.JsonParser;
import com.framework.gft.api.apiTemplate.json.JsonSerializer;
import com.framework.gft.api.apiTemplate.security.DefaultCsrfValidator;
import com.framework.gft.api.apiTemplate.security.DefaultSignatureValidator;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpClientBuilder {

	final static Logger logger = Logger.getLogger(DataRouterHttpClientBuilder.class);

	private static final int DEFAULT_TIMEOUT_MS = 3000;
	private static final int DEFAULT_MAX_TOTAL_CONNECTION = 20;
	private static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 20;

	private int timeoutMs;
	private int maxTotalConnections;
	private int maxConnectionsPerRoute;
	private Optional<Integer> validateAfterInactivityMs = Optional.empty();
	private HttpClientBuilder httpClientBuilder;
	private DataRouterHttpRetryHandler retryHandler;
	private JsonSerializer jsonSerializer;
	private CloseableHttpClient customHttpClient;
	private DefaultSignatureValidator signatureValidator;
	private DefaultCsrfValidator csrfValidator;
	private Supplier<String> apiKeySupplier;
	private DataRouterHttpClientConfig config;
	private boolean ignoreSsl;

	public DataRouterHttpClientBuilder() {
		this.retryHandler = new DataRouterHttpRetryHandler();
		this.timeoutMs = DEFAULT_TIMEOUT_MS;
		this.maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTION;
		this.maxConnectionsPerRoute = DEFAULT_MAX_CONNECTION_PER_ROUTE;
		this.httpClientBuilder = HttpClientBuilder.create().setRetryHandler(retryHandler).setRedirectStrategy(new LaxRedirectStrategy());
	}

	public DataRouterHttpClient build() {
		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectTimeout(timeoutMs).setConnectionRequestTimeout(timeoutMs).setSocketTimeout(timeoutMs).build();
		httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
		PoolingHttpClientConnectionManager connectionManager;
		if (ignoreSsl) {
			SSLConnectionSocketFactory sslsf;
			try {
				SSLContextBuilder builder = new SSLContextBuilder();
				builder.loadTrustMaterial(null, (chain, authType) -> true);
				sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
			} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
			connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		} else {
			connectionManager = new PoolingHttpClientConnectionManager();
		}
		connectionManager.setMaxTotal(maxTotalConnections);
		connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
		if (validateAfterInactivityMs.isPresent()) {
			connectionManager.setValidateAfterInactivity(validateAfterInactivityMs.get());
		}
		httpClientBuilder.setConnectionManager(connectionManager);
		CloseableHttpClient builtHttpClient;

		if (customHttpClient == null)
			builtHttpClient = httpClientBuilder.build();
		else
			builtHttpClient = customHttpClient;

		if (config == null)
			config = new DataRouterHttpClientDefaultConfig();

		if (jsonSerializer == null)
			jsonSerializer = new JsonParser();

		return new DataRouterHttpClient(builtHttpClient, this.jsonSerializer, this.signatureValidator, this.csrfValidator, this.apiKeySupplier, this.config, connectionManager);
	}

	public DataRouterHttpClientBuilder setRetryCount(int retryCount) {
		if (customHttpClient != null) {
			throw new UnsupportedOperationException("You cannot change the retry count of a custom http client");
		}
		this.retryHandler.setRetryCount(retryCount);
		return this;
	}

	public DataRouterHttpClientBuilder setJsonSerializer(JsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
		return this;
	}

	public DataRouterHttpClientBuilder setCustomHttpClient(CloseableHttpClient httpClient) {
		this.customHttpClient = httpClient;
		return this;
	}

	public DataRouterHttpClientBuilder setSignatureValidator(DefaultSignatureValidator signatureValidator) {
		this.signatureValidator = signatureValidator;
		return this;
	}

	public DataRouterHttpClientBuilder setCsrfValidator(DefaultCsrfValidator csrfValidator) {
		this.csrfValidator = csrfValidator;
		return this;
	}

	public DataRouterHttpClientBuilder setApiKeySupplier(Supplier<String> apiKeySupplier) {
		this.apiKeySupplier = apiKeySupplier;
		return this;
	}

	public DataRouterHttpClientBuilder setConfig(DataRouterHttpClientConfig config) {
		this.config = config;
		return this;
	}

	public DataRouterHttpClientBuilder setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
		return this;
	}

	public DataRouterHttpClientBuilder setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
		this.maxConnectionsPerRoute = maxConnectionsPerRoute;
		return this;
	}

	public DataRouterHttpClientBuilder setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
		return this;
	}

	public DataRouterHttpClientBuilder setIgnoreSsl(boolean ignoreSsl) {
		this.ignoreSsl = ignoreSsl;
		return this;
	}

	public DataRouterHttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
		httpClientBuilder.setRedirectStrategy(redirectStrategy);
		return this;
	}

	public DataRouterHttpClientBuilder setLogOnRetry(boolean logOnRetry) {
		retryHandler.setLogOnRetry(logOnRetry);
		return this;
	}

	public DataRouterHttpClientBuilder setValidateAfterInactivityMs(int validateAfterInactivityMs) {
		this.validateAfterInactivityMs = Optional.of(validateAfterInactivityMs);
		return this;
	}
}
