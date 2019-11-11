package com.github.fsmi.eido.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCommand {

	private final List<String> identifier;

	public AbstractCommand(String id, String... identifiers) {
		//having a single string here at the start enforces at least one to be given at compile time
		List <String> tempList = new LinkedList<>(Arrays.asList(identifiers));
		tempList.add(id);
		this.identifier = Collections.unmodifiableList(tempList);
	}

	/**
	 * @return Identifiers usable to call this command
	 */
	public List<String> getIdentifiers() {
		return identifier;
	}

	/**
	 * Called when the command is executed so it can do its thing
	 * 
	 * @param args Arguments passed, split up by spaces inbetween them
	 * @return Reply to send to the user
	 */
	public abstract String handle(String[] args);

}
