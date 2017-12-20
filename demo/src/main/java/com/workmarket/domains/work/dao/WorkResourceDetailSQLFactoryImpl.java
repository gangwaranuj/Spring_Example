package com.workmarket.domains.work.dao;

import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.stereotype.Component;

@Component
public class WorkResourceDetailSQLFactoryImpl implements WorkResourceDetailSQLFactory {

	private final static String[] DEFAULT_COLUMNS = new String[] {
			"resource.id as resourceId, \n " +
			"user.id AS userId, \n" +
			"user.user_number, \n" +
			"user.first_name, \n" +
			"user.email, \n" +
			"user.last_name, \n" +
			"user.created_on as joined_on, \n" +
			"company.name AS companyName, \n" +
			"company.id AS companyId, \n" +
			"resource.work_resource_status_type_code, \n" +
			"resource.created_on, \n" +
			"resource.modified_on, \n" +
			"resource.assign_to_first_resource AS assignToFirstToAccept, \n" +
			"address.line1, address.line2, \n" +
			"lane.lane_type_id laneType, \n" +
			"COALESCE(address.city, postal_code.city) AS city, \n" +
			"state.short_name AS state, \n" +
			"COALESCE(address.postal_code, postal_code.postal_code) AS postal_code, \n" +
			"COALESCE(address.country, postal_code.country_id) AS country, \n" +
			"COALESCE(address.latitude, postal_code.latitude) AS latitude, \n" +
			"COALESCE(address.longitude, postal_code.longitude) AS longitude, \n" +
			"profile.work_phone, \n" +
			"profile.work_phone_extension, \n" +
			"profile.mobile_phone, \n" +
			"(SELECT COUNT(*) FROM rating WHERE rating.rated_user_id = userId AND rating.deleted = false AND (rating.rater_company_id = companyId OR rating.rating_shared_flag = 'Y')) AS rating_count, \n" +
			"(SELECT AVG(value) FROM rating WHERE rating.rated_user_id = userId AND rating.deleted = false AND (rating.rater_company_id = companyId OR rating.rating_shared_flag = 'Y')) AS rating_average, \n" +
			"IF((SELECT max(qa.id) FROM work_question_answer_pair qa WHERE qa.work_id = resource.work_id AND qa.questioner_user_id = resource.user_id) IS NULL, 0, 1) AS question_pending, \n" +
			"resource.targeted as targeted, \n" +
			"blocked_user_association.id IS NOT NULL AS blocked, \n" +
			"COALESCE(asset_cdn_uri.cdn_uri_prefix, asset_remote_uri.remote_uri_prefix) AS avatarCdnUri, \n" +
			"asset.uuid AS avatarUUID, " +
		    "asset.availability_type_code AS avatarAvailabilityType"
	};

	private final static String[] NON_APPLY_NEGOTIATION_COLUMNS = new String[] {
			"IF((SELECT n.expires_on FROM work_negotiation n WHERE n.approval_status <> 6 AND n.work_id = resource.work_id AND n.requestor_id = user.id ORDER BY n.created_on LIMIT 1) <= NOW(), 1, 0) AS negotiation_expired",
			"(SELECT n.approval_status FROM work_negotiation n WHERE n.approval_status <> 6 AND n.work_id = resource.work_id AND n.requestor_id = user.id ORDER BY n.created_on LIMIT 1) AS negotiation_status",
			"(SELECT n.requested_on FROM work_negotiation n WHERE n.approval_status <> 6 AND n.work_id = resource.work_id AND n.requestor_id = user.id ORDER BY n.created_on LIMIT 1) AS negotiationRequestedOn"
	};

	private static final String NEGOTIATION_SPEND_LIMIT_COLUMN =
			"CASE negotiation.pricing_strategy_type " +
					"WHEN 'FLAT' THEN negotiation.flat_price + negotiation.additional_expenses + negotiation.bonus " +
					"WHEN 'PER_HOUR' THEN (negotiation.per_hour_price * negotiation.max_number_of_hours) + negotiation.additional_expenses + negotiation.bonus " +
					"WHEN 'PER_UNIT' THEN (negotiation.per_unit_price * negotiation.max_number_of_units) + negotiation.additional_expenses + negotiation.bonus " +
					"WHEN 'BLENDED_PER_HOUR' THEN ((negotiation.initial_per_hour_price * negotiation.initial_number_of_hours) + (negotiation.additional_per_hour_price * negotiation.max_blended_number_of_hours)) + negotiation.additional_expenses + negotiation.bonus  " +
					"WHEN 'BLENDED_PER_UNIT' THEN ((negotiation.initial_per_unit_price * negotiation.initial_number_of_units) + (negotiation.additional_per_unit_price * negotiation.max_blended_number_of_units)) + negotiation.additional_expenses + negotiation.bonus  " +
					"WHEN 'INTERNAL' THEN 0 " +
					"ELSE NULL " +
					"END";

