package com.workmarket.data.report.work;

import com.workmarket.configuration.Constants;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.StringUtilities;
import java.math.BigDecimal;

import java.util.Calendar;

public class StandardDecoratedWorkReportRow extends DecoratedWorkReportRow {

	private String workNumber;
	private String title;
	private String timeZoneId;

	private Long companyId;

	private String buyerFullName;
	private Long buyerId;

	private Boolean offSite;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private Double latitude;
	private Double longitude;

	private Long locationId;
	private String locationName;
	private String locationNumber;

	private Calendar scheduleFrom;
	private Calendar scheduleThrough;

	private boolean paymentTermsEnabled;
	private Integer paymentTermsDays;
	private Double buyerTotalCost;
	private Double spendLimit = 0D;
	private Double spendLimitWithFee = 0D;
	private String pricingType;
	private Calendar createdOn;
	private Calendar sentOn;
	private Calendar paidOn;
	private Calendar dueOn;

	private Long invoiceId;
	private String invoiceNumber;

	private String assignedResourceFirstName;
	private String assignedResourceLastName;
	private String assignedResourceCompanyName;

	private Long clientCompanyId;
	private String clientCompanyName;

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getBuyerFullName() {
		return buyerFullName;
	}

	public void setBuyerFullName(String buyerFullName) {
		this.buyerFullName = buyerFullName;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Boolean isOffSite() {
		return offSite;
	}

	public void setOffSite(Boolean offSite) {
		this.offSite = offSite;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationNumber() {
		return locationNumber;
	}

	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	public Calendar getScheduleFrom() {
		return scheduleFrom;
	}

	public void setScheduleFrom(Calendar scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
	}

	public Calendar getScheduleThrough() {
		return scheduleThrough;
	}

	public void setScheduleThrough(Calendar scheduleThrough) {
		this.scheduleThrough = scheduleThrough;
	}

	public boolean isPaymentTermsEnabled() {
		return paymentTermsEnabled;
	}

	public void setPaymentTermsEnabled(boolean paymentTermsEnabled) {
		this.paymentTermsEnabled = paymentTermsEnabled;
	}

	public Double getBuyerTotalCost() {
		return buyerTotalCost;
	}

	public void setBuyerTotalCost(Double buyerTotalCost) {
		this.buyerTotalCost = buyerTotalCost;
	}

	public Double getSpendLimit() {
		return spendLimit;
	}

	public void setSpendLimit(Double spendLimit) {
		this.spendLimit = spendLimit;
	}

	public Double getSpendLimitWithFee() {
		return spendLimitWithFee;
	}

	public void setSpendLimitWithFee(Double spendLimitWithFee) {
		this.spendLimitWithFee = spendLimitWithFee;
	}

	public String getPricingType() {
		return pricingType;
	}

	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}

	public Calendar getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Calendar createdOn) {
		this.createdOn = createdOn;
	}

	public Calendar getSentOn() {
		return sentOn;
	}

	public void setSentOn(Calendar sentOn) {
		this.sentOn = sentOn;
	}

	public Calendar getPaidOn() {
		return paidOn;
	}

	public void setPaidOn(Calendar paidOn) {
		this.paidOn = paidOn;
	}

	public Calendar getDueOn() {
		return dueOn;
	}

	public void setDueOn(Calendar dueOn) {
		this.dueOn = dueOn;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Integer getPaymentTermsDays() {
		if (isPaymentTermsEnabled()) {
			return paymentTermsDays;
		}

		return 0;
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.paymentTermsDays = paymentTermsDays;
	}

	public String getAssignedResourceFirstName() {
		return assignedResourceFirstName;
	}

	public void setAssignedResourceFirstName(String assignedResourceFirstName) {
		this.assignedResourceFirstName = assignedResourceFirstName;
	}

	public String getAssignedResourceLastName() {
		return assignedResourceLastName;
	}

	public void setAssignedResourceLastName(String assignedResourceLastName) {
		this.assignedResourceLastName = assignedResourceLastName;
	}

	public String getAssignedResourceFullName() {
		return StringUtilities.fullName(getAssignedResourceFirstName(), getAssignedResourceLastName());
	}

	public String getAssignedResourceCompanyName() {
		return assignedResourceCompanyName;
	}

	public void setAssignedResourceCompanyName(String assignedResourceCompanyName) {
		this.assignedResourceCompanyName = assignedResourceCompanyName;
	}

	public Long getClientCompanyId() {
		return clientCompanyId;
	}

	public void setClientCompanyId(Long clientCompanyId) {
		this.clientCompanyId = clientCompanyId;
	}

	public String getClientCompanyName() {
		return clientCompanyName;
	}

	public void setClientCompanyName(String clientCompanyName) {
		this.clientCompanyName = clientCompanyName;
	}

	public String getLocation() {
		return ((this.city == null) ? "" : this.city) +
			((this.state == null) ? "" : ", " + this.state) +
			((this.postalCode == null) ? "" : " " + this.postalCode) +
			((this.country == null) ? "" : " " + this.country);
	}

	public static double calculateSpendLimitWithFee(BigDecimal spendLimit, BigDecimal workFeePercentage) {
		if (NumberUtilities.isPositive(spendLimit) && NumberUtilities.isPositive(workFeePercentage)) {
			BigDecimal fee = workFeePercentage.movePointLeft(2).multiply(spendLimit);
			if (fee.compareTo(Constants.MAX_WORK_FEE) > 0) {
				fee = Constants.MAX_WORK_FEE;
			}
			return spendLimit.add(fee).doubleValue();
		}
		if (spendLimit == null) {
			return 0d;
		}
		return spendLimit.doubleValue();
	}
}
