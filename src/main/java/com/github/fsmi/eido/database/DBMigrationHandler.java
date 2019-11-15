package com.github.fsmi.eido.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.util.Guard;

public class DBMigrationHandler {

	private DBConnection db;
	private TreeSet<DBMigration> migrations;
	private Logger logger;

	/**
	 * @param db     Database connection to use for version tracking table
	 * @param logger Logger to use
	 */
	public DBMigrationHandler(DBConnection db, Logger logger) {
		Guard.nullCheck(db, logger);
		this.db = db;
		// we use a treeset, because iterating over it will always be in ascending key
		// order, which we define through the compareTo() method of DBMigration
		this.migrations = new TreeSet<>();
	}

	private int loadCurrentVersion() {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement("create table if not exists eido_version"
						+ "(id int not null primary key, name varchar(255) not null, "
						+ "done_when timestamp not null default now()")) {
			ps.execute();
		} catch (SQLException e) {
			logger.error("Unable to create versioning table", e);
			return -1;
		}
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement("select max(id) from eido_version;");
				ResultSet rs = ps.executeQuery()) {
			if (!rs.next()) {
				return 0;
			}
			return rs.getInt(1);
		} catch (SQLException e) {
			logger.error("Unable to retrieve current version", e);
			return -1;
		}
	}

	/**
	 * Applies all registered database migrations in order
	 * 
	 * @return True if everything worked out fine, false if errors occured
	 */
	public boolean migrateAll() {
		int currentVersion = loadCurrentVersion();
		if (currentVersion == -1) {
			return false;
		}
		for (DBMigration migration : migrations) {
			if (migration.getID() <= currentVersion) {
				continue;
			}
			if (!migration.execute()) {
				logger.error(String.format("Failed to execute migration %s", migration));
				return false;
			}
			currentVersion = migration.getID();
			insertUpdateExecuted(currentVersion);
		}
		return true;
	}

	private void insertUpdateExecuted(int id) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement("insert into eido_version (id, name) values(?);")) {
			ps.setInt(1, id);
			ps.execute();
		} catch (SQLException e) {
			logger.error("Failed to insert executed database update", e);
		}
	}

	void registerMigration(DBMigration migration) {
		migrations.add(migration);
	}

}
