package com.framework.gft.api.apiTemplate.router.exception;

import java.time.Duration;
import java.util.Optional;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import com.framework.gft.api.apiTemplate.response.DataRouterHttpResponse;

/**
 * 
 * @author a73s
 *
 */
@SuppressWarnings("serial")
public class DataRouterHttpResponseException extends DataRouterHttpException {

	public static final String X_EXCEPTION_ID = "x-eid";

	private final DataRouterHttpResponse response;

	public DataRouterHttpResponseException(DataRouterHttpResponse response, Duration duration) {
		super(buildMessage(response, duration), null);
		this.response = response;
	}

	private static String buildMessage(DataRouterHttpResponse response, Duration duration) {
		String message = "HTTP response returned with status code " + response.getStatusCode();
		Header header = response.getFirstHeader(X_EXCEPTION_ID);
		if (header != null) {
			message += " and exception id " + header.getValue();
		}
		message += " after " + duration.toMillis() + "ms";
		message += " with entity:\n" + response.getEntity();
		return message;
	}

	public DataRouterHttpResponse getResponse() {
		return response;
	}

	public boolean isClientError() {
		int statusCode = response.getStatusCode();
		return statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}

	public boolean isServerError() {
		int statusCode = response.getStatusCode();
		return statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}

	public Optional<String> getExceptionId() {
		Header header = response.getFirstHeader(X_EXCEPTION_ID);
		return Optional.ofNullable(header).map(Header::getValue);
	}

}
