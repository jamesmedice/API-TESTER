package com.framework.gft.api.apiTemplate.router.exception;

/**
 * 
 * @author a73s
 *
 */
@SuppressWarnings("serial")
public class DataRouterHttpConnectionAbortedException extends DataRouterHttpException {

	public DataRouterHttpConnectionAbortedException(Exception ex, long requestStartTimeMs) {
		super("HTTP connection aborted after " + (System.currentTimeMillis() - requestStartTimeMs) + "ms", ex);
	}

}
