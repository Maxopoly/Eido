package com.github.fsmi.eido;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.fsmi.eido.database.DBConnection;

public class EidoConfig {

	private static final String CONFIG_PATH = "config.json";

	private Logger logger;
	private JSONObject config;
	private boolean production;

	private int httpPort;
	private String httpHost;
	private String topLevelPath;
	private DBConnection fsmiDbConnection;
	private DBConnection garfieldDbConnection;

	public EidoConfig(Logger logger) {
		this.logger = logger;
		saveDefaultConfig();
		if (!reloadConfig()) {
			throw new IllegalArgumentException();
		}
	}

	public boolean reloadConfig() {
		if (!reloadFromFile()) {
			return false;
		}
		parseValues();
		return true;
	}

	private void parseValues() {
		logger.info("Parsing config values");
		// err on the safe side by not enabling production unless really wanted
		production = config.optBoolean("production", false);
		logger.info("Production mode: " + production);

		JSONObject httpSection = config.optJSONObject("http");
		if (httpSection == null) {
			httpSection = new JSONObject();
		}
		httpPort = httpSection.optInt("port", 80);
		httpHost = httpSection.optString("host", "localhost");
		topLevelPath = httpSection.optString("topLevelPath", "/eido/");
		logger.info(String.format("Server location: %s:%d%s", httpHost, httpPort, topLevelPath));
		JSONObject databaseJson = config.getJSONObject("databases");
		if (databaseJson == null) {
			throw new IllegalArgumentException("No databases specified in config");
		}
		fsmiDbConnection = parseDatabase(databaseJson.optJSONObject("fsmi"),"fsmi");
		garfieldDbConnection = parseDatabase(databaseJson.optJSONObject("garfield"), "garfield");
		
		
		
		logger.info("Successfully parsed entire config");
		
	}

	private void saveDefaultConfig() {
		File configFile = new File(CONFIG_PATH);
		if (configFile.exists()) {
			return;
		}
		logger.info("No config file found, saving default one...");
		URL inputUrl = getClass().getResource("/config.json");
		try {
			FileUtils.copyURLToFile(inputUrl, configFile);
		} catch (IOException e) {
			logger.error("Failed to save default config", e);
		}
	}

	private boolean reloadFromFile() {
		File configFile = new File(CONFIG_PATH);
		if (!configFile.exists()) {
			logger.error("Config file could not be read, this is likely a permission problem");
			return false;
		}
		logger.info("Existing config file found, attempting to parse it...");
		StringBuilder sb = new StringBuilder();
		try {
			Files.readAllLines(new File(CONFIG_PATH).toPath()).forEach(sb::append);
			this.config = new JSONObject(sb.toString());
			return true;
		} catch (IOException | JSONException e) {
			logger.error("Failed to load config file", e);
			return false;
		}
	}

	private DBConnection parseDatabase(JSONObject json, String defaultDbName) {
		if (json == null) {
			throw new IllegalArgumentException(String.format("No database of type %s was specified", defaultDbName));
		}
		String user = json.optString("user", "postgres");
		String password = json.optString("password", null);
		String host = json.optString("host", "localhost");
		String schema = json.optString("schema", null);
		int port = json.optInt("port", 5433);
		int poolSize = json.optInt("poolSize", 30);
		long connectionTimeout = json.optLong("connection_timeout", 10_000L);
		long idleTimeout = json.optLong("idle_timeout", 600_000L);
		long maxLifeTime = json.optLong("max_life_time", 900_000L);
		String database = json.optString("database", defaultDbName);
		return new DBConnection(logger, user, password, host, port, database, schema,  poolSize,
				connectionTimeout, idleTimeout, maxLifeTime);
	}
	
	/**
	 * @return Database connection used for the general fsmi database
	 */
	public DBConnection getFsmiDBConnection() {
		return fsmiDbConnection;
	}
	
	/**
	 * @return Database connection used for garfield and odie internals (documents etc.)
	 */
	public DBConnection getGarfieldDBConnection() {
		return garfieldDbConnection;
	}

	/**
	 * Various debug things will automatically be enabled in non-production mode
	 * 
	 * @return Is this instance in production mode
	 */
	public boolean isProduction() {
		return production;
	}

	/**
	 * @return Port to run on
	 */
	public int getHttpPort() {
		return httpPort;
	}

	/**
	 * @return Host to run on
	 */
	public String getHttpHost() {
		return httpHost;
	}

	/**
	 * @return URL location to run under
	 */
	public String getTopLevelPath() {
		return topLevelPath;
	}
}
