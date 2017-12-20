package com.workmarket.domains.onboarding.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.Gender;
import com.workmarket.domains.model.directory.ContactContextType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ianha on 8/27/14
 */
public class OnboardProfilePropertyMapCreator {
	private WorkerOnboardingDTO dto;

	public OnboardProfilePropertyMapCreator(WorkerOnboardingDTO dto) {
		this.dto = dto;
	}

	public OnboardProfilePropertyMap profileAddressPropertiesMap() {
		OnboardProfilePropertyMap result = new OnboardProfilePropertyMap();

		if (StringUtils.isNotBlank(dto.getAddress1())) {
			result.put(OnboardProfilePropertyMap.ADDRESS1, dto.getAddress1());
		}

		if (StringUtils.isNotBlank(dto.getAddress2())) {
			result.put(OnboardProfilePropertyMap.ADDRESS2, dto.getAddress2());
		}

		if (StringUtils.isNotBlank(dto.getCity())) {
			result.put(OnboardProfilePropertyMap.CITY, dto.getCity());
		}

		if (StringUtils.isNotBlank(dto.getStateShortName())) {
			result.put(OnboardProfilePropertyMap.STATE, dto.getStateShortName());
		}

		if (StringUtils.isNotBlank(dto.getPostalCode())) {
			result.put(OnboardProfilePropertyMap.POSTAL_CODE, dto.getPostalCode());
		}

		if (StringUtils.isNotBlank(dto.getCountryIso())) {
			result.put(OnboardProfilePropertyMap.COUNTRY, dto.getCountryIso());
		}

		if (StringUtils.isNotBlank(dto.getLatitude())) {
			result.put(OnboardProfilePropertyMap.LATITUDE, dto.getLatitude());
		}

		if (StringUtils.isNotBlank(dto.getLongitude())) {
			result.put(OnboardProfilePropertyMap.LONGITUDE, dto.getLongitude());
		}

		if (MapUtils.isNotEmpty(result)) {
			result.put(OnboardProfilePropertyMap.ADDRESS_TYPE, "profile");
		}

		return result;
	}

	public OnboardProfilePropertyMap userPropertiesMap() {
		OnboardProfilePropertyMap result = new OnboardProfilePropertyMap();

		if (StringUtils.isNotBlank(dto.getFirstName())) {
			result.put(OnboardProfilePropertyMap.FIRST_NAME, dto.getFirstName());
		}

		if (StringUtils.isNotBlank(dto.getLastName())) {
			result.put(OnboardProfilePropertyMap.LAST_NAME, dto.getLastName());
		}

		if (StringUtils.isNotBlank(dto.getSecondaryEmail())) {
			result.put(OnboardProfilePropertyMap.SECONDARY_EMAIL, dto.getSecondaryEmail());
		}

		return result;
	}

	public OnboardProfilePropertyMap companyPropertiesMap() {
		OnboardProfilePropertyMap result = new OnboardProfilePropertyMap();

		if (dto.isIndividual() != null) {
			result.put(OnboardProfilePropertyMap.OPERATING_AS_IND_FLAG, dto.isIndividual().toString());
		}

		if (StringUtils.isNotBlank(dto.getCompanyName())) {
			result.put(OnboardProfilePropertyMap.COMPANY_NAME, dto.getCompanyName());
		}

		if (StringUtils.isNotBlank(dto.getCompanyWebsite())) {
			result.put(OnboardProfilePropertyMap.COMPANY_WEBSITE, dto.getCompanyWebsite());
		}

		if (dto.getCompanyEmployees() != null) {
			SimpleValueDTO simpleDTO = getFirstChecked(dto.getCompanyEmployees());

			if (simpleDTO != null) {
				result.put(OnboardProfilePropertyMap.COMPANY_NUM_WORKERS_ENUM, simpleDTO.getValue());
			}
		}

		if (dto.getCompanyYearFounded() != null) {
			result.put(OnboardProfilePropertyMap.COMPANY_YEAR_FOUNDED, dto.getCompanyYearFounded().toString());
		}

		if (StringUtils.isNotBlank(dto.getCompanyOverview())) {
			result.put(OnboardProfilePropertyMap.COMPANY_OVERVIEW, dto.getCompanyOverview());
		}

		return result;
	}

