package com.github.fsmi.eido;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.handler.CommandHandler;

public class Main {
	
	private static Logger logger;
	private static EidoConfig config;
	private static CommandHandler commandHandler;
	
	public static void main(String [] args) {
		logger = LogManager.getLogger("Main");
		logger.info("Starting up Eido...");
		config = new EidoConfig(logger);
		commandHandler = new CommandHandler(logger);
		new Thread(commandHandler::beginReading).start();
	}
	
	/**
	 * Shuts everything down
	 */
	public static void shutDown() {
		logger.info("Shutting down Eido. Goodbye and have a nice day!");
		commandHandler.stopReading();
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static EidoConfig getConfig() {
		return config;
	}

}
