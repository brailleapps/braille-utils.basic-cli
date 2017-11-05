package org.daisy.streamline.cli;

import java.util.Collections;
import java.util.List;

public interface CommandDetails {

	/**
	 * Gets the name for the UI
	 * @return returns the UI name
	 */
	public String getName();

	/**
	 * Gets the UI description
	 * @return returns the UI description
	 */
	public String getDescription();

	/**
	 * Gets required arguments
	 * @return returns a list of required arguments that can be
	 * passed to the UI on startup
	 */
	public default List<Argument> getRequiredArguments() {
		return Collections.emptyList();
	}

	/**
	 * Gets optional arguments
	 * @return returns a list of optional arguments that can be
	 * passed to the UI on startup
	 */
	public default List<OptionalArgument> getOptionalArguments() {
		return Collections.emptyList();
	}
	
	/**
	 * Gets the switches
	 * @return returns the switches
	 */
	public default SwitchMap getSwitches() {
		return new SwitchMap.Builder().build();
	}

}
