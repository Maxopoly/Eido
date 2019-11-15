package com.github.fsmi.eido.database;

import org.apache.logging.log4j.Logger;

public class OdieDAO extends AbstractDAO {

	public OdieDAO(Logger logger, DBConnection db) {
		super(logger, db, "odie");
	}

	@Override
	public void registerMigrations() {
		DBMigration.createMigration(db, migrationID, 1, 
				"CREATE TABLE orders(id serial NOT NULL,name varchar(256) NOT NULL,"
				+ "creation_time timestamp with time zone NOT NULL DEFAULT now(),CONSTRAINT orders_pkey PRIMARY KEY (id))",
				
				//odie had a foreign key on document_id to the documents table, which we don't have here
				"CREATE TABLE order_documents(index integer NOT NULL,order_id integer NOT NULL,"
				+ "document_id integer NOT NULL,CONSTRAINT order_documents_pkey PRIMARY KEY "
				+ "(index, order_id, document_id),CONSTRAINT order_documents_order_id_fkey "
				+ "FOREIGN KEY (order_id)REFERENCES orders (id) MATCH SIMPLEON "
				+ "UPDATE NO ACTION ON DELETE NO ACTION)"

				);
		
	}

}
