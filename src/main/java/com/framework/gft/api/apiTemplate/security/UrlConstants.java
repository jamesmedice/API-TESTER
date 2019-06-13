package com.framework.gft.api.apiTemplate.security;

/**
 * 
 * @author a73s
 *
 */
public class UrlConstants {

	public static final int PORT_HTTP_STANDARD = 80;
	public static final int PORT_HTTPS_STANDARD = 443;

	public static final int PORT_HTTP_DEV = 8080;
	public static final int PORT_HTTPS_DEV = 8443;

	public static final String LOCAL_HOST = "127.0.0.1";
	public static final String LOCAL_DEV_SERVER = LOCAL_HOST + ":" + PORT_HTTP_DEV;
	public static final String LOCAL_DEV_SERVER_HTTPS = LOCAL_HOST + ":" + PORT_HTTPS_DEV;
	public static final String LOCAL_DEV_SERVER_URL = UrlSchemeTypes.HTTP.getStringRepresentation() + "://" + LOCAL_HOST + ":" + PORT_HTTP_DEV;
	public static final String LOCAL_DEV_SERVER_HTTPS_URL = UrlSchemeTypes.HTTPS.getStringRepresentation() + "://" + LOCAL_HOST + ":" + PORT_HTTPS_DEV;
}
