package com.workmarket.service.search;

import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.data.solr.repository.WorkSearchableFields;

public class SearchResultConstants {

	/**
	 * These are the "tagged" facets within specific categories. All of them
	 * only return the name of the facet, the type of facet, the ID, and if it
	 * was marked active for a facet or not.
	 */
	public static final String[] STATIC_FACET_FIELD_NAMES = {
		UserSearchableFields.STATE_LICENSE_IDS.getName(), UserSearchableFields.CERTIFICATION_IDS.getName(),
		UserSearchableFields.COMPANY_ASSESSMENT_IDS.getName(), UserSearchableFields.COMPANY_GROUP_IDS.getName(), UserSearchableFields.SHARED_GROUP_IDS.getName(),
		UserSearchableFields.INDUSTRIES_ID.getName(), UserSearchableFields.VERIFICATION_IDS.getName(),
		UserSearchableFields.COMPANY_TYPE.getName(), UserSearchableFields.INSURANCE_IDS.getName(),
		UserSearchableFields.COUNTRY.getName(), UserSearchableFields.HAS_AVATAR.getName(),
		UserSearchableFields.COMPANY_ID.getName(),

		WorkSearchableFields.BUYER_USER_ID.getName(), WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName(),
		WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION.getName(), WorkSearchableFields.BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION.getName(),
		WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName(), WorkSearchableFields.EXTERNAL_UNIQUE_IDS.getName(),
		WorkSearchableFields.APPLICANT_IDS.getName()
	};
}
