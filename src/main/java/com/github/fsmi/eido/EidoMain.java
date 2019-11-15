package com.github.fsmi.eido;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.database.DBMigrationHandler;
import com.github.fsmi.eido.database.DocumentDAO;
import com.github.fsmi.eido.handler.CommandHandler;
import com.github.fsmi.eido.handler.TomcatHandler;

public class EidoMain {

	private static Logger logger;
	private static EidoConfig config;
	private static CommandHandler commandHandler;
	private static TomcatHandler tomcatHandler;
	private static DBMigrationHandler dbMigrationHandler;
	private static DocumentDAO fsmiDao;

	public static void main(String[] args) {
		logger = LogManager.getLogger("Main");
		logger.info("Starting up Eido...");
		try {
			config = new EidoConfig(logger);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to parse config, shutting down");
			return;
		}
		dbMigrationHandler = new DBMigrationHandler(config.getFsmiDBConnection(), logger);
		fsmiDao = new DocumentDAO(logger, config.getFsmiDBConnection());
		fsmiDao.registerMigrations();
		if (!dbMigrationHandler.migrateAll()) {
			logger.error("Failed to update database, shutting down");
			return;
		}
		commandHandler = new CommandHandler(logger);
		tomcatHandler = new TomcatHandler(logger, config);
		new Thread(commandHandler::beginReading).start();
		tomcatHandler.startWebServer();
		// new Thread(tomcatHandler::startWebServer).start();
	}

	/**
	 * Shuts everything down
	 */
	public static void shutDown() {
		logger.info("Shutting down Eido. Goodbye and have a nice day!");
		commandHandler.stopReading();
		try {
			config.getFsmiDBConnection().close();
			config.getGarfieldDBConnection().close();
		} catch (SQLException e) {
			logger.error("Failed to close database, this is probably fine though", e);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public static DBMigrationHandler getDBMigrationHandler() {
		return dbMigrationHandler;
	}

	public static EidoConfig getConfig() {
		return config;
	}

	public static DocumentDAO getFSMIDao() {
		return fsmiDao;
	}

}
