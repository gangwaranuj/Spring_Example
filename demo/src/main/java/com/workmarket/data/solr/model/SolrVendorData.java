package com.workmarket.data.solr.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.company.CompanyType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Solr vendor data, containing fields specific to vendor.
 */
public class SolrVendorData implements UuidSolrData {

	private static final SolrUserType USER_TYPE = SolrUserType.VENDOR;

	private long id;
	private String uuid;
	private CompanyType companyType;
	/**
	 * vendorNumber is companyNumber.
	 */
	private String vendorNumber;
	private String name;
	private String effectiveName;
	private String overview;

	private GeoPoint geoPoint;
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String country;

	private String companyStatusType;

	private DateTime createdOn;

	private String avatarSmallAssetUri;

	private List<SolrUserData> employees;

	public static SolrUserType getUserType() {
		return USER_TYPE;
	}

	private List<SolrGroupData> groupMember;
	private List<SolrGroupData> groupMemberOverride;
	private List<SolrGroupData> groupPending;
	private List<SolrGroupData> groupPendingOverride;
	private List<SolrGroupData> groupInvited;
	private List<SolrGroupData> groupDeclined;

	@Override
	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public void setCompanyType(final CompanyType companyType) {
		this.companyType = companyType;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(final String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEffectiveName() {
		return effectiveName;
	}

	public void setEffectiveName(final String effectiveName) {
		this.effectiveName = effectiveName;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(final String overview) {
		this.overview = overview;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(final GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public DateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(final DateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getAvatarSmallAssetUri() {
		return avatarSmallAssetUri;
	}

	public void setAvatarSmallAssetUri(final String avatarSmallAssetUri) {
		this.avatarSmallAssetUri = avatarSmallAssetUri;
	}

	public String getCompanyStatusType() {
		return companyStatusType;
	}

	public void setCompanyStatusType(final String companyStatusType) {
		this.companyStatusType = companyStatusType;
	}

	public List<SolrUserData> getEmployees() {
		return employees;
	}

	public void setEmployees(final List<SolrUserData> employees) {
		this.employees = employees;
	}

	public List<SolrGroupData> getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(List<SolrGroupData> groupMember) {
		this.groupMember = groupMember;
	}

	public List<SolrGroupData> getGroupMemberOverride() {
		return groupMemberOverride;
	}

	public void setGroupMemberOverride(List<SolrGroupData> groupMemberOverride) {
		this.groupMemberOverride = groupMemberOverride;
	}

	public List<SolrGroupData> getGroupPending() {
		return groupPending;
	}

	public void setGroupPending(List<SolrGroupData> groupPending) {
		this.groupPending = groupPending;
	}

	public List<SolrGroupData> getGroupPendingOverride() {
		return groupPendingOverride;
	}

	public void setGroupPendingOverride(List<SolrGroupData> groupPendingOverride) {
		this.groupPendingOverride = groupPendingOverride;
	}

	public List<SolrGroupData> getGroupInvited() {
		return groupInvited;
	}

	public void setGroupInvited(List<SolrGroupData> groupInvited) {
		this.groupInvited = groupInvited;
	}

	public List<SolrGroupData> getGroupDeclined() {
		return groupDeclined;
	}

	public void setGroupDeclined(List<SolrGroupData> groupDeclined) {
		this.groupDeclined = groupDeclined;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SolrVendorData that = (SolrVendorData) o;

		if (id != that.getId()) return false;
		if (!uuid.equals(that.getUuid())) return false;
		if (name != null ? !name.equals(that.getName()) : that.getName() != null) return false;
		if (vendorNumber != null ? !vendorNumber.equals(that.getVendorNumber()) : that.getVendorNumber() != null)
			return false;
		if (effectiveName != null ? !effectiveName.equals(that.getEffectiveName()) : that.getEffectiveName() != null)
			return false;
		if (overview != null ? !overview.equals(that.getOverview()) : that.getOverview() != null) return false;
		if (companyType != null ? !companyType.equals(that.getCompanyType()) : that.getCompanyType() != null)
			return false;
		if (geoPoint != null ? !geoPoint.equals(that.getGeoPoint()) : that.getGeoPoint() != null) return false;
		if (address != null ? !address.equals(that.getAddress()) : that.getAddress() != null) return false;
		if (city != null ? !city.equals(that.getCity()) : that.getCity() != null) return false;
		if (state != null ? !state.equals(that.getState()) : that.getState() != null) return false;
		if (postalCode != null ? !postalCode.equals(that.getPostalCode()) : that.getPostalCode() != null) return false;
		if (country != null ? !country.equals(that.getCountry()) : that.getCountry() != null) return false;
		if (createdOn != null ? !createdOn.equals(that.getCreatedOn()) : that.getCreatedOn() != null) return false;
		if (avatarSmallAssetUri != null ?
			!avatarSmallAssetUri.equals(that.getAvatarSmallAssetUri()) : that.getAvatarSmallAssetUri() != null) {
			return false;
		}
		if (companyStatusType != null ? !companyStatusType.equals(that.companyStatusType) : that.getCompanyStatusType() != null) {
			return false;
		}
		if (employees != null ? !employees.equals(that.getEmployees()) : that.getEmployees() != null) return false;
		if (groupMember != null ? !groupMember.equals(that.getGroupMember()) : that.getGroupMember() != null)
			return false;
		if (groupMemberOverride != null ? !groupMemberOverride.equals(that.getGroupMemberOverride()) : that.getGroupMemberOverride() != null)
			return false;
		if (groupPending != null ? !groupPending.equals(that.getGroupPending()) : that.getGroupPending() != null)
			return false;
		if (groupPendingOverride != null ? !groupPendingOverride.equals(that.getGroupPendingOverride()) : that.getGroupPendingOverride() != null)
			return false;
		if (groupInvited != null ? !groupInvited.equals(that.getGroupInvited()) : that.getGroupInvited() != null)
			return false;
		if (groupDeclined != null ? !groupDeclined.equals(that.getGroupDeclined()) : that.getGroupDeclined() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (int) (id ^ (id >>> 32));
		result = 31 * result + uuid.hashCode();
		result = 31 * result + (vendorNumber != null ? vendorNumber.hashCode() : 0);
		result = 31 * result + (effectiveName != null ? effectiveName.hashCode() : 0);
		result = 31 * result + (overview != null ? overview.hashCode() : 0);
		result = 31 * result + (companyType != null ? companyType.hashCode() : 0);
		result = 31 * result + (employees != null ? employees.hashCode() : 0);
		result = 31 * result + (geoPoint != null ? geoPoint.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
		result = 31 + result + (companyStatusType != null ? companyStatusType.hashCode() : 0);
		result = 31 * result + (avatarSmallAssetUri != null ? avatarSmallAssetUri.hashCode() : 0);
		result = 31 * result + (groupMember != null ? groupMember.hashCode() : 0);
		result = 31 * result + (groupMemberOverride != null ? groupMemberOverride.hashCode() : 0);
		result = 31 * result + (groupPending != null ? groupPending.hashCode() : 0);
		result = 31 * result + (groupPendingOverride != null ? groupPendingOverride.hashCode() : 0);
		result = 31 * result + (groupInvited != null ? groupInvited.hashCode() : 0);
		result = 31 * result + (groupDeclined != null ? groupDeclined.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {

		final String employeeList = employees == null || employees.size() == 0 ?
			"[]" : "[" + StringUtils.join(Lists.transform(employees, new Function<SolrUserData, String>() {
			public String apply(SolrUserData employee) { return employee.toString(); }
		}), ",") + "]";

		return "SolrCompanyCoreData{" +
			"name='" + name + "'" +
			", id=" + id +
			", uuid='" + uuid + "'" +
			", companyNumber='" + vendorNumber + "'" +
			", effectiveName='" + effectiveName + "'" +
			", overview='" + overview + "'" +
			", companyType='" + companyType + "'" +
			", geoPoint=" + geoPoint +
			", address='" + address + "'" +
			", city='" + city + "'" +
			", state='" + state + "'" +
			", postalCode='" + postalCode + "'" +
			", country='" + country + "'" +
			", createdOn=" + createdOn +
			", companyStatusType=" + companyStatusType +
			", avatarSmallAssetUri='" + avatarSmallAssetUri + "'" +
			", employees=" + employeeList +
			'}';
	}
}
