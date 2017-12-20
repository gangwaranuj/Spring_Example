package com.workmarket.domains.model.account.pricing;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


@Entity(name = "accountServiceType")
@Table(name = "account_service_type")
public class AccountServiceType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String VENDOR_OF_RECORD = "vor";
	public static final String TAX_SERVICE_1099 = "tax";
	public static final String MBO = "mbo";
	public static final String NONE = "none";

	/*
        This is confusing because MBO should not be a service type, however this is the explanation:

        MBO and NONE are considered NON VOR Service types for TAX purposes (1099, earning reports, etc)
        For Journal Entry Summary's throughput, NON VOR types include TAX SERVICE (aka the service we provide for Yahoo)
		VOR service type to this date is no only VOR
	 */
	public static final List<String> NON_VOR_AND_TAX_SERVICE_TYPES = ImmutableList.of(TAX_SERVICE_1099, MBO, NONE);
	public static final List<String> VOR_SERVICE_TYPES = ImmutableList.of(VENDOR_OF_RECORD);
	public static final List<String> TAX_SERVICE_1099_SERVICE_TYPE = ImmutableList.of(TAX_SERVICE_1099);
	public static final List<String> NON_VOR_SERVICE_TYPES = ImmutableList.of(MBO, NONE);

	public AccountServiceType() {
		super();
	}

	public AccountServiceType(String code) {
		super(code);
	}

	@Transient
	public boolean isVendorOfRecord() {
		return getCode().equals(VENDOR_OF_RECORD);
	}

	@Transient
	public boolean isTaxService() {
		return getCode().equals(TAX_SERVICE_1099);
	}

	@Transient
	public boolean isMboService() {
		return getCode().equals(MBO);
	}

	@Transient
	public boolean isNone() {
		return getCode().equals(NONE);
	}
}