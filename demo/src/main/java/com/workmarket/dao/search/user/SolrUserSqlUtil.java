package com.workmarket.dao.search.user;

import com.workmarket.dao.network.UserGroupNetworkAssociationDAOImpl;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.insurance.UserInsuranceType;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

class SolrUserSqlUtil {

	/*
	 * User tiebreak metrics (used in SELECT_SQL, destination workercore)
	 * "recent_working_weeks_ratio"
	 * "weighted_average_rating"
	 */

	// Components of recentWorkingWeeksRatio:
	// What rough percent of the past 3 months (13 weeks) has this user had paid work (bucketed across 10 buckets)
	private static final String TOTAL_WORKING_WEEKS_CLAUSE =
			"COUNT(DISTINCT time_dimension.week_of_year)";

	private static final String TOTAL_WEEKS_ON_PLATFORM_WITHIN_3MO_CLAUSE =
			"FLOOR(DATEDIFF(NOW(), GREATEST(u.created_on, DATE_SUB(NOW(), INTERVAL 3 MONTH))) / 7.0)";

	private static final String BUCKETED_WORKING_RATIO_CLAUSE = "COALESCE(TRUNCATE( "
			+ TOTAL_WORKING_WEEKS_CLAUSE + " / "
			+ "(" + TOTAL_WEEKS_ON_PLATFORM_WITHIN_3MO_CLAUSE + " + 1)"
			+ ", 1), 0)";


	// Components of weightedAverageRating:
	// Allow workers with less ratings on the platform to skew toward the mean.
	/*
	 * Formula from BeerAdvocate (uses Bayesian estimate):
	 * --------------------------------------------------
	 * weighted rank (WR) = (v / (v+m)) × R + (m / (v+m)) × C
	 *
	 * where:
	 *  R = review average for the beer
	 *  v = number of reviews for the beer
	 *  m = threshold/minimum reviews required to be listed (currently 10)
	 *  C = the mean across the list (currently 2.87)
	 */
	private static int RATING_THRESHOLD = 10;
	private static float RATING_MEAN = 2.87f; // This could obviously change over time, but not dramatically
	private static final String NUMBER_OF_WORKER_RATINGS = "COALESCE(COUNT(DISTINCT rating.work_id),0)";
	private static final String WORKER_MEAN_OVERALL_RATING = "AVG(rating.value)";
	private static final String WEIGHTED_RATING_CLAUSE = "COALESCE(" +
			"(" + NUMBER_OF_WORKER_RATINGS + " / " +
			"  (" + NUMBER_OF_WORKER_RATINGS + "+" + RATING_THRESHOLD + ")) * " + WORKER_MEAN_OVERALL_RATING +
	 		" + " +
			"(" + RATING_THRESHOLD + " / " +
			"  (" + NUMBER_OF_WORKER_RATINGS + "+" + RATING_THRESHOLD + ")) * " + RATING_MEAN +
			"," + RATING_MEAN + ")";


