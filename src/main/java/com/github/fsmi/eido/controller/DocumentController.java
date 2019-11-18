package com.github.fsmi.eido.controller;

import java.util.Collection;

import com.github.fsmi.eido.database.DocumentDAO;
import com.github.fsmi.eido.model.documents.Document;
import com.github.fsmi.eido.model.documents.Examinant;
import com.github.fsmi.eido.model.documents.Lecture;

public class DocumentController {
	
	private DocumentDAO dao;
	
	public DocumentController(DocumentDAO dao) {
		this.dao = dao;
	}
	
	public Collection<Document> getDocumentsByFilter(Collection<Lecture> lectures, Collection<Examinant> examinants) {
return null;
	}
	
	public void acceptProtocol(int id) {
		
	}
	
	public void createDocument(Document document) {
		
	}

}
