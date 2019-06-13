package com.framework.gft.api.apiTemplate.security;

/**
 * 
 * @author a73s
 *
 */
public interface ApiKeyPredicate {

	boolean check(String parameter);

	String getApiKey();
}
