package org.daisy.streamline.cli;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.CommandParser;
import org.daisy.streamline.cli.SwitchArgument;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings("javadoc")
public class DefaultCommandParserTest {

	@Test
	public void testSwitchProcessing_01() {
		CommandDetails details = Mockito.mock(CommandDetails.class);
		Mockito.when(details.getSwitches()).thenReturn(new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('c', "copy", "true", "Turns on copying."))
				.build());
		CommandParser parser = new CommandParser.Builder(details)
				.build();
		CommandParserResult result = parser.parse(new String[]{"-c"});

		Map<String, String> opts = result.getOptional();

		assertEquals(1, opts.size());
		assertEquals("true", opts.get("copy"));
	}

	@Test
	public void testSwitchProcessing_02() {
		CommandDetails details = Mockito.mock(CommandDetails.class);
		Mockito.when(details.getSwitches()).thenReturn(new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('c', "copy", "true", "Turns on copying."))
				.addSwitch(new SwitchArgument('d', "delete", "all", "Delete originals."))
				.build());
		CommandParser parser = new CommandParser.Builder(details)
				.build();

		CommandParserResult result = parser.parse(new String[]{"-c", "--copy=true", "-e", "-d"});

		List<String> req = result.getRequired();
		assertEquals(1, req.size());
		assertEquals("-e", req.get(0));

		Map<String, String> opts = result.getOptional();
		assertEquals(2, opts.size());
		assertEquals("true", opts.get("copy"));
		assertEquals("all", opts.get("delete"));
	}

	@Test
	public void testCommandParser_01() {
		CommandDetails details = Mockito.mock(CommandDetails.class);
		CommandParser parser = new CommandParser.Builder(details).build();
		CommandParserResult result = parser.parse(new String[]{"R1", "R2", "--option=value"});
		Map<String, String> opts = result.getOptional();
		List<String> req = result.getRequired();
		assertEquals(2, req.size());
		assertEquals("R1", req.get(0));
		assertEquals("R2", req.get(1));

		assertEquals(1, opts.size());
		assertEquals("value", opts.get("option"));
	}

	@Test
	public void testCommandParser_02() {
		CommandDetails details = Mockito.mock(CommandDetails.class);
		Mockito.when(details.getSwitches()).thenReturn(new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('d', "option", "value", "Switch option value."))
				.build());
		CommandParser parser = new CommandParser.Builder(details).build();
		CommandParserResult result = parser.parse(new String[]{"R1", "R2", "-d"});
		Map<String, String> opts = result.getOptional();
		List<String> req = result.getRequired();
		assertEquals(2, req.size());
		assertEquals("R1", req.get(0));
		assertEquals("R2", req.get(1));

		assertEquals(1, opts.size());
		assertEquals("value", opts.get("option"));
	}

	@Test
	public void testCommandParser_03() {
		CommandDetails details = Mockito.mock(CommandDetails.class);
		Mockito.when(details.getSwitches()).thenReturn(new SwitchMap.Builder()
				.addSwitch(new SwitchArgument('d', "default", "option", "value", "Switch option value."))
				.build());
		CommandParser parser = new CommandParser.Builder(details)
				.optionalArgumentPrefix("--")
				.build();
		CommandParserResult result = parser.parse(new String[]{"--default"});
		Map<String, String> opts = result.getOptional();
		List<String> req = result.getRequired();
		assertEquals(0, req.size());

		assertEquals(1, opts.size());
		assertEquals("value", opts.get("option"));
	}

}
