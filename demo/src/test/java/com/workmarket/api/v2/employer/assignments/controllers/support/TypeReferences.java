package com.workmarket.api.v2.employer.assignments.controllers.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.employer.settings.models.ACHBankAccountDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyProfileDTO;
import com.workmarket.api.v2.employer.settings.models.CompanyWorkersDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentResponseDTO;
import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.api.v2.employer.settings.models.UserDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingDTO;
import com.workmarket.api.v2.employer.uploads.models.MappingsDTO;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;

import java.util.List;
import java.util.Map;

public class TypeReferences {
	public static final TypeReference<ApiV2Response<TemplateDTO>> templateType = new TypeReference<ApiV2Response<TemplateDTO>>() {};
	public static final TypeReference<ApiV2Response<AssignmentDTO>> assignmentType = new TypeReference<ApiV2Response<AssignmentDTO>>() {};
	public static final TypeReference<ApiV2Response<ScheduleDTO>> scheduleType = new TypeReference<ApiV2Response<ScheduleDTO>>() {};
	public static final TypeReference<ApiV2Response<PricingDTO>> pricingType = new TypeReference<ApiV2Response<PricingDTO>>() {};
	public static final TypeReference<ApiV2Response<LocationDTO>> locationType = new TypeReference<ApiV2Response<LocationDTO>>() {};
	public static final TypeReference<ApiV2Response<RoutingDTO>> routingType = new TypeReference<ApiV2Response<RoutingDTO>>() {};
	public static final TypeReference<ApiV2Response<CustomFieldGroupDTO>> customFieldGroupsType = new TypeReference<ApiV2Response<CustomFieldGroupDTO>>() {};
	public static final TypeReference<ApiV2Response<ShipmentGroupDTO>> shipmentsType = new TypeReference<ApiV2Response<ShipmentGroupDTO>>() {};
	public static final TypeReference<ApiV2Response<SurveyDTO>> surveyType = new TypeReference<ApiV2Response<SurveyDTO>>() {};
	public static final TypeReference<ApiV2Response<ConfigurationDTO>> configurationType = new TypeReference<ApiV2Response<ConfigurationDTO>>() {};
	public static final TypeReference<ApiV2Response<DocumentDTO>> documentType = new TypeReference<ApiV2Response<DocumentDTO>>() {};
	public static final TypeReference<ApiV2Response<DeliverablesGroupDTO>> deliverablesType = new TypeReference<ApiV2Response<DeliverablesGroupDTO>>() {};
	public static final TypeReference<ApiV2Response<Map<String, String>>> mapType = new TypeReference<ApiV2Response<Map<String, String>>>() {};
	public static final TypeReference<ApiV2Response<ApiBaseError>> errorType = new TypeReference<ApiV2Response<ApiBaseError>>() {};
	public static final TypeReference<ApiV2Response<List<LocationDTO>>> locationsListType = new TypeReference<ApiV2Response<List<LocationDTO>>>() {};

	// Company Onboarding:
	public static final TypeReference<ApiV2Response<CompanyProfileDTO>> companyProfileType = new TypeReference<ApiV2Response<CompanyProfileDTO>>() {};
	public static final TypeReference<ApiV2Response<CompanyWorkersDTO>> companyWorkersType = new TypeReference<ApiV2Response<CompanyWorkersDTO>>() {};
	public static final TypeReference<ApiV2Response<ACHBankAccountDTO>> bankAccountType = new TypeReference<ApiV2Response<ACHBankAccountDTO>>() {};
	public static final TypeReference<ApiV2Response<List<String>>> listType = new TypeReference<ApiV2Response<List<String>>>() {};
	public static final TypeReference<ApiV2Response<TaxInfoDTO>> taxEntityType = new TypeReference<ApiV2Response<TaxInfoDTO>>() {};
	public static final TypeReference<ApiV2Response<SettingsCompletenessDTO>> settingsCompletenessType = new TypeReference<ApiV2Response<SettingsCompletenessDTO>>() {};
	public static final TypeReference<ApiV2Response<CreditCardPaymentResponseDTO>> paymentResponseType = new TypeReference<ApiV2Response<CreditCardPaymentResponseDTO>>() {};
	public static final TypeReference<ApiV2Response<UserDTO.Builder>> userResponseType = new TypeReference<ApiV2Response<UserDTO.Builder>>() {};

	// Bulk Assignment Upload
	public static final TypeReference<ApiV2Response<MappingsDTO>> mappingsType = new TypeReference<ApiV2Response<MappingsDTO>>() {};
	public static final TypeReference<ApiV2Response<MappingDTO>> mappingType = new TypeReference<ApiV2Response<MappingDTO>>() {};
	public static final TypeReference<ApiV2Response<List<ApiBaseError>>> validationErrorListType = new TypeReference<ApiV2Response<List<ApiBaseError>>>() {};
	public static final TypeReference<ApiV2Response<SettingsDTO>> settingsType = new TypeReference<ApiV2Response<SettingsDTO>>() {};
}

