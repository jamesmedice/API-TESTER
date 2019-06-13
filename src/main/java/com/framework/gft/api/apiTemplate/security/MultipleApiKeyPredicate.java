package com.framework.gft.api.apiTemplate.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author a73s
 *
 */
public class MultipleApiKeyPredicate implements ApiKeyPredicate {

	private final String apiKey;
	private final Set<String> keys;

	public MultipleApiKeyPredicate(String apiKey, Collection<String> otherApiKeys) {
		this.apiKey = apiKey;
		this.keys = new HashSet<>(otherApiKeys.size() + 1);
		this.keys.add(apiKey);
		this.keys.addAll(otherApiKeys);
	}

	@Override
	public boolean check(String parameter) {
		return keys.contains(parameter);
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

}
