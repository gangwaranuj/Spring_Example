package com.workmarket.service.business;

import com.workmarket.dao.requirement.ResourceTypeDAO;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {
	@Autowired private ResourceTypeDAO dao;

	@Override
	public List<ResourceTypeRequirable> findAll() {
		return dao.findAll();
	}
}
