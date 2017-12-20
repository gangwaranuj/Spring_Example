package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

@Entity(name = "address_type")
@Table(name = "address_type")
public class AddressType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String PROFILE = "profile";
	public static final String COMPANY = "company";
	public static final String TAX = "tax_entity";
	public static final String BUSINESS = "business";
	public static final String RESIDENCE = "residence";
	public static final String CLIENT_LOCATION = "client";
	public static final String PARTS_LOGISTICS = "parts";
	public static final String ASSIGNMENT = "work";
	public static final String SERVICE_AREA = "srvc_area";

	public AddressType() {}

	public AddressType(String code) {
		super(code);
		switch (code) {
			case PROFILE:
				setCode(PROFILE);
				break;
			case COMPANY:
				setCode(COMPANY);
				break;
			case TAX:
				setCode(TAX);
				break;
			case BUSINESS:
				setCode(BUSINESS);
				break;
			case RESIDENCE:
				setCode(RESIDENCE);
				break;
			case PARTS_LOGISTICS:
				setCode(PARTS_LOGISTICS);
				break;
			case SERVICE_AREA:
				setCode(SERVICE_AREA);
				break;
		}
	}

	public static AddressType newAddressType(String addressTypeCode) {
		return new AddressType(addressTypeCode);
	}

	@Transient
	public boolean isCompanyAddress() {
		return getCode().equals(COMPANY);
	}
	
	@Transient
	public boolean isProfileAddress() {
		return getCode().equals(PROFILE);
	}
	
	@Transient
	public boolean isTaxEntity() {
		return getCode().equals(TAX);
	}
	
	@Transient
	public boolean isClientLocation() {
		return getCode().equals(CLIENT_LOCATION);
	}
	
	public static AddressType valueOf(String value) {
		return new AddressType(StringUtils.lowerCase(value));
	}

}
