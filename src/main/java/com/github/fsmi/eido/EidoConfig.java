package com.github.fsmi.eido;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class EidoConfig {

	private static final String CONFIG_PATH = "config.json";

	private Logger logger;
	private JSONObject config;
	private boolean production;

	private int httpPort;
	private String httpHost;
	private String topLevelPath;

	public EidoConfig(Logger logger) {
		this.logger = logger;
		saveDefaultConfig();
		if (!reloadConfig()) {
			logger.error("Failed to read config, shutting down");
			System.exit(1);
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
