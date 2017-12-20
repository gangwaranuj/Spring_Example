package com.workmarket.web.validators;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.integration.WebHookDTO;
import com.workmarket.service.business.dto.integration.WebHookHeaderDTO;
import com.workmarket.service.business.integration.hooks.webhook.WebHookIntegrationService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebHookValidatorTest {

	private static final String SOME_EVENT_TYPE = "workAccept";
	private static final Long SOME_COMPANY_ID = 999999999L;
	private static final Long SOME_WORK_CUSTOM_FIELD_GROUP_ID = 88888888L;
	private static final Long SOME_WORK_CUSTOM_FIELD_ID = 77777777L;

	@Mock WorkService workService;
	@Mock WebHookIntegrationService webHookIntegrationService;
	@Mock CustomFieldService customFieldService;
	@InjectMocks WebHookValidator webHookValidator = new WebHookValidator();

	@Captor ArgumentCaptor<Object[]> captor;

	WebHookDTO dto;
	Errors errors;

	AbstractWebHookClient abstractWebHookClient;
	Company company;
	Optional<AbstractWebHookClient> optional;

	WebHookHeaderDTO webHookHeaderDTO;
	List<WebHookHeaderDTO> webHookHeaderDTOs = Lists.newArrayList();

	WorkCustomFieldGroup workCustomFieldGroup;
	List<WorkCustomFieldGroup> workCustomFieldGroups = Lists.newArrayList();

	WorkCustomField workCustomField;
	List<WorkCustomField> workCustomFields = Lists.newArrayList();

	@Before
	public void setUp() throws Exception {
		dto = mock(WebHookDTO.class);
		when(dto.getBody()).thenReturn(SOME_EVENT_TYPE);
		when(dto.getIntegrationEventTypeCode()).thenReturn(SOME_EVENT_TYPE);

		webHookHeaderDTO = mock(WebHookHeaderDTO.class);
		when(webHookHeaderDTO.getValue()).thenReturn("YO");
		webHookHeaderDTOs.add(webHookHeaderDTO);
		when(dto.getHeaders()).thenReturn(webHookHeaderDTOs);

		errors = mock(Errors.class);

		company = mock(Company.class);
		when(company.getId()).thenReturn(SOME_COMPANY_ID);

		abstractWebHookClient = mock(AbstractWebHookClient.class);
		when(abstractWebHookClient.getCompany()).thenReturn(company);

		optional = mock(Optional.class);
		when(optional.isPresent()).thenReturn(true);
		when(optional.get()).thenReturn(abstractWebHookClient);
		when(webHookIntegrationService.findWebHookClientById(dto.getWebHookClientId())).thenReturn(optional);

		workCustomFieldGroup = mock(WorkCustomFieldGroup.class);
		when(workCustomFieldGroup.getId()).thenReturn(SOME_WORK_CUSTOM_FIELD_GROUP_ID);
		workCustomFieldGroups.add(workCustomFieldGroup);
		when(customFieldService.findWorkCustomFieldGroups(SOME_COMPANY_ID)).thenReturn(workCustomFieldGroups);

		workCustomField = mock(WorkCustomField.class);
		when(workCustomField.getId()).thenReturn(SOME_WORK_CUSTOM_FIELD_ID);
		workCustomFields.add(workCustomField);
		when(customFieldService.findAllFieldsForCustomFieldGroup(SOME_WORK_CUSTOM_FIELD_GROUP_ID)).thenReturn(workCustomFields);
	}

	@Test
	public void validate_noErrors() throws Exception {
		webHookValidator.validate(dto, errors);
		verify(errors, never()).reject(anyString());
		verify(errors, never()).reject(anyString(), any(Object[].class), anyString());
		verify(errors, never()).rejectValue(anyString(), anyString());
	}

	@Test
	public void validate_urlIsNull_rejectValue() throws Exception {
		when(dto.getUrl()).thenReturn(null);
		webHookValidator.validate(dto, errors);

		verify(errors).rejectValue(eq("url"), eq("NotNull"), captor.capture(), (String) isNull());

		Object[] errorArgs = captor.getValue();
		assertThat(errorArgs[0], Matchers.<Object>is("URL"));
	}

	@Test
	public void validate_headerIsEmpty_rejectValue() throws Exception {
		when(webHookHeaderDTO.getValue()).thenReturn("");
		webHookValidator.validate(dto, errors);
		verify(errors).rejectValue("headers", "mmw.integration.webhook.empty_header");
	}

	@Test
	public void validate_headerIsBlank_rejectValue() throws Exception {
		when(webHookHeaderDTO.getValue()).thenReturn("    ");
		webHookValidator.validate(dto, errors);
		verify(errors).rejectValue("headers", "mmw.integration.webhook.empty_header");
	}

	@Test
	public void validate_headerIsNull_rejectValue() throws Exception {
		when(webHookHeaderDTO.getValue()).thenReturn(null);
		webHookValidator.validate(dto, errors);
		verify(errors).rejectValue("headers", "mmw.integration.webhook.empty_header");
	}

	@Test
	public void validate_abstractWebHookClientIsNotPresent_reject() throws Exception {
		when(optional.isPresent()).thenReturn(false);
		webHookValidator.validate(dto, errors);
		verify(errors).reject("mmw.integration.webhook.invalid_client");
	}

	@Test
	public void validate_validWebHookGeneralField_noErrors() throws Exception {
		when(dto.getBody()).thenReturn("${assignment_id}");
		webHookValidator.validate(dto, errors);
		verify(errors, never()).reject(eq("mmw.integration.webhook.invalid_variable"), any(String[].class), eq(""));
	}

	@Test
	public void validate_validEventVariable_noErrors() throws Exception {
		when(dto.getBody()).thenReturn("${resource_id}");
		webHookValidator.validate(dto, errors);
		verify(errors, never()).reject(eq("mmw.integration.webhook.invalid_variable"), any(String[].class), eq(""));
	}

	@Test
	public void validate_validCustomField_noErrors() throws Exception {
		when(dto.getBody()).thenReturn("${custom_field_77777777}");
		webHookValidator.validate(dto, errors);
		verify(errors, never()).reject(eq("mmw.integration.webhook.invalid_variable"), any(String[].class), eq(""));
	}

	@Test
	public void validate_invalidVariable_reject() throws Exception {
		when(dto.getBody()).thenReturn("${SOMEGARBAGE}");
		webHookValidator.validate(dto, errors);
		verify(errors).reject(eq("mmw.integration.webhook.invalid_variable"), captor.capture(), eq(""));

		Object[] errorArgs = captor.getValue();
		assertThat(errorArgs[0], Matchers.<Object>is("SOMEGARBAGE"));

	}

}