	// @formatter:off
	static final String SELECT_SQL =
		" SELECT u.id, u.uuid, u.user_number, u.first_name, u.last_name, u.email, u.screening_status_type_code, user_summary.last_assigned_work_date, p.overview, \n" +
				" c.id as companyId, c.uuid as companyUuid, c.name as companyName, IF (c.operating_as_individual_flag = TRUE, 2, 1) as companyType, \n" +
				" p.job_title, p.hourly_rate, p.max_travel_distance, p.work_phone, p.mobile_phone, \n" +
				" u.created_on, u.email_confirmed, u.lane3_approval_status, u.user_status_type_code, warp_requisition_id, \n" +
				" COALESCE(a.city, postal_code.city) AS city, \n" +
				" COALESCE(st.short_name, postal_state.short_name) AS state, \n" +
				" COALESCE(a.postal_code, postal_code.postal_code) AS postal_code, \n" +
				" COALESCE(a.country, postal_code.country_id) AS country, \n" +
				" COALESCE(a.latitude, postal_code.latitude) AS latitude, \n" +
				" COALESCE(a.longitude, postal_code.longitude) AS longitude, \n" +
				" u.recruiting_campaign_id, recruiting_campaign.title, \n" +
				" time_zone.time_zone_id, lms.cbsa_name, mbo_profile.status mbo_status, \n" +
				//Shared worker role
				" EXISTS (SELECT role.id FROM user_acl_role role \n" +
				" WHERE role.deleted = false \n" +
				" AND 	role.acl_role_id = 7 AND role.user_id = u.id) sharedWorkerRole, \n" +
				//Completed work count
				" (SELECT 	COALESCE(COUNT(DISTINCT work_id),0) FROM work_history_summary \n" +
				" WHERE 	work_status_type_code = 'paid' \n" +
				" AND 		work_history_summary.active_resource_user_id = u.id) AS workCount, \n" +
				//Completed work count last 6 months
				" (SELECT 	COALESCE(COUNT(DISTINCT work_id),0) FROM work_history_summary \n" +
				" INNER 	JOIN time_dimension ON time_dimension.id = work_history_summary.date_id \n" +
				" WHERE 	work_status_type_code = 'paid' \n" +
				" AND 		work_history_summary.active_resource_user_id = u.id " +
				" AND		time_dimension.date >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS paidWorkLastSixMonths, \n" +
				//Cancelled work count
				" (SELECT 	COALESCE(COUNT(work_resource_label.id), 0) FROM work_resource_label\n" +
				" WHERE  	work_resource_label.work_resource_user_id = u.id \n" +
				" AND		work_resource_label.work_resource_label_type_code = 'cancelled'\n" +
				" AND 		work_resource_label.ignored = false AND work_resource_label.confirmed = true) AS workCancelled, \n" +
				//Rating count
				" (SELECT 	COALESCE(COUNT(DISTINCT rating.work_id),0) FROM rating \n" +
				" INNER 	JOIN work_resource ON work_resource.user_id = rated_user_id AND work_resource.work_id = rating.work_id " +
				" WHERE 	rating.rating_shared_flag = 'Y' AND rating.deleted = 0 AND rating.is_pending = 0 \n" +
				" AND 		rating.rated_user_id = u.id AND rating.created_on >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS ratingCount, \n" +
				// Good Rating count
				" (SELECT 	COALESCE(COUNT(DISTINCT rating.work_id),0) FROM rating \n" +
				" INNER 	JOIN work_resource ON work_resource.user_id = rated_user_id AND work_resource.work_id = rating.work_id " +
				" WHERE 	rating.rating_shared_flag = 'Y' AND rating.deleted = 0 AND rating.is_pending = 0 \n" +
				" AND 		rating.rated_user_id = u.id and rating.value in (2, 3) AND rating.created_on >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS goodRatingCount, \n" +
				//Rating sum
				" (SELECT 	COALESCE(AVG(rating.value), 0) FROM rating  \n" +
				" INNER 	JOIN work_resource ON work_resource.user_id = rated_user_id AND work_resource.work_id = rating.work_id " +
				" WHERE 	rating.rating_shared_flag = 'Y' AND rating.deleted = 0 \n" +
				" AND 		rating.rated_user_id = u.id AND rating.created_on >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS ratingAvg, \n" +
				// Roughly bucketed percent of weeks working in past three months
				"(SELECT " + BUCKETED_WORKING_RATIO_CLAUSE +
				" FROM work_history_summary \n" +
				" INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id \n" +
				" INNER JOIN user ON user.id = active_resource_user_id \n" +
				" WHERE work_history_summary.work_status_type_code = 'paid' \n" +
				" AND work_history_summary.active_resource_user_id = u.id \n" +
				" AND time_dimension.date > DATE_SUB(NOW(), INTERVAL 3 MONTH)) AS recentWorkingWeeksRatio, \n" +
				// Weighted average overall rating
				"(SELECT " + WEIGHTED_RATING_CLAUSE +
				" FROM		rating \n" +
				" INNER 	JOIN work_resource ON work_resource.user_id = rating.rated_user_id AND work_resource.work_id = rating.work_id \n" +
				" WHERE 	rating.rating_shared_flag = 'Y' AND rating.deleted = 0 AND rating.is_pending = 0 AND rating.is_buyer_rating = 1 \n" +
				" AND 		rating.rated_user_id = u.id and rating.value in (1, 2, 3)) AS weightedAverageRating, \n" +
				//Avatar
				"(SELECT  	COALESCE(a_1.cdn_uri, a_1.remote_uri) FROM user_asset_association AS uaa_1 \n" +
				"LEFT 		JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id \n" +
				"WHERE 		uaa_1.user_id = u.id AND uaa_1.asset_type_code ='avatar' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0 \n" +
				"ORDER 		BY a_1.created_on DESC LIMIT 1) AS avatarUri, \n" +
				// Avatar cdn/remote URI. Needed in the case that avatarURI is null and we have to construct the asset url manually in code
				"(SELECT  COALESCE(acu_1.cdn_uri_prefix, aru_1.remote_uri_prefix) FROM user_asset_association AS uaa_1\n" +
				"LEFT    JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id\n" +
				"LEFT    JOIN asset_remote_uri aru_1 on aru_1.id = a_1.asset_remote_uri_id\n" +
				"LEFT    JOIN asset_cdn_uri acu_1 on acu_1.id = a_1.asset_cdn_uri_id\n" +
				"WHERE   uaa_1.user_id = u.id  AND uaa_1.asset_type_code ='avatar' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0\n" +
				"ORDER   BY a_1.created_on DESC LIMIT 1) AS avatarUriPrefix, \n" +
				// Avatar UUID. Needed in the case that avatarURI is null and we have to construct the asset url manually in code
				"(SELECT  a_1.uuid FROM user_asset_association AS uaa_1\n" +
				"LEFT    JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id\n" +
				"WHERE   uaa_1.user_id = u.id  AND uaa_1.asset_type_code ='avatar' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0\n" +
				"ORDER   BY a_1.created_on DESC LIMIT 1) AS avatarUuid, \n" +
				//Video
				"(SELECT  	COALESCE(a_1.cdn_uri, a_1.remote_uri) FROM user_asset_association AS uaa_1 \n" +
				"LEFT 		JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id \n" +
				"WHERE 		uaa_1.user_id = u.id AND uaa_1.asset_type_code ='profile_video' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0 \n" +
				"ORDER 		BY a_1.created_on DESC LIMIT 1) AS videoUri, \n" +
				// Video cdn/remote URI. Needed in the case that avatarURI is null and we have to construct the asset url manually in code
				"(SELECT  COALESCE(acu_1.cdn_uri_prefix, aru_1.remote_uri_prefix) FROM user_asset_association AS uaa_1\n" +
				"LEFT    JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id\n" +
				"LEFT    JOIN asset_remote_uri aru_1 on aru_1.id = a_1.asset_remote_uri_id\n" +
				"LEFT    JOIN asset_cdn_uri acu_1 on acu_1.id = a_1.asset_cdn_uri_id\n" +
				"WHERE   uaa_1.user_id = u.id  AND uaa_1.asset_type_code ='profile_video' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0\n" +
				"ORDER   BY a_1.created_on DESC LIMIT 1) AS videoUriPrefix, \n" +
				// Video UUID. Needed in the case that avatarURI is null and we have to construct the asset url manually in code
				"(SELECT  a_1.uuid FROM user_asset_association AS uaa_1\n" +
				"LEFT    JOIN asset AS a_1 ON a_1.id = uaa_1.transformed_small_asset_id\n" +
				"WHERE   uaa_1.user_id = u.id  AND uaa_1.asset_type_code ='profile_video' AND uaa_1.approval_status = 1 AND uaa_1.active = 1 AND uaa_1.deleted = 0\n" +
				"ORDER   BY a_1.created_on DESC LIMIT 1) AS videoUuid \n" +
		" FROM 		profile p \n" +
		" INNER 	JOIN user u ON u.id = p.user_id \n" +
		" INNER 	JOIN company c ON c.id = u.company_id \n" +
		" INNER 	JOIN time_zone ON time_zone.id = p.time_zone_id \n" +
		" LEFT 		JOIN address companyAddress ON companyAddress.id = c.address_id \n" +
		" LEFT 		JOIN user_summary ON user_summary.user_id = u.id \n" +
		" LEFT 		JOIN recruiting_campaign ON recruiting_campaign.id = u.recruiting_campaign_id \n" +
		" LEFT  	JOIN postal_code ON postal_code.id = p.postal_code_id \n" +
		" LEFT  	JOIN state as postal_state ON postal_state.id = postal_code.state_province \n" +
		" LEFT  	JOIN address a ON a.id = p.address_id \n" +
		" LEFT  	JOIN state st ON st.id = a.state \n" +
		" LEFT 		JOIN location_mapping_simple lms ON lms.postal_code = COALESCE(COALESCE(a.postal_code, postal_code.postal_code), companyAddress.postal_code) " +
		" LEFT 		JOIN mbo_profile ON mbo_profile.user_id = u.id  \n" +

