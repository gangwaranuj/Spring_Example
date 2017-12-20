package com.workmarket.reporting.format;


import com.workmarket.domains.model.reporting.GenericField;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.Assert;

public class LastNameFormat extends Format {
	
	
	public LastNameFormat(String url){
		this.url = url;
	}

	/**
	 * Instance variables and constants
	 */
	private String url;
	private static final long serialVersionUID = 5837272024556503206L;

	public String format(GenericField genericField){
		try{
			//TODO perhaps switch to regex Pattern and Matcher
			Assert.notNull(genericField, "genericField can't be null");
			String userNumber = String.valueOf(genericField.getRowOfGenericFields().get("workResourceID").getValue());
			String title = String.valueOf(genericField.getValue());
			StringBuilder sb = new StringBuilder();
			sb.append("<a href=\"" + getUrl());
			sb.append(userNumber);
			sb.append("\" target='_blank'>");
			sb.append(StringEscapeUtils.escapeHtml(title));
			sb.append("</a>");
			return sb.toString();
		}catch(Exception e){
			return "";
		}
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}