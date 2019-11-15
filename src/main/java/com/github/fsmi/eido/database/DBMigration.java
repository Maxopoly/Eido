package com.github.fsmi.eido.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.github.fsmi.eido.EidoMain;
import com.github.fsmi.eido.util.Guard;

public final class DBMigration {

	private static Set<Integer> usedIds = new TreeSet<>();

	/**
	 * Access point for any changes made to the database structure. Registered
	 * migrations will be executed in ascending id order and only if no update with
	 * an id bigger than them has ever completed successfully for the same migration
	 * key. Once a migration has run successfully, it can thus never run again.
	 * Migration ids may be at minimum 1 and no migrations may share the same id.
	 * 
	 * When running the migration, first all of the given queries will be executed.
	 * The migration will fail and cancel if any errors occur when executing these.
	 * Then the given Callable will be executed, if it is not null. Your Callable
	 * may indicate whether its updating efforts were successful through its return
	 * value. The migration is only considered successful if it returns true
	 * 
	 * @param db           Database connection to run the updating efforts on
	 * @param migrationKey Key unique to this classes migration cycle
	 * @param id           Unique identifiying id for this migration, which will
	 *                     also determine the order in which updates are executed
	 * @param executable   Callable to run optionally after the queries
	 * @param queries      Queries to run at the beginning of the migration
	 */
	public static void createMigration(DBConnection db, String migrationKey, int id, Callable<Boolean> executable,
			String... queries) {
		if (executable == null && queries.length == 0) {
			throw new IllegalArgumentException(String.format("Can not register empty migration with id %d", id));
		}
		if (usedIds.contains(id)) {
			throw new IllegalArgumentException(String.format("Migration with id %d was already registered", id));
		}
		usedIds.add(id);
		DBMigration migration = new DBMigration(db, id, migrationKey, executable, queries);
		EidoMain.getDBMigrationHandler().registerMigration(migration);
	}

	/**
	 * Access point for any changes made to the database structure. Registered
	 * migrations will be executed in ascending id order and only if no update with
	 * an id bigger than them has ever completed successfully for the same migration
	 * key. Once a migration has run successfully, it can thus never run again.
	 * Migration ids may be at minimum 1 and no migrations may share the same id.
	 * 
	 * When running the migration, all of the given queries will be executed. The
	 * migration is only considered successful if all of these go through without
	 * any exceptions
	 * 
	 * @param db           Database connection to run the updating efforts on
	 * @param migrationKey Key unique to this classes migration cycle
	 * @param id           Unique identifiying id for this migration, which will
	 *                     also determine the order in which updates are executed
	 * @param queries      Queries to run as part of the migration
	 */
	public static void createMigration(DBConnection db, String migrationKey, int id, String query, String... queries) {
		// add single element to front of array
		String[] allQueries = new String[queries.length + 1];
		allQueries[0] = query;
		for (int i = 1; i < allQueries.length; i++) {
			allQueries[i] = queries[i - 1];
		}
		createMigration(db, migrationKey, id, (Callable<Boolean>) null, allQueries);
	}

	private final int id;
	private final String name;
	private Callable<Boolean> executable;
	private String[] queries;
	private DBConnection db;

	private DBMigration(DBConnection db, int id, String name, Callable<Boolean> executable, String[] queries) {
		Guard.nullCheck(db, name);
		if (id <= 0) {
			throw new IllegalArgumentException("Migration id must be bigger than 0");
		}
		this.db = db;
		this.id = id;
		this.name = name;
		this.executable = executable;
		this.queries = queries;
	}

	boolean execute() {
		for (String query : queries) {
			try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
				ps.execute();
			} catch (SQLException e) {
				EidoMain.getLogger().error(String.format("Failed to run query %s for migration %s", query, toString()),
						e);
				return false;
			}
		}
		if (executable != null) {
			try {
				executable.call();
			} catch (Exception e) {
				EidoMain.getLogger().error(String.format("Failed to run callable for migration %s", toString()), e);
			}
		}
		return true;
	}

	/**
	 * @return ID based on which the order in which this migration is applied is determined
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * @return Name of the database section this migration is for
	 */
	public String getName() {
		return name;
	}

	public int compareTo(DBMigration otherMigration) {
		return Integer.compare(id, otherMigration.id);
	}

	@Override
	public String toString() {
		return String.format("DBMigration %d", id);
	}

}
