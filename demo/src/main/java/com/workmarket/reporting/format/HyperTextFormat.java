package com.workmarket.reporting.format;

import java.util.regex.Pattern;

import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public class HyperTextFormat extends Format {
	
	public HyperTextFormat(String tag){
		tag = tag.replace("&lt;", "<");
		tag = tag.replace("&gt;", ">");
		tag = tag.replace('\'', '\"');
		this.tag = tag;
		//Precompile pattern and matcher.
		pattern = Pattern.compile(token);
		matcher = pattern.matcher(tag);
	}

	/**
	 * Instance variables and constants
	 */
	private java.util.regex.Pattern pattern;
	private java.util.regex.Matcher matcher;
	private String tag = "";	
	private static final long serialVersionUID = 5837272024556503206L;
	

	/* 	Do what On null? 	
		strip markup? */

	public String format(GenericField genericField){
		try{
			Assert.notNull(genericField, "genericField can't be null");
			String s = String.valueOf(genericField.getValue());
			s = matcher.replaceAll(s);
			return s;
		}catch(Exception e){
			return "";
		}
	}


	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}


	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
}