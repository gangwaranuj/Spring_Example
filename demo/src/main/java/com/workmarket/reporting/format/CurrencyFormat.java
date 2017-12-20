package com.workmarket.reporting.format;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;
import com.workmarket.domains.model.reporting.GenericField;

import org.springframework.util.Assert;

public class CurrencyFormat extends Format {

	/**
	 * 
	 */
	public CurrencyFormat(){		
		super();
		numberFormat = NumberFormat.getCurrencyInstance(getLocale());
	}
	
	/**
	 * @param formatPattern
	 */
	public CurrencyFormat(String formatPattern){
		super(formatPattern);
		numberFormat = new DecimalFormat(getFormatPattern());
	}
	
	public CurrencyFormat(String formatPattern, Locale locale){
		super(formatPattern, locale);
		numberFormat = new DecimalFormat(getFormatPattern());
	}
	

	/**
	 * Instance variables and constants
	 */	
	private NumberFormat numberFormat;
	public static String DEFAULT_PATTERN = "#0.00";
	private static final long serialVersionUID = -2424378496669689551L;

	@Override
	public String format(GenericField genericField) {
		Assert.notNull(genericField, "number genericField can't be null");
		if(genericField.getValue() != null)
			return numberFormat.format(genericField.getValue());
		
		return null;
	}

	/**
	 * @return the numberFormat
	 */
	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}
}
