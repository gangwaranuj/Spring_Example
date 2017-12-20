package com.workmarket.service.thrift.transactional.work;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.thrift.EnumValue;
import com.workmarket.thrift.work.WorkResponse;
import org.springframework.scheduling.annotation.Async;

import java.util.EnumSet;

public enum WorkRequestInfo implements EnumValue {
	CONTEXT_INFO(1) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildContext(response, currentUser, work);
		}
	},

	STATUS_INFO(2) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildStatus(response, currentUser, work);
		}
	},

	PROJECT_INFO(3) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildProject(response, work);
		}
	},

	COMPANY_INFO(4) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildCompany(response, work);
		}
	},

	CLIENT_COMPANY_INFO(5) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildClientCompany(response, work);
		}
	},

	BUYER_INFO(6) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildBuyer(response, work);
		}
	},

	LOCATION_CONTACT_INFO(7) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildLocationContact(response, work);
		}
	},

	SUPPORT_CONTACT_INFO(8) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildSupportContact(response, work);
		}
	},

	LOCATION_INFO(9) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildLocation(response, work);
		}
	},

	SCHEDULE_INFO(10) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildSchedule(response, work);
		}
	},

	PRICING_INFO(11) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildPricing(response, work);
		}
	},

	PAYMENT_INFO(12) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildPayment(response, work);
		}
	},

	ASSETS_INFO(13) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildAssets(response, work);
		}
	},

	MANAGE_MY_WORK_MARKET_INFO(14) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildManageMyWorkMarket(response, work, currentUser);
		}
	},

	NOTES_INFO(15) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildNotes(response, currentUser, work);
		}
	},

	QUESTION_ANSWER_PAIRS_INFO(16) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildQuestionAnswerPairs(response, work);
		}
	},

	WORKFLOW_INFO(17) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			return;
		}
	},

	PARTS_INFO(18) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildParts(response, work);
		}
	},

	ACTIVE_RESOURCE_INFO(19) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildActiveResource(response, work);
		}
	},

	RESOURCES_INFO(20) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildResources(response, work);
		}
	},

	CUSTOM_FIELDS_VALUES_AND_DATA_INFO(21) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildCustomFields(response, work, true);
		}
	},

	CHANGE_LOG_INFO(22) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildChangeLog(response, work);
		}
	},

	PRICING_HISTORY_INFO(23) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildPricingHistory(response, work);
		}
	},

	REQUIRED_ASSESSMENTS_INFO(24) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildRequiredAssessments(response, work);
		}
	},

	INDUSTRY_INFO(25) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildIndustry(response, work);
		}
	},

	RATINGS_INFO(26) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildRatingsForWork(response, currentUser, work);
		}
	},

	ROUTING_STRATEGIES_INFO(28) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildRoutingStrategies(response, work);
		}
	},

	ACTIVE_RESOURCE_NEGOTIATION_INFO(29) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) throws Exception {
			builder.buildPendingRescheduleNegotiationForActiveResource(response, work);
			builder.buildBudgetNegotiationForActiveResource(response, work);
			builder.buildExpenseNegotiationForActiveResource(response, work);
			builder.buildBonusForActiveResource(response, work);
		}
	},

	CUSTOM_FIELD_NAMES(32) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildCustomFields(response, work, false);
		}
	},

	REQUIREMENT_SET_INFO(33) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildRequirementSetIds(response, work);
		}
	},

	FOLLOWER_INFO(34) {
		@Override
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildFollowers(response, work);
		}
	},

	GROUP_INFO(35) {
		@Override
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildGroups(response, work);
		}
	},

	DELIVERABLES_INFO(36) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildDeliverables(response, work);
		}
	},

	BASE_INFO(37) {
		@Override
		@Async
		public void buildPartOf(WorkResponseBuilderImpl builder, WorkResponse response, AbstractWork work, User currentUser) {
			builder.buildBaseWork(response, work);
		}
	};

	private final int value;

	private WorkRequestInfo(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static EnumSet<WorkRequestInfo> getWorkDetailInfoEnumSet() {
		return EnumSet.of(
				CONTEXT_INFO,
				STATUS_INFO,
				INDUSTRY_INFO,
				PROJECT_INFO,
				COMPANY_INFO,
				BUYER_INFO,
				LOCATION_INFO,
				SCHEDULE_INFO,
				PRICING_INFO,
				MANAGE_MY_WORK_MARKET_INFO,
				NOTES_INFO,
				QUESTION_ANSWER_PAIRS_INFO,
				PARTS_INFO,
				FOLLOWER_INFO,
				GROUP_INFO,
				DELIVERABLES_INFO
		);
	}

	public static EnumSet<WorkRequestInfo> getWorkDetailsEnumSet() {
		return EnumSet.of(
			BASE_INFO,
			CONTEXT_INFO,
			STATUS_INFO,
			COMPANY_INFO,
			BUYER_INFO,
			LOCATION_INFO,
			SCHEDULE_INFO,
			PRICING_INFO,
			MANAGE_MY_WORK_MARKET_INFO,
			PARTS_INFO,
			DELIVERABLES_INFO
		);
	}

	public static EnumSet<WorkRequestInfo> getAdminUserInfoEnumSet() {
		return EnumSet.of(
				CLIENT_COMPANY_INFO,
				LOCATION_CONTACT_INFO,
				SUPPORT_CONTACT_INFO,
				ACTIVE_RESOURCE_INFO,
				ASSETS_INFO,
				REQUIRED_ASSESSMENTS_INFO,
				PAYMENT_INFO
		);
	}

	public static EnumSet<WorkRequestInfo> getInfoEnumSet() {
		return EnumSet.of(
			ASSETS_INFO,
			BASE_INFO,
			BUYER_INFO,
			CLIENT_COMPANY_INFO,
			COMPANY_INFO,
			CONTEXT_INFO,
			CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
			DELIVERABLES_INFO,
			FOLLOWER_INFO,
			GROUP_INFO,
			INDUSTRY_INFO,
			LOCATION_CONTACT_INFO,
			LOCATION_INFO,
			MANAGE_MY_WORK_MARKET_INFO,
			NOTES_INFO,
			PARTS_INFO,
			PAYMENT_INFO,
			PRICING_INFO,
			PROJECT_INFO,
			QUESTION_ANSWER_PAIRS_INFO,
			REQUIRED_ASSESSMENTS_INFO,
			REQUIREMENT_SET_INFO,
			RESOURCES_INFO,
			SCHEDULE_INFO,
			STATUS_INFO,
			SUPPORT_CONTACT_INFO,
			WORKFLOW_INFO
		);
	}

	@Async
	public abstract void buildPartOf(
			WorkResponseBuilderImpl builder,
			WorkResponse response,
			AbstractWork work,
			User currentUser) throws Exception;

	public static WorkRequestInfo findByValue(int value) {
		switch (value) {
			case 1:
				return CONTEXT_INFO;
			case 2:
				return STATUS_INFO;
			case 3:
				return PROJECT_INFO;
			case 4:
				return COMPANY_INFO;
			case 5:
				return CLIENT_COMPANY_INFO;
			case 6:
				return BUYER_INFO;
			case 7:
				return LOCATION_CONTACT_INFO;
			case 8:
				return SUPPORT_CONTACT_INFO;
			case 9:
				return LOCATION_INFO;
			case 10:
				return SCHEDULE_INFO;
			case 11:
				return PRICING_INFO;
			case 12:
				return PAYMENT_INFO;
			case 13:
				return ASSETS_INFO;
			case 14:
				return MANAGE_MY_WORK_MARKET_INFO;
			case 15:
				return NOTES_INFO;
			case 16:
				return QUESTION_ANSWER_PAIRS_INFO;
			case 18:
				return PARTS_INFO;
			case 19:
				return ACTIVE_RESOURCE_INFO;
			case 20:
				return RESOURCES_INFO;
			case 21:
				return CUSTOM_FIELDS_VALUES_AND_DATA_INFO;
			case 22:
				return CHANGE_LOG_INFO;
			case 23:
				return PRICING_HISTORY_INFO;
			case 24:
				return REQUIRED_ASSESSMENTS_INFO;
			case 25:
				return INDUSTRY_INFO;
			case 26:
				return RATINGS_INFO;
			case 28:
				return ROUTING_STRATEGIES_INFO;
			case 29:
				return ACTIVE_RESOURCE_NEGOTIATION_INFO;
			case 32:
				return CUSTOM_FIELD_NAMES;
			case 33:
				return REQUIREMENT_SET_INFO;
			case 34:
				return FOLLOWER_INFO;
			case 35:
				return GROUP_INFO;
			case 36:
				return DELIVERABLES_INFO;
			case 37:
				return BASE_INFO;
			default:
				return null;
		}
	}
}
