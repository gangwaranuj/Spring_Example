package com.workmarket.service.business.requirementsets;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.workmarket.common.metric.MetricRegistryFacade;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.service.UserGroupRequirementSetService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.contract.ContractVersionUserSignaturePagination;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.model.requirementset.EligibilityUser;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.RequirementSetable;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.search.response.user.PeopleSearchResult;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.user.PeopleSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.on;

@Service
public class EligibilityServiceImpl implements EligibilityService {
	private static final Log logger = LogFactory.getLog(EligibilityServiceImpl.class);
	private static final String AGREEMENT_TYPE = "Agreement";

	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private WorkVendorInvitationDAO workVendorInvitationDAO;
	@Autowired private EligibilityVisitor visitor;
	@Autowired private PeopleSearchService peopleSearchService;
	@Autowired private ContractService contractService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private UserGroupRequirementSetService userGroupRequirementSetService;

	private MetricRegistryFacade metricRegistryFacade;

	@PostConstruct
	protected void init() {
		metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "eligibility_service");
	}

	@VisibleForTesting
	protected void setMetricRegistryFacade(final MetricRegistryFacade metricRegistryFacade) {
		this.metricRegistryFacade = metricRegistryFacade;
	}

	@Override
	public Eligibility getEligibilityFor(Long userId, com.workmarket.thrift.work.Work thriftWork) {
		Work work = workDAO.get(thriftWork.getId());
		User user = userDAO.get(userId);

		PeopleSearchResult peopleSearchResult = new PeopleSearchResult();
		EligibilityUser eligibilityUser = new EligibilityUser(user, peopleSearchResult);

		final Eligibility eligibility = getEligibilityFor(eligibilityUser, work);

		final List<Long> groupIds =
			workVendorInvitationDAO.getVendorInvitedByGroupIds(work.getId(), eligibilityUser.getCompany().getId());

		if (groupIds.isEmpty()) {
			return eligibility;
		}

		final Set<Criterion> criteria = eligibility.getCriteria();

		for (Long groupId : groupIds) {
			UserGroup userGroup = userGroupService.findGroupById(groupId);
			Eligibility groupEligibility = getEligibilityFor(eligibilityUser, userGroup);
			criteria.addAll(groupEligibility.getCriteria());
		}

		return makeEligibility(criteria);
	}

	@Override
	public Eligibility getEligibilityFor(Long userId, UserGroup userGroup) {
		User user = userDAO.get(userId);

		PeopleSearchResult peopleSearchResult = new PeopleSearchResult();
		EligibilityUser eligibilityUser = new EligibilityUser(user, peopleSearchResult);

		return getEligibilityFor(eligibilityUser, userGroup);
	}

	@Override
	public Eligibility getEligibilityFor(EligibilityUser eligibilityUser, RequirementSetable objWithRequirements) {
		Collection<RequirementSet> requirementSets = objWithRequirements.getRequirementSetCollection();
		Set<Criterion> criteria = Sets.newTreeSet();

		if (requirementSets.isEmpty()) {
			makeEligibility(criteria);
		}

		List<AbstractRequirement> abstractRequirements =
			flatten(extract(requirementSets, on(RequirementSet.class).getRequirements()));

		for (AbstractRequirement requirement : abstractRequirements) {
			Criterion criterion = new Criterion(eligibilityUser, objWithRequirements);
			Timer timer = metricRegistryFacade.timer(requirement.getHumanTypeName());
			final Timer.Context context = timer.time();

			try {
				requirement.accept(visitor, criterion);
			} finally {
				context.stop();
			}

			addCriteria(criteria, criterion);
		}

		return makeEligibility(criteria);
	}

	private void addCriteria(Set<Criterion> criteria, Criterion criterion) {
		if(!criterion.isAnyOf()) {
			criteria.add(criterion);
			return;
		}
		for (Criterion crit : criteria) {
			if (!crit.getTypeName().equals(criterion.getTypeName())) {
				continue;
			}
			if (crit.getName().contains(", or ")) {
				crit.setName(StringUtils.substringBefore(crit.getName(), ", or ") +
					", " + StringUtils.substringAfter(crit.getName(), ", or "));
			}
			crit.setName(crit.getName() + ", or " + criterion.getName());
			if (!crit.isMet()) {
				crit.setMet(criterion.isMet());
			}
			return;
		}
		criteria.add(criterion);
	}

	@Override
	public List<ContractVersion> getMissingContractVersions(Long groupId, ExtendedUserDetails user) {
		UserGroup userGroup = userGroupService.findGroupById(groupId);
		if (userGroup == null) {
			return null;
		}

		if (!userGroupRequirementSetService.userGroupHasAgreementRequirements(groupId)) {
			return Collections.EMPTY_LIST;
		}

		// Find most recent version of the required contracts
		List<Long> contractIds = userGroupRequirementSetService.findUserGroupRequiredAgreementIds(groupId);

		Set<ContractVersion> requiredContractVersions = Sets.newLinkedHashSet();
		for (Long contractId : contractIds) {
			requiredContractVersions.add(contractService.findMostRecentContractVersionByContractId(contractId));
		}

		// Find the user's signed versions
		Set<ContractVersion> signedContractVersions = Sets.newLinkedHashSet();
		Long userId = user.getId();
		for (ContractVersionUserSignature signature : contractService.findAllContractVersionsUserSignaturesByUserId(userId, new ContractVersionUserSignaturePagination(true)).getResults()) {
			signedContractVersions.add(signature.getContractVersion());
		}
		// Diff the sets
		requiredContractVersions.removeAll(signedContractVersions);

		return new ArrayList<>(requiredContractVersions);
	}

	@Override
	public boolean hasNonAgreementRequirements(Eligibility validation) {
		Set<Criterion> requirements = validation.getCriteria();
		for (Criterion criterion : requirements) {
			if (!AGREEMENT_TYPE.equals(criterion.getTypeName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Eligibility makeEligibility(Set<Criterion> criteria) {
		boolean eligible = true;
		for (Criterion criterion : criteria) {
			eligible = criterion.isMet();
			if (!eligible) {
				break;
			}
		}
		return new Eligibility(criteria, eligible);
	}

	// TODO: should we get eligibility for a user from Solr? Should eligibility be checked from database?
	@Deprecated
	public PeopleSearchResult getUserFromSolr(Long userId,  RequirementSetable objWithRequirements) {
		PeopleSearchRequest searchRequest = makePeopleSearchRequest();
		searchRequest.setLaneFilter(new HashSet<LaneType>());
		searchRequest.setUserId(userId);
		searchRequest.addToUserIds(userId);
		searchRequest.setPaginationRequest(new Pagination().setCursorPosition(0).setPageSize(1).setPageNumber(0));
		searchRequest.setSearchType(SearchType.PEOPLE_SEARCH_ELIGIBILITY);

		// We need to properly lane filter the company of the work, not the company of the logged
		// in user. For example, in the case of contractors, we need to make sure the current use
		// has the proper lane association with the company of the work, not the user's company.
		if (objWithRequirements instanceof Work) {
			Work work = (Work) objWithRequirements;
			Long companyId = work.getCompany().getId();
			searchRequest.setLaneFilterCompanyId(companyId);
		}

		try {
			PeopleSearchResponse response = peopleSearchService.searchPeople(searchRequest);

			if (CollectionUtils.isNotEmpty(response.getResults())) {
				return response.getResults().get(0);
			}

			return null;
		} catch (SearchException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public PeopleSearchRequest makePeopleSearchRequest() {
		return new PeopleSearchRequest();
	}
}
