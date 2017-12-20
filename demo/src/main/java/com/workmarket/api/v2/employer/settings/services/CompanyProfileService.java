package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.thrift.core.ValidationException;

import java.io.IOException;

public interface CompanyProfileService {
	CompanyProfileDTO saveOrUpdate(CompanyProfileDTO builder) throws ValidationException, InvalidAclRoleException, IOException, HostServiceException, AssetTransformationException;
	CompanyProfileDTO get();
	CompanyProfileDTO get(String companyNumber);
	void follow(String companyNumber);
	void unfollow(String companyNumber);
	UserGroup getFollowers(Long companyId, Long actorId);
}
