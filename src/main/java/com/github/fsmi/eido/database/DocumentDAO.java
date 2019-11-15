package com.github.fsmi.eido.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.model.documents.Department;
import com.github.fsmi.eido.model.documents.Document;
import com.github.fsmi.eido.model.documents.DocumentType;
import com.github.fsmi.eido.model.documents.SolutionType;

public class DocumentDAO extends AbstractDAO {

	public DocumentDAO(Logger logger, DBConnection db) {
		super(logger, db, "documents");
	}

	@Override
	public void registerMigrations() {
		// creates old odie db structure

		// TODO custom data types
		DBMigration.createMigration(db, migrationID, 1,
				"CREATE TABLE IF NOT EXISTS documents(id serial NOT NULL, department department NOT NULL, date date NOT NULL,"
						+ "number_of_pages integer NOT NULL DEFAULT 0,"
						+ "solution solution,comment varchar NOT NULL DEFAULT '',document_type document_type NOT NULL,"
						+ "has_file boolean NOT NULL DEFAULT false, has_barcode boolean NOT NULL DEFAULT false,"
						+ "validation_time timestamp with time zone, submitted_by character varying,"
						+ "legacy_id integer, early_document_eligible boolean NOT NULL DEFAULT false,"
						+ "deposit_return_eligible boolean NOT NULL DEFAULT false, "
						+ "CONSTRAINT documents_pkey PRIMARY KEY (id))",

				"CREATE TABLE IF NOT EXISTS examinants(id serial NOT NULL,name varchar NOT NULL,"
						+ "validated boolean NOT NULL,CONSTRAINT examinants_pkey PRIMARY KEY (id))",

				"CREATE TABLE IF NOT EXISTS document_examinants(document_id integer NOT NULL,"
						+ "examinant_id integer NOT NULL,CONSTRAINT document_examinants_document_id_fkey "
						+ "FOREIGN KEY (document_id)REFERENCES documents.documents (id) MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE,"
						+ "CONSTRAINT document_examinants_examinant_id_fkey "
						+ "FOREIGN KEY (examinant_id)REFERENCES documents.examinants (id) MATCH SIMPLEON "
						+ "UPDATE NO ACTION ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS deposits(id serial NOT NULL,price integer NOT NULL,name varchar NOT NULL,"
						+ "by_user varchar NOT NULL,date timestamp with time zone NOT NULL DEFAULT now(),CONSTRAINT deposits_pkey PRIMARY KEY (id))",

				"CREATE TABLE IF NOT EXISTS deposit_lectures(deposit_id integer NOT NULL,lecture_id integer NOT NULL,CONSTRAINT deposit_lectures_deposit_id_fkey "
						+ "FOREIGN KEY (deposit_id)REFERENCES deposits (id) MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE,"
						+ "CONSTRAINT deposit_lectures_lecture_id_fkey FOREIGN KEY (lecture_id)REFERENCES lectures (id) "
						+ "MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE)",

				// Odie previously had a foreign key for garfield.locations for the locations in
				// this table, which was rather weird data structure wise and couldnt be
				// properly done here as we only work on a single schema, so we did not include
				// it in this creation statement. Handling of this foreign key is addressed in
				// migrations further down
				"CREATE TABLE IF NOT EXISTS folders(id serial NOT NULL,name varchar NOT NULL,location_id integer NOT NULL,"
						+ "document_type document_type NOT NULL, CONSTRAINT folders_pkey PRIMARY KEY (id))",

				"CREATE TABLE IF NOT EXISTS folder_docs(folder_id integer NOT NULL,document_id integer NOT NULL,CONSTRAINT folder_docs_document_id_fkey "
						+ "FOREIGN KEY (document_id)REFERENCES documents (id) MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE,"
						+ "CONSTRAINT folder_docs_folder_id_fkey FOREIGN KEY (folder_id)REFERENCES folders (id) "
						+ "MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS folder_examinants(folder_id integer NOT NULL,"
						+ "examinant_id integer NOT NULL,CONSTRAINT folder_examinants_examinant_id_fkey "
						+ "FOREIGN KEY (examinant_id)REFERENCES examinants (id) MATCH SIMPLEON "
						+ "UPDATE NO ACTION ON DELETE CASCADE,CONSTRAINT folder_examinants_folder_id_fkey "
						+ "FOREIGN KEY (folder_id)REFERENCES folders (id) MATCH SIMPLEON UPDATE NO ACTION "
						+ "ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS lectures(id serial NOT NULL,name varchar NOT NULL,aliases varchar[] "
						+ "NOT NULL DEFAULT '{}',comment varchar NOT NULL DEFAULT '',validated boolean NOT NULL,"
						+ "CONSTRAINT lectures_pkey PRIMARY KEY (id))",

				"CREATE TABLE IF NOT EXISTS lecture_docs(lecture_id integer NOT NULL,document_id integer NOT NULL,"
						+ "CONSTRAINT lecture_docs_pkey PRIMARY KEY (lecture_id, document_id),"
						+ "CONSTRAINT lecture_docs_document_id_fkey FOREIGN KEY (document_id)REFERENCES documents (id) "
						+ "MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE,CONSTRAINT lecture_docs_lecture_id_fkey "
						+ "FOREIGN KEY (lecture_id)REFERENCES lectures (id) MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE)",

				"CREATE TABLE IF NOT EXISTS folder_lectures(folder_id integer NOT NULL,lecture_id integer NOT NULL,"
						+ "CONSTRAINT folder_lectures_pkey PRIMARY KEY (folder_id, lecture_id),"
						+ "CONSTRAINT folder_lectures_folder_id_fkey FOREIGN KEY (folder_id)REFERENCES folders (id) "
						+ "MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE,CONSTRAINT folder_lectures_lecture_id_fkey "
						+ "FOREIGN KEY (lecture_id)REFERENCES lectures (id) MATCH SIMPLEON UPDATE NO ACTION ON DELETE CASCADE)"

		);
	}

