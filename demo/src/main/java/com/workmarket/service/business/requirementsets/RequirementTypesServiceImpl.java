package com.workmarket.service.business.requirementsets;

import com.workmarket.dao.requirement.RequirementTypeDAO;
import com.workmarket.domains.model.requirementset.RequirementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequirementTypesServiceImpl implements RequirementTypesService {
	@Autowired private RequirementTypeDAO dao;

	@Override
	public List<RequirementType> findAll() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
		return dao.findAll();
	}
}