		String.format(" WHERE 	u.user_status_type_code IN ('%s', '%s', '%s') \n", UserStatusType.ACTIVE_USER_STATUS_TYPES.toArray()) +
		" AND 		u.api_enabled = 0 \n";

	static final String CERTIFICATION_ASSOCIATION_SQL = "SELECT certification.id, certification.name, association.user_id, vendor.name AS vendorName \n" +
		" FROM 		certification \n" +  
		" INNER 	JOIN user_certification_association association \n" +  
		" ON 		certification.id = association.certification_id \n" + 
		" INNER 	JOIN certification_vendor vendor \n" +
		" ON 		certification.certification_vendor_id = vendor.id \n" + 
		" INNER 	JOIN user u \n" +  
		" ON 		u.id = association.user_id \n" + 
		" WHERE 	certification.deleted = FALSE \n" + 
		" AND 		association.deleted = FALSE \n" + 
		" AND 		association.verification_status IN (0,1,3,4,5)";

	static final String INDUSTRY_ASSOCIATION_SQL = "SELECT association.industry_id AS industryId, profile.user_id \n" +
		" FROM 		profile \n" +
		" INNER 	JOIN profile_industry_association association \n" +
		" ON 		profile.id = association.profile_id \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.id = profile.user_id \n" +
		" INNER		JOIN industry i \n" +
		" ON		i.id = association.industry_id \n" +
		" WHERE 	association.deleted = FALSE \n" +
		" AND		i.deleted = FALSE";
	
