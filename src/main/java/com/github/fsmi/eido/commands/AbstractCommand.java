package com.github.fsmi.eido.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand {

	private final List<String> identifier;

	public AbstractCommand(String... identifiers) {
		if (identifiers.length == 0) {
			throw new IllegalArgumentException("Can not create command without identifiers");
		}
		this.identifier = Collections.unmodifiableList(Arrays.asList(identifiers));
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
