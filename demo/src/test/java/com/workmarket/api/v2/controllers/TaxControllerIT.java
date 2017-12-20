package com.workmarket.api.v2.controllers;

import com.workmarket.api.exceptions.UnprocessableEntityException;
import com.workmarket.api.model.CountryEnum;
import com.workmarket.api.model.TaxEntityTypeCodeEnum;
import com.workmarket.api.v2.model.TaxInfoApiDTO;
import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.feature.gen.Messages.FeatureToggle;
import com.workmarket.feature.gen.Messages.Status;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TaxControllerIT extends BaseServiceIT {
  private static final String COMPANY_NAME = "company-name";
  private static final String ADDRESS = "240 west 37th";
  private static final String CITY = "New York";
  private static final String STATE = "NY";
  private static final String POSTAL_CODE = "10018";
  private static final String SIGNATURE = "Signer John Smith";
  private static final String SSN = "807870837";
  private static final long UNXI_TIME_NOW = DateUtilities.getUnixTime(Calendar.getInstance()) * 1000;
  private static final String FIRST_NAME = "John";
  private static final String MIDDLE_NAME = "Smith";
  private static final String LAST_NAME = "Doe";
  private static final String SIN = "046454286";
  private static final String CANADIAN_BUSINESS_NUMBER = "999999999-RT-9999";
  private static final String FOREIGN_TAX_NUMBER = "some-foreign-tax-number";
  private static final String COUNTRY_OF_INCORPORATION = "USA";
  private static final String BUSINESS_NAME = "business-name";

  @Autowired
  TaxController taxController;

  @PostConstruct
  public void setup() {
  }

  @Test
  public void saveUsaBusiness() throws Exception {
    final TaxInfoApiDTO info = taxController.save(buildValidUsaBusinessDto().build()).getResults().get(0);

    assertEquals(ADDRESS, info.getAddress());
    assertEquals(BUSINESS_NAME, info.getBusinessAsName());
    assertEquals(CITY, info.getCity());
    assertEquals(COMPANY_NAME, info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_USA, info.getCountry().code());
    assertEquals(null, info.getCountryOfIncorporation());
    assertEquals(null, info.getFirstName());
    assertEquals(null, info.getMiddleName());
    assertEquals(COMPANY_NAME, info.getLastName());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(SIGNATURE, info.getSignature());
    assertEquals(UNXI_TIME_NOW, info.getSignatureDate() * 1000);
    assertEquals(STATE, info.getState());
    assertEquals(TaxEntityType.INDIVIDUAL, info.getTaxEntityTypeCode().code());
    assertNotNull(info.getActiveDate());
    assertEquals(TaxVerificationStatusType.UNVERIFIED, info.getTaxVerificationStatusCode().code());
    assertEquals(Long.valueOf(0), info.getEffectiveDate());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertTrue(info.isBusiness());
  }

  @Test
  public void saveUsaIndividual() throws Exception {
    final TaxInfoApiDTO info = taxController.save(buildValidUsaIndividualDto().build()).getResults().get(0);

    assertEquals(ADDRESS, info.getAddress());
    assertEquals(CITY, info.getCity());
    assertEquals(STATE, info.getState());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(BUSINESS_NAME, info.getBusinessAsName());
    assertEquals(info.getFullName(), info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_USA, info.getCountry().code());
    assertEquals(FIRST_NAME, info.getFirstName());
    assertEquals(MIDDLE_NAME, info.getMiddleName());
    assertEquals(LAST_NAME, info.getLastName());
    assertEquals(null, info.getCountryOfIncorporation());
    assertEquals(SIGNATURE, info.getSignature());
    assertEquals(UNXI_TIME_NOW, info.getSignatureDate() * 1000);
    assertEquals(TaxEntityType.INDIVIDUAL, info.getTaxEntityTypeCode().code());
    assertNotNull(info.getActiveDate());
    assertEquals(TaxVerificationStatusType.UNVERIFIED, info.getTaxVerificationStatusCode().code());
    assertEquals(Long.valueOf(0), info.getEffectiveDate());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertFalse(info.isBusiness());
    assertEquals(TaxEntityType.INDIVIDUAL, info.getTaxEntityTypeCode().code());
  }

  @Test
  public void saveCanadaIndividual() throws Exception {
    final TaxInfoApiDTO info = taxController.save(buildValidCanadianIndividualDto().build()).getResults().get(0);

    assertNotNull(info.getActiveDate());
    assertEquals(ADDRESS, info.getAddress());
    assertEquals(null, info.getBusinessAsName());
    assertEquals(CITY, info.getCity());
    assertEquals(info.getFullName(), info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_CANADA, info.getCountry().code());
    assertNull(info.getCountryOfIncorporation());
    assertNotNull(info.getEffectiveDate());
    assertEquals(FIRST_NAME, info.getFirstName());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertEquals(LAST_NAME, info.getLastName());
    assertEquals(MIDDLE_NAME, info.getMiddleName());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(null, info.getSignature());
    assertTrue(info.getSignatureDate() == null || info.getSignatureDate() == 0);
    assertEquals(STATE, info.getState());
    assertEquals(TaxEntityType.NONE, info.getTaxEntityTypeCode().code());
    assertEquals(TaxVerificationStatusType.VALIDATED, info.getTaxVerificationStatusCode().code());
    assertTrue(info.isActive());
    assertFalse(info.isAgreeToTerms());
    assertFalse(info.isBusiness());
    assertFalse(info.isForeignStatusAccepted());
  }

  @Test
  public void saveCanadaBusiness() throws Exception {
    final TaxInfoApiDTO info = taxController.save(buildValidCanadianBusinessDto().build()).getResults().get(0);

    assertNotNull(info.getActiveDate());
    assertEquals(ADDRESS, info.getAddress());
    assertEquals(null, info.getBusinessAsName());
    assertEquals(CITY, info.getCity());
    assertEquals(COMPANY_NAME, info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_CANADA, info.getCountry().code());
    assertNull(info.getCountryOfIncorporation());
    assertNotNull(info.getEffectiveDate());
    assertEquals(null, info.getFirstName());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertEquals(COMPANY_NAME, info.getLastName());
    assertEquals(null, info.getMiddleName());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(null, info.getSignature());
    assertTrue(info.getSignatureDate() == null || info.getSignatureDate() == 0);
    assertEquals(STATE, info.getState());
    assertEquals(TaxEntityType.NONE, info.getTaxEntityTypeCode().code());
    assertEquals(TaxVerificationStatusType.VALIDATED, info.getTaxVerificationStatusCode().code());
    assertTrue(info.isActive());
    assertFalse(info.isAgreeToTerms());
    assertTrue(info.isBusiness());
    assertFalse(info.isForeignStatusAccepted());
  }

  @Test
  public void saveForeignIndividual() throws Exception {
    final TaxInfoApiDTO info = taxController.save(buildValidForeignIndividualDto().build()).getResults().get(0);

    assertNotNull(info.getActiveDate());
    assertEquals(ADDRESS, info.getAddress());
    assertEquals(null, info.getBusinessAsName());
    assertEquals(CITY, info.getCity());
    assertEquals(info.getFullName(), info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_OTHER, info.getCountry().code());
    assertNull(info.getCountryOfIncorporation());
    assertNotNull(info.getEffectiveDate());
    assertEquals(FIRST_NAME, info.getFirstName());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertEquals(LAST_NAME, info.getLastName());
    assertEquals(MIDDLE_NAME, info.getMiddleName());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(null, info.getSignature());
    assertTrue(info.getSignatureDate() == null || info.getSignatureDate() == 0);
    assertEquals(STATE, info.getState());
    assertEquals(TaxEntityType.INDIVIDUAL, info.getTaxEntityTypeCode().code());
    assertEquals(TaxVerificationStatusType.SIGNED_FORM_W8, info.getTaxVerificationStatusCode().code());
    assertTrue(info.isActive());
    assertFalse(info.isAgreeToTerms());
    assertFalse(info.isBusiness());
    assertTrue(info.isForeignStatusAccepted());
  }

  @Test(expected = UnprocessableEntityException.class)
  public void saveForeignBusinessPartnerStateTooLong() throws Exception {
    TaxEntityTypeCodeEnum taxEntityTypeCode = TaxEntityTypeCodeEnum.PARTNER;
    String state = "12345678901234567";
    verifyForeignBusiness(state, taxEntityTypeCode);
  }

  @Test
  public void saveForeignBusinessPartnerOK() throws Exception {
    TaxEntityTypeCodeEnum taxEntityTypeCode = TaxEntityTypeCodeEnum.PARTNER;
    String state = "1234567890123456";
    verifyForeignBusiness(state, taxEntityTypeCode);
  }

  @Test
  public void saveForeignBusinessCorp() throws Exception {
    TaxEntityTypeCodeEnum taxEntityTypeCode = TaxEntityTypeCodeEnum.CORP;
    verifyForeignBusiness(STATE, taxEntityTypeCode);
  }

  private void verifyForeignBusiness(String state, TaxEntityTypeCodeEnum taxEntityTypeCode) throws Exception {
    TaxInfoSaveApiDTO taxInfoSaveApiDTO = buildValidForeignBusinessDto(
      state, taxEntityTypeCode).build();
    final TaxInfoApiDTO info = taxController.save(taxInfoSaveApiDTO).getResults().get(0);

    assertNotNull(info.getActiveDate());
    assertEquals(ADDRESS, info.getAddress());
    assertEquals(null, info.getBusinessAsName());
    assertEquals(CITY, info.getCity());
    assertEquals(COMPANY_NAME, info.getCompanyName());
    assertEquals(AbstractTaxEntity.COUNTRY_OTHER, info.getCountry().code());
    assertEquals(COUNTRY_OF_INCORPORATION, info.getCountryOfIncorporation());
    assertNotNull(info.getEffectiveDate());
    assertEquals(null, info.getFirstName());
    assertTrue(info.getInactiveDate() == null || info.getInactiveDate() == 0);
    assertEquals(COMPANY_NAME, info.getLastName());
    assertEquals(null, info.getMiddleName());
    assertEquals(POSTAL_CODE, info.getPostalCode());
    assertEquals(null, info.getSignature());
    assertTrue(info.getSignatureDate() == null || info.getSignatureDate() == 0);
    assertEquals(state, info.getState());
    assertEquals(taxEntityTypeCode.code(), info.getTaxEntityTypeCode().code());
    assertEquals(TaxVerificationStatusType.SIGNED_FORM_W8, info.getTaxVerificationStatusCode().code());
    assertTrue(info.isActive());
    assertFalse(info.isAgreeToTerms());
    assertTrue(info.isBusiness());
    assertTrue(info.isForeignStatusAccepted());
  }

  private TaxInfoSaveApiDTO.Builder buildValidUsaBusinessDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.USA)
      .setBusiness(true)
      .setCompanyName(COMPANY_NAME)
      .setAddress(ADDRESS)
      .setBusinessAsName(BUSINESS_NAME)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE)
      .setTaxNumber(SSN)
      .setAgreeToTerms(true)
      .setSignature(SIGNATURE)
      .setSignatureDate(UNXI_TIME_NOW)
      .setTaxEntityTypeCode(TaxEntityTypeCodeEnum.INDIVIDUAL);
  }

  private TaxInfoSaveApiDTO.Builder buildValidUsaIndividualDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.USA)
      .setBusiness(false)
      .setFirstName(FIRST_NAME)
      .setMiddleName(MIDDLE_NAME)
      .setLastName(LAST_NAME)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE)
      .setTaxNumber(SSN)
      .setAgreeToTerms(true)
      .setSignature(SIGNATURE)
      .setSignatureDate(UNXI_TIME_NOW)
      .setBusinessAsName(BUSINESS_NAME)
      .setTaxEntityTypeCode(TaxEntityTypeCodeEnum.INDIVIDUAL);
  }

  private TaxInfoSaveApiDTO.Builder buildValidCanadianIndividualDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.CANADA)
      .setBusiness(false)
      .setTaxNumber(SIN)
      .setFirstName(FIRST_NAME)
      .setMiddleName(MIDDLE_NAME)
      .setLastName(LAST_NAME)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE);
  }

  private TaxInfoSaveApiDTO.Builder buildValidCanadianBusinessDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.CANADA)
      .setBusiness(true)
      .setTaxNumber(CANADIAN_BUSINESS_NUMBER)
      .setCompanyName(COMPANY_NAME)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE);
  }

  private TaxInfoSaveApiDTO.Builder buildValidForeignIndividualDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.OTHER)
      .setBusiness(false)
      .setTaxNumber(FOREIGN_TAX_NUMBER)
      .setFirstName(FIRST_NAME)
      .setMiddleName(MIDDLE_NAME)
      .setLastName(LAST_NAME)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE)
      .setForeignStatusAccepted(true)
      .setTaxEntityTypeCode(TaxEntityTypeCodeEnum.INDIVIDUAL);
  }

  private TaxInfoSaveApiDTO.Builder buildValidForeignBusinessDto(TaxEntityTypeCodeEnum taxEntityTypeCode) {
    return buildValidForeignBusinessDto(STATE, taxEntityTypeCode);
  }
  private TaxInfoSaveApiDTO.Builder buildValidForeignBusinessDto(String state, TaxEntityTypeCodeEnum taxEntityTypeCode) {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.OTHER)
      .setTaxNumber(FOREIGN_TAX_NUMBER)
      .setBusiness(true)
      .setCompanyName(COMPANY_NAME)
      .setCountryOfIncorporation(COUNTRY_OF_INCORPORATION)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(state)
      .setPostalCode(POSTAL_CODE)
      .setForeignStatusAccepted(true)
      .setTaxEntityTypeCode(taxEntityTypeCode);
  }

}