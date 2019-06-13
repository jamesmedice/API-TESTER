package com.framework.gft.api.apiTemplate.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * @author a73s
 *
 */
public class JsonParser implements JsonSerializer {

	public boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String serializeAsJsonString(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		StringWriter sw = new StringWriter();
		objMapper.writeValue(sw, object);
		return sw.toString();
	}

	public String serializeAsJsonString(Object object, boolean indent) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		if (indent == true) {
			objMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		}

		StringWriter stringWriter = new StringWriter();
		objMapper.writeValue(stringWriter, object);
		return stringWriter.toString();
	}

	public <T> T jsonStringToObject(String content, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		T obj = null;
		ObjectMapper objMapper = new ObjectMapper();
		obj = objMapper.readValue(content, clazz);
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public <T> T jsonStringToObjectArray(String content) throws JsonParseException, JsonMappingException, IOException {
		T obj = null;
		ObjectMapper mapper = new ObjectMapper();
		obj = mapper.readValue(content, new TypeReference<List>() {
		});
		return obj;
	}

	public <T> T jsonStringToObjectArray(String content, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		T obj = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		obj = mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
		return obj;
	}

}
