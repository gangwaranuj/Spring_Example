package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.thrift.core.ValidationException;

import java.io.IOException;

public interface CompanyPublicProfileService {
	CompanyProfileDTO get(String companyNumber);
}
