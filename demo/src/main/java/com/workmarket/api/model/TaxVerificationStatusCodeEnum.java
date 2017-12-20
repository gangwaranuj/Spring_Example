package com.workmarket.api.model;

import com.workmarket.domains.model.tax.TaxVerificationStatusType;

public enum TaxVerificationStatusCodeEnum {
	UNVERIFIED(TaxVerificationStatusType.UNVERIFIED),
	APPROVED(TaxVerificationStatusType.APPROVED),
	VALIDATED(TaxVerificationStatusType.VALIDATED),
	DUPLICATE_TIN(TaxVerificationStatusType.DUPLICATE_TIN),
	INVALID_REQUEST(TaxVerificationStatusType.INVALID_REQUEST),
	NOT_ISSUED(TaxVerificationStatusType.NOT_ISSUED),
	NOT_MATCHED(TaxVerificationStatusType.NOT_MATCHED),
	SIGNED_FORM_W8(TaxVerificationStatusType.SIGNED_FORM_W8);
	private final String code;

	TaxVerificationStatusCodeEnum(final String code) {
		this.code = code;
	}

	public static TaxVerificationStatusCodeEnum fromCode(final String code) {
		for (final TaxVerificationStatusCodeEnum e : TaxVerificationStatusCodeEnum.values()) {
			if (e.code().equals(code)) {
				return e;
			}
		}

		throw new RuntimeException(String.format("Unknown tax verification status code %s", code));
	}

	public String code() {
		return code;
	}
}