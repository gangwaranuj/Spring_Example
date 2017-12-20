package com.workmarket.reporting.format;


import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public class TitleLinkFormat extends Format {
	

	public TitleLinkFormat(String url){
		this.url = url;
	}

	/**
	 * Instance variables and constants
	 */

	private String url;	
	private static final long serialVersionUID = 5997348352389223463L;
	

	/* (non-Javadoc)
	 * @see com.workmarket.reporting.format.Format#format(com.workmarket.domains.model.reporting.GenericField)
	 */
	public String format(GenericField genericField){
		try{
			//TODO perhaps switch to regex Pattern and Matcher
			Assert.notNull(genericField, "genericField can't be null");
			String workNumber = String.valueOf(genericField.getRowOfGenericFields().get("workNumber").getValue());
			String title = String.valueOf(genericField.getValue());
			StringBuilder sb = new StringBuilder();
			sb.append("<a href=\"" + getUrl());
			sb.append(workNumber);
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