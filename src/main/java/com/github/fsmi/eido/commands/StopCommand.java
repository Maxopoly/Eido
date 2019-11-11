package com.github.fsmi.eido.commands;

import com.github.fsmi.eido.EidoMain;

public class StopCommand extends AbstractCommand {

	public StopCommand() {
		super("stop", "end", "quit", "exit");
	}

	@Override
	public String handle(String[] args) {
		EidoMain.shutDown();
		return "";
	}

}