	private static final String WORK_SPEND_LIMIT_COLUMN =
			"CASE work.pricing_strategy_type " +
					"WHEN 'FLAT' THEN work.flat_price + work.additional_expenses + work.bonus " +
					"WHEN 'PER_HOUR' THEN (work.per_hour_price * work.max_number_of_hours) + work.additional_expenses + work.bonus " +
					"WHEN 'PER_UNIT' THEN (work.per_unit_price * work.max_number_of_units) + work.additional_expenses + work.bonus " +
					"WHEN 'BLENDED_PER_HOUR' THEN ((work.initial_per_hour_price * work.initial_number_of_hours) + (work.additional_per_hour_price * work.max_blended_number_of_hours)) + work.additional_expenses + work.bonus " +
					"WHEN 'BLENDED_PER_UNIT' THEN ((work.initial_per_unit_price * work.initial_number_of_units) + (work.additional_per_unit_price * work.max_blended_number_of_units)) + work.additional_expenses + work.bonus " +
					"WHEN 'INTERNAL' THEN 0 " +
					"ELSE NULL " +
					"END";

	private final static String[] APPLY_NEGOTIATION_COLUMNS = new String[] {
			"IFNULL(asset.cdn_uri, asset.remote_uri) AS avatarUri",

			"negotiation.id IS NOT NULL AS hasNegotiation",
			"negotiation.id AS negotiationId",

			"negotiation.requested_on AS negotiationRequestedOn",
			"negotiation.expires_on AS negotiationExpiresOn",
			"negotiation.approval_status AS negotiationApprovalStatus",

			"negotiation.negotiate_price_flag AS negotiationNegotiatePriceFlag",
			"negotiation.pricing_strategy_type AS negotiationPricingStrategyType",
			"negotiation.flat_price AS negotiationFlatPrice",
			"negotiation.max_flat_price AS negotiationMaxFlatPrice",
			"negotiation.per_hour_price AS negotiationPerHourPrice",
			"negotiation.max_number_of_hours AS negotiationMaxNumberOfHours",
			"negotiation.per_unit_price AS negotiationPerUnitPrice",
			"negotiation.max_number_of_units AS negotiationMaxNumberOfUnits",
			"negotiation.initial_per_hour_price AS negotiationInitialPerHourPrice",
			"negotiation.initial_number_of_hours AS negotiationInitialNumberOfHours",
			"negotiation.additional_per_hour_price AS negotiationAdditionalPerHourPrice",
			"negotiation.max_blended_number_of_hours AS negotiationMaxBlendedNumberOfHours",
			"negotiation.initial_per_unit_price AS negotiationInitialPerUnitPrice",
			"negotiation.initial_number_of_units AS negotiationInitialNumberOfUnits",
			"negotiation.additional_per_unit_price AS negotiationAdditionalPerUnitPrice",
			"negotiation.max_blended_number_of_units AS negotiationMaxBlendedNumberOfUnits",
			"negotiation.additional_expenses AS negotiationAdditionalExpenses",
			"negotiation.bonus AS negotiationBonus",
			"negotiation.override_price AS negotiationOverridePrice",

			String.format("%s AS negotiationSpendLimit", NEGOTIATION_SPEND_LIMIT_COLUMN),
			"COALESCE(legacy_work_fee_configuration.percentage, COALESCE(account_register.current_work_fee_percentage,0)) AS negotiationFeePercentage",

			"negotiation.negotiate_schedule_flag AS negotiationNegotiateScheduleFlag",
			"negotiation.schedule_is_range_flag AS negotiationScheduleIsRangeFlag",
			"negotiation.schedule_from AS negotiationScheduleFrom",
			"negotiation.schedule_through AS negotiationScheduleThrough",

			"note.note_content AS negotiationNoteContent",
			"NULL AS onTimePercentage",
			"NULL AS deliverableOnTimePercentage"
	};

	private static final String[] BEST_PRICE_NEGOTIATION_COLUMNS = new String[] {
			"resource.id AS resourceId",
			"resource.user_id AS userId",
			String.format("IF(negotiation.negotiate_price_flag, %s, %s) AS spendLimit", NEGOTIATION_SPEND_LIMIT_COLUMN, WORK_SPEND_LIMIT_COLUMN),
			"COUNT(*) AS count"
	};

