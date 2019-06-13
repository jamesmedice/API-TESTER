package com.framework.gft.api.apiTemplate.security;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author a73s
 *
 */
public interface SignatureValidator {

	SecurityValidationResult validate(HttpServletRequest request);
}
