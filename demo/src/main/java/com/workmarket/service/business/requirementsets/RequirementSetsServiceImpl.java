package com.workmarket.service.business.requirementsets;

import com.google.common.collect.ImmutableList;
import com.workmarket.dao.requirement.AbstractRequirementDAO;
import com.workmarket.dao.requirement.RequirementSetDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.ProjectionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RequirementSetsServiceImpl implements RequirementSetsService {
	private static final Logger logger = LoggerFactory.getLogger(RequirementSetsServiceImpl.class);

	@Autowired private RequirementSetDAO dao;
	@Autowired private AuthenticationService auth;
	@Autowired private AbstractRequirementDAO abstractRequirementDAO;

	@Override
	public List<RequirementSet> findAll() {
		return dao.findAllBy(
			"company", auth.getCurrentUser().getCompany(),
			"userGroup", null);
	}

	@Override
	public List<RequirementSet> findAllActive() {
		return dao.findAllBy(
			"company", auth.getCurrentUser().getCompany(),
			"active", true
		);
	}

	@Override
	public RequirementSet find(Long id) {
		return dao.findBy(
			"id",      id,
			"company", auth.getCurrentUser().getCompany()
		);
	}

	@Override
	public void update(RequirementSet requirementSet) {
		logger.info("updating requirementSetId={} for companyId={} of size={}", requirementSet.getId(), requirementSet.getCompany().getId(), requirementSet.getRequirements().size());
		dao.merge(requirementSet);
	}

	@Override
	public void save(RequirementSet requirementSet) {
		requirementSet.setCompany(auth.getCurrentUser().getCompany());
		logger.info("creating requirementSet with name={} for companyId={} of size={}", requirementSet.getName(), requirementSet.getCompany().getId(), requirementSet.getRequirements().size());
		dao.save(requirementSet);
	}

	@Override
	public void destroy(Long id) {
		Company company = auth.getCurrentUser().getCompany();
		logger.info("deleting requirementSetId={} for companyId={}", id, company.getId());
		
		dao.delete(dao.findBy(
			"id",      id,
			"company", company
		));
	}

	@Override
	public int getMandatoryRequirementCountByWorkId(Long workId) {
		return abstractRequirementDAO.getMandatoryRequirementCountByWorkId(workId);
	}

	@Override
	public ImmutableList<Map> getProjectedRequirementSets(String[] fields) throws Exception {
		return ImmutableList.copyOf(ProjectionUtilities.projectAsArray(fields, this.findAllActive()));
	}
}
