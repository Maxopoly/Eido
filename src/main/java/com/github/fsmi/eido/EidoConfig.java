package com.github.fsmi.eido;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class EidoConfig {

	private static final String CONFIG_PATH = "config.json";

	private Logger logger;
	private JSONObject config;
	private boolean production;

	public EidoConfig(Logger logger) {
		this.logger = logger;
		reloadConfig();
	}

	public void reloadConfig() {
		reloadFromFile();
		parseValues();
	}

	private void parseValues() {
		// err on the safe side by not enabling production unless really wanted
		production = config.optBoolean("production", false);
	}

	private boolean reloadFromFile() {
		final StringBuilder sb = new StringBuilder();
		try {
			Files.readAllLines(new File(CONFIG_PATH).toPath()).forEach(sb::append);
			config = new JSONObject(sb.toString());
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
}
