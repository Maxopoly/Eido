package com.github.fsmi.eido.database;

import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.util.Guard;

abstract class AbstractDAO {
	
	protected final DBConnection db;
	protected final Logger logger;
	protected final String migrationID;
	
	public AbstractDAO(Logger logger, DBConnection db, String migrationID) {
		Guard.nullCheck(logger, db);
		this.logger = logger;
		this.db = db;
		this.migrationID = migrationID;
	}
	
	public abstract void registerMigrations();

}
