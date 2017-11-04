package org.daisy.streamline.cli;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Provides an abstract base for command line UI's.
 * @author Joel Håkansson
 */
public abstract class AbstractUI {

	/**
	 * Prefix used for required arguments in the arguments map
	 */
	public static final String ARG_PREFIX = "required-";
	private static final int WIDTH = 50;
	protected final DefaultCommandParser parser;

	/**
	 * Expands the short form of the value with the given key in the provided map using the specified resolver.
	 * @param map the map with keys
	 * @param key the key to find
	 * @param resolver the resolver
	 */
	public void expandShortForm(Map<String, String> map, String key, ShortFormResolver resolver) {
		String value = map.get(key);
		if (value!=null) {
			String id = resolver.resolve(value);
			if (id!=null) {
				map.put(key, id);
			} else {
				System.out.println("Unknown value for "+key+": '" + value + "'");
				System.exit(-ExitCode.ILLEGAL_ARGUMENT_VALUE.ordinal());
			}
		}
	}

	/**
	 * Creates a new AbstractUI using the default key/value delimiter '=' and
	 * the default optional argument prefix '-'
	 */
	public AbstractUI() {
		this.parser = new DefaultCommandParser();
		setKeyValueDelimiter("=");
		setOptionalArgumentPrefix("--");
	}

	/**
	 * Sets the delimiter to use between key and value in the argument
	 * strings passed to the UI.
	 * @param value the delimiter to use
	 */
	public void setKeyValueDelimiter(String value) {
		parser.setKeyValueDelimiter(value);
	}

	/**
	 * Sets the optional argument prefix to use in argument strings passed to
	 * the UI.
	 * @param value the prefix to use
	 */
	public void setOptionalArgumentPrefix(String value) {
		if (ARG_PREFIX.equals(value)) {
			throw new IllegalArgumentException("Prefix is reserved: " + ARG_PREFIX);
		}
		parser.setOptionalArgumentPrefix(value);
	}

	/**
	 * Gets the name for the UI
	 * @return returns the UI name
	 */
	public abstract String getName();

	/**
	 * Gets the UI description
	 * @return returns the UI description
	 */
	public abstract String getDescription();

	/**
	 * Gets required arguments
	 * @return returns a list of required arguments that can be
	 * passed to the UI on startup
	 */
	public abstract List<Argument> getRequiredArguments();

	/**
	 * Gets optional arguments
	 * @return returns a list of optional arguments that can be
	 * passed to the UI on startup
	 */
	public abstract List<OptionalArgument> getOptionalArguments();

	/**
	 * Quits the application with the specified code.
	 * @param e the code
	 */
	public static void exitWithCode(ExitCode e) {
		exitWithCode(e, null);
	}

	/**
	 * Quits the application with the specified code and message. 
	 * @param e the code
	 * @param message the message
	 */
	public static void exitWithCode(ExitCode e, String message) {
		if (message!=null) {
			System.out.println(message);
		}
		System.exit(-e.ordinal());
	}

	/**
	 * Displays a help text for the UI based on the implementation of 
	 * the methods getName, getOptionalArguments and getRequiredArguments. 
	 * @param ps The print stream to use, typically System.out
	 */
	public void displayHelp(PrintStream ps) {
		ps.println("NAME");
		ps.println("\t"+getName());
		ps.println();
		ps.println("SYNOPSIS");
		ps.print("\t"+getName());
		if (getRequiredArguments()!=null && getRequiredArguments().size()>0) {
			for (Argument a : getRequiredArguments()) {
				ps.print(" ");
				ps.print("<"+a.getName()+">");
			}
		}
		if (getOptionalArguments()!=null && getOptionalArguments().size()>0) {
			ps.print(" [options ... ]");
		}
		ps.println();
		ps.println();
		ps.println("DESCRIPTION");
		format(ps,getDescription(), "\t", WIDTH);
		ps.println();
		if ((getRequiredArguments()!=null && getRequiredArguments().size()>0)||
				(getOptionalArguments()!=null && getOptionalArguments().size()>0)||
				(parser.getSwitches()!=null && parser.getSwitches().size()>0)) {
			ps.println("OPTIONS");
			displayRequired(ps);
			displayOptions(ps);
			displaySwitches(ps);
		}
	}

	/**
	 * Prints the required arguments to the specified stream.
	 * @param ps the print stream
	 */
	public void displayRequired(PrintStream ps) {
		for (Argument a : getRequiredArguments()) {
			ps.println("\t<" + a.getName()+ ">");
			format(ps, a.getDescription(), "\t\t", WIDTH);
			if (a.hasValues()) {
				ps.println("\t\tValues:");
				for (Definition value : a.getValues()) {
					ps.println("\t\t\t'"+value.getName()+"'");
					format(ps, value.getDescription(), "\t\t\t\t", WIDTH);
				}
				ps.println();
			}
		}
	}

	/**
	 * Prints the optional arguments to the specified stream.
	 * @param ps the print stream
	 */
	public void displayOptions(PrintStream ps) {
		if (getOptionalArguments()==null) {
			return;
		}
		for (OptionalArgument a : getOptionalArguments()) {
			ps.print("\t" + parser.getOptionalArgumentPrefix() + a.getName() + parser.getKeyValueDelimiter() + "<value>");
			if (!a.hasValues()) {
				ps.print(" (default '"  + a.getDefault() + "')");
			}
			ps.println();
			format(ps, a.getDescription(), "\t\t", WIDTH);
			if (a.hasValues()) {
				ps.println("\t\tValues:");
				for (Definition value : a.getValues()) {
					ps.print("\t\t\t'"+value.getName() + "'");
					if (value.getName().equals(a.getDefault())) {
						ps.println(" (default)");
					} else {
						ps.println();
					}
					format(ps, value.getDescription(), "\t\t\t\t", WIDTH);
				}
			}
			ps.println();
		}
	}

	/**
	 * Prints switches to the specified stream. 
	 * @param ps the print stream
	 */
	public void displaySwitches(PrintStream ps) {
		if (parser.getSwitches()==null) {
			return;
		}
		for (SwitchArgument a : parser.getSwitches()) {
			ps.print("\t");
			if (a.getKey()!=null) {
				ps.print(parser.getSwitchArgumentPrefix() + a.getKey());
			}
			if (a.getAlias()!=null) {
				if (a.getKey()!=null) {
					ps.print(", ");
				}
				ps.print(parser.getOptionalArgumentPrefix() + a.getAlias());
			}
			ps.println();
			format(ps, a.getDescription(), "\t\t", WIDTH);
			ps.println();
		}
	}

	void format(PrintStream ps, String str, String prefix, int w) {
		while (str.length()>w) {
			int i = w;
			while (i>0 && !Character.isWhitespace(str.charAt(i))) {
				i--;
			}
			if (i==0) {
				i = w;
			}
			ps.println(prefix+str.substring(0, i));
			str = str.substring(i+1);
		}
		ps.println(prefix+str);
	}

}
