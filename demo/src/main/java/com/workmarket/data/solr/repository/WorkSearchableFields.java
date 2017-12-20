package com.workmarket.data.solr.repository;

import org.springframework.data.solr.core.query.Field;

public enum WorkSearchableFields implements Field {

	ID("id"),
	UUID("uuid"),
	TITLE("titleSort"),
	PUBLIC_TITLE("publicTitle"),
	DESCRIPTION("description"),
	INSTRUCTIONS("instructions"),
	WORK_NUMBER("workNumber"),
	INDUSTRY_ID("industryId"),
	COMPANY_ID("companyId"),
	COMPANY_NAME("companyName"),
	COMPANY_ID_STRING("companyIdString"),
	BUYER_USER_ID("buyerUserId"),
	BUYER_FULL_NAME("buyerFullName"),
	CREATOR_USER_ID("creatorUserId"),
	WORK_STATUS_TYPE_CODE("workStatusTypeCode"),
	SEARCHABLE_WORK_STATUS_TYPE_CODE("searchableWorkStatusTypeCode"),
	RESOURCE_WORK_STATUS_TYPE_CODE("resourceWorkStatusTypeCode"),
	WORK_STATUS_TYPE_DESCRIPTION("workStatusTypeDescription"),
	BUYER_LABELS_ID("buyerLabelsId"),
	BUYER_LABELS_ID_DESCRIPTION("buyerLabelsIdDescription"),
	BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION("buyerLabelsWorkStatusIdDescription"),
	ALERT("alert"),
	CLIENT_LOCATION_ID("clientLocationId"),
	CLIENT_LOCATION_NAME("clientLocationName"),
	CLIENT_COMPANY_ID("clientCompanyId"),
	CLIENT_COMPANY_NAME("clientCompanyName"),
	CONTACT_NAME("contactName"),
	CONTACT_PHONE("contactPhone"),
	CONTACT_EMAIL("contactEmail"),
	SUPPORT_NAME("supportName"),
	SUPPORT_PHONE("supportPhone"),
	SUPPORT_EMAIL("supportEmail"),
	PROJECT_ID("projectId"),
	PROJECT_NAME("projectName"),
	WORK_RESOURCES_IDS("workResourceIds"),
	WORK_RESOURCES_COMPANY_IDS("workResourceCompanyIds"),
	CANCELLED_WORK_RESOURCE_IDS("cancelledWorkResourceIds"),
	WORK_RESOURCES_NAMES("workResourceNames"),
	ASSIGNED_RESOURCE_ID("assignedResourceId"),
	DISPATCHER_ID("dispatcherId"),
	PARENT_ID("parentId"),
	ASSIGNED_RESOURCE_NAME("assignedResourceName"),
	ASSIGNED_RESOURCE_COMPANY_NAME("assignedResourceCompanyName"),
	ASSIGNED_RESOURCE_COMPANY_ID("assignedResourceCompanyId"),
	SCHEDULE_FROM_DATE("scheduleFromDate"),
	SCHEDULE_FROM_YEAR("scheduleFromYear"),
	SCHEDULE_FROM_MONTH_OF_YEAR("scheduleFromMonthOfYear"),
	SCHEDULE_FROM_DAY_OF_MONTH("scheduleFromDayOfMonth"),
	LAST_MODIFIED_DATE("lastModifiedDate"),
	INDEX_DATE("indexDate"),
	CREATED_DATE("createdDate"),
	SEND_DATE("sendDate"),
	APPROVED_DATE("approvedDate"),
	PAID_DATE("paidDate"),
	SPEND_LIMIT("spendLimit"),
	COMPLETED_DATE("completedDate"),
	ASSIGNED_RESOURCE_MOBILE("assignedResourceMobile"),
	ASSIGNED_RESOURCE_WORK_PHONE_NUMBER("assignedResourceWorkPhoneNumber"),
	ASSIGNED_RESOURCE_WORK_PHONE_EXTENSION("assignedResourceWorkPhoneExtension"),
	FOLLOWER_IDS("followerIds"),
	CITY("city"),
	STATE("state"),
	POSTAL_CODE("postalCode"),
	SHOW_IN_FEED("showInFeed"),
	APPLICANT_IDS("applicantIds"),
	FIRST_TO_ACCEPT("assignToFirstResource"),
	LATITUDE("latitude"),
	LONGITUDE("longitude"),
	LOCATION("location"),
	PRICING_TYPE("pricingType"),
	WORK_PRICE("workPrice"),
	SCHEDULE_THROUGH_DATE("scheduleThroughDate"),
	IS_OFFSITE("offSite"),
	COUNTY_ID("countyId"),
	COUNTY_NAME("countyName"),
	COUNTRY("country"),
	DUE_DATE("dueDate"),
	EXTERNAL_UNIQUE_IDS("externalUniqueIds");

	private final String fieldName;

	private WorkSearchableFields(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getName() {
		return this.fieldName;
	}
}
