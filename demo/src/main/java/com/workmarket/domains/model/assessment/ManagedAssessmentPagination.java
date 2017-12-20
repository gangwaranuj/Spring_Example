package com.workmarket.domains.model.assessment;

import java.util.List;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Sort;
import com.workmarket.utility.sql.SQLOperator;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

public class ManagedAssessmentPagination extends AbstractPagination<ManagedAssessment> implements Pagination<ManagedAssessment>{

	private TYPE_FILTER_KEYS           typeFilter;
	private ATTEMPT_STATUS_FILTER_KEYS attemptFilter;
	private TAKEABILITY_FILTER_KEYS    takeabilityFilter;
	private OWNER_FILTER_KEYS          ownerFilter;
	private INVITATION_FILTER_KEYS     invitationFilter;
	private ACTIVITY_FILTER_KEYS       activityFilter     = ACTIVITY_FILTER_KEYS.ACTIVE;
	private PRIVACY_FILTER_KEYS        privacyFilter;
	private List<Long>                 idFilter;
	private List<REQUEST_INFO>         requestInformation = Lists.newArrayList();
	private static final int           RECOMMENDED_PAGINATION_MAX_RESULTS = 20;

	/**************** COMPLEX FILTERS ****************/

	public enum TYPE_FILTER_KEYS {
		/** Tests */
		GRADED(AbstractAssessment.GRADED_ASSESSMENT_TYPE),
		/** Surveys */
		SURVEY(AbstractAssessment.SURVEY_ASSESSMENT_TYPE);

		private final static String column   = "assessment.type";
		private final static String operator = SQLOperator.EQUALS;

		private final String        value;

		TYPE_FILTER_KEYS(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static String getColumn() {
			return column;
		}

		public static String getOperator() {
			return operator;
		}
	}

	public enum ATTEMPT_STATUS_FILTER_KEYS {
		/** Tests that the user has tried to pass lately but hasn't finished yet. */
		IN_PROGRESS(AttemptStatusType.INPROGRESS),
		/** The test has been taken and passed at least once */
		PASSED(AttemptStatusType.GRADED),
		/** The test has been taken but it has never been passed */
		FAILED(AttemptStatusType.GRADED),
		/** The test is pending grading */
		GRADE_PENDING(AttemptStatusType.GRADE_PENDING),
		/** Surveys which are neither passed nor failed, they are just completed. */
		COMPLETED(AttemptStatusType.COMPLETE);

		private final static String column   = "association.attempt_status_type_code";
		private final static String operator = SQLOperator.EQUALS;

		private final String        value;

		ATTEMPT_STATUS_FILTER_KEYS(String value) {
			this.value = value;
		}

		public static String getColumn() {
			return column;
		}

		public static String getOperator() {
			return operator;
		}

		public String getValue() {
			return value;
		}
	}

	public enum TAKEABILITY_FILTER_KEYS {
		/** Tests that haven’t been taken but can be taken, or tests that the user failed but is eligible to re-take */
		TAKEABLE,
		/** Tests that the user failed but is able to re-take. */
		RETAKEABLE,
		/** Tests that have been passed, tests than have been failed, or tests that are grade pending */
		TAKEN,
		/** Tests that have not been taken (ever) */
		NOT_TAKEN;
	}


	public enum OWNER_FILTER_KEYS {
		/** Tests the selected user has created */
		USER("assessment.creator_id"),
		/** Tests that the selected company has created */
		COMPANY("assessment.company_id");

		private Long                value;
		private final String        column;
		private final static String operator = SQLOperator.EQUALS;

		OWNER_FILTER_KEYS(String column) {
			this.column = column;
		}

		public Long getValue() {
			return value;
		}

		/** Sets the userId or companyId
		 * @param value The userId or companyId (depends on the filter) */
		public OWNER_FILTER_KEYS setValue(Long value) {
			this.value = value;
			return this;
		}

		public String getColumn() {
			return column;
		}

		public static String getOperator() {
			return operator;
		}
	}

	public enum INVITATION_FILTER_KEYS {
		/** The user can take this test because his|her industries match the test industries */
		INDUSTRY_INVITED,
		/** The user has been invited to take this test. */
		DIRECTLY_INVITED,
		/** Tests that are required for groups a user has been invited to (even if the user hasn’t been explicitly invited to the test itself). */
		GROUP_INVITED,
		/** The user has been invited to take this test or tests that are required for groups a user has been invited to (even if the user hasn’t been explicitly invited to the test itself). */
		DIRECTLY_OR_GROUP_INVITED;

		private final static int monthsAgo = 4;

		public static int getMonthsago() {
			return monthsAgo;
		}
	}

    public enum PRIVACY_FILTER_KEYS {

        PRIVACY_PRIVATE(0),
        PRIVACY_PUBLIC(1);

        private final static String column = "assessment.featured_flag";
        private final static String operator = SQLOperator.EQUALS;

        private final int value;

        PRIVACY_FILTER_KEYS(int value){
            this.value = value;
        }

        public static String getColumn() {
			return column;
		}

		public static String getOperator() {
			return operator;
		}

		public int getValue() {
			return value;
		}

    }

	public enum ACTIVITY_FILTER_KEYS {
		/** Active tests or surveys */
		ACTIVE(AssessmentStatusType.ACTIVE),
		/** Inactive tests or surveys */
		INACTIVE(AssessmentStatusType.INACTIVE);

		private final static String column   = "assessment.assessment_status_type_code";
		private final static String operator = SQLOperator.EQUALS;

		private final String        value;

		ACTIVITY_FILTER_KEYS(String value) {
			this.value = value;
		}

