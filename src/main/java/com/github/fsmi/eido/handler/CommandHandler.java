package com.github.fsmi.eido.handler;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.commands.AbstractCommand;

public class CommandHandler {

	private Logger logger;
	private Map<String, AbstractCommand> commands;
	private boolean reading;

	public CommandHandler(Logger logger) {
		this.logger = logger;
		registerCommands();
	}

	/**
	 * Begins parsing commands from the commandline
	 */
	public void beginReading() {
		reading = true;
		Console c = System.console();
		Scanner scanner = null;
		if (c == null) {
			logger.warn("System console not detected, using scanner as fallback behavior");
			scanner = new Scanner(System.in);
		}
		while (reading) {
			String msg;
			if (c == null) {
				msg = scanner.nextLine();
			} else {
				msg = c.readLine("");
			}
			if (msg == null) {
				continue;
			}
			logger.info("Console ran command: " + msg);
			String reply = handleMsg(msg);
			logger.info(reply);
		}
		if (scanner != null) {
			scanner.close();
		}
	}

	/**
	 * Stops reading commands from the command line
	 */
	public void stopReading() {
		this.reading = false;
	}

	private String handleMsg(String msg) {
		String[] splitArgs = msg.split(" ");
		String key = splitArgs[0];
		AbstractCommand command = commands.get(key);
		if (command == null) {
			return String.format("Command %s was not recognized", key);
		}
		return command.handle(Arrays.copyOfRange(splitArgs, 1, splitArgs.length));
	}

	private void registerCommands() {
		commands = new HashMap<>();
	}

}
