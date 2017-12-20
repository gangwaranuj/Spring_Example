package com.workmarket.dao.search.work;

import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;

class SolrWorkSqlUtil {

	// @formatter:off

	private static final String[] COMMON_COLUMNS = new String[] { "DISTINCT work.id AS workId",
			"work.uuid",
			"work.title",
			"work.work_number",
			"work.parent_id",
			"work.description",
			"work.instructions",
			"work.desired_skills AS skills",
			"work.created_on",
			"work.creator_id",
			"work.industry_id",
			"work.closed_on",
			"work.pricing_strategy_type",
			"work.schedule_from",
			"work.schedule_through",
			"work.confirmed_flag",
			"work.company_id",
			"work.resource_confirmation_flag",
			"work.due_on AS dueDate",
			"work.payment_terms_days",
			"work.payment_terms_enabled",
			"CASE work.address_onsite_flag WHEN 'Y' THEN false WHEN 'N' THEN true ELSE NULL END AS offSite",
			"COALESCE(work.buyer_fee, 0) AS buyerFee",
			"COALESCE(work.buyer_total_cost, 0) AS buyerTotalCost",
			"work.work_price",
			"work.amount_earned",
			"COALESCE(work.show_in_feed, company.show_in_feed) AS showInFeed",
			"work.buyer_support_user_id",
			"company.auto_pay_enabled",
			"work.assign_to_first_resource",
			"assignedResource.dispatcher_id AS dispatcherId",
			//status
			"work_status_type.description AS workStatusDescription",

			"time_zone.time_zone_id AS timeZoneId",
			//support contact
			"buyerSupport.email AS buyerSupportEmail",
			"buyerSupport.first_name AS buyerSupportFirstName",
			"buyerSupport.last_name AS buyerSupportLastName",
			//address
			"address.city",
			"state.short_name as state",
			"address.postal_code AS postalCode",
			"address.country",
			"address.latitude",
			"address.longitude",
			//client and location
			"client_company.id AS clientCompanyId",
			"client_company.name AS clientCompanyName",
			"location.id AS locationId",
			"location.name AS locationName",
			"location.location_number",
			//buyer
			"buyer.id AS buyerId",
			"buyer.last_name AS buyerLastName",
			"buyer.first_name AS buyerFirstName",
			//project
			"project.id AS projectId",
			"project.name AS projectName",
			//company
			"company.name AS companyName",
			//dates
			"milestones.sent_on",
			"milestones.accepted_on",
			"milestones.complete_on",
			"milestones.paid_on",
			//invoice
			"invoice.id as invoiceId",
			"invoice.invoice_number AS invoiceNumber",

			"IF(work.modified_on > waa.last_action_on, work.modified_on, waa.last_action_on) AS lastModifiedOn",
			"IF(work.modified_on > waa.last_action_on, work_modifier.last_name, modifier.last_name) AS modifierLastName",
			"IF(work.modified_on > waa.last_action_on, work_modifier.first_name, modifier.first_name) AS modifierFirstName",
			"IF(work.account_pricing_type_code = 'transactional', COALESCE(legacy_work_fee_configuration.percentage, " +
					" COALESCE(account_register.current_work_fee_percentage,0)),0) AS workFeePercentage",
			"EXISTS ( SELECT wqa.id FROM work_question_answer_pair wqa " +
					" WHERE wqa.answer IS NULL AND wqa.work_id = work.id) AS openQuestions ",
			"IF(work.work_status_type_code = 'sent', EXISTS ( SELECT wn.id FROM work_negotiation wn " +
					" WHERE wn.approval_status = 0 AND (wn.expires_on >= :today OR wn.expires_on IS NULL) AND wn.work_id = work.id), false) AS openNegotiations",

			// parent
			"parent.title AS parentTitle, parent.description AS parentDescription",
			//county
			"location_mapping.county AS countyName",
			"location_mapping.fips AS countyId",
			"work_unique_id.id_value as uniqueExternalId",
			"work_to_recurrence_association.recurrence_uuid as recurrenceUUID",
			"work_to_decision_flow_association.decision_flow_uuid as decisionFlowUuid"
	};