	static final String LICENSE_ASSOCIATION_SQL = "SELECT license.id, license.name, association.user_id, license.state \n" +
		" FROM 		license \n" +  
		" INNER 	JOIN user_license_association association \n" +  
		" ON 		license.id = association.license_id \n" + 
		" INNER 	JOIN user u \n" +  
		" ON 		u.id = association.user_id \n" + 
		" WHERE 	license.deleted = FALSE \n" + 
		" AND 		association.deleted = FALSE \n" + 
		" AND 		association.verification_status IN (0,1,3,4,5)";
	
	private static final String COMMON_ENTITY_ASSOCIATION_SQL = "SELECT <entity>.id, <entity>.name, association.user_id \n" +
		" FROM 		<entity> \n" +
		" INNER 	JOIN user_<entity>_association association \n" +  
		" ON 		<entity>.id = association.<entity>_id \n" + 
		" INNER 	JOIN user u \n" +  
		" ON 		u.id = association.user_id \n" + 
		" WHERE 	<entity>.deleted = FALSE \n" + 
		" AND 		association.deleted = FALSE \n" + 
		" AND 		association.verification_status IN (0,1,3,4,5) \n";

	private static final String INSURANCE_COVERAGE_ASSOCIATION_SQL = "SELECT DISTINCT uia.user_id, uia.coverage \n" +
		" FROM      user_insurance_association uia \n" +
		" INNER     JOIN user u \n" +
		" ON        u.id = uia.user_id \n" +
		" WHERE     uia.insurance_id = <insurance_id> \n" +
		" AND       uia.deleted = FALSE" +
		" AND       uia.verification_status = 1 \n" +
		" AND       uia.expiration_date >= DATE(NOW()) + INTERVAL 0 SECOND \n";

	static final String COMPANY_TAG_ASSOCIATION_SQL = "SELECT DISTINCT association.company_id, tag.name, association.user_id \n" +
		" FROM 		user_tag_association association \n" +
		" INNER 	JOIN tag \n" +
		" ON 		tag.id = association.tag_id \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.id = association.user_id \n" + 
		" WHERE 	association.type = 'CUT' \n" +
		" AND 		association.deleted = false \n" +
		" AND 		tag.deleted = false \n";

