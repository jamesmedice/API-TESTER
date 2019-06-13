package com.framework.gft.api.apiTemplate.provider;

import java.util.ResourceBundle;

/**
 * 
 * @author a73s
 *
 */
public class BundleProvider {

	public static String getPropertiesFromBundle(String key) {
		ResourceBundle rb = ResourceBundle.getBundle("config");
		return rb.getString(key);
	}

}