	public boolean hasPhoneInfo(String type) {
		return CollectionUtils.isNotEmpty(dto.getPhones());
	}

	public OnboardProfilePropertyMap profilePhoneCodePropertiesMap(boolean overwriteMissingPhoneNumbers) {
		OnboardProfilePropertyMap result = new OnboardProfilePropertyMap();

		PhoneInfoDTO phone = getPhoneInfoByType(ContactContextType.MOBILE.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.MOBILE_CODE, phone.getCode());
		}

		phone = getPhoneInfoByType(ContactContextType.SMS.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.SMS_CODE, phone.getCode());
		}

		phone = getPhoneInfoByType(ContactContextType.WORK.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.WORK_CODE, phone.getCode());
		}

		return result;
	}

	public OnboardProfilePropertyMap profilePropertiesMap(boolean overwriteMissingPhoneNumbers) {
		OnboardProfilePropertyMap result = new OnboardProfilePropertyMap();

		PhoneInfoDTO phone = getPhoneInfoByType(ContactContextType.MOBILE.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.MOBILE_PHONE, phone.getNumber());
		}

		phone = getPhoneInfoByType(ContactContextType.SMS.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.SMS_PHONE, phone.getNumber());
		}

		phone = getPhoneInfoByType(ContactContextType.WORK.toString(), overwriteMissingPhoneNumbers);
		if (phone != null) {
			result.put(OnboardProfilePropertyMap.WORK_PHONE, phone.getNumber());
		}

		if (dto.getJobTitle() != null && StringUtils.isNotBlank(dto.getJobTitle().getName())) {
			result.put(OnboardProfilePropertyMap.JOB_TITLE, dto.getJobTitle().getName());
		}

		if (StringUtils.isNotBlank(dto.getOverview())) {
			result.put(OnboardProfilePropertyMap.OVERVIEW, dto.getOverview());
		}

		if (dto.getYearsOfExperience() != null) {
			SimpleValueDTO simpleDTO = getFirstChecked(dto.getYearsOfExperience());

			if (simpleDTO != null) {
				result.put(OnboardProfilePropertyMap.YEARS_OF_EXPERIENCE, simpleDTO.getValue());
			}
		}

		if (StringUtils.isNotBlank(dto.getGender())) {
			Gender gender = Gender.getEnumFromCode(dto.getGender());
			if (gender != null) {
				result.put(OnboardProfilePropertyMap.GENDER, gender.getCode());
			}
		}

		if (dto.getMaxTravelDistance() != null) {
			result.put(OnboardProfilePropertyMap.MAX_TRAVEL_DISTANCE, dto.getMaxTravelDistance().toString());
		}

		if (BooleanUtils.isTrue(dto.isVideoWatched())) {
			result.put(OnboardProfilePropertyMap.VIDEO_WATCHED_ON, Calendar.getInstance().getTime().toString());
		}

		return result;
	}

	private SimpleValueDTO getFirstChecked(List<SimpleValueDTO> list) {
		return Iterables.find(list, new Predicate<SimpleValueDTO>() {
			@Override
			public boolean apply(SimpleValueDTO dto) {
				return dto.isChecked();
			}
		}, null);
	}

	public PhoneInfoDTO getPhoneInfoByType(String type, boolean overwriteMissingPhoneNumbers) {
		if (dto.getPhones() == null) {
			return null;
		}

		final String t = type;
		return Iterables.find(dto.getPhones(), new Predicate<PhoneInfoDTO>() {
			@Override
			public boolean apply(@Nullable PhoneInfoDTO phoneInfoDTO) {
				return phoneInfoDTO.getType().toLowerCase().equals(t.toLowerCase());
			}
		}, overwriteMissingPhoneNumbers ? new PhoneInfoDTO(ContactContextType.valueOf(type.toUpperCase()).toString()) : null);
	}
}