	static final String BLOCKED_BY_USERS_SQL = " SELECT DISTINCT association.blocked_user_id AS user_id, association.user_id AS blocked_by_user_id \n" +
		" FROM 		blocked_user_association association \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.id = association.blocked_user_id \n" +
		" INNER 	JOIN user blocking_user \n" +
		" ON 		(blocking_user.id = association.user_id OR blocking_user.company_id = association.blocking_company_id) \n" +
		" WHERE 	association.type IN (1,2) \n" +
		" AND 		association.deleted = false \n";
	
	static final String BLOCKED_COMPANIES_SQL = " SELECT DISTINCT association.user_id, association.blocked_company_id \n" +
		" FROM 		blocked_user_association association \n" +
		" INNER 	JOIN user u \n" +
		" ON 		(u.id = association.user_id OR u.company_id = association.blocking_company_id) \n" +
		" WHERE 	association.type IN (3,4) \n" +
		" AND 		association.deleted = false \n";
	
	static final String BLOCKED_BY_COMPANIES_SQL = " SELECT DISTINCT u.id AS user_id, association.blocking_company_id AS blocked_by_company_id \n" +
		" FROM 		blocked_user_association association \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.company_id = association.blocked_company_id \n" +
		" INNER 	JOIN user blocking_user \n" +
		" ON 		(blocking_user.id = association.user_id OR blocking_user.company_id = association.blocking_company_id) \n" +
		" WHERE 	association.type IN (3,4) \n" +
		" AND 		association.deleted = false \n";
	
	static final String LANE_ASSOCIATION_SQL = "SELECT DISTINCT lane.company_id, c.uuid as company_uuid, lane.user_id, lane.lane_type_id, lane.approval_status \n" +
		" FROM 		lane_association lane \n" +
		" INNER 	JOIN user u  \n" +
		" ON 		lane.user_id = u.id \n" +
		" INNER 	JOIN company c  \n" +
		" ON 		lane.company_id = c.id \n" +
		" LEFT 		JOIN user_acl_role role \n" + 
		" ON 		(u.id = role.user_id AND role.deleted = false) \n" +
		" WHERE		lane.deleted = false \n" +
		" AND 		( (lane.lane_type_id IN (1,2) OR u.email_confirmed = 'Y')) \n" +
		" AND 		u.user_status_type_code IN ('approved','pending') \n" +
		" AND 		( (lane.lane_type_id = 1 AND role.acl_role_id = 6 AND lane.approval_status IN (1,5) ) \n" +
					" OR (lane.lane_type_id = 3 AND role.acl_role_id = 7 AND u.lane3_approval_status = 1 AND lane.approval_status IN (1,5) ) \n" +
					" OR (lane.lane_type_id = 2) ) \n";

	/* ASSESSMENT STATUS */
	//only passed TESTS
	static final String ASSESSMENT_ASSOCIATION_SQL = "SELECT association.assessment_id, assessment.company_id, association.user_id \n" +
		" FROM 		assessment_user_association association \n" +
		" INNER 	JOIN assessment \n" +
		" ON  		assessment.id = association.assessment_id \n" +
		" INNER 	JOIN user u  \n" +
		" ON 		u.id = association.user_id \n" +
		" WHERE 	association.passed_flag = 1 \n" +
		" AND 		assessment.assessment_status_type_code = 'active' \n";

	//failed TESTS (surveys can't be failed)
	static final String FAILED_TEST_ASSOCIATION_SQL = "SELECT association.assessment_id, assessment.company_id, association.user_id \n" +
		" FROM 		assessment_user_association association \n" +
		" INNER 	JOIN assessment \n" +
		" ON  		assessment.id = association.assessment_id \n" +
		" INNER 	JOIN user u  \n" +
		" ON 		u.id = association.user_id \n" +
		" WHERE 	association.passed_flag = 0 \n" +
		" AND 		assessment.assessment_status_type_code = 'active' \n" +
		" AND 	    association.attempt_status_type_code = 'graded' \n" +
		" AND 	    assessment.type = 'graded' \n";

