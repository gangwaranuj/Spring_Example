package com.workmarket.api.model;

import com.workmarket.domains.model.tax.TaxEntityType;

public enum TaxEntityTypeCodeEnum {
	INDIVIDUAL(TaxEntityType.INDIVIDUAL),
	CORP(TaxEntityType.CORP),
	C_CORP(TaxEntityType.C_CORP),
	S_CORP(TaxEntityType.S_CORP),
	PARTNER(TaxEntityType.PARTNER),
	OTHER(TaxEntityType.OTHER),
	LLC_CORPORATION(TaxEntityType.LLC_CORPORATION),
	LLC_C_CORPORATION(TaxEntityType.LLC_C_CORPORATION),
	LLC_S_CORPORATION(TaxEntityType.LLC_S_CORPORATION),
	LLC_DISREGARDED(TaxEntityType.LLC_DISREGARDED),
	LLC_PARTNERSHIP(TaxEntityType.LLC_PARTNERSHIP),
	TRUST(TaxEntityType.TRUST),
	EXEMPT(TaxEntityType.EXEMPT),
	NONE(TaxEntityType.NONE);

	private final String code;

	TaxEntityTypeCodeEnum(final String code) {
		this.code = code;
	}

	public static TaxEntityTypeCodeEnum fromCode(final String code) {
		for (final TaxEntityTypeCodeEnum e : TaxEntityTypeCodeEnum.values()) {
			if (e.code().equals(code)) {
				return e;
			}
		}

		throw new RuntimeException(String.format("Unknown tax entity type code %s", code));
	}

	public String code() {
		return code;
	}
}