package com.workmarket.service.business.requirementsets;

import com.workmarket.dao.requirement.AbstractRequirementDAO;
import com.workmarket.dao.requirement.RequirementSetDAO;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbstractRequirementsServiceImpl implements AbstractRequirementsService {
	@Autowired private AbstractRequirementDAO dao;
	@Autowired private RequirementSetDAO requirementSetDAO;
	@Autowired private AuthenticationService auth;

	@Override
	public List<AbstractRequirement> findAllByRequirementSetId(long requirementSetId) {
		RequirementSet requirementSet = requirementSetDAO.findBy(
			"id", requirementSetId,
			"company", auth.getCurrentUser().getCompany()
		);

		return dao.findAllBy("requirementSet", requirementSet);
	}
}
