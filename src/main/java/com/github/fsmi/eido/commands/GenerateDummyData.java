package com.github.fsmi.eido.commands;

import com.github.fsmi.eido.EidoMain;
import com.github.fsmi.eido.database.FSMIDAO;
import com.github.fsmi.eido.model.documents.Department;
import com.github.fsmi.eido.model.documents.DocumentType;
import com.github.fsmi.eido.model.documents.SolutionType;

public class GenerateDummyData extends AbstractCommand {

	public GenerateDummyData() {
		super("gendummydata", "generatedummydata");
	}

	@Override
	public String handle(String[] args) {
		if (EidoMain.getConfig().isProduction()) {
			return "Can not generated dummy entries in production setup";
		}
		// TODO
		FSMIDAO dao = null;
		dao.createDocument(Department.COMPUTER_SCIENCE, 10, SolutionType.NONE, null, DocumentType.ORAL, true, false, "",
				false, false);
		return "Generated dummy entries for all tables";
	}

}
