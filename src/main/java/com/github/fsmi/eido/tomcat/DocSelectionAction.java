package com.github.fsmi.eido.tomcat;

import java.util.List;

import com.github.fsmi.eido.model.documents.Department;
import com.github.fsmi.eido.model.documents.Document;
import com.github.fsmi.eido.model.documents.DocumentType;
import com.github.fsmi.eido.model.documents.SolutionType;
import com.opensymphony.xwork2.ActionSupport;

public class DocSelectionAction extends ActionSupport {

	private List<Document> documents;

	@Override
	public String execute() {
		documents.add(new Document(1, Department.MATHEMATICS, System.currentTimeMillis(), 5, SolutionType.INOFFICIAL,
				null, DocumentType.WRITTEN, true, true, -1, "subguy", false, false));
		documents.add(new Document(3, Department.COMPUTER_SCIENCE, System.currentTimeMillis(), 5, SolutionType.INOFFICIAL,
				null, DocumentType.ORAL, true, true, -1, "subguy", false, false));
		return SUCCESS;
	}

	public List<Document> getDocuments() {
		return documents;
	}

}
