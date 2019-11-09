package com.github.fsmi.eido.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;

import com.github.fsmi.eido.model.documents.Department;
import com.github.fsmi.eido.model.documents.Document;
import com.github.fsmi.eido.model.documents.DocumentType;
import com.github.fsmi.eido.model.documents.SolutionType;

public class FSMIDatabase {

	private DBConnection db;
	private Logger logger;

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