	private static final String SPEND_LIMIT_CASE_CLAUSE =
		"IF(work.work_status_type_code = 'complete', " +
			" IF(work.override_price IS NOT NULL, work.override_price, " +
			"CASE work.pricing_strategy_type " +
			"WHEN 'FLAT' THEN " +
				"work.flat_price " +
				" + IF(COALESCE(assignedResource.additional_expenses, 0) > 0, LEAST(COALESCE(assignedResource.additional_expenses, 0), work.additional_expenses), COALESCE(work.additional_expenses, 0)) " +
				" + COALESCE(assignedResource.bonus, 0) " +
			"WHEN 'PER_HOUR' THEN " +
				"(work.per_hour_price * COALESCE(assignedResource.hours_worked,0)) + IF(COALESCE(assignedResource.additional_expenses, 0) > 0, LEAST(COALESCE(assignedResource.additional_expenses, 0), work.additional_expenses), COALESCE(work.additional_expenses, 0)) + COALESCE(assignedResource.bonus, 0) " +
			"WHEN 'PER_UNIT' THEN " +
				"(work.per_unit_price * COALESCE(assignedResource.units_processed,0)) + IF(COALESCE(assignedResource.additional_expenses, 0) > 0, LEAST(COALESCE(assignedResource.additional_expenses, 0), work.additional_expenses), COALESCE(work.additional_expenses, 0)) + COALESCE(assignedResource.bonus, 0) " +
			"WHEN 'BLENDED_PER_HOUR' THEN " +
				"((work.initial_per_hour_price * IF(COALESCE(assignedResource.hours_worked,0) > work.initial_number_of_hours, work.initial_number_of_hours, assignedResource.hours_worked)) " +
				" + (work.additional_per_hour_price * IF(COALESCE(assignedResource.hours_worked,0) > work.initial_number_of_hours, COALESCE(assignedResource.hours_worked,0) - work.initial_number_of_hours, 0)) " +
				" + IF(COALESCE(assignedResource.additional_expenses, 0) > 0, LEAST(COALESCE(assignedResource.additional_expenses, 0), work.additional_expenses), COALESCE(work.additional_expenses, 0)) " +
				" + COALESCE(assignedResource.bonus, 0)) " +
			"WHEN 'BLENDED_PER_UNIT' THEN " +
				"((work.initial_per_unit_price * IF(COALESCE(assignedResource.units_processed,0) > work.initial_number_of_units, work.initial_number_of_units, assignedResource.units_processed)) " +
				" + (work.additional_per_unit_price * IF(COALESCE(assignedResource.units_processed,0) > work.initial_number_of_units, COALESCE(assignedResource.units_processed,0) - work.initial_number_of_units, 0)) " +
				" + IF(COALESCE(assignedResource.additional_expenses, 0) > 0, LEAST(COALESCE(assignedResource.additional_expenses, 0), work.additional_expenses), COALESCE(work.additional_expenses, 0)) + COALESCE(assignedResource.bonus, 0)) " +
			"WHEN 'INTERNAL' THEN 0 " +
			"ELSE NULL END), " +
				"IF (work.work_status_type_code IN ('active', 'declined', 'draft', 'sent', 'void'), " +
				"CASE work.pricing_strategy_type " +
				"WHEN 'FLAT' THEN work.flat_price + work.additional_expenses + work.bonus " +
				"WHEN 'PER_HOUR' THEN (work.per_hour_price * work.max_number_of_hours) + work.additional_expenses + work.bonus " +
				"WHEN 'PER_UNIT' THEN (work.per_unit_price * work.max_number_of_units) + work.additional_expenses + work.bonus " +
				"WHEN 'BLENDED_PER_HOUR' THEN ((work.initial_per_hour_price * work.initial_number_of_hours) + (work.additional_per_hour_price * work.max_blended_number_of_hours)) + work.additional_expenses + work.bonus " +
				"WHEN 'BLENDED_PER_UNIT' THEN ((work.initial_per_unit_price * work.initial_number_of_units) + (work.additional_per_unit_price * work.max_blended_number_of_units)) + work.additional_expenses + work.bonus " +
				"WHEN 'INTERNAL' THEN 0 " +
				"ELSE NULL END, " +
				"(COALESCE(work.buyer_total_cost, 0)))) AS spendLimit";

