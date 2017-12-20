package com.workmarket.reporting.format;

import java.text.NumberFormat;
import com.workmarket.domains.model.reporting.GenericField;
import java.text.DecimalFormat;
import java.util.Locale;

import org.springframework.util.Assert;

public class WMDecimalFormat extends Format {

	/**
	 * 
	 */
	public WMDecimalFormat(){		
		numberFormat = new DecimalFormat(DEFAULT_PATTERN);
	}
	
	/**
	 * @param formatPattern
	 */
	public WMDecimalFormat(String formatPattern){
		super(formatPattern);
		numberFormat = new DecimalFormat(getFormatPattern());
	}
	
	public WMDecimalFormat(String formatPattern, Locale locale){
		super(formatPattern, locale);
		numberFormat = new DecimalFormat(getFormatPattern());
	}
	

	/**
	 * Instance variables and constants
	 */	
	private NumberFormat numberFormat;
	public static String DEFAULT_PATTERN = "#0.00";
	private static final long serialVersionUID = -2424378496669689551L;

	/* (non-Javadoc)
	 * @see com.workmarket.reporting.format.Format#format(com.workmarket.domains.model.reporting.GenericField)
	 */
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