	//passed assessments (tests AND surveys)
	static final String PASSED_ASSESSMENT_ASSOCIATION_SQL = "SELECT association.assessment_id, assessment.company_id, association.user_id \n" +
		" FROM assessment_user_association association \n" +
		" INNER JOIN assessment \n" +
		" ON assessment.id = association.assessment_id \n" +
		" INNER JOIN user u \n" +
		" ON u.id = association.user_id \n" +
		" WHERE assessment.assessment_status_type_code = 'active' \n" +
		" AND assessment.type IN ('graded','survey') \n" +
		" AND association.attempt_status_type_code IN ('graded','complete') \n" +
		" AND ((assessment.type = 'graded' AND association.passed_flag = 1) OR (assessment.type = 'survey')) \n";

	/* GROUP MEMBERSHIP STATUSES */
	// Members + Override Members
	static final String COMPANY_USER_GROUP_SQL = "SELECT uuga.user_group_id, ug.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE 	uuga.approval_status = 1 \n" +
		" AND       u.user_status_type_code = '" + UserStatusType.APPROVED + "'\n" +
		" AND 		uuga.deleted = false \n" +
		" AND 		ug.deleted = false \n";

	static final String MEMBER_GROUP_ASSOCIATION_SQL = " SELECT uuga.user_group_id, u.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE     uuga.approval_status = 1 \n" +
		" AND       u.user_status_type_code = '" + UserStatusType.APPROVED + "'\n" +
		" AND       uuga.deleted = false \n" +
		" AND       ug.deleted = false \n" +
		" AND       uuga.override_member = false \n";

	static final String MEMBER_OVERRIDE_GROUP_ASSOCIATION_SQL = " SELECT uuga.user_group_id, u.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE     uuga.approval_status = 1 \n" +
		" AND       u.user_status_type_code = '" + UserStatusType.APPROVED + "'\n" +
		" AND       uuga.deleted = false \n" +
		" AND       ug.deleted = false \n" +
		" AND       uuga.override_member = true \n";

	static final String PENDING_PASSED_GROUP_ASSOCIATION_SQL = " SELECT uuga.user_group_id, u.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE     uuga.approval_status = 0 \n" +
		" AND       u.user_status_type_code = '" + UserStatusType.APPROVED + "'\n" +
		" AND       uuga.deleted = false \n" +
		" AND       ug.deleted = false \n" +
		" AND       uuga.verification_status IN (0,1) \n";

	static final String PENDING_FAILED_GROUP_ASSOCIATION_SQL = " SELECT uuga.user_group_id, u.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE     uuga.approval_status = 0 \n" +
		" AND       u.user_status_type_code = '" + UserStatusType.APPROVED + "'\n" +
		" AND       uuga.deleted = false \n" +
		" AND       ug.deleted = false \n" +
		" AND       uuga.verification_status = 2 \n";

	static final String INVITED_GROUP_ASSOCIATION_SQL = " SELECT DISTINCT rgi.user_group_id as 'user_group_id', u.company_id, u.id as 'user_id', g.uuid AS user_group_uuid \n" +
		" FROM user u \n" +
		" INNER JOIN request r ON u.id = r.invitee_user_id \n" +
		" INNER JOIN request_group_invitation rgi ON r.id = rgi.id \n" +
		" INNER JOIN user_group g on g.id = rgi.user_group_id \n" +
		" WHERE r.deleted = 0 \n" +
		" AND r.request_status_type_code = :sent \n" +
		" AND g.active_flag = 1 \n" +
		" AND g.deleted = 0 \n" +
		" AND NOT EXISTS ( \n" +
			" SELECT id \n" +
			" FROM user_user_group_association \n" +
			" WHERE approval_status IN (0,1,2) \n" +
			" AND deleted = false \n" +
			" AND verification_status IN (0,1,2) \n" +
			" AND user_id = u.id \n" +
			" AND user_group_id = rgi.user_group_id \n" +
		") \n";

	static final String DECLINED_GROUP_ASSOCIATION_SQL = " SELECT uuga.user_group_id, u.company_id, uuga.user_id, ug.uuid AS user_group_uuid \n" +
		" FROM      user u \n" +
		" LEFT      JOIN user_user_group_association uuga ON uuga.user_id = u.id \n" +
		" LEFT      JOIN user_group ug ON ug.id = uuga.user_group_id \n" +
		" WHERE     uuga.approval_status = 2 \n" +
		" AND       uuga.deleted = 0 \n" +
		" AND       ug.deleted = 0 \n";

