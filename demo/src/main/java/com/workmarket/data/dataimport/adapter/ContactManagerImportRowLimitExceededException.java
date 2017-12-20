package com.workmarket.data.dataimport.adapter;

/**
 * User: alexsilva Date: 5/29/14 Time: 4:59 PM
 */
public class ContactManagerImportRowLimitExceededException extends Exception {
	private static final long serialVersionUID = -2324212792463499800L;

	public ContactManagerImportRowLimitExceededException(String why) {
		super(why);
	}

	public String getWhy() {
		return super.getMessage();
	}
}
