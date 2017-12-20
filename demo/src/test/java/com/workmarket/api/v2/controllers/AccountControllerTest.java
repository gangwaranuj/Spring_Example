package com.workmarket.api.v2.controllers;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.workmarket.api.ApiBaseError;
import com.workmarket.api.ApiBaseHttpMessageConverter;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.ApiCreateCompanyRequestDTO;
import com.workmarket.api.v2.model.ApiCreateCompanyResponseDTO;
import com.workmarket.api.v2.model.validator.ApiCreateCompanyRequestDTOValidator;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.SalesforceLeadService;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.EncryptionService;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.web.validators.PasswordValidator;
import com.workmarket.web.validators.UserEmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest extends BaseApiControllerTest {

  private static final String V2_CONFIRM_ACCOUNT = "/v2/confirm_account/{encryptedId}";
  private static final String V2_RESEND_CONFIRMATION_EMAIL_USER_NUMBER = "/v2/resend_confirmation_email/{userNumber}";
  private static final String V2_EMPLOYER_CREATE_ACCOUNT = "/v2/employer/create-account";
  private static final String ENCRYPTED_ID = "CA74B864FE125F6BC4718120C6D23964AA06C8BB429C2F31AE152E42B95796AB";
  private static final String USER_NUMBER = "1234";
  private static final String BAD_REQUEST_MESSAGE = "bad-request-message";
  private static final String FORBIDDEN_MESSAGE = "forbidden-message";
  private static final String FAILURE_MESSAGE = "failure-message";
  private static final String SUCCESS_MESSAGE = "success-message";

  private static final TypeReference<ApiV2Response<String>> apiV2ResponseTypeString =
    new TypeReference<ApiV2Response<String>>() { };

  private static final TypeReference<ApiV2Response<ApiBaseError>> apiV2ResponseTypeError =
    new TypeReference<ApiV2Response<ApiBaseError>>() { };

  private static final TypeReference<ApiV2Response<ApiCreateCompanyResponseDTO>> apiV2ResponseTypeCreateCompany =
    new TypeReference<ApiV2Response<ApiCreateCompanyResponseDTO>>() { };

  @Mock
  private RegistrationService registrationService;
  @Mock
  private EncryptionService encryptionService;
  @Mock
  private FeatureEntitlementService featureEntitlementService;
  @Mock
  private LocaleService localeService;
  @Mock
  private MetricRegistry registry;
  @Mock
  private ApiCreateCompanyRequestDTOValidator apiCreateCompanyRequestDTOValidator;
  @Mock
  private UserEmailValidator userEmailValidator;
  @Mock
  private PasswordValidator passwordValidator;
  @Mock
  private SalesforceLeadService salesforceLeadService;

  @InjectMocks
  private AccountController controller = new AccountController();
  private String salesForceAuthToken = "salesForceAuthToken";

  @Before
  public void setup() throws Exception {
    super.setup(controller);
    when(registry.meter(any(String.class))).thenReturn(new Meter());
    controller.init();
    when(messageHelper.getMessage("users.resend_confirmation_email.user_not_found")).thenReturn(BAD_REQUEST_MESSAGE);
    when(messageHelper.getMessage("user.confirmation.resend.failure")).thenReturn(FAILURE_MESSAGE);
    when(messageHelper.getMessage("user.confirmation.resend.success")).thenReturn(SUCCESS_MESSAGE);
    when(messageHelper.getMessage("user.account.confirm.error_user_not_found")).thenReturn(BAD_REQUEST_MESSAGE);
    when(messageHelper.getMessage("user.account.confirm.error_user_invalid")).thenReturn(FORBIDDEN_MESSAGE);
    when(messageHelper.getMessage("user.account.confirm.error_email_not_found")).thenReturn(FORBIDDEN_MESSAGE);
    when(messageHelper.getMessage("user.account.confirm.success")).thenReturn(SUCCESS_MESSAGE);
  }

  @Test(expected = BindException.class)
	public void ifBindingResultHasErrorsThrowException() throws Exception {
		final BindException bindingResult = new BindException(new Object(), "test");
		bindingResult.addError(new ObjectError("test", "test error"));
		controller.postSignUpEmployer(null, null, bindingResult);
	}


  @Test
  public void confirmAccount_Success() throws Exception {
    when(encryptionService.decryptId(any(String.class))).thenReturn(0L);
    when(userService.findUserNumber(any(Long.class))).thenReturn(USER_NUMBER);
    when(registrationService.confirmAndApproveAccount(any(Long.class))).thenReturn(new User());

    final MvcResult result =
      mockMvc
        .perform(get(V2_CONFIRM_ACCOUNT, ENCRYPTED_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);
    assertEquals("expect response meta to have success message", SUCCESS_MESSAGE, responseMeta.get("message"));
  }


  @Test
  public void confirmAccount_ErrorUserNotFound() throws Exception {
    when(encryptionService.decryptId(any(String.class))).thenReturn(0L);
    when(userService.findUserNumber(any(Long.class))).thenReturn(null);

    final MvcResult result =
      mockMvc
        .perform(get(V2_CONFIRM_ACCOUNT, ENCRYPTED_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(400, responseMeta);
    assertEquals("expect response meta to have generic error message", BAD_REQUEST_MESSAGE, responseMeta.get("message"));
  }


  @Test
  public void confirmAccount_ErrorUserInvalid() throws Exception {
    when(encryptionService.decryptId(any(String.class))).thenReturn(0L);
    when(userService.findUserNumber(any(Long.class))).thenReturn("");

    final MvcResult result =
      mockMvc
        .perform(get(V2_CONFIRM_ACCOUNT, ENCRYPTED_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(403, responseMeta);
    assertEquals("expect response meta to have bad request message", FORBIDDEN_MESSAGE, responseMeta.get("message"));
  }


  @Test
  public void confirmAccount_ErrorEmailNotFound() throws Exception {
    when(encryptionService.decryptId(any(String.class))).thenReturn(0L);
    when(userService.findUserNumber(any(Long.class))).thenReturn(USER_NUMBER);
    when(registrationService.confirmAndApproveAccount(any(Long.class))).thenReturn(null);

    final MvcResult result =
      mockMvc
        .perform(get(V2_CONFIRM_ACCOUNT, ENCRYPTED_ID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(403, responseMeta);
    assertEquals("expect response meta to have generic error message", FORBIDDEN_MESSAGE, responseMeta.get("message"));
  }


  @Test
  public void shouldReturnBadRequest() throws Exception {
    when(userService.findUserId(any(String.class))).thenReturn(null);

    final MvcResult result =
        mockMvc
            .perform(get(V2_RESEND_CONFIRMATION_EMAIL_USER_NUMBER, USER_NUMBER).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(400, responseMeta);
    assertEquals("expect response meta to have bad request message", BAD_REQUEST_MESSAGE, responseMeta.get("message"));
  }

  @Test
  public void shouldReturnInternalServerErrorOnServiceException() throws Exception {
    when(userService.findUserId(any(String.class))).thenReturn(1L);
    doThrow(new Exception("uh-oh")).when(registrationService).sendRemindConfirmationEmail(any(Long.class));

    final MvcResult result =
        mockMvc
            .perform(get(V2_RESEND_CONFIRMATION_EMAIL_USER_NUMBER, USER_NUMBER).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(500, responseMeta);
    assertEquals("expect response meta to have generic error message", FAILURE_MESSAGE, responseMeta.get("message"));
  }

  @Test
  public void shouldReturn200OnSuccess() throws Exception {
    when(userService.findUserId(any(String.class))).thenReturn(1L);

    final MvcResult result =
        mockMvc
            .perform(get(V2_RESEND_CONFIRMATION_EMAIL_USER_NUMBER, USER_NUMBER).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);
    verify(registrationService, times(0)).sendRemindConfirmationWithPasswordResetEmail(any(Long.class));
    verify(registrationService).sendRemindConfirmationEmail(any(Long.class));
    assertEquals("expect response meta to have success message", SUCCESS_MESSAGE, responseMeta.get("message"));
  }

  @Test
  public void shouldCallResetPasswordIfFlagTrue() throws Exception {
    when(userService.findUserId(any(String.class))).thenReturn(1L);

    final MvcResult result =
        mockMvc
            .perform(get(V2_RESEND_CONFIRMATION_EMAIL_USER_NUMBER + "?resetPassword=true", USER_NUMBER).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    final ApiV2Response<String> response = expectApiV2Response(result, apiV2ResponseTypeString);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);
    verify(registrationService).sendRemindConfirmationWithPasswordResetEmail(any(Long.class));
    verify(registrationService, times(0)).sendRemindConfirmationEmail(any(Long.class));
  }

  @Test
  public void createAccount_usernameAlreadyTakenReturnsErrors() throws Exception {
    when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(false);

    ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
      .userEmail("jwald@workmarket.com")
      .password("1234xyzu")
      .build();

    final FilterProvider filters = new SimpleFilterProvider().addFilter(
      ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
      new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
    );

    String apiCreateCompanyRequestDTOJson = jackson.writer(filters).writeValueAsString(apiCreateCompanyRequestDTO);

    BindException bindException = new BindException(new Object(), "Obj");
    bindException.reject("register.emailuseerror");
    when(registrationService.registerNew(
      any(UserDTO.class),
      anyLong(),
      any(String.class),
      any(AddressDTO.class),
      any(ProfileDTO.class),
      eq(true),
      eq(true))).thenThrow(bindException);

    final MvcResult result =
        mockMvc
            .perform(post(V2_EMPLOYER_CREATE_ACCOUNT).content(apiCreateCompanyRequestDTOJson).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(apiCreateCompanyRequestDTOValidator, times(1)).validate(any(ApiCreateCompanyRequestDTO.class), any(BindingResult.class));

    final ApiV2Response<ApiBaseError> response = expectApiV2Response(result, apiV2ResponseTypeError);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(400, responseMeta);
    expectApiErrorCode(response.getResults(), "register.emailuseerror");
  }

  @Test
  public void createAccount_validInputWorksWithoutLocaleFeature() throws Exception {
    when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(false);
    ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
      .userEmail("jwald@workmarket.com")
      .password("1234xyzu")
      .build();

    User user = new User();
    user.setUuid("user-uuid7654321");
    user.setUserNumber("U1234567");
    Company company = new Company();
    company.setId(1234567L);
    company.setUuid("company-uuid7654321");
    company.setCompanyNumber("C1234567");
    user.setCompany(company);

    when(salesforceLeadService.authenticateToken()).thenReturn(salesForceAuthToken);
    when(registrationService.registerNew(
      any(UserDTO.class),
      anyLong(),
      any(String.class),
      any(AddressDTO.class),
      any(ProfileDTO.class),
      eq(true),
      eq(true))).thenReturn(user);

    final FilterProvider filters = new SimpleFilterProvider().addFilter(
      ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
      new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
    );

    String apiCreateCompanyRequestDTOJson = jackson.writer(filters).writeValueAsString(apiCreateCompanyRequestDTO);


    final MvcResult result =
      mockMvc
        .perform(post(V2_EMPLOYER_CREATE_ACCOUNT).content(apiCreateCompanyRequestDTOJson).contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isOk())
        .andReturn();
    verify(apiCreateCompanyRequestDTOValidator, times(1)).validate(any(ApiCreateCompanyRequestDTO.class), any(BindingResult.class));
    verify(salesforceLeadService, times(1)).authenticateToken();
    verify(salesforceLeadService, times(1)).generateBuyerLead(
      eq(salesForceAuthToken),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyString(),
      anyLong(),
      anyString(),
      anyString()
    );
    final ApiV2Response<ApiCreateCompanyResponseDTO> response = expectApiV2Response(result, apiV2ResponseTypeCreateCompany);
    ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);
    assertEquals("Expected userUuid to match", user.getUuid(), response.getResults().get(0).getUserUuid());
    assertEquals("Expected userNumber to match", user.getUserNumber(), response.getResults().get(0).getUserNumber());
    assertEquals("Expected companyUuid to match", user.getCompany().getUuid(), response.getResults().get(0).getCompanyUuid());
    assertEquals("Expected companyNumber to match", user.getCompany().getCompanyNumber(), response.getResults().get(0).getCompanyNumber());
  }

  @Test
  public void createAccount_validInputWorksWithLocaleFeature() throws Exception {
    when(featureEntitlementService.hasFeatureToggle(any(Long.class), any(String.class))).thenReturn(true);
    final ApiCreateCompanyRequestDTO apiCreateCompanyRequestDTO = new ApiCreateCompanyRequestDTO.Builder()
            .userEmail("jwald@workmarket.com")
            .password("1234xyzu")
            .language("en_US")
            .build();

    final User user = new User();
    user.setUuid("user-uuid7654321");
    user.setUserNumber("U1234567");
    Company company = new Company();
    company.setId(1234567L);
    company.setUuid("company-uuid7654321");
    company.setCompanyNumber("C1234567");
    user.setCompany(company);

    when(salesforceLeadService.authenticateToken()).thenReturn(salesForceAuthToken);
    when(registrationService.registerNew(
            any(UserDTO.class),
            anyLong(),
            any(String.class),
            any(AddressDTO.class),
            any(ProfileDTO.class),
            eq(true),
            eq(true))).thenReturn(user);

    final FilterProvider filters = new SimpleFilterProvider().addFilter(
            ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
            new ApiBaseHttpMessageConverter.APIProjectionsFilter(Collections.EMPTY_SET)
    );

    final String apiCreateCompanyRequestDTOJson = jackson.writer(filters).writeValueAsString(apiCreateCompanyRequestDTO);


    final MvcResult result =
            mockMvc
                    .perform(post(V2_EMPLOYER_CREATE_ACCOUNT).content(apiCreateCompanyRequestDTOJson).contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    .andReturn();
    verify(apiCreateCompanyRequestDTOValidator, times(1)).validate(any(ApiCreateCompanyRequestDTO.class), any(BindingResult.class));
    verify(salesforceLeadService, times(1)).authenticateToken();
    verify(salesforceLeadService, times(1)).generateBuyerLead(
            eq(salesForceAuthToken),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyLong(),
            anyString(),
            anyString()
    );
    final ApiV2Response<ApiCreateCompanyResponseDTO> response = expectApiV2Response(result, apiV2ResponseTypeCreateCompany);
    final ApiJSONPayloadMap responseMeta = response.getMeta();
    expectApiV3ResponseMetaSupport(responseMeta);
    expectStatusCode(200, responseMeta);
    assertEquals("Expected userUuid to match", user.getUuid(), response.getResults().get(0).getUserUuid());
    assertEquals("Expected userNumber to match", user.getUserNumber(), response.getResults().get(0).getUserNumber());
    assertEquals("Expected companyUuid to match", user.getCompany().getUuid(), response.getResults().get(0).getCompanyUuid());
    assertEquals("Expected companyNumber to match", user.getCompany().getCompanyNumber(), response.getResults().get(0).getCompanyNumber());
    verify(localeService).setPreferredLocale(user.getUuid(), "en_US");
  }

}
