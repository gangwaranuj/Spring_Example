package com.workmarket.reporting.format;

import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public class StriptHtmlFormat extends Format {
	

	/**
	 * 
	 */
	public StriptHtmlFormat(){
		this.regex = DEFAULT_PATTERN;
		this.replacement = "";
	}
	
	/**
	 * @param regex
	 * @param replacement
	 */
	public StriptHtmlFormat(String regex, String replacement){
		this.regex = regex;
		this.replacement = replacement;
	}
	

	/**
	 * Instance variables and constants
	 */	
	private String regex;
	private String replacement;
	public static String DEFAULT_PATTERN = "\\<.*?\\>";
	private static final long serialVersionUID = -2424379496569289551L;

	/* (non-Javadoc)
	 * @see com.workmarket.reporting.format.Format#format(com.workmarket.domains.model.reporting.GenericField)
	 */
	@Override
	public String format(GenericField genericField) {
		Assert.notNull(genericField, "number genericField can't be null");
		String s = (String)genericField.getValue();
		return s.replaceAll(getRegex(), getReplacement());
	}

	/**
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * @return the replacement
	 */
	public String getReplacement() {
		return replacement;
	}

	/**
	 * @param replacement the replacement to set
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}


	
}
