package com.github.fsmi.eido.model.documents;

import java.util.List;

/**
 * A lecture, independent of who is holding it
 *
 */
public class Lecture {

	private int id;
	private String name;
	private List<String> aliases;
	private String comment;
	private boolean validated;

	public Lecture(int id, String name, List<String> aliases, String comment, boolean validated) {
		this.id = id;
		this.name = name;
		this.aliases = aliases;
		this.comment = comment;
		this.validated = validated;
	}

	/**
	 * @return Alternative identifiers which can be used to find this lecture
	 */
	public List<String> getAliases() {
		return aliases;
	}

	/**
	 * @return Optional comment regarding this lecture, may be null
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return Internal database id
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return Human friendly name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Does this lecture have additional comments associated with it
	 */
	public boolean hasComment() {
		return comment != null;
	}
	
	public boolean isValidated() {
		return validated;
	}

}
