package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.SkillDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.dto.UploadDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.exception.authentication.InvalidAclRoleException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.thrift.core.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
public class CreateCompanyProfileUseCase
	extends AbstractSettingsUseCase<CreateCompanyProfileUseCase, CompanyProfileDTO> {

	@Autowired private CompanyService companyService;
	@Autowired AssetManagementService assetService;
	@Autowired UploadService uploadService;

	private CompanyPreference companyPreference;
	private List<LocationDTO> companyLocations;
	private List<SkillDTO> companySkills;

	public CreateCompanyProfileUseCase(CompanyProfileDTO companyProfileDTO) {
		this.companyProfileDTO = companyProfileDTO;
	}

	@Override
	protected CreateCompanyProfileUseCase me() {
		return this;
	}

	@Override
	protected CreateCompanyProfileUseCase handleExceptions() throws ValidationException, InvalidAclRoleException,
		HostServiceException, IOException, AssetTransformationException {
		handleValidationException();
		handleInvalidAclRoleException();
		handleHostServiceException();
		handleIOException();
		handleAssetTransformationException();
		return this;
	}

	@Override
	protected void failFast() {
		Assert.notNull(companyProfileDTOBuilder);
	}

	@Override
	protected void init() {
		getUser();
		getCompany();
		getCompanyPreference();
	}

	@Override
	protected void prepare() {
		copyCompanyProfileDTO();
		validateCompanyProfile();
		validateCompanyAddress();
	}

	@Override
	protected void process() throws BeansException {
		loadCompany();
		loadCompanyLocations();
		loadCompanySkills();
		loadCompanyPreferences();
	}

	@Override
	protected void save() throws ValidationException, InvalidAclRoleException, HostServiceException, IOException,
		AssetTransformationException {
		saveCompanyProfile();
		saveCompanyLogo();
		saveWorkInviteSentTo();
		saveCompanyPreferences();
		saveCompanyLocations();
		saveCompanySkills();
	}

	@Override
	protected void finish() {
		updateListInVendorSearch();
	}

	@Override
	public CompanyProfileDTO andReturn() {
		return companyProfileDTOBuilder.build();
	}

	private void loadCompanyLocations() {
		companyLocations = companyProfileDTO.getLocationsServiced();
	}

	private void loadCompanySkills() {
		companySkills = companyProfileDTO.getSkills();
	}

	private void getCompanyPreference() {
		companyPreference = companyService.getCompanyPreference(company.getId());
	}

	private void loadCompanyPreferences() {
		this.companyPreference.setBackgroundCheck(companyProfileDTO.getBackgroundCheck() != null && companyProfileDTO.getBackgroundCheck());
		this.companyPreference.setDrugTest(companyProfileDTO.getDrugTest() != null && companyProfileDTO.getDrugTest());
	}

	private void saveCompanyPreferences() {
		companyService.updateCompanyPreference(companyPreference);
	}

	private void saveCompanyLocations() {
		List<Long> locationIds = Lists.newArrayList();
		for(LocationDTO locationDTO : companyLocations) {
			if(locationDTO.getId() != 0) {
				locationIds.add(locationDTO.getId());
			}
			else {
				Location location = companyService.saveCompanyLocation(company.getId(), locationDTO);
				locationIds.add(location.getId());
			}
		}
		companyService.setCompanyLocations(locationIds, company.getId());
	}

	private void saveCompanySkills() {
		List<Long> skillIds = Lists.newArrayList();
		for(SkillDTO skill : companySkills) {
			if(skill.getId() != 0) {
				skillIds.add(skill.getId());
			}
			else {
				Skill savedSkill = companyService.saveCompanySkill(skill.getName());
				skillIds.add(savedSkill.getId());
			}
		}
		companyService.setCompanySkills(skillIds, company.getId());
	}

	private void saveCompanyLogo() throws IOException, HostServiceException, AssetTransformationException {

		if(!StringUtils.isEmpty(companyProfileDTO.getAvatarUUID())) {
			Upload upload = uploadService.findUploadByUUID(companyProfileDTO.getAvatarUUID());
			if (upload != null) {

				UploadDTO dto = new UploadDTO();
				dto.setUploadId(upload.getId());
				dto.setUploadUuid(upload.getUUID());
				dto.setMimeType(upload.getMimeType());
				dto.setDescription("company management avatar");
				dto.setAddToCompanyLibrary(true);
				dto.setAssociationType("avatar");
				dto.setSmallTransformation(true);
				dto.setLargeTransformation(true);
				dto.setName(upload.getFilename());
				assetService.addUploadToCompany(dto, company.getId());
			}
		}
	}

	private void updateListInVendorSearch() {
		companyService.updateListInVendorSearch(company.getId(), companyProfileDTO.getInVendorSearch());
	}

	protected void handleHostServiceException() throws HostServiceException {
		if (exception instanceof HostServiceException) {
			throw (HostServiceException) exception;
		}
	}

	protected void handleIOException() throws IOException {
		if (exception instanceof IOException) {
			throw (IOException) exception;
		}
	}

	protected void handleAssetTransformationException() throws AssetTransformationException {
		if (exception instanceof AssetTransformationException) {
			throw (AssetTransformationException) exception;
		}
	}
}
