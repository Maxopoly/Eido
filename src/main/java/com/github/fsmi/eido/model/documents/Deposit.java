package com.github.fsmi.eido.model.documents;

import com.github.fsmi.eido.util.Guard;

public class Deposit {

	private int id;
	private int price;
	private String name;
	private String user;
	private long creationTime;
	
	public Deposit(int id, int price, String name, String user, long creationTime) {
		Guard.nullCheck(name, user);
		this.id = id;
		this.price = price;
		this.name = name;
		this.user = user;
		this.creationTime = creationTime;
	}

	/**
	 * @return Time when the deposit was taken out
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * @return Internal database id
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return Name set for this deposit
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Price in cents
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Name of who took out the deposit
	 */
	public String getUser() {
		return user;
	}

}
