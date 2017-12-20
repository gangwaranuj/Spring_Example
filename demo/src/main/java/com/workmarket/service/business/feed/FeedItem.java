package com.workmarket.service.business.feed;

import com.workmarket.utility.StringUtilities;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

public class FeedItem {
	@Field private Long id;
	@Field private String workNumber;
	@Field private String publicTitle;
	@Field private String description;
	@Field private String city;
	@Field private String state;
	@Field private String postalCode;
	@Field private boolean offSite;
	@Field private Date scheduleFromDate;
	@Field private Date createdDate;
	@Field private double spendLimit;
	@Field private String companyName;
	@Field private String workStatusTypeCode;
	@Field private double latitude;
	@Field private double longitude;
	@Field private boolean assignToFirstResource;
	@Field private boolean showInFeed;
	@Field private String pricingType;
	@Field private double workPrice;
	@Field private String country;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getPublicTitle() {
		return publicTitle;
	}

	public void setPublicTitle(String publicTitle) {
		this.publicTitle = publicTitle;
	}

	public String getDescription() {
		return stripDescription(description);
	}

	public void setDescription(String description) {
		this.description = stripDescription(description);
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

	public Date getScheduleFromDate() {
		return scheduleFromDate;
	}

	public void setScheduleFromDate(Date scheduleFromDate) {
		this.scheduleFromDate = scheduleFromDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public double getSpendLimit() {
		return spendLimit;
	}

	public void setSpendLimit(double spendLimit) {
		this.spendLimit = spendLimit;
	}

	private String stripDescription(String description) {
		return StringUtilities.stripHTML(description);
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public boolean isOffSite() {
		return offSite;
	}

	public void setOffSite(boolean offSite) {
		this.offSite = offSite;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWorkStatusTypeCode() {
		return workStatusTypeCode;
	}

	public void setWorkStatusTypeCode(String workStatusTypeCode) {
		this.workStatusTypeCode = workStatusTypeCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isAssignToFirstResource() {
		return assignToFirstResource;
	}

	public void setAssignToFirstResource(boolean assignToFirstResource) {
		this.assignToFirstResource = assignToFirstResource;
	}

	public boolean isShowInFeed() {
		return showInFeed;
	}

	public void setShowInFeed(boolean showInFeed) {
		this.showInFeed = showInFeed;
	}

	public String getPricingType() {
		return pricingType;
	}

	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}

	public double getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(double workPrice) {
		this.workPrice = workPrice;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
