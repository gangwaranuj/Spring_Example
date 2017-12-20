package com.workmarket.domains.model.realtime;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.services.realtime.ResourceIconType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class RealtimeResourceMapper implements RowMapper<IRealtimeResource> {

	@Override
	public IRealtimeResource mapRow(ResultSet rs, int rowNum) throws SQLException {
		RealtimeResourceDecorator realtimeResource = new RealtimeResourceDecorator();
		extractLocationDataToResource(rs, realtimeResource);
		String firstName = rs.getString(ResourceDataFields.FIRST_NAME.getColumn());
		String lastName = rs.getString(ResourceDataFields.LAST_NAME.getColumn());
		String nameStr = extractName(firstName, lastName);
		realtimeResource.setName(nameStr);
		if (isNotEmpty(firstName)) {
			realtimeResource.setFirstName(firstName);
		}
		if (isNotEmpty(lastName)) {
			realtimeResource.setLastName(lastName);
		}
		extractIconsToResource(rs, realtimeResource);
		realtimeResource.setWorkId(rs.getLong(ResourceDataFields.WORK_ID.getColumn()));
		realtimeResource.setUserNumber(rs.getString(ResourceDataFields.USER_NUMBER.getColumn()));
		realtimeResource.setDateSentOn(rs.getTimestamp("date_sent_on").getTime());
		String workPhoneNumber = rs.getString("work_phone");
		if (workPhoneNumber != null) {
			realtimeResource.setWorkPhoneNumber(workPhoneNumber);
		}
		String mobilePhoneNumber = rs.getString("mobile_phone");
		if (mobilePhoneNumber != null) {
			realtimeResource.setMobilePhoneNumber(mobilePhoneNumber);
		}
		realtimeResource.setEmail(rs.getString("email"));
		Boolean isEmployee = rs.getBoolean("is_employee");
		if (isEmployee) {
			realtimeResource.addToIcons(ResourceIconType.IS_EMPLOYEE);
		}
		String companyName = rs.getString("company_name");
		if (isNotEmpty(companyName)) {
			realtimeResource.setCompanyName(companyName);
		}
		Integer laneTypeId = rs.getInt("laneType");
		realtimeResource.setLaneType(LaneType.findByValue(laneTypeId));

		Date viewedOn = rs.getDate("viewed_on");
		if (viewedOn != null) {
			String viewType = rs.getString("view_type");
			if (viewType == null) {
				//TODO: THIS SHOULD NEVER HAPPEN.  However, we're going to assume WEB for now
				realtimeResource.addToIcons(ResourceIconType.VIEWED_ON_WEB);
			} else if (viewType.toLowerCase().contains("web")) {
				realtimeResource.addToIcons(ResourceIconType.VIEWED_ON_WEB);
			} else {
				realtimeResource.addToIcons(ResourceIconType.VIEWED_ON_MOBILE);
			}
		}
		Address address = parseAddress(rs);
		if (address != null) {
			realtimeResource.setAddress(address);
		}
		realtimeResource.setResourceId(rs.getLong("resource_id"));
		Long ratingCount = rs.getLong("ratingCount");
		if (ratingCount != null && ratingCount > 0) {
			Long ratingSum = rs.getLong("ratingSum");
			Double ratingAverage = Double.longBitsToDouble(ratingSum) / Double.longBitsToDouble(ratingCount);
			realtimeResource.setAverageStarRating(ratingAverage);
			realtimeResource.setNumberOfRatings(ratingCount.intValue());
		}

		return realtimeResource;
	}

	private Address parseAddress(ResultSet rs) throws SQLException {
		String addressLine1 = rs.getString("address1");
		if (addressLine1 == null) {
			return null;
		}
		Address address = new Address();
		address.setAddressLine1(addressLine1);
		String addressLine2 = rs.getString("address2");
		if (addressLine2 != null) {
			address.setAddressLine2(addressLine2);
		}
		String city = rs.getString("city");
		if (city != null) {
			address.setCity(city);
		}
		String state = rs.getString("state");
		if (state != null) {
			address.setState(state);
		}
		String zip = rs.getString("zip");
		if (zip != null) {
			address.setZip(zip);
		}
		String country = rs.getString("country");
		if (country != null) {
			address.setCountry(country);
		}
		return address;
	}

	private void extractLocationDataToResource(ResultSet rs, IRealtimeResource realtimeResource) throws SQLException {
		Double latitude = rs.getDouble(ResourceDataFields.LATITUDE.getColumn());
		if (!rs.wasNull() && latitude != null) {
			realtimeResource.setLatitude(latitude);
		}
		Double longitude = rs.getDouble(ResourceDataFields.LONGITUDE.getColumn());
		if (!rs.wasNull() && longitude != null) {
			realtimeResource.setLongitude(longitude);
		}
	}

	private void extractIconsToResource(ResultSet rs, RealtimeResourceDecorator realtimeResource) throws SQLException {
		String workResourceStatusTypeCode = rs.getString("wr.work_resource_status_type_code");

		if (workResourceStatusTypeCode.equals("declined")) {
			realtimeResource.addToIcons(ResourceIconType.OFFER_DECLINED);
			return;
		}

		Boolean isQuestion = rs.getBoolean(ResourceDataFields.IS_QUESTION_ASKED.getColumn());
		if (isQuestion) {
			realtimeResource.addToIcons(ResourceIconType.QUESTION);
		}

		Integer latestNegotiationApprovalStatus = rs.getInt(ResourceDataFields.LATEST_NEGOTIATION_APPROVAL_STATUS.getColumn());
		if (!rs.wasNull()) {
			ApprovalStatus status = ApprovalStatus.lookupByCode(latestNegotiationApprovalStatus);

			Boolean isLatestOfferExpired = rs.getBoolean(ResourceDataFields.IS_EXPIRED.getColumn());

			if (!isLatestOfferExpired && (status != null && status.isPending())) {
				realtimeResource.addToIcons(ResourceIconType.OFFER_OPEN);
			}
		}
	}

	private String extractName(String firstName, String lastName) throws SQLException {

		final StringBuilder name = new StringBuilder(20);
		if (!isEmpty(firstName)) {
			name.append(firstName).append(' ');
		}
		if (!isEmpty(lastName)) {
			name.append(lastName);
		}
		String nameStr = name.toString();
		return nameStr.trim();
	}

	public enum ResourceDataFields {
		FIRST_NAME("u.first_name"),
		LAST_NAME("u.last_name"),
		USER_NUMBER("u.user_number"),
		LATITUDE("latitude"),
		LONGITUDE("longitude"),
		IS_QUESTION_ASKED("is_question_asked"),
		IS_EXPIRED("is_expired"),
		LATEST_NEGOTIATION_APPROVAL_STATUS("latest_negotiation_approval_status"),
		IS_DECLINED("is_declined"),
		WORK_ID("wr.work_id");
		private final String column;

		private ResourceDataFields(String column) {
			this.column = column;
		}

		public String getColumn() {
			return column;
		}

	}

}