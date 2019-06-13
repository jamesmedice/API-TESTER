package com.framework.gft.api.apiTemplate.security;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author a73s
 *
 */
public class SecurityValidationResult {

	private final boolean success;
	private final HttpServletRequest wrappedRequest;
	private String failureMessage;

	public static SecurityValidationResult success(HttpServletRequest request) {
		return new SecurityValidationResult(request, true, null);
	}

	public static SecurityValidationResult failure(HttpServletRequest request) {
		return new SecurityValidationResult(request, false, null);
	}

	public SecurityValidationResult(HttpServletRequest wrappedRequest, boolean checkPassed, String failureMessage) {
		this.success = checkPassed;
		this.wrappedRequest = wrappedRequest;
		this.failureMessage = failureMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public HttpServletRequest getWrappedRequest() {
		return wrappedRequest;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public SecurityValidationResult setFailureMessage(String message) {
		this.failureMessage = message;
		return this;
	}

	public static SecurityValidationResult of(Function<HttpServletRequest, SecurityValidationResult> check, HttpServletRequest request) {
		return check.apply(request);
	}

	public SecurityValidationResult combinedWith(Function<HttpServletRequest, SecurityValidationResult> nextCheck) {
		if (this.success) {
			return nextCheck.apply(this.wrappedRequest);
		}
		return this;
	}
}
