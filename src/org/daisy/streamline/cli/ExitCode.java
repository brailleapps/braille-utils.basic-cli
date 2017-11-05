package org.daisy.streamline.cli;
/**
 * Provides exit codes to be used by implementing classes.
 */
public enum ExitCode {
	/**
	 * Normal application termination
	 */
	OK(0),
	/**
	 * General argument error
	 */
	ARGUMENT_ERROR(10),
	/**
	 * Missing a required argument
	 */
	MISSING_ARGUMENT(11),
	/**
	 * Argument is unknown to the application
	 */
	UNKNOWN_ARGUMENT(12),
	/**
	 * Argument value is illegal
	 */
	ILLEGAL_ARGUMENT_VALUE(13),
	/**
	 * General resource error
	 */
	RESOURCE_ERROR(20),
	/**
	 * Failed to read a required input
	 */
	FAILED_TO_READ_RESOURCE(21),
	/**
	 * Failed to locate a required input
	 */
	MISSING_RESOURCE(22),
	/**
	 * The supplied data didn't meet the expectations 
	 */
	UNEXPECTED_RESOURCE_CONTENTS(23),
	/**
	 * An internal error occurred  
	 */
	INTERNAL_ERROR(50);

	private final int status;

	private ExitCode(int status) {
		this.status = status;
	}

	/**
	 * Gets the exit value.
	 * @return returns the exit value
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Quits the application.
	 */
	public void exitSystem() {
		exitSystem(null);
	}

	/**
	 * Quits the application with the specified message. 
	 * @param message the message
	 */
	public void exitSystem(String message) {
		if (message!=null) {
			System.out.println(message);
		}
		System.exit(this.status);
	}
};