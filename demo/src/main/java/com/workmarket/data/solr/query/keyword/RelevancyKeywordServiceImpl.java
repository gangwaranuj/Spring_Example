package com.workmarket.data.solr.query.keyword;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.configuration.BoostConfiguration;
import com.workmarket.data.solr.repository.GroupSearchableFields;
import com.workmarket.data.solr.repository.UserBoostFields;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.model.PeopleSearchTransientData;
import com.workmarket.search.model.SearchUser;
import com.workmarket.search.request.user.Constants;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.request.user.PeopleSearchSortByType;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.workmarket.utility.SearchUtilities.joinWithOR;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class RelevancyKeywordServiceImpl implements RelevancyKeywordService {

	private static final Log logger = LogFactory.getLog(RelevancyKeywordServiceImpl.class);

	@Autowired private BoostConfiguration boostConfig;
	@Autowired private StopWordFilterService stopwords;

	@Override
	public String createRelevancyString(SearchUser user, PeopleSearchTransientData data) throws SearchException {
		if (data.isEnhancedRelevancy()) {
			List<String> boostFields = createBoostFieldList(user);
			return StringUtils.join(boostFields, " ");
		}
		return createBoostString(user);
	}

	private List<String> createBoostFieldList(SearchUser user) throws SearchException {
		List<String> boostFields = newArrayListWithExpectedSize(BoostConfiguration.getBoostFields().size());
		for (UserBoostFields searchColumnBoost : BoostConfiguration.getBoostFields()) {
			String boostString = createBoostString(searchColumnBoost, user);
			boostFields.add(boostString);
		}
		return boostFields;
	}

	private String createBoostString(SearchUser currentUser) {
		List<String> boostFields = Lists.newArrayList();
		boostFields.add("(" + UserSearchableFields.LANE0_COMPANY_IDS.getName() + ":" + currentUser.getCompanyId() + ")^0.25");
		boostFields.add("(" + UserSearchableFields.LANE1_COMPANY_IDS.getName() + ":" + currentUser.getCompanyId() + ")^2.0");
		boostFields.add("(" + UserSearchableFields.LANE2_COMPANY_IDS.getName() + ":" + currentUser.getCompanyId() + ")^1.5");
		boostFields.add("(" + UserSearchableFields.LANE3_COMPANY_IDS.getName() + ":" + currentUser.getCompanyId() + ")^1.0");
		boostFields.add("( -lane0CompanyIds:(" + currentUser.getCompanyId() + ") AND -lane1CompanyIds:(" + currentUser.getCompanyId() + ") AND -lane2CompanyIds:(" + currentUser.getCompanyId()
				+ ") AND -lane3CompanyIds:(" + currentUser.getCompanyId() + ") AND +lane4Active:true )^0.5");

		List<Long> industries = currentUser.getIndustries();
		if (CollectionUtils.isNotEmpty(industries)) {
			boostFields.add("(" + UserSearchableFields.INDUSTRIES_ID.getName() + ":(" + StringUtils.join(industries, ' ') + ")^5.0)");
		}
		return " (" + joinWithOR(boostFields) + ") ";
	}

	private String createBoostString(UserBoostFields searchColumnBoost, SearchUser currentUser) throws SearchException {
		// recip(x,m,a,b) = a/(m*x+b)
		switch (searchColumnBoost) {
			case PAID_ASSIGNMENTS_COUNT:
				return searchColumnBoost.getName() + "^" + boostConfig.getPaidAssignmentsBoost();
			case SATISFACTION_RATE:
				return searchColumnBoost.getName() + "^" + boostConfig.getSatisfactionRate();
			case ON_TIME_PERCENTAGE:
				return searchColumnBoost.getName() + "^" + boostConfig.getOnTimePercentageBoost();
			case LATE_LABEL_COUNT:
				return "recip(" + searchColumnBoost.getName() + ",10,100,1)^" + boostConfig.getLateLabelCountBoost();
			case CANCELLED_LABEL_COUNT:
				return "recip(" + searchColumnBoost.getName() + ",10,100,1)^" + boostConfig.getCancelledLabelCountBoost();
			case ABANDONED_LABEL_COUNT:
				return "recip(" + searchColumnBoost.getName() + ",10,100,1)^" + boostConfig.getAbandonedLabelCountBoost();
			case COMPLETED_ON_TIME_LABEL_COUNT:
				return searchColumnBoost.getName() + "^" + boostConfig.getCompletedOnTimeLabelCountBoost();
			case AVERAGE_STAR_RATING:
				return searchColumnBoost.getName() + "^" + boostConfig.getAverageStarRatingBoost();
			case REPEATED_CLIENTS_COUNT:
				return searchColumnBoost.getName() + "^" + boostConfig.getRepeatedClientsBoost();
			case PASSED_BACKGROUND_CHECK:
				return String.format("query({!v='%s:true'})^%s", searchColumnBoost.getName(), boostConfig.getBackgroundCheckBoost());
			case PASSED_DRUG_TEST:
				return String.format("query({!v='%s:true'})^%s", searchColumnBoost.getName(), boostConfig.getDrugTestBoost());
			case SCREEENING_STATUS:
				return searchColumnBoost.getName() + "^" + boostConfig.getScreeningStatusBoost();
			case WORK_COMPLETED_FOR_COMPANIES:
				return String.format("exists(query({!v='%s:%s'}))^%s", searchColumnBoost.getName(), currentUser.getCompanyId(), boostConfig.getCompletedWorkForCompaniesBoost());
			case DISTINCT_COMPANY_BLOCKS_COUNT:
				return "recip(" + searchColumnBoost.getName() + ",10,1000,1)^" + boostConfig.getBlocksCountBoost();
			case HAS_AVATAR:
				return String.format("if(%s,2,0)^%s", searchColumnBoost.getName(), boostConfig.getProfilePictureBoost());

			case LAST_ASSIGNED_WORK_DATE:
				return "recip(ms(NOW," + searchColumnBoost.getName() + "),3.16e-11,1,1)^" + boostConfig.getLastAssignedWorkDateBoost();
			default: {
				logger.error("UNSUPPORTED search boost " + searchColumnBoost.getName() + " found.  Please update the "
						+ "business rules.  Failing search");
				throw new SearchException();
			}
		}
	}

	@Override
	public String createAssignmentRelevancyString(String skills) {
		if (StringUtils.isEmpty(skills)) {
			return StringUtils.EMPTY;
		}
		List<String> filteredSkills = stopwords.filterStopWords(skills);
		String allWordsEncoded = SearchUtilities.extractKeywords(Joiner.on(" ").skipNulls().join(filteredSkills), 100);

		return UserSearchableFields.WORK_SKILLS_MATCHING.getName() + ":(" + allWordsEncoded + ")^" + boostConfig.getSkillsAssignmentBoost();
	}

	@Override
	public String createGroupInNetworkRelevanceString(List<Long> networkIds) {
		if (isNotEmpty(networkIds)) {
			List<String> boostQueries = Lists.newArrayListWithExpectedSize(networkIds.size());
			for (Long networkId : networkIds) {
				boostQueries.add(String.format("exists(query({!v='%s:%s'}))^%s", GroupSearchableFields.NETWORK_IDS.getName(), networkId, 5));
			}
			if (isNotEmpty(boostQueries)) {
				return StringUtils.join(boostQueries, " ");
			}
		}
		return StringUtils.EMPTY;
	}

	@Override
	public boolean isRequestSortedByRelevancy(PeopleSearchRequest request) {
		// check if we have pagination, if not, it's sorted by relevance
		if (!request.isSetPaginationRequest() && Constants.DEFAULT_SORT == PeopleSearchSortByType.RELEVANCY) {
			return true;
		}
		return request.isSetPaginationRequest() && PeopleSearchSortByType.RELEVANCY.equals(request.getPaginationRequest().getSortBy());
	}

}