	public Document createDocument(Department department, int numberOfPages, SolutionType solutionType, String comment,
			DocumentType documentType, boolean hasFile, boolean hasBarcode, String submitter,
			boolean earlyDocumentEligible, boolean depositReturnEligible) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"insert into documents (department, date, number_of_pages, solution, comment, "
								+ "document_type, has_file, has_barcode, submitted_by, "
								+ "early_document_eligible, deposit_return_eligible) "
								+ "values(?,?,?,?,?,?, ?,?,?,?,?,?);",
						Statement.RETURN_GENERATED_KEYS)) {
			// TODO
			long creationTime = System.currentTimeMillis();
			ps.setDate(2, new Date(creationTime));
			ps.setInt(3, numberOfPages);
			// TODO
			ps.setString(5, comment);
			// TODO
			ps.setBoolean(7, hasFile);
			ps.setBoolean(8, hasBarcode);
			ps.setString(9, submitter);
			ps.setBoolean(10, earlyDocumentEligible);
			ps.setBoolean(11, depositReturnEligible);
			int id;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (!rs.next()) {
					logger.error("No key created for document?");
					return null;
				}
				id = rs.getInt(1);
			}
			return new Document(id, department, creationTime, numberOfPages, solutionType, comment, documentType,
					hasFile, hasBarcode, -1, submitter, earlyDocumentEligible, depositReturnEligible);
		} catch (SQLException e) {
			logger.error("Failed to create document ", e);
			return null;
		}

	}

	public Document getDocumentById(int id) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn
						.prepareStatement("select department, date, number_of_pages, solution, comment, "
								+ "document_type, has_file, has_barcode, validation_time, submitted_by, "
								+ "early_document_eligible, deposit_return_eligible from documents "
								+ "where id = ?")) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null;
				}
				Department department = null; // TODO
				long creationTime = rs.getDate(2).getTime();
				int numPages = rs.getInt(3);
				SolutionType solution = null; // TODO
				String comment = rs.getString(5);
				DocumentType type = null; // TODO
				boolean hasFile = rs.getBoolean(7);
				boolean hasBarcode = rs.getBoolean(8);
				Timestamp validationTimeStamp = rs.getTimestamp(9);
				long validationTime;
				if (validationTimeStamp == null) {
					validationTime = -1;
				} else {
					validationTime = validationTimeStamp.getTime();
				}
				String submitter = rs.getString(10);
				boolean earlyDocumentEligible = rs.getBoolean(11);
				boolean depositReturnEligible = rs.getBoolean(12);
				return new Document(id, department, creationTime, numPages, solution, comment, type, hasFile,
						hasBarcode, validationTime, submitter, earlyDocumentEligible, depositReturnEligible);
			}
		} catch (SQLException e) {
			logger.error("Failed to retrieve document by id ", e);
			return null;
		}
	}

}
