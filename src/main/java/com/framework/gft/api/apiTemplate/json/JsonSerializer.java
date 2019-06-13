package com.framework.gft.api.apiTemplate.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author a73s
 *
 */
public interface JsonSerializer {

	public boolean isJSONValid(String jsonInString);

	public String serializeAsJsonString(Object object) throws JsonGenerationException, JsonMappingException, IOException;

	public String serializeAsJsonString(Object object, boolean indent) throws JsonGenerationException, JsonMappingException, IOException;

	public <T> T jsonStringToObject(String content, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException;

	public <T> T jsonStringToObjectArray(String content) throws JsonParseException, JsonMappingException, IOException;

	public <T> T jsonStringToObjectArray(String content, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException;

}
