package com.workmarket.reporting.format;

import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public class StringFormat extends Format {

	/**
	 * Instance variables and constants
	 */
	private static final long serialVersionUID = 7850329438749984187L;


	/* (non-Javadoc)
	 * @see com.workmarket.reporting.format.Format#format(com.workmarket.domains.model.reporting.GenericField)
	 */
	public String format(GenericField genericField){
		try{
			Assert.notNull(genericField, "genericField can't be null");
			return String.valueOf(genericField.getValue());
		}catch(Exception e){
			return "";
		}
	}

}