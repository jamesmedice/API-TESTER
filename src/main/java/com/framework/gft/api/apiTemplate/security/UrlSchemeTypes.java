package com.framework.gft.api.apiTemplate.security;

/**
 * 
 * @author a73s
 *
 */
public enum UrlSchemeTypes {

	ANY("any"), HTTP("http"), HTTPS("https");

	private final String stringRepresentation;

	UrlSchemeTypes(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}

	public String getStringRepresentation() {
		return stringRepresentation;
	}
}