  //invited tests AND surveys, not yet taken
  static final String INVITED_ASSESSMENT_ASSOCIATION_SQL = "SELECT rai.assessment_id, a.company_id, r.invitee_user_id as 'user_id' \n" +
		" FROM      request r \n" +
		" INNER     JOIN request_assessment_invitation rai \n" +
		" ON        r.id = rai.id \n" +
		" INNER     JOIN user u \n" +
		" ON        u.id = r.invitee_user_id \n" +
		" INNER     JOIN assessment a \n" +
		" ON        rai.assessment_id = a.id \n" +
		" WHERE     a.assessment_status_type_code = 'active' \n" +
		" AND NOT EXISTS ( \n" +
		"   SELECT      aua.assessment_id, aua.user_id \n" +
		"   FROM        assessment_user_association aua \n" +
		"   WHERE       aua.assessment_id = rai.assessment_id \n" +
		"   AND         aua.user_id = r.invitee_user_id \n" +
		"   AND         aua.attempt_status_type_code != 'inprogress' \n" +
		") \n";

	static final String TOOLS_ASSOCIATION_SQL = "SELECT tool.name, association.user_id \n" +
		" FROM 		user_tool_association association \n" +
		" INNER 	JOIN tool \n" +
		" ON 		tool.id = association.tool_id \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.id = association.user_id \n" +
		" WHERE 	association.deleted = false \n" +
		" AND 		tool.deleted = false \n";
	
	static final String SPECIALTIES_ASSOCIATION_SQL = "SELECT specialty.name, association.user_id \n" +
		" FROM 		user_specialty_association association \n" + 
		" INNER 	JOIN specialty \n" + 
		" ON 		specialty.id = association.specialty_id \n" +
		" INNER 	JOIN user u \n" + 
		" ON 		u.id = association.user_id \n" + 
		" WHERE 	association.deleted = false \n" + 
		" AND 		specialty.deleted = false \n";
	
	private static final String LINKEDIN_EDUCATION_SQL = " SELECT personData.userId, education.school_name AS name, education.field_of_Study, 'education' AS linkedInDataType \n" +
		" FROM      linkedin_education education \n" +
		" INNER     JOIN (SELECT u.id AS userId, (SELECT MAX(person.id) \n" +
		" FROM 		linkedin_person person \n" +  
		" WHERE 	person.deleted = false AND person.user_id = u.id ) AS personId \n" + 
		" FROM 		user u WHERE 1=1 <userWhereClause>) AS personData \n" + 
		" ON 		personData.personId = education.linkedin_person_id \n" +  
		" WHERE 	education.deleted = false \n"; 
	
	private static final String LINKEDIN_POSITION_SQL = " SELECT personData.userId, position.company_name AS name, position.title AS degree, 'position' AS linkedInDataType \n" +
		" FROM 		linkedin_position position \n" +   
		" INNER 	JOIN (SELECT u.id AS userId, (SELECT MAX(person.id) \n" +   
		" FROM 		linkedin_person person \n" +   
		" WHERE 	person.deleted = false AND person.user_id = u.id ) AS personId \n" +  
		" FROM 		user u WHERE 1=1 <userWhereClause>) AS personData \n" +  
		" ON 		personData.personId = position.linkedin_person_id \n" +   
		" WHERE 	position.deleted = false \n";

	static final String COMPLETED_WORK_LAST_TWELVE_MONTHS_SQL = "SELECT work_resource.user_id AS userId, " +
		" work.title, work.desired_skills AS skills, work.description, work.company_id companyId \n" +
		" FROM 		work\n" +
		" INNER 	JOIN work_resource  \n" +
		" ON 		work_resource.work_id = work.id \n" +
		" INNER 	JOIN user u \n" +
		" ON 		u.id = work_resource.user_id \n" +
		" WHERE 	work.type = 'W' AND work.deleted = false " +
		" AND 		work.work_status_type_code = 'paid' " +
		" AND		work.created_on >=  DATE_SUB(now(), INTERVAL 12 MONTH) " +
		" AND 		work_resource.work_resource_status_type_code = 'active' ";

