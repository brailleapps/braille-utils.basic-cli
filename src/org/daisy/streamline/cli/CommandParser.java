package org.daisy.streamline.cli;

import java.io.PrintStream;

/**
 * Provides a command parser.
 * @author Joel Håkansson
 *
 */
public class CommandParser {
	private final CommandDetails details;
	private final String delimiter;
	private final String optionalArgumentPrefix;
	private final String switchArgumentPrefix;
	private final int displayWidth;

	public static class Builder {
		private final CommandDetails details;
		// Optional
		private String delimiter = "=";
		private String optionalArgumentPrefix = "--";
		private String switchArgumentPrefix = "-";
		private int displayWidth = 50;
		
		/**
		 * Creates a new builder
		 * @param details the CLI details
		 */
		public Builder(CommandDetails details) {
			this.details = details;
		}
		
		/**
		 * Sets the delimiter to use between key and value in the argument
		 * strings passed to the UI.
		 * @param value the delimiter to use
		 * @return returns this object
		 */
		public Builder keyValueDelimiter(String value) {
			delimiter = value;
			return this;
		}
		
		/**
		 * Sets the optional argument prefix to use in argument strings passed to
		 * the UI.
		 * @param value the prefix to use
		 * @return returns this object
		 */
		public Builder optionalArgumentPrefix(String value) {
			optionalArgumentPrefix = value;
			return this;
		}
		
		/**
		 * Sets the switch argument prefix.
		 * @param value the switch argument prefix
		 * @return returns this object
		 */
		public Builder switchArgumentPrefix(String value) {
			this.switchArgumentPrefix = value;
			return this;
		}
		
		/**
		 * Sets the width of the help text.
		 * @param value the width
		 * @return returns this object
		 */
		public Builder displayWidth(int value) {
			this.displayWidth = value;
			return this;
		}

		/**
		 * Builds the parser.
		 * @return returns a new instance
		 */
		public CommandParser build() {
			return new CommandParser(this);
		}
		
	}

	/**
	 * Creates a new command parser.
	 */
	private CommandParser(Builder builder) {
		this.details = builder.details;
		this.delimiter = builder.delimiter;
		this.optionalArgumentPrefix = builder.optionalArgumentPrefix;
		this.switchArgumentPrefix = builder.switchArgumentPrefix;
		this.displayWidth = builder.displayWidth;
	}
	
	/**
	 * Creates a new parser with the default settings.
	 * @param details the CLI details
	 * @return returns a new parser
	 */
	public static CommandParser create(CommandDetails details) {
		return new CommandParser.Builder(details).build();
	}

	/**
	 * Gets the key/value delimiter.
	 * @return returns the delimiter
	 */
	public String getKeyValueDelimiter() {
		return delimiter;
	}

	/**
	 * Gets the optional argument prefix.
	 * @return returns the prefix
	 */
	public String getOptionalArgumentPrefix() {
		return optionalArgumentPrefix;
	}

	/**
	 * Gets the switch argument prefix.
	 * @return returns the prefix
	 */
	public String getSwitchArgumentPrefix() {
		return switchArgumentPrefix;
	}

	/**
	 * Parses the supplied strings with this parser.
	 * @param args the arguments
	 * @return returns the parser result
	 */
	public CommandParserResult parse(String[] args) {
		String[] t;
		SwitchMap switches = details.getSwitches();
		DefaultCommandParserResult.Builder builder = new DefaultCommandParserResult.Builder();
		for (String s : args) {
			s = s.trim();
			t = s.split(delimiter, 2);
			if (s.startsWith(optionalArgumentPrefix) && t.length<=2) {
				if (t.length==2) {
					builder.addOptional(t[0].substring(optionalArgumentPrefix.length()), t[1]);
				} else {
					SwitchArgument sc = switches.get(s.substring(optionalArgumentPrefix.length()));
					if (sc!=null) {
						builder.addOptional(sc.getName(), sc.getValue());
					} else {
						builder.addRequired(s);
					}
				}
			} else if (s.startsWith(switchArgumentPrefix) && s.length()==switchArgumentPrefix.length()+1) {
				SwitchArgument sc = switches.get(s.substring(switchArgumentPrefix.length()));
				if (sc!=null) {
					builder.addOptional(sc.getName(), sc.getValue());
				} else {
					builder.addRequired(s);
				}
			} else {
				builder.addRequired(s);
			}
		}
		return builder.build();
	}
	
	/**
	 * Displays a help text for the UI based on the implementation of 
	 * the methods getName, getOptionalArguments and getRequiredArguments. 
	 * @param ps The print stream to use, typically System.out
	 */
	public void displayHelp(PrintStream ps) {
		ps.println("NAME");
		ps.println("\t"+details.getName());
		ps.println();
		ps.println("SYNOPSIS");
		ps.print("\t"+details.getName());
		if (details.getRequiredArguments()!=null && details.getRequiredArguments().size()>0) {
			for (Argument a : details.getRequiredArguments()) {
				ps.print(" ");
				ps.print("<"+a.getName()+">");
			}
		}
		if (details.getOptionalArguments()!=null && details.getOptionalArguments().size()>0) {
			ps.print(" [options ... ]");
		}
		ps.println();
		ps.println();
		ps.println("DESCRIPTION");
		format(ps, details.getDescription(), "\t", displayWidth);
		ps.println();
		if ((details.getRequiredArguments()!=null && details.getRequiredArguments().size()>0)||
				(details.getOptionalArguments()!=null && details.getOptionalArguments().size()>0)||
				(details.getSwitches()!=null && details.getSwitches().values().size()>0)) {
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
		for (Argument a : details.getRequiredArguments()) {
			ps.println("\t<" + a.getName()+ ">");
			format(ps, a.getDescription(), "\t\t", displayWidth);
			if (a.hasValues()) {
				ps.println("\t\tValues:");
				for (Definition value : a.getValues()) {
					ps.println("\t\t\t'"+value.getName()+"'");
					format(ps, value.getDescription(), "\t\t\t\t", displayWidth);
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
		if (details.getOptionalArguments()==null) {
			return;
		}
		for (OptionalArgument a : details.getOptionalArguments()) {
			ps.print("\t" + getOptionalArgumentPrefix() + a.getName() + getKeyValueDelimiter() + "<value>");
			if (!a.hasValues()) {
				ps.print(" (default '"  + a.getDefault() + "')");
			}
			ps.println();
			format(ps, a.getDescription(), "\t\t", displayWidth);
			if (a.hasValues()) {
				ps.println("\t\tValues:");
				for (Definition value : a.getValues()) {
					ps.print("\t\t\t'"+value.getName() + "'");
					if (value.getName().equals(a.getDefault())) {
						ps.println(" (default)");
					} else {
						ps.println();
					}
					format(ps, value.getDescription(), "\t\t\t\t", displayWidth);
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
		if (details.getSwitches()==null) {
			return;
		}
		for (SwitchArgument a : details.getSwitches().values()) {
			ps.print("\t");
			if (a.getKey()!=null) {
				ps.print(getSwitchArgumentPrefix() + a.getKey());
			}
			if (a.getAlias()!=null) {
				if (a.getKey()!=null) {
					ps.print(", ");
				}
				ps.print(getOptionalArgumentPrefix() + a.getAlias());
			}
			ps.println();
			format(ps, a.getDescription(), "\t\t", displayWidth);
			ps.println();
		}
	}

	private void format(PrintStream ps, String str, String prefix, int w) {
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
