package com.workmarket.thrift.core;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Company implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String companyNumber;
	private String companyUuid;
	private String name;
	private Asset avatarOriginal;
	private Asset avatarLarge;
	private Asset avatarSmall;
	private String dunsNumber;
	private int yearsInBusiness;
	private int numberOfEmployees;
	private long createdOn;
	private String customSignatureLine;

	public Company() {
	}

	public Company(
			long id,
			String companyNumber,
			String companyUuid,
			String name,
			Asset avatarOriginal,
			Asset avatarLarge,
			Asset avatarSmall,
			String dunsNumber,
			int yearsInBusiness,
			int numberOfEmployees,
			long createdOn,
			String customSignatureLine) {
		this();
		this.id = id;
		this.companyNumber = companyNumber;
		this.companyUuid = companyUuid;
		this.name = name;
		this.avatarOriginal = avatarOriginal;
		this.avatarLarge = avatarLarge;
		this.avatarSmall = avatarSmall;
		this.dunsNumber = dunsNumber;
		this.yearsInBusiness = yearsInBusiness;
		this.numberOfEmployees = numberOfEmployees;
		this.createdOn = createdOn;
		this.customSignatureLine = customSignatureLine;
	}

	public long getId() {
		return this.id;
	}

	public Company setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getCompanyNumber() {
		return this.companyNumber;
	}

	public Company setCompanyNumber(final String companyNumber) {
		this.companyNumber = companyNumber;
		return this;
	}

	public boolean isSetCompanyNumber() {
		return this.companyNumber != null;
	}

	public String getCompanyUuid() { return this.companyUuid; }

	public Company setCompanyUuid(final String companyUuid) {
		this.companyUuid = companyUuid;
		return this;
	}

	public boolean isSetCompanyUuid() {return this.companyUuid != null;}

	public String getName() {
		return this.name;
	}

	public Company setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isSetName() {
		return this.name != null;
	}

	public Asset getAvatarOriginal() {
		return this.avatarOriginal;
	}

	public Company setAvatarOriginal(Asset avatarOriginal) {
		this.avatarOriginal = avatarOriginal;
		return this;
	}

	public boolean isSetAvatarOriginal() {
		return this.avatarOriginal != null;
	}

	public Asset getAvatarLarge() {
		return this.avatarLarge;
	}

	public Company setAvatarLarge(Asset avatarLarge) {
		this.avatarLarge = avatarLarge;
		return this;
	}

	public boolean isSetAvatarLarge() {
		return this.avatarLarge != null;
	}

	public Asset getAvatarSmall() {
		return this.avatarSmall;
	}

	public Company setAvatarSmall(Asset avatarSmall) {
		this.avatarSmall = avatarSmall;
		return this;
	}

	public boolean isSetAvatarSmall() {
		return this.avatarSmall != null;
	}

	public String getDunsNumber() {
		return this.dunsNumber;
	}

	public Company setDunsNumber(String dunsNumber) {
		this.dunsNumber = dunsNumber;
		return this;
	}

	public boolean isSetDunsNumber() {
		return this.dunsNumber != null;
	}

	public int getYearsInBusiness() {
		return this.yearsInBusiness;
	}

	public Company setYearsInBusiness(int yearsInBusiness) {
		this.yearsInBusiness = yearsInBusiness;
		return this;
	}

	public boolean isSetYearsInBusiness() {
		return (yearsInBusiness > 0);
	}

	public int getNumberOfEmployees() {
		return this.numberOfEmployees;
	}

	public Company setNumberOfEmployees(int numberOfEmployees) {
		this.numberOfEmployees = numberOfEmployees;
		return this;
	}

	public boolean isSetNumberOfEmployees() {
		return (numberOfEmployees > 0);
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public Company setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public boolean isSetCreatedOn() {
		return (createdOn > 0L);
	}

	public String getCustomSignatureLine() {
		return this.customSignatureLine;
	}

	public Company setCustomSignatureLine(String customSignatureLine) {
		this.customSignatureLine = customSignatureLine;
		return this;
	}

	public boolean isSetCustomSignatureLine() {
		return this.customSignatureLine != null;
	}

	@Override
	public boolean equals(Object that) {
		return that != null && that instanceof Company && this.equals((Company) that);
	}

	private boolean equals(Company that) {
		if (that == null)
			return false;

		if (this.id != that.id) {
			return false;
		}

		if ((isSetCompanyNumber() != that.isSetCompanyNumber()) ||
			(isSetCompanyNumber() && !getCompanyNumber().equals(that.getCompanyNumber()))) {
			return false;
		}

		if ((isSetCompanyUuid() != that.isSetCompanyUuid()) ||
				(isSetCompanyUuid() && !getCompanyUuid().equals(that.getCompanyUuid()))) {
			return false;
		}

		if ((isSetName() != that.isSetName()) ||
			(isSetName() && !getName().equals(that.getName()))) {
			return false;
		}

		if ((isSetAvatarOriginal() != that.isSetAvatarOriginal()) ||
			(isSetAvatarOriginal() && !getAvatarOriginal().equals(that.getAvatarOriginal()))) {
			return false;
		}

		if ((isSetAvatarLarge() != that.isSetAvatarLarge()) ||
			(isSetAvatarLarge() && !getAvatarLarge().equals(that.getAvatarLarge()))) {
			return false;
		}


		if ((isSetAvatarSmall() != that.isSetAvatarSmall()) ||
			(isSetAvatarSmall() && !getAvatarSmall().equals(that.getAvatarSmall()))) {
			return false;
		}

		if ((isSetDunsNumber() != that.isSetDunsNumber()) ||
			(isSetDunsNumber() && !getDunsNumber().equals(that.getDunsNumber()))) {
			return false;
		}

		if ((isSetYearsInBusiness() != that.isSetYearsInBusiness()) ||
			(isSetYearsInBusiness() && getYearsInBusiness() != that.getYearsInBusiness())) {
			return false;
		}

		if ((isSetNumberOfEmployees() != that.isSetNumberOfEmployees()) ||
			(isSetNumberOfEmployees() && getNumberOfEmployees() != that.getNumberOfEmployees())) {
			return false;
		}

		if ((isSetCreatedOn() != that.isSetCreatedOn()) ||
			(isSetCreatedOn() && getCreatedOn() != that.getCreatedOn())) {
			return false;
		}

		if ((isSetCustomSignatureLine() != isSetCustomSignatureLine()) ||
			(isSetCustomSignatureLine() && !getCustomSignatureLine().equals(that.getCustomSignatureLine()))) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(true).append(id);

		builder.append(isSetCompanyNumber());
		if (isSetCompanyNumber()) {
			builder.append(companyNumber);
		}
		builder.append(isSetCompanyUuid());
		if (isSetCompanyUuid()) {
			builder.append(companyUuid);
		}
		builder.append(isSetName());
		if (isSetName())
			builder.append(name);

		builder.append(isSetAvatarOriginal());
		if (isSetAvatarOriginal())
			builder.append(avatarOriginal);

		builder.append(isSetAvatarLarge());
		if (isSetAvatarLarge())
			builder.append(avatarLarge);

		builder.append(isSetAvatarSmall());
		if (isSetAvatarSmall())
			builder.append(avatarSmall);

		builder.append(isSetDunsNumber());
		if (isSetDunsNumber())
			builder.append(dunsNumber);

		builder.append(isSetYearsInBusiness());
		if (isSetYearsInBusiness())
			builder.append(yearsInBusiness);

		builder.append(isSetNumberOfEmployees());
		if (isSetNumberOfEmployees())
			builder.append(numberOfEmployees);

		builder.append(isSetCreatedOn());
		if (isSetCreatedOn())
			builder.append(createdOn);

		builder.append(isSetCustomSignatureLine());
		if (isSetCustomSignatureLine())
			builder.append(customSignatureLine);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Company(");

		sb.append("id:").append(id);
		sb.append(", companyNumber:");
		if (isSetCompanyNumber()) {
			sb.append(companyNumber);
		} else {
			sb.append("null");
		}
		sb.append(", companyUuid:");
		if (isSetCompanyUuid()) {
			sb.append(companyUuid);
		} else {
			sb.append("null");
		}
		sb.append(", name:");
		if (isSetName()) {
			sb.append(name);
		} else {
			sb.append("null");
		}
		sb.append(", avatarOriginal:");
		if (isSetAvatarOriginal()) {
			sb.append(avatarOriginal);
		} else {
			sb.append("null");
		}
		sb.append(", avatarLarge:");
		if (isSetAvatarLarge()) {
			sb.append(avatarLarge);
		} else {
			sb.append("null");
		}
		sb.append(", avatarSmall:");
		if (isSetAvatarSmall()) {
			sb.append(avatarSmall);
		} else {
			sb.append("null");
		}
		sb.append(", dunsNumber:");
		if (isSetDunsNumber()) {
			sb.append(dunsNumber);
		} else {
			sb.append("null");
		}
		sb.append(", yearsInBusiness:").append(yearsInBusiness);
		sb.append(", numberOfEmployees:").append(numberOfEmployees);
		sb.append(", createdOn:").append(this.createdOn);
		sb.append(", customSignatureLine:");
		if (isSetCustomSignatureLine()) {
			sb.append(customSignatureLine);
		} else {
			sb.append("null");
		}
		sb.append(")");
		return sb.toString();
	}
}