	@Override
	public SQLBuilder getResourceListBuilder(Long workId, WorkResourceDetailPagination pagination) {
		SQLBuilder sqlb = new SQLBuilder()
			.addColumns(DEFAULT_COLUMNS)
			.addColumn("COALESCE((3959 * acos( cos( radians(COALESCE(address.latitude, postal_code.latitude)) ) * cos( radians(w_address.latitude) ) * cos( radians(w_address.longitude) - radians(COALESCE(address.longitude, postal_code.longitude)) )"
				+	"            + sin( radians(COALESCE(address.latitude, postal_code.latitude)) ) * sin( radians(w_address.latitude) ) ) )"
				+ ", 0.0) as distance")
			.addColumn("w_address.postal_code as work_postal_code")
			.addTable("work_resource  as resource")
			.addJoin("INNER JOIN user ON resource.user_id = user.id")
			.addJoin("INNER JOIN company ON user.company_id = company.id")
			.addJoin("INNER JOIN profile ON profile.user_id = user.id")
			.addJoin("INNER JOIN work w ON resource.work_id = w.id")
			.addJoin("LEFT JOIN address ON profile.address_id = address.id")
			.addJoin("LEFT JOIN state ON state.id = address.state")
			.addJoin("LEFT JOIN postal_code ON postal_code.id = profile.postal_code_id")
			.addJoin("LEFT JOIN lane_association lane \n" +
					 " ON (lane.user_id = user.id AND lane.company_id = w.company_id " +
					 " AND lane.deleted = false AND lane.approval_status IN (1,5) AND lane.verification_status = 1) ")
			.addJoin("LEFT JOIN legacy_work_fee_configuration ON legacy_work_fee_configuration.work_id = w.id")
			.addJoin("LEFT JOIN blocked_user_association" +
					" ON w.company_id = blocked_user_association.blocking_company_id" +
					" AND resource.user_id = blocked_user_association.blocked_user_id" +
					" AND blocked_user_association.deleted = 0")
			.addJoin("LEFT JOIN user_asset_association ON user_asset_association.user_id = user.id AND user_asset_association.id = (" +
						"SELECT MAX(aa.id) FROM user_asset_association aa WHERE aa.user_id = user.id AND aa.asset_type_code ='avatar' AND aa.approval_status = 1 AND aa.active = 1 AND aa.deleted = 0" +
						")")

			.addJoin("LEFT JOIN asset ON asset.id = user_asset_association.transformed_small_asset_id")
			.addJoin("LEFT JOIN asset_remote_uri ON asset_remote_uri.id = asset.asset_remote_uri_id ")
			.addJoin("LEFT JOIN asset_cdn_uri ON asset_cdn_uri.id = asset.asset_cdn_uri_id")

			.addJoin("LEFT OUTER JOIN address w_address ON w_address.id=w.address_id")
			.addWhereClause("resource.work_id = :workId")
			.addWhereClause("blocked_user_association.id IS NULL")
			.addParam("workId", workId);

		if (pagination.isIncludeApplyNegotiation()) {
			sqlb.addColumns(APPLY_NEGOTIATION_COLUMNS)
				.addJoin("INNER JOIN account_register on w.company_id = account_register.company_id ")
				.addJoin("LEFT JOIN work_negotiation negotiation ON negotiation.work_id = w.id AND user.id = negotiation.requestor_id AND negotiation.type IN ('apply', 'negotiation') AND negotiation.approval_status <> 6")
				.addJoin("LEFT JOIN note ON note.id = negotiation.note_id");
		} else {
			sqlb.addColumns(NON_APPLY_NEGOTIATION_COLUMNS);
		}

		return sqlb;
	}

	@Override
	public SQLBuilder getResourceBestPriceBuilder(Long workId) {
		return new SQLBuilder()
				.addColumns(BEST_PRICE_NEGOTIATION_COLUMNS)
				.addTable("work_resource AS resource")
				.addJoin("INNER JOIN work ON resource.work_id = work.id")
				.addJoin("INNER JOIN work_negotiation negotiation ON negotiation.work_id = work.id AND resource.user_id = negotiation.requestor_id AND negotiation.approval_status = 0")
				.addWhereClause("resource.work_id = :workId")
				.addParam("workId", workId)
				.addGroupColumns("spendLimit")
				.addAscOrderBy("spendLimit")
				.setStartRow(0)
				.setPageSize(1);
	}
}