		public static String getColumn() {
			return column;
		}

		public static String getOperator() {
			return operator;
		}

		public String getValue() {
			return value;
		}
	}

	/**************** SIMPLE FILTERS ****************/

	public enum FILTER_KEYS {
		NAME("assessment.name", SQLOperator.LIKE, String.class),
		COMPANY_NAME("company.effective_name", SQLOperator.LIKE, String.class),
		FEATURED("featured_flag", SQLOperator.EQUALS, Boolean.class);

		private final String   column;
		private final String   operator;
		private final Class<?> expectedClass;

		FILTER_KEYS(String column, String operator, Class<?> expectedClass) {
			this.column = column;
			this.operator = operator;
			this.expectedClass = expectedClass;
		}

		public String getColumn() {
			return column;
		}

		public String getOperator() {
			return operator;
		}

		public Class<?> getExpectedClass() {
			return expectedClass;
		}
	}

	public enum SORTS {
		/** The day the user was invited to take the test */
		CREATED_ON("assessment.created_on"),
		NAME("assessment.name"),
		COMPANY_NAME("company.effective_name"),
		INVITATION_DATE("invitationDate"),
		STATUS("association.attempt_status_type_code"),
		/** The day the user completed the test */
		COMPLETED_ON("association.completed_on"),
		/** The day the user started the test */
		STARTED_ON("association.created_on"),
		/** The amount of users that passed the test */
		PASSED_COUNT("passedCount");

		private String column;

		SORTS(String column) {
			this.column = column;
		}

		public Sort getSort(SORT_DIRECTION sortDirection) {
			return new Sort(column, sortDirection);
		}
	}

	/**************** REQUEST INFORMATION ****************/

	public enum REQUEST_INFO {
		/** Include PASSED and GRADE_PENDING statistics */
		STATISTICS,
		/** Include % completed of IN_PROGRESS assignments */
		PROGRESS;
	}

	public ManagedAssessmentPagination() {}

	public ManagedAssessmentPagination(boolean returnAllRows) {
		super(returnAllRows);
	}

	public static ManagedAssessmentPagination getRecommendedAssessmentPagination(boolean withFilters) {
		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination();
		if (withFilters) {
			pagination
				.setTakeabilityFilter(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE)
				.setActivityFilter(ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.ACTIVE)
				.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED)
				.setPrivacyFilter(ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.PRIVACY_PUBLIC)
				.setInvitationFilter(INVITATION_FILTER_KEYS.INDUSTRY_INVITED);
		} else {
			pagination.setActivityFilter(null);
		}
		pagination.addRequestInformation(REQUEST_INFO.PROGRESS);
		pagination.addSort(ManagedAssessmentPagination.SORTS.PASSED_COUNT.getSort(SORT_DIRECTION.DESC));
		pagination.setPage(1);
		pagination.setResultsLimit(RECOMMENDED_PAGINATION_MAX_RESULTS);
		return pagination;
	}

	public TYPE_FILTER_KEYS getTypeFilter() {
		return typeFilter;
	}

	public ManagedAssessmentPagination setTypeFilter(TYPE_FILTER_KEYS typeFilter) {
		this.typeFilter = typeFilter;
		return this;
	}

	public ATTEMPT_STATUS_FILTER_KEYS getAttemptFilter() {
		return attemptFilter;
	}

	public ManagedAssessmentPagination setAttemptFilter(ATTEMPT_STATUS_FILTER_KEYS attemptFilter) {
		this.attemptFilter = attemptFilter;
		return this;
	}

	public TAKEABILITY_FILTER_KEYS getTakeabilityFilter() {
		return takeabilityFilter;
	}

	public ManagedAssessmentPagination setTakeabilityFilter(TAKEABILITY_FILTER_KEYS takeabilityFilter) {
		this.takeabilityFilter = takeabilityFilter;
		return this;
	}

	public OWNER_FILTER_KEYS getOwnerFilter() {
		return ownerFilter;
	}

	public ManagedAssessmentPagination setOwnerFilter(OWNER_FILTER_KEYS ownerFilter) {
		this.ownerFilter = ownerFilter;
		return this;
	}

	public INVITATION_FILTER_KEYS getInvitationFilter() {
		return invitationFilter;
	}

	public ManagedAssessmentPagination setInvitationFilter(INVITATION_FILTER_KEYS invitationFilter) {
		this.invitationFilter = invitationFilter;
		return this;
	}

    public PRIVACY_FILTER_KEYS getPrivacyFilter() {
        return privacyFilter;
    }

    public ManagedAssessmentPagination setPrivacyFilter(PRIVACY_FILTER_KEYS privacyFilter) {
        this.privacyFilter = privacyFilter;
        return this;
    }

    public ACTIVITY_FILTER_KEYS getActivityFilter() {
		return activityFilter;
	}

	public ManagedAssessmentPagination setActivityFilter(ACTIVITY_FILTER_KEYS activityFilter) {
		this.activityFilter = activityFilter;
		return this;
	}

	public List<REQUEST_INFO> getRequestInformation() {
	    return requestInformation;
    }

	public ManagedAssessmentPagination addRequestInformation(REQUEST_INFO requestInfo) {
		this.requestInformation.add(requestInfo);
		return this;
	}

	public List<Long> getIdFilter() {
		return idFilter;
	}

	public ManagedAssessmentPagination setIdFilter(List<Long> idFilter) {
		this.idFilter = idFilter;
		return this;
	}

	public List<Long> getResultIds() {
		 return extract(this.getResults(), on(ManagedAssessment.class).getAssessmentId());
	}

}