	static final String GROUPS_APPLIED_LAST_SIX_MONTHS_SQL = "SELECT u.id AS userId, user_group.name,  user_group.description \n" +
		" FROM 		user_group \n" +
		" INNER 	JOIN user_user_group_association uag ON user_group.id = uag.user_group_id \n" +
		" INNER 	JOIN user u ON u.id = uag.user_id\n" +
		" WHERE 	uag.deleted = false " +
		" AND 		user_group.deleted = false " +
		" AND 		user_group.active_flag = true " +
		" AND 		uag.approval_status = 1 \n" +
		" AND 		COALESCE(uag.date_approved, uag.created_on) >= DATE_SUB(now(), INTERVAL 6 MONTH)";

	private static final String PAID_ASSIGNMENTS_GROUPED_BY_COMPANY_AND_USER = "SELECT count(whs.company_id) count, whs.company_id, whs.active_resource_user_id AS user_id FROM work_history_summary whs \n" +
		" INNER      JOIN time_dimension td ON td.id = whs.date_id \n" +
		" WHERE      whs.work_status_type_code ='paid' \n" +
		" AND        whs.active_resource_user_id IN (<userIds>) \n" +
		" AND        td.date >= DATE_SUB(now(), INTERVAL 6 MONTH) \n" + // last 6 months
		" GROUP BY   whs.active_resource_user_id, whs.company_id";

	// All contracts for which users have signed the *latest* contract version (you may have had a contract signed, but not the latest version).
	private static final String LATEST_CONTRACT_VERSION_SIGNED_FOR_USERS_SQL = "SELECT c.company_id, cv_1.contract_id, cvus.user_id, cvus.contract_version_id \n" +
		" FROM       contract_version_user_signature cvus \n" +
		" INNER      JOIN (SELECT cv.contract_id, MAX(cv.id) AS latest_contract_version_id FROM contract_version cv GROUP BY cv.contract_id) cv_1 \n" +
		" ON         cv_1.latest_contract_version_id=cvus.contract_version_id \n" +
		" INNER      JOIN contract c \n" +
		" ON         c.id=cv_1.contract_id \n" +
		" WHERE      c.active=1 \n" +
		" AND        cvus.signature=1 \n" +
		" AND        cvus.user_id IN (<userIds>) \n" +
		" GROUP BY   cv_1.contract_id, cvus.user_id";

	static final String UUIDS = "SELECT uuid from user where id in (:userIds)";

	// @formatter:on

	static String getPaidAssignmentsGroupedByCompanyAndUserQuery(List<Long> userIds) {
		if (CollectionUtils.isEmpty(userIds)) {
			return "";
		}

		return PAID_ASSIGNMENTS_GROUPED_BY_COMPANY_AND_USER.replaceAll("<userIds>", CollectionUtilities.join(userIds, ","));
	}

	static String getContractAssociationUserQuery(List<Long> userIds) {
		if (CollectionUtils.isEmpty(userIds)) {
			return "";
		}

		return LATEST_CONTRACT_VERSION_SIGNED_FOR_USERS_SQL.replaceAll("<userIds>", CollectionUtilities.join(userIds, ","));
	}

	static String getEntityAssociationQuery(String entityName, String whereClause) {
		return COMMON_ENTITY_ASSOCIATION_SQL.replaceAll("<entity>", entityName).concat(whereClause);
	}

	static String createInsuranceCoverageQuery(UserInsuranceType insuranceType, String whereClause) {
		String strId = Integer.toString(insuranceType.getValue());
		return INSURANCE_COVERAGE_ASSOCIATION_SQL.replaceAll("<insurance_id>",  strId).concat(whereClause);
	}

	static String getLinkedInQuery(String whereClause) {
		return LINKEDIN_EDUCATION_SQL.replaceAll("<userWhereClause>", whereClause)
			.concat(" UNION ALL \n ")
			.concat(LINKEDIN_POSITION_SQL.replaceAll("<userWhereClause>", whereClause));
	}

	static String getSharedGroupsQuery(String whereClause) {
		return UserGroupNetworkAssociationDAOImpl.getSharedGroupsByMemberIdSQLBuilder()
			.addJoin("INNER JOIN user u on uuga.user_id = u.id")
			.addColumn("DISTINCT na.network_id, na.user_group_id, uuga.user_id")
			.build()
			.concat(whereClause);
	}
}
