package com.workmarket.domains.work.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Entity(name="cancellation_reason_type")
@Table(name="cancellation_reason_type")
public class CancellationReasonType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NOT_CANCELLED = "not_cancelled";
	public static final String END_USER_CANCELLED = "end_user_cancelled";
	public static final String RESOURCE_NO_SHOW = "resource_no_show";
	public static final String RESOURCE_CANCELLED = "resource_cancelled";
	public static final String RESOURCE_ABANDONED = "resource_abandoned";
	public static final String PERSONAL_EMERGENCY = "personal_emergency";
	public static final String BUYER_CANCELLED = "buyer_cancelled";
	public static final String OTHER = "other";

	public static final String RESOURCE_CANCELLED_DESCRIPTION = "Worker cancelled prior to start";
	public static final String RESOURCE_ABANDONED_DESCRIPTION = "Worker abandoned assignment";

	public static final String[] CANCELLATION_REASON_TYPES = new String[] {
		NOT_CANCELLED,
		END_USER_CANCELLED,
		RESOURCE_NO_SHOW,
		PERSONAL_EMERGENCY,
		RESOURCE_CANCELLED,
		RESOURCE_ABANDONED,
		BUYER_CANCELLED,
		OTHER
	};

	public static final Map<String,String> UNASSIGN_REASON_MAP = ImmutableMap.<String,String>builder()
		.put(RESOURCE_CANCELLED, RESOURCE_CANCELLED_DESCRIPTION)
		.put(RESOURCE_ABANDONED, RESOURCE_ABANDONED_DESCRIPTION)
		.build();

	public static List<String> cancellationReasons = Arrays.asList(CANCELLATION_REASON_TYPES);

	public static CancellationReasonType createResourceCanceledReasonType() {
		return new CancellationReasonType(RESOURCE_CANCELLED, RESOURCE_CANCELLED_DESCRIPTION);
	}

	public static CancellationReasonType createResourceAbandonedReasonType() {
		return new CancellationReasonType(RESOURCE_ABANDONED, RESOURCE_ABANDONED_DESCRIPTION);
	}
	
	public CancellationReasonType() {}
	
	public CancellationReasonType(String code) {
		super(code);
	}

	public CancellationReasonType(String code, String description) {
		super(code, description);
	}

	@Transient
	public boolean isResourceCancelled() {
		return RESOURCE_CANCELLED.equals(getCode());
	}

	@Transient
	public boolean isBuyerCancelled() {
		return BUYER_CANCELLED.equals(getCode());
	}

	@Transient
	public boolean isResourceAbandoned() {
		return RESOURCE_ABANDONED.equals(getCode());
	}

	@Transient
	public boolean isNotCancelled() {
		return NOT_CANCELLED.equals(getCode());
	}

	@Transient
	public boolean isPersonalEmergency() {
		return PERSONAL_EMERGENCY.equals(getCode());
	}

}
