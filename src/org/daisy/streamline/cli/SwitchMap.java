package org.daisy.streamline.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchMap {
	private final List<SwitchArgument> switches;
	private final Map<String, SwitchArgument> switchesMap;
	public static class Builder {
		private final List<SwitchArgument> switches = new ArrayList<>();
		private final Map<String, SwitchArgument> switchesMap = new HashMap<>();
		/**
		 * Adds a switch.
		 * @param value the switch
		 * @return returns this object
		 * @throws IllegalArgumentException if the key or alias is already in use
		 */
		public Builder addSwitch(SwitchArgument value) {
			if (value.getKey()!=null) {
				if (switchesMap.put(""+value.getKey(), value)!=null) {
					throw new IllegalArgumentException("Key already in use: " + value.getKey());
				}
			}
			if (value.getAlias()!=null) {
				if (switchesMap.put(value.getAlias(), value)!=null)  {
					throw new IllegalArgumentException("Alias already in use: " + value.getAlias());
				}
			}
			switches.add(value);
			return this;
		}
		public SwitchMap build() {
			return new SwitchMap(this);
		}
	}
	
	private SwitchMap(Builder builder) {
		this.switches = Collections.unmodifiableList(new ArrayList<>(builder.switches));
		this.switchesMap = Collections.unmodifiableMap(new HashMap<>(builder.switchesMap));
	}
	
	/**
	 * Gets the switch arguments.
	 * @return returns the switch arguments
	 */
	public Collection<SwitchArgument> values() {
		return switches;
	}
	
	/**
	 * Gets a switch from its key or alias.
	 * @param key the key or alias
	 * @return returns the switch argument, or null if not found
	 */
	public SwitchArgument get(String key) {
		return switchesMap.get(key);
	}

}
