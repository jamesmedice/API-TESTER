package com.framework.gft.api.apiTemplate.provider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author a73s
 *
 */
public class JsonPayloadProvider {

	public String getJsonFromFile(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();

		try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {

			return IOUtils.toString(inputStream, StandardCharsets.UTF_8);

		} catch (IOException e) {
			return null;
		}
	}

}
