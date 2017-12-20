package com.workmarket.reporting.format;

import java.util.Locale;

import org.springframework.util.Assert;
import com.workmarket.domains.model.reporting.GenericField;

public abstract class Format implements java.io.Serializable {

	public Format(){
		locale = DEFAULT_LOCALE;
	}
	
	public Format(String formatPattern){
		this();
		Assert.notNull(formatPattern, "formatPattern object can't be null");
		this.formatPattern = formatPattern;
	}
	
	public Format(String formatPattern, Locale locale){
		Assert.notNull(locale, "locale object can't be null");
		Assert.notNull(formatPattern, "formatPattern object can't be null");
		this.formatPattern = formatPattern;
		this.locale = locale;
	}

	/**
	 * Instance variables and constants
	 * http://www.loc.gov/standards/iso639-2/php/English_list.php
	 * Using ISO 639-1 two digit language codes.
	 */
	private static final long serialVersionUID = -4129782672550005732L;
	public static final String ENGLISH_639_1 = "en";
	public static final Locale DEFAULT_LOCALE = new Locale(ENGLISH_639_1, Locale.US.getCountry());
	private String formatPattern;
	private Locale locale;	
	public static final String token = "\\*"; 
	
	
	/**
	 * @param object
	 * @return
	 */
	public abstract String format(GenericField genericField);

	/**
	 * @return the formatPattern
	 */
	public String getFormatPattern() {
		return formatPattern;
	}

	/**
	 * @param formatPattern the formatPattern to set
	 */
	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

/*	public abstract Object format(Object object);

	public abstract <T extends java.util.Calendar> String formatToString(T calendar);
	
	public abstract String formatToString(java.util.GregorianCalendar gc);

	public abstract String formatToString(String object);
*/
	
	
}
