package com.framework.gft.api.apiTemplate.router.exception;

/**
 * 
 * @author a73s
 *
 */
@SuppressWarnings("serial")
public abstract class DataRouterHttpException extends Exception {

	protected DataRouterHttpException(String message, Exception exception) {
		super(message, exception);
	}
}
