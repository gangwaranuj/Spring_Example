package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CompanyPublicProfileServiceImpl implements CompanyPublicProfileService {
	@Autowired UseCaseFactory useCaseFactory;

	@Override
	public CompanyProfileDTO get(String companyNumber) {
		return useCaseFactory
			.getUseCase(GetCompanyPublicProfileUseCase.class, companyNumber)
			.execute()
			.andReturn();
	}
}
