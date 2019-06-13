package com.framework.gft.api.apiTemplate.security;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author a73s
 *
 */
public interface CsrfValidator {

	public boolean check(HttpServletRequest request);

	public Long getRequestTimeMs(HttpServletRequest request);
}