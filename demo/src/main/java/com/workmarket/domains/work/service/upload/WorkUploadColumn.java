package com.workmarket.domains.work.service.upload;

import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.utility.SerializationUtilities;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public enum WorkUploadColumn {

	ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE("additional_per_hour_price_client_fee", WorkUploadColumnType.FLOAT, "Additional Per Hour Price (including fees)", "pricing_blended_per_hour", 4, true),
	ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE("additional_per_hour_price_resource_fee", WorkUploadColumnType.FLOAT, "Additional Per Hour Price (excluding fees)", "pricing_blended_per_hour", 3, true),
	CLIENT_NAME("client_name", WorkUploadColumnType.STRING, "Client Name", "general", 11, true),
	CONTACT_EMAIL("contact_email", WorkUploadColumnType.STRING, "Email", "location_contact", 2, true),
	CONTACT_FIRST_NAME("contact_first_name", WorkUploadColumnType.STRING, "First Name", "location_contact", 0, true),
	CONTACT_LAST_NAME("contact_last_name", WorkUploadColumnType.STRING, "Last Name", "location_contact", 1, true),
	CONTACT_PHONE("contact_phone", WorkUploadColumnType.STRING, "Phone", "location_contact", 3, true),
	CONTACT_PHONE_EXTENSION("contact_phone_extension", WorkUploadColumnType.STRING, "Phone Extension", "location_contact", 4, true),
	CUSTOM_FIELD("custom_field", WorkUploadColumnType.STRING, "Custom Field", "custom_field", 0, false),
	DESCRIPTION("description", WorkUploadColumnType.STRING, "Description", "general", 1, true),
	DESIRED_SKILLS("desired_skills", WorkUploadColumnType.STRING, "Desired Skills", "general", 3, true),
	DISTRIBUTION_METHOD("distribution_method", WorkUploadColumnType.STRING, "Distribution Method", "parts", 1, true),
	END_DATE("end_date", WorkUploadColumnType.STRING, "End Date", "scheduling", 4, true),
	END_DATE_TIME("end_date_time", WorkUploadColumnType.DATETIME, "End Date & Time", "scheduling", 3, true),
	END_TIME("end_time", WorkUploadColumnType.DATETIME, "End Time", "scheduling", 5, true),
	FLAT_PRICE_CLIENT_FEE("flat_price_client_fee", WorkUploadColumnType.FLOAT, "Flat Price (including fees)", "pricing_flat_price", 1, true),
	FLAT_PRICE_RESOURCE_FEE("flat_price_resource_fee", WorkUploadColumnType.FLOAT, "Flat Price (excluding fees)", "pricing_flat_price", 0, true),
	IGNORE("ignore", WorkUploadColumnType.BOOLEAN, "Ignore This Column", "ignore", 0, false),
	INDUSTRY_ID("industry_id", WorkUploadColumnType.DOUBLE, "Industry ID", "general", 4, true),
	INDUSTRY_NAME("industry_name", WorkUploadColumnType.STRING, "Industry Name", "general", 5, true),
	INITIAL_PER_HOUR_PRICE_CLIENT_FEE("initial_per_hour_price_client_fee", WorkUploadColumnType.FLOAT, "Initial Per Hour Price (including fees)", "pricing_blended_per_hour", 1, true),
	INITIAL_PER_HOUR_PRICE_RESOURCE_FEE("initial_per_hour_price_resource_fee", WorkUploadColumnType.FLOAT, "Initial Per Hour Price (excluding fees)", "pricing_blended_per_hour", 0, true),
	INSTRUCTIONS("instructions", WorkUploadColumnType.STRING, "Instructions", "general", 2, true),
	LOCATION_ADDRESS_1("location_address_1", WorkUploadColumnType.STRING, "Location Address 1", "location", 3, true),
	LOCATION_ADDRESS_2("location_address_2", WorkUploadColumnType.STRING, "Location Address 2", "location", 4, true),
	LOCATION_CITY("location_city", WorkUploadColumnType.STRING, "Location City", "location", 5, true),
	LOCATION_COUNTRY("location_country", WorkUploadColumnType.STRING, "Location Country", "location", 8, true),
	@Deprecated
	LOCATION_DRESS_CODE("location_dress_code", WorkUploadColumnType.INTEGER, "Location Dress Code", "location", 9, true),
	LOCATION_NAME("location_name", WorkUploadColumnType.STRING, "Location Name", "location", 2, true),
	LOCATION_NUMBER("location_number", WorkUploadColumnType.INTEGER, "Location Number", "location", 1, true),
	LOCATION_OFFSITE("location_offsite", WorkUploadColumnType.BOOLEAN, "Offsite", "location", 0, true),
	LOCATION_STATE("location_state", WorkUploadColumnType.STRING, "Location State/Province", "location", 6, true),
	LOCATION_TYPE("location_type", WorkUploadColumnType.STRING, "Location Type", "location", 10, true),
	LOCATION_INSTRUCTIONS("location_instructions", WorkUploadColumnType.STRING, "Location Travel Instructions", "location", 11, true),
	LOCATION_POSTAL_CODE("location_postal_code", WorkUploadColumnType.STRING, "Location Postal Code", "location", 7, true),
	MAX_NUMBER_OF_HOURS("max_number_of_hours", WorkUploadColumnType.FLOAT, "Max Number of Hours", "pricing_per_hour", 2, true),
	MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE("max_number_of_hours_at_additional_price", WorkUploadColumnType.FLOAT, "Max Number of Hours at Additional Price", "pricing_blended_per_hour", 5, true),
	MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE("max_number_of_hours_at_initial_price", WorkUploadColumnType.FLOAT, "Max Number of Hours at Initial Price", "pricing_blended_per_hour", 2, true),
	MAX_NUMBER_OF_UNITS("max_number_of_units", WorkUploadColumnType.FLOAT, "Max Number of Units", "pricing_per_unit", 2, true),
	OWNER_EMAIL("owner_email", WorkUploadColumnType.STRING, "Owner Email", "general", 7, true),
	OWNER_USER_NUMBER("owner_user_number", WorkUploadColumnType.STRING, "Owner ID", "general", 6, true),
	PER_HOUR_PRICE_CLIENT_FEE("per_hour_price_client_fee", WorkUploadColumnType.POSITIVE_PRICE, "Per Hour Price (including fees)", "pricing_per_hour", 1, true),
	PER_HOUR_PRICE_RESOURCE_FEE("per_hour_price_resource_fee", WorkUploadColumnType.POSITIVE_PRICE, "Per Hour Price (excluding fees)", "pricing_per_hour", 0, true),
	PER_UNIT_PRICE_CLIENT_FEE("per_unit_price_client_fee", WorkUploadColumnType.POSITIVE_PRICE, "Per Unit Price (including fees)", "pricing_per_unit", 1, true),
	PER_UNIT_PRICE_RESOURCE_FEE("per_unit_price_resource_fee", WorkUploadColumnType.POSITIVE_PRICE, "Per Unit Price (excluding fees)", "pricing_per_unit", 0, true),
	PICKUP_LOCATION_ADDRESS_1("pickup_location_address_1", WorkUploadColumnType.STRING, "Pickup Location Address 1", "parts_pickup", 2, true),
	PICKUP_LOCATION_ADDRESS_2("pickup_location_address_2", WorkUploadColumnType.STRING, "Pickup Location Address 2", "parts_pickup", 3, true),
	PICKUP_LOCATION_CITY("pickup_location_city", WorkUploadColumnType.STRING, "Pickup Location City", "parts_pickup", 4, true),
	PICKUP_LOCATION_COUNTRY("pickup_location_country", WorkUploadColumnType.STRING, "Pickup Location Country", "parts_pickup", 7, true),
	PICKUP_LOCATION_NAME("pickup_location_name", WorkUploadColumnType.STRING, "Pickup Location Name", "parts_pickup", 1, true),
	PICKUP_LOCATION_NUMBER("pickup_location_number", WorkUploadColumnType.STRING, "Pickup Location Number", "parts_pickup", 0, true),
	PICKUP_LOCATION_STATE("pickup_location_state", WorkUploadColumnType.STRING, "Pickup Location State", "parts_pickup", 5, true),
	PICKUP_LOCATION_TYPE("pickup_location_type", WorkUploadColumnType.STRING, "Pickup Location Type", "parts_pickup", 8, true),
	PICKUP_LOCATION_POSTAL_CODE("pickup_location_postal_code", WorkUploadColumnType.STRING, "Pickup Location Postal Code", "parts_pickup", 6, true),
	PICKUP_PART_VALUE("pickup_part_value", WorkUploadColumnType.STRING, "Pickup Part Value", "parts_pickup", 11, true),
	@Deprecated
	PICKUP_SHIPPING_PROVIDER("pickup_shipping_provider", WorkUploadColumnType.STRING, "Pickup Shipping Provider", "parts_pickup", 10, true),
	PICKUP_TRACKING_NUMBER("pickup_tracking_number", WorkUploadColumnType.STRING, "Pickup Tracking Number", "parts_pickup", 9, true),
	PROJECT_NAME("project_name", WorkUploadColumnType.STRING, "Project Name", "general", 10, true),
	RETURN_LOCATION_ADDRESS_1("return_location_address_1", WorkUploadColumnType.STRING, "Return Location Address 1", "parts_return", 3, true),
	RETURN_LOCATION_ADDRESS_2("return_location_address_2", WorkUploadColumnType.STRING, "Return Location Address 2", "parts_return", 4, true),
	RETURN_LOCATION_CITY("return_location_city", WorkUploadColumnType.STRING, "Return Location City", "parts_return", 5, true),
	RETURN_LOCATION_COUNTRY("return_location_country", WorkUploadColumnType.STRING, "Return Location Country", "parts_return", 8, true),
	RETURN_LOCATION_NAME("return_location_name", WorkUploadColumnType.STRING, "Return Location Name", "parts_return", 2, true),
	RETURN_LOCATION_NUMBER("return_location_number", WorkUploadColumnType.STRING, "Return Location Number", "parts_return", 1, true),
	RETURN_LOCATION_STATE("return_location_state", WorkUploadColumnType.STRING, "Return Location State", "parts_return", 6, true),
	RETURN_LOCATION_TYPE("return_location_type", WorkUploadColumnType.STRING, "Return Location Type", "parts_return", 9, true),
	RETURN_LOCATION_POSTAL_CODE("return_location_postal_code", WorkUploadColumnType.STRING, "Return Location Postal Code", "parts_return", 7, true),
	RETURN_PART_VALUE("return_part_value", WorkUploadColumnType.STRING, "Return Part Value", "parts_return", 12, true),
	RETURN_REQUIRED("return_required", WorkUploadColumnType.BOOLEAN, "Return Required", "parts_return", 0, true),
	@Deprecated
	RETURN_SHIPPING_PROVIDER("return_shipping_provider", WorkUploadColumnType.STRING, "Return Shipping Provider", "parts_return", 11, true),
	RETURN_TRACKING_NUMBER("return_tracking_number", WorkUploadColumnType.STRING, "Return Tracking Number", "parts_return", 10, true),
	SECONDARY_CONTACT_EMAIL("secondary_contact_email", WorkUploadColumnType.STRING, "Email", "location_contact_secondary", 2, true),
	SECONDARY_CONTACT_FIRST_NAME("secondary_contact_first_name", WorkUploadColumnType.STRING, "First Name", "location_contact_secondary", 0, true),
	SECONDARY_CONTACT_LAST_NAME("secondary_contact_last_name", WorkUploadColumnType.STRING, "Last Name", "location_contact_secondary", 1, true),
	SECONDARY_CONTACT_PHONE("secondary_contact_phone", WorkUploadColumnType.STRING, "Phone", "location_contact_secondary", 3, true),
	SECONDARY_CONTACT_PHONE_EXTENSION("secondary_contact_phone_extension", WorkUploadColumnType.STRING, "Phone Extension", "location_contact_secondary", 4, true),
	START_DATE("start_date", WorkUploadColumnType.STRING, "Start Date", "scheduling", 1, true),
	START_DATE_TIME("start_date_time", WorkUploadColumnType.DATETIME, "Start Date & Time", "scheduling", 0, true),
	START_TIME("start_time", WorkUploadColumnType.DATETIME, "Start Time", "scheduling", 2, true),
	SUPPLIED_BY_RESOURCE("supplied_by_resource", WorkUploadColumnType.STRING, "Supplied By Resource", "parts", 0, true),
	SUPPORT_CONTACT_EMAIL("support_contact_email", WorkUploadColumnType.STRING, "Support Contact Email", "general", 9, true),
	SUPPORT_CONTACT_USER_NUMBER("support_contact_user_number", WorkUploadColumnType.STRING, "Support Contact ID", "general", 8, true),
	TEMPLATE_ID("template_id", WorkUploadColumnType.INTEGER, "Template ID", "general", 12, true), // Does not exist in DB - used for extracting custom field info for template
	TEMPLATE_NUMBER("template_number", WorkUploadColumnType.STRING, "Template ID", "general", 12, true),
	USER_NUMBER("user_number", WorkUploadColumnType.STRING, "Worker IDs", "general", 13, true),
	TITLE("title", WorkUploadColumnType.STRING, "Title", "general", 0, true),
	NEW_BUNDLE_NAME("new_bundle_name", WorkUploadColumnType.STRING, "New Bundle Name", "bundles", 0, true),
	NEW_BUNDLE_DESCRIPTION("new_bundle_description", WorkUploadColumnType.STRING, "New Bundle Description", "bundles", 1, true),
	EXISTING_BUNDLE_ID("existing_bundle_id", WorkUploadColumnType.INTEGER, "Existing Bundle ID", "bundles", 2, true),
	UNIQUE_EXTERNAL_ID("unique_external_id", WorkUploadColumnType.STRING, "Unique External Id", "general", 14, true);

	private static final Log logger = LogFactory.getLog(WorkUploadColumn.class);
	private final String uploadColumnName;
	private final WorkUploadColumnType columnType;
	private final String uploadColumnDescription;
	private final String uploadColumnCategory;
	private final int order;
	private final boolean visible;
	private final FieldType fieldType;

	private WorkUploadColumn(String columnName, WorkUploadColumnType columnType,
							 String uploadColumnDescription, String uploadColumnCategory, int order, boolean visible) {
		this.uploadColumnName = columnName;
		this.columnType = columnType;
		this.uploadColumnDescription = uploadColumnDescription;
		this.uploadColumnCategory = uploadColumnCategory;
		this.order = order;
		this.visible = visible;
		FieldType fieldType = new FieldType();
		fieldType.setCode(uploadColumnName);
		fieldType.setDescription(uploadColumnDescription);
		fieldType.setOrder(order);
		this.fieldType = fieldType;
	}

	public static WorkUploadColumn findUploadColumn(String column) {
		for (WorkUploadColumn currentColumn : WorkUploadColumn.values()) {
			if (currentColumn.getUploadColumnName().endsWith(column)) {
				return currentColumn;
			}
		}
		return null;
	}

	public static Integer parseInt(Map<String, String> typesToValues, WorkUploadColumn uploadColumn) {
		String uploadColumnName = uploadColumn.getUploadColumnName();
		String value = typesToValues.get(uploadColumnName);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Integer.parseInt(value);
	}

	public static Float parseFloat(Map<String, String> typesToValues, WorkUploadColumn uploadColumn) {
		String uploadColumnName = uploadColumn.getUploadColumnName();
		String value = typesToValues.get(uploadColumnName);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException nfe) {
			logger.error("Bad number format " + value, nfe);
			throw nfe;
		}
	}

	public static Float parsePrice(Map<String, String> types, WorkUploadColumn priceColumn) {
		String uploadColumnName = priceColumn.getUploadColumnName();
		String value = types.get(uploadColumnName);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		value = StringUtils.removeStart(value.trim(), "$");
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException nfe) {
			logger.error("Bad number format " + value, nfe);
			throw nfe;
		}
	}

	public static BigDecimal parseBigDecimal(Map<String, String> types, WorkUploadColumn priceColumn) {
		String uploadColumnName = priceColumn.getUploadColumnName();
		String value = types.get(uploadColumnName);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return new BigDecimal(StringUtils.removeStart(value.trim(), "$"));
	}

	public static Boolean parseBoolean(Map<String, String> types, WorkUploadColumn column) {
		String value = types.get(column.getUploadColumnName());
		if (NumberUtils.isDigits(value)) {
			return BooleanUtils.toBoolean(NumberUtils.toInt(value));
		}
		return BooleanUtils.toBoolean(value);
	}

	public static String get(Map<String, String> types, WorkUploadColumn column) {
		return types.get(column.getUploadColumnName());
	}

	public static String get(Map<String, String> types, WorkUploadColumn column, String defaultTo) {
		if (StringUtils.isEmpty(types.get(column.getUploadColumnName())))
			return defaultTo;
		return types.get(column.getUploadColumnName());
	}

	public static Boolean isEmpty(Map<String, String> types, WorkUploadColumn column) {
		return StringUtils.isEmpty(WorkUploadColumn.get(types, column));
	}

	public static Boolean isNotEmpty(Map<String, String> types, WorkUploadColumn column) {
		return StringUtils.isNotEmpty(WorkUploadColumn.get(types, column));
	}

	public static Boolean containsAny(Map<String, String> types, WorkUploadColumn... columns) {
		for (WorkUploadColumn c : columns) {
			if (isNotEmpty(types, c)) {
				return true;
			}
		}
		return false;
	}

	public static Boolean containsAny(Map<String, String> types, List<WorkUploadColumn> columns) {
		for (WorkUploadColumn c : columns) {
			if (isNotEmpty(types, c)) {
				return true;
			}
		}
		return false;
	}

	public static Boolean containsAll(Map<String, String> types, WorkUploadColumn... columns) {
		for (WorkUploadColumn c : columns)
			if (isEmpty(types, c))
				return false;
		return true;
	}

	public FieldType createFieldType() {
		return (FieldType) SerializationUtilities.clone(fieldType);
	}

	public String getUploadColumnName() {
		return uploadColumnName;
	}

	public WorkUploadColumnType getColumnType() {
		return columnType;
	}

	public String getUploadColumnDescription() {
		return uploadColumnDescription;
	}

	public String getUploadColumnCategory() {
		return uploadColumnCategory;
	}

	public int getOrder() {
		return order;
	}

	public boolean isVisible() {
		return visible;
	}
}
