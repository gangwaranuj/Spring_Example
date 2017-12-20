package com.workmarket.utility;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.Map;

public class LocaleUtilities {

	private static final Map<String, Locale> COUNTRY_CODE_TO_LOCALE_MAP;

	static {
		Map<String, Locale> temp = Maps.newHashMap();

		for (Locale l : Locale.getAvailableLocales()) {
			try{
				temp.put(l.getISO3Country(), l); // avoids dupes
			}catch(Exception e){
			}
		}
		COUNTRY_CODE_TO_LOCALE_MAP = ImmutableMap.copyOf(temp);
	}


	private LocaleUtilities() {
	}

	public static Locale getDefaultLocale() {
		return Locale.US;
	}

	public static Boolean isValidCountryCode(String code) {
		if (StringUtils.isBlank(code)) return false;
		return COUNTRY_CODE_TO_LOCALE_MAP.containsKey(code.toUpperCase());
	}

	public static Locale getLocaleFromCountryCode(String code) {
		if (StringUtils.isBlank(code)) return null;
		return COUNTRY_CODE_TO_LOCALE_MAP.get(code.toUpperCase());
	}
}
