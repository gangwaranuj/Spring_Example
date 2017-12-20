package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.assignments.services.UseCaseFactory;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

import java.io.IOException;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

	@Autowired UseCaseFactory useCaseFactory;
	@Autowired private UserGroupService userGroupService;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;

	@Override
	public CompanyProfileDTO saveOrUpdate(CompanyProfileDTO companyProfileDTO) throws ValidationException,
		InvalidAclRoleException, IOException, HostServiceException, AssetTransformationException {
		return useCaseFactory
			.getUseCase(CreateCompanyProfileUseCase.class, companyProfileDTO)
			.execute()
			.handleExceptions()
			.andReturn();
	}

	@Override
	public CompanyProfileDTO get() {
		return useCaseFactory
			.getUseCase(GetCompanyProfileUseCase.class)
			.execute()
			.andReturn();
	}

	@Override
	public CompanyProfileDTO get(String companyNumber) {
		return useCaseFactory
			.getUseCase(GetCompanyProfileUseCase.class, companyNumber)
			.execute()
			.andReturn();
	}

	public void follow(String companyNumber) {
		Company company = companyService.findCompanyByNumber(companyNumber);
		UserGroup companyFollowers = getFollowers(company.getId(), company.getCreatorId());
		userGroupService.addUsersToGroup(Arrays.asList(authenticationService.getCurrentUser().getId()), companyFollowers.getId(), company.getCreatorId());
	}

	public void unfollow(String companyNumber) {
		Company company = companyService.findCompanyByNumber(companyNumber);
		UserGroup companyFollowers = getFollowers(company.getId(), company.getCreatorId());
		userGroupService.removeAssociation(companyFollowers.getId(), authenticationService.getCurrentUser().getId());
	}


	public UserGroup getFollowers(Long companyId, Long actorId) {
		return userGroupService.findOrCreateCompanyGroup(companyId, Constants.MY_COMPANY_FOLLOWERS, userService.findUserById(actorId), Constants.MY_COMPANY_FOLLOWERS);
	}
}