	private static final String HAS_BUYER_PENDING_APPLY_NEGOTIATIONS_COLUMN =
			"IF(work.work_status_type_code = 'sent' " +
				"AND EXISTS(" +
					"SELECT wn.id " +
					"FROM work w " +
						"INNER JOIN work_negotiation wn " +
							"ON w.id = wn.work_id " +
						"LEFT OUTER JOIN blocked_user_association bua " +
							"ON w.company_id = bua.blocking_company_id " +
							"AND wn.requestor_id = bua.blocked_user_id " +
							"AND bua.deleted = 0 " +
					"WHERE wn.approval_status = 0 " +
					"AND wn.work_id = work.id " +
					"AND bua.blocked_user_id IS NULL " +
					"AND (wn.expires_on >= :today OR wn.expires_on IS NULL)), " +
				"1, 0) AS applicationsPending";

	private static void buildJoinClause(SQLBuilder builder) {
		builder
				.addJoin("INNER JOIN company ON work.company_id = company.id ")
				.addJoin("INNER JOIN account_register on company.id = account_register.company_id ")
				.addJoin("INNER JOIN user as buyer ON work.buyer_user_id = buyer.id")
				.addJoin("INNER JOIN work_status_type ON work.work_status_type_code = work_status_type.code")
				.addJoin("INNER JOIN work_action_audit waa ON waa.work_id = work.id")
				.addJoin("INNER JOIN user as work_modifier ON work.modifier_id = work_modifier.id")
				.addJoin("INNER JOIN user as modifier ON waa.modifier_id = modifier.id")
				.addJoin("INNER JOIN time_zone ON time_zone.id = work.time_zone_id ")
				.addJoin("INNER JOIN work_milestones milestones ON work.id = milestones.work_id")

				.addJoin("LEFT JOIN work_resource AS assignedResource ON (work.id = assignedResource.work_id and assignedResource.assigned_to_work = true)")
				.addJoin("LEFT JOIN invoice  ON (invoice.id = work.invoice_id AND invoice.deleted = 0) ")
				.addJoin("LEFT JOIN client_company ON work.client_company_id = client_company.id")
				.addJoin("LEFT JOIN address ON work.address_id = address.id")
				.addJoin("LEFT JOIN state ON state.id = address.state")
				.addJoin("LEFT JOIN location_mapping ON location_mapping.postal_code = address.postal_code ")
				.addJoin("LEFT JOIN user buyerSupport ON work.buyer_support_user_id = buyerSupport.id")
				.addJoin("LEFT JOIN location ON work.client_location_id = location.id")
				.addJoin("LEFT JOIN project_work_association pwa ON pwa.work_id = work.id")
				.addJoin("LEFT JOIN project ON project.id = pwa.project_id")
				.addJoin("LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = work.id")
				.addJoin("LEFT JOIN work parent ON work.parent_id = parent.id")
				.addJoin("LEFT JOIN work_unique_id ON work.id = work_unique_id.work_id")
				.addJoin("LEFT JOIN work_to_recurrence_association ON work.id = work_to_recurrence_association.work_id")
				.addJoin("LEFT JOIN work_to_decision_flow_association ON (work.id = work_to_decision_flow_association.work_id AND work_to_decision_flow_association.deleted = 0)");
	}

	static SQLBuilder newWorkSolrIndexSQLBuilder() {
		SQLBuilder builder = new SQLBuilder();

		builder
				.addColumns(COMMON_COLUMNS)
				.addColumn(SPEND_LIMIT_CASE_CLAUSE)
				.addColumn(HAS_BUYER_PENDING_APPLY_NEGOTIATIONS_COLUMN)
				.addColumn(" CASE 	work.work_status_type_code " +
						" WHEN 	'active' " +
						" THEN 	IF( (assignedResource.checkedin_flag = true \n" +
						"		OR (work.checkin_required_flag = false AND work.checkin_call_required = false \n" +
						" 		AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= :today)),  'inprogress', 'active')" +
						" ELSE 	work.work_status_type_code END AS workStatusCode ")
				.addTable("work");

		buildJoinClause(builder);

		builder
				.addWhereClause(" work.deleted = 0 AND work.type = 'W'")
				/*
				 * We use this format of date to avoid the use of sysdate() or now() which invalidate the use of the query cache
				 */
				.addParam("today", DateUtilities.formatTodayForSQL());
		return builder;

	}
}
