package com.workmarket.domains.model.validation;

public class MessageKeys {

	public static final String NOT_NULL = "not_empty";

	public static class Work {
		public static final String UNEXPECTED_ERROR = "unexpected_error";
		public static final String RESOLUTION_REQUIRED = "resolution_required";
		public static final String CHECKIN_REQUIRED = "checkin_required";
		public static final String CHECKOUT_REQUIRED = "checkout_required";
		public static final String CHECK_IN_CALL_NAME_REQUIRED = "check_in_call_name_required";
		public static final String CHECK_IN_CALL_PHONE_REQUIRED = "check_in_call_phone_required";
		public static final String TAX_RATE_REQUIRED = "tax_rate_required";
		public static final String WORK_RESOURCE_REQUIRED = "work_resource_required";
		public static final String HOURS_WORKED_REQUIRED = "hours_worked_required";
		public static final String UNITS_PROCESSED_REQUIRED = "units_processed_required";
		public static final String INCOMPLETE_ASSESSMENTS = "incomplete_assessments";

		public static final String ADMIN_OR_MANAGER_ROLE_REQUIRED = "admin_or_manager_role_required";
		public static final String ADMIN_ROLE_OR_PAYMENT_CENTER_REQUIRED = "admin_role_or_payment_center_required";
		public static final String COMPLETE_STATUS_REQUIRED = "complete_status_required";
		public static final String PAYMENT_PENDING_STATUS_REQUIRED = "payment_pending_status_required";
		public static final String CLOSED_STATUS_REQUIRED = "closed_status_required";
		public static final String ACTIVE_STATUS_REQUIRED = "active_status_required";
		public static final String INVALID_STATUS_FOR_VOID = "invalid_status_for_void";
		public static final String INVALID_STATUS_FOR_PAID = "invalid_status_for_paid";
		public static final String INVALID_STATUS_FOR_CANCEL = "invalid_status_for_cancel";
		public static final String NOT_ACTIVE_RESOURCE = "not_active_resource";
		public static final String WORK_INVOICE_LOCKED = "work_editable_invoice_required";
		public static final String WORK_INVOICE_BUNDLED = "work_invoice_bundled";
		public static final String WORK_INVOICE_REQUIRED = "work_invoice_required";

		public static final String LANE1_RESOURCE_REQUIRED = "lane1_resource_required";
		public static final String LANE2_ACTIVE_REQUIRED = "lane2_active_required";
		public static final String INVALID_COUNTRY = "invalid_country";

		public static final String INSUFFICIENT_FUNDS = "insufficient_funds";
		public static final String INVALID_TIMEFRAME = "invalid_timeframe";
		public static final String PAID_ASSIGNMENT = "paid_assignment";
		public static final String FAILED_FULFILLMENT = "failed_fulfillment";
		public static final String INVALID_INVOICE = "invalid_invoice";
		public static final String MAX_SPEND_EXCEEDED = "max_spend_exceeded";
		public static final String MAX_SPEND_TOO_LOW = "max_spend_too_low";
		public static final String INVALID_SPEND_LIMIT = "invalid_spend_limit";
		public static final String INVALID_PRICING_TYPE_CHANGE = "invalid_pricing_type_change";
		public static final String INVALID_PRICING_CHANGE = "invalid_pricing_change";

		public static final String CUSTOM_FIELD_GROUP_REQUIRED = "custom_field_group_required";

		public static final String MAX = "Max";
		public static final String MAXLEN = "max_length";
		public static final String MINLEN = "min_length";
		public static final String NOT_NULL = "not_empty";
		public static final String NOT_AUTHORIZED = "not_authorized";
		public static final String UNABLE_TO_PAY_ASSIGNMENT = "unable_to_pay_assignment";
		public static final String WORK_IS_PENDING_FULFILLMENT = "work_is_pending_fulfillment";
		public static final String WORK_INVOICE_FULFILLMENT_MISMATCH = "assignment_invoice_fulfillment_mismatch";

		public static final String SCHEDULING_INVALID_TIMEFRAME_ORDER = "scheduling_invalid_timeframe_order";
		public static final String SCHEDULING_TIMEFRAME_TOO_LONG = "scheduling_timeframe_too_long";
		public static final String SCHEDULING_MISSING_VALUES = "scheduling_missing_values";
		public static final String SCHEDULING_INVALID_DATE = "scheduling_invalid_date";

		public static final String LABEL_INVALID_SCOPE = "label_invalid_scope";
		public static final String INVALID_BUNDLE_WORK_STATE = "assignment_bundle.add.fail.state";
		public static final String INVALID_BUNDLE_WORK_INBUNDLE = "assignment_bundle.add.fail.inbundle";
	}

	public static class Assessment {
		public static final String CHOICES_NOT_ALLOWED = "choices_not_allowed";
		public static final String CHOICES_REQUIRED = "choices_required";
		public static final String CHOICES_EMPTY = "choices_empty";
		public static final String CORRECT_CHOICE_REQUIRED = "correct_choice_required";
		public static final String INCORRECT_PASSING_SCORE = "lms.manage.assessment.configuration.passingscore.wrong";
		public static final String APPROXIMATE_DURATION_MINUTES_INCORRECT_VALUES_RANGE = "lms.manage.assessment.configuration.incorrectApproximateDurationMinutes";
		public static final String CONFIGURATION_DURATION_MINUTES_INCORRECT_VALUES_RANGE = "lms.manage.assessment.configuration.incorrectDurationMinutes";
		public static final String INCORRECT_DURATION_APPROXIMATE_MINUTES_RANGE = "lms.manage.assessment.configuration.incorrectDurationMinutesRange";
		public static final String INCORRECT_RETAKES_ALLOWED = "lms.manage.assessment.configuration.incorrectTimesAllowedRetakeCourse";
		public static final String INCORRECT_EMAIL_NOTIFICATIONS_DAYS = "lms.manage.assessment.configuration.incorrectEmailNotificationsDays";
		public static final String INVALID_EMBED_URL = "lms.manage.assessment.configuration.invalidEmbedUrl";
		public static final String TOO_MANY_MEDIA_UPLOAD_ERROR = "lms.manage.assessment.configuration.tooManyMediaUploadError";

	}

	public static class User {
		public static final String BLOCKED_USER_OR_COMPANY = "blocked_user_or_company";
	}

	public static class Contact {
		public static final String FIRST_NAME_TOO_LONG = "crm_contact.first_name.max_length";
		public static final String LAST_NAME_TOO_LONG = "crm_contact.last_name.max_length";
	}

	public static class Invoice {
		public static final String INVOICE_INVALID_ACCESS = "invoice_invalid_access";
		public static final String INVOICE_IS_BUNDLED = "invoice_is_bundled";
	}
}
