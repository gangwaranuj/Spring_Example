package com.workmarket.api.model;

import com.workmarket.domains.model.tax.AbstractTaxEntity;

public enum CountryEnum {
	USA(AbstractTaxEntity.COUNTRY_USA),
	CANADA(AbstractTaxEntity.COUNTRY_CANADA),
	OTHER(AbstractTaxEntity.COUNTRY_OTHER);

	private final String code;

	CountryEnum(final String code) {
		this.code = code;
	}

	public static CountryEnum fromCode(final String code) {
		for (final CountryEnum e : CountryEnum.values()) {
			if (e.code().equals(code)) {
				return e;
			}
		}

		throw new RuntimeException(String.format("Unknown country code %s", code));
	}

	public String code() {
		return code;
	}
}