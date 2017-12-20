package com.workmarket.domains.model.screening;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity(name="screening_status_type")
@Table(name="screening_status_type")
public class ScreeningStatusType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String REQUESTED = "requested";
	public static final String ERROR = "error";
	public static final String PASSED = "passed";
	public static final String FAILED = "failed";
	public static final String NOT_REQUESTED = "noRequest";
	public static final String CANCELLED = "cancelled";
	public static final String REVIEW = "review";
	//status should only be set when user chooses to take a new background check
	public static final String EXPIRED = "expired";

	public static final List<String> BACKGROUND_CHECK_PURCHASE_STATUS_TYPES = ImmutableList.of(ERROR, CANCELLED, PASSED, FAILED);
	
	public ScreeningStatusType() {}
	public ScreeningStatusType(String code) {
		super(code);
	}
	
	public static String[] getResponseTypes() {
		return new String[] {
			ScreeningStatusType.PASSED,
			ScreeningStatusType.FAILED,
			ScreeningStatusType.CANCELLED,
			ScreeningStatusType.REVIEW
		};
	}
}
