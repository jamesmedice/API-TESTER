package com.framework.gft.api.apiTemplate.router;

import java.io.IOException;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpRetryHandler implements HttpRequestRetryHandler {

	final static Logger logger = Logger.getLogger(DataRouterHttpRetryHandler.class);

	public static final String RETRY_SAFE_ATTRIBUTE = "retrySafe";
	private static final int DEFAULT_RETRY_COUNT = 2;

	private int retryCount;
	private boolean logOnRetry;

	public DataRouterHttpRetryHandler() {
		retryCount = DEFAULT_RETRY_COUNT;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		if (logOnRetry) {
			HttpClientContext clientContext = HttpClientContext.adapt(context);

			logger.warn("Request " + clientContext.getRequest().getRequestLine() + " failure Nº " + executionCount, exception);
		}
		Object retrySafe = context.getAttribute(RETRY_SAFE_ATTRIBUTE);
		if (retrySafe == null || !(retrySafe instanceof Boolean) || !(Boolean) retrySafe || executionCount > retryCount) {
			return false;
		}
		return true;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void setLogOnRetry(boolean logOnRetry) {
		this.logOnRetry = logOnRetry;
	}

}
