package org.daisy.streamline.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultCommandParserResult implements CommandParserResult {
	private final Map<String, String> optional;
	private final List<String> unnamed;

	static class Builder {
		private final Map<String, String> optional;
		private final List<String> unnamed;

		Builder() {
			optional = new HashMap<>();
			unnamed = new ArrayList<>();
		}

		Builder addOptional(String key, String value) {
			optional.put(key, value);
			return this;
		}

		Builder addRequired(String value) {
			unnamed.add(value);
			return this;
		}

		CommandParserResult build() {
			return new DefaultCommandParserResult(this);
		}
	}

	private DefaultCommandParserResult(Builder builder) {
		this.optional = Collections.unmodifiableMap(new HashMap<>(builder.optional));
		this.unnamed = Collections.unmodifiableList(new ArrayList<>(builder.unnamed));
	}

	@Override
	public List<String> getRequired() {
		return unnamed;
	}

	@Override
	public Map<String, String> getOptional() {
		return optional;
	}

	@Override
	public Map<String, String> toMap(String prefix) {
		Map<String, String> ret = new HashMap<>();
		int i = 0;
		for (String s : unnamed) {
			ret.put(prefix+i, s);
			i++;
		}
		for (String key : optional.keySet()) {
			ret.put(key, optional.get(key));
		}
		return ret;
	}

}
