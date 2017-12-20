package com.workmarket.api.v2.utils;

import com.workmarket.api.model.CountryEnum;
import com.workmarket.api.model.TaxEntityTypeCodeEnum;
import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.utility.DateUtilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TaxInfoSaveValidatorTest {
  private static final String COMPANY_NAME = "company-name";
  private static final String ADDRESS = "240 west 37th";
  private static final String CITY = "New York";
  private static final String STATE = "NY";
  private static final String POSTAL_CODE = "10018";
  private static final String SIGNATURE = "Signer John Smith";
  private static final String SSN = "807870837";
  private static final long UNIX_TIME_NOW = DateUtilities.getUnixTime(Calendar.getInstance()) * 1000;
  private static final long UNIX_TIME_YESTERDAY = UNIX_TIME_NOW - 24 * 60 * 60 * 1000;
  private static final String FIRST_NAME = "John";
  private static final String MIDDLE_NAME = "Smith";
  private static final String LAST_NAME = "Doe";
  private static final String SIN = "046454286";
  private static final String CANADIAN_BUSINESS_NUMBER = "999999999-RT-9999";
  private static final String FOREIGN_TAX_NUMBER = "some-foreign-tax-number";
  private static final String COUNTRY_OF_INCORPORATION = "USA";

  @Test
  public void validateNullDto() {
    assertEquals("Save request can not be null.", TaxInfoSaveValidator.validate(null).get(0));
  }

  @Test
  public void validateUnknownCountryFalse() {
    final TaxInfoSaveApiDTO dto = buildValidUsaIndividualDto().setCountry(null).build();
    assertEquals("Country must be defined.", TaxInfoSaveValidator.validate(dto).get(0));
  }

  @Test
  public void validateUsaBusinessTrue() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidUsaBusinessDto().build()).size());
  }

  @Test
  public void validateUsaBusinessInvalidEffectiveDate() {
    assertInvalidEffectiveDate(buildValidUsaBusinessDto());
  }

  @Test
  public void validateAllValidUsaBusinessTaxEntityTypes() {
    final TaxInfoSaveApiDTO.Builder dto = buildValidUsaBusinessDto();

    // allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.C_CORP);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.S_CORP);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.PARTNER);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.TRUST);
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_C_CORPORATION);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_S_CORPORATION);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_DISREGARDED);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_PARTNERSHIP);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_CORPORATION);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());

    // not allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.OTHER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.EXEMPT);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.NONE);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  @Test
  public void validateUsaBusinessMissingCompanyName() {
    assertInvalidCompanyName(buildValidUsaBusinessDto());
  }


  @Test
  public void validateUsaBusinessInvalidEINTaxNumber() {
    assertInvalidTaxNumber(buildValidUsaBusinessDto());
  }

  @Test
  public void validateUsaBusinessInvalidAddress() {
    assertInvalidAddress(buildValidUsaBusinessDto());
  }

  @Test
  public void validateUsaBusinessMissingAgreeToTerms() {
    assertInvalidAgreeToTerms(buildValidUsaBusinessDto());
  }

  @Test
  public void validateUsaBusinessInvalidSignatureInfo() {
    assertInvalidSignature(buildValidUsaBusinessDto());
  }

  @Test
  public void validateTrueUsaIndividual() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidUsaIndividualDto().build()).size());
  }

  @Test
  public void validateAllValidUsaIndividualTaxEntityTypes() {
    final TaxInfoSaveApiDTO.Builder dto = buildValidUsaIndividualDto();

    // not allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.C_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.S_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.PARTNER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.TRUST);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.OTHER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_C_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_S_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_DISREGARDED);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_PARTNERSHIP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.EXEMPT);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.NONE);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  @Test
  public void validateUsaIndividualMissingAgreeToTerms() {
    assertInvalidAgreeToTerms(buildValidUsaIndividualDto());
  }

  @Test
  public void validateUsaIndividualInvalidSignatureInfo() {
    assertInvalidSignature(buildValidUsaIndividualDto());
  }

  @Test
  public void validateUsaIndividualInvalidEffectiveDate() {
    assertInvalidEffectiveDate(buildValidUsaIndividualDto());
  }

  @Test
  public void validateUsaInividualInvalidAddress() {
    assertInvalidAddress(buildValidUsaIndividualDto());
  }

  @Test
  public void validateUsaInividualInvalidName() {
    assertInvalidNames(buildValidUsaIndividualDto());
  }

  @Test
  public void validateInvalideUsaIndividualTaxNumber() {
    assertInvalidTaxNumber(buildValidUsaIndividualDto());
  }

  @Test
  public void validateCanadianIndividualTrue() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidCanadianIndividualDto().build()).size());
  }

  @Test
  public void validateCanadianIndividualInvalidTaxNumber() {
    assertInvalidTaxNumber(buildValidCanadianIndividualDto());
  }

  @Test
  public void validateCanadianIndividualInvalidName() {
    assertInvalidNames(buildValidCanadianIndividualDto());
  }

  @Test
  public void validateCanadianIndividualInvalidAddress() {
    assertInvalidAddress(buildValidCanadianIndividualDto());
  }

  @Test
  public void validateCanadianBusinessTrue() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidCanadianBusinessDto().build()).size());
  }

  @Test
  public void validateCanadianBusinessInvalidTaxNumber() {
    assertInvalidTaxNumber(buildValidCanadianBusinessDto());
  }

  @Test
  public void validateCanadianBusinessInvalidCompanyName() {
    assertInvalidCompanyName(buildValidCanadianBusinessDto());
  }

  @Test
  public void validateCanadianBusinessInvalidAddress() {
    assertInvalidAddress(buildValidCanadianBusinessDto());
  }

  @Test
  public void validateForeignIndividualTrue() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidForeignIndividualDto().build()).size());
  }

  @Test
  public void validateForeignIndividualInvalidTaxNumber() {
    assertInvalidTaxNumber(buildValidForeignIndividualDto());
  }

  @Test
  public void validateForeignIndividualInvalidName() {
    assertInvalidNames(buildValidForeignIndividualDto());
  }

  @Test
  public void validateForeignIndividualInvalidAddress() {
    assertInvalidAddress(buildValidForeignIndividualDto());
  }

  @Test
  public void validateForeignIndividualInvalidForeignStatusAccepted() {
    assertInvalidForeignStatusAccepted(buildValidForeignIndividualDto());
  }

  @Test
  public void validateAllValidForeignInividualTaxEntityTypes() {
    final TaxInfoSaveApiDTO.Builder dto = buildValidForeignIndividualDto();

    // not allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.C_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.S_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.PARTNER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.TRUST);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.OTHER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_C_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_S_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_DISREGARDED);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_PARTNERSHIP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.EXEMPT);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.NONE);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  @Test
  public void validateForeignBusinessTrue() {
    assertEquals(0, TaxInfoSaveValidator.validate(buildValidForeignBusinessDto().build()).size());
  }

  @Test
  public void validateForeignBusinessInvalidTaxNumber() {
    assertInvalidTaxNumber(buildValidForeignBusinessDto());
  }

  @Test
  public void validateForeignBusinessInvalidCompanyName() {
    assertInvalidCompanyName(buildValidForeignBusinessDto());
  }

  @Test
  public void validateForeignBusinessInvalidCountryOfIncorporation() {
    assertInvalidCompanyOfIncorporation(buildValidForeignBusinessDto());
  }

  @Test
  public void validateForeignBusinessInvalidAddress() {
    assertInvalidAddress(buildValidForeignBusinessDto());
  }

  @Test
  public void validateForeignBusinessValidForeignStatusAccepted() {
    assertValidForeignStatusAccepted(buildValidForeignBusinessDto());
  }

  @Test
  public void validateAllValidForeignBusinessTaxEntityTypes() {
    final TaxInfoSaveApiDTO.Builder dto = buildValidForeignBusinessDto();

    // allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.CORP);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_DISREGARDED);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.PARTNER);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());

    // not allowed
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.INDIVIDUAL);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.S_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.C_CORP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.OTHER);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_C_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_S_CORPORATION);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.LLC_PARTNERSHIP);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.TRUST);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.EXEMPT);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setTaxEntityTypeCode(TaxEntityTypeCodeEnum.NONE);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  @Test
  public void validateMaxFirstName() {
    final TaxInfoSaveApiDTO dto = buildValidUsaIndividualDto()
      .setFirstName(new String(new char[TaxInfoSaveValidator.MAX_FIRST_NAME + 1]).replace("\0", "*")).build();
    assertEquals(1, TaxInfoSaveValidator.validate(dto).size());
  }

  @Test
  public void validateMaxMiddleName() {
    final TaxInfoSaveApiDTO dto = buildValidUsaIndividualDto()
      .setMiddleName(new String(new char[TaxInfoSaveValidator.MAX_MIDDLE_NAME + 1]).replace("\0", "*")).build();
    assertEquals(1, TaxInfoSaveValidator.validate(dto).size());
  }

  @Test
  public void validateMaxLastName() {
    final TaxInfoSaveApiDTO dto = buildValidUsaIndividualDto()
      .setLastName(new String(new char[TaxInfoSaveValidator.MAX_LAST_NAME + 1]).replace("\0", "*")).build();
    assertEquals(1, TaxInfoSaveValidator.validate(dto).size());
  }

  private void assertInvalidCompanyOfIncorporation(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setCountryOfIncorporation("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertValidForeignStatusAccepted(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setForeignStatusAccepted(false);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setForeignStatusAccepted(true);
    assertEquals(0, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidForeignStatusAccepted(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setForeignStatusAccepted(false);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidAgreeToTerms(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setAgreeToTerms(false);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidCompanyName(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setCompanyName("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidEffectiveDate(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setEffectiveDate(UNIX_TIME_NOW);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidTaxNumber(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setTaxNumber("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidSignature(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setSignature("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setSignature(SIGNATURE);
    dto.setSignatureDate(null);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setSignatureDate(UNIX_TIME_YESTERDAY);
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    assertEquals("Signature date must be today.", TaxInfoSaveValidator.validate(dto.build()).get(0));
  }

  private void assertInvalidAddress(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setAddress("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setAddress(ADDRESS);
    dto.setCity("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setCity(CITY);
    dto.setState("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setState(STATE);
    dto.setPostalCode("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private void assertInvalidNames(final TaxInfoSaveApiDTO.Builder dto) {
    dto.setFirstName("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
    dto.setFirstName(FIRST_NAME);
    dto.setLastName("");
    assertEquals(1, TaxInfoSaveValidator.validate(dto.build()).size());
  }

  private TaxInfoSaveApiDTO.Builder buildValidUsaBusinessDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.USA)
      .setBusiness(true)
      .setCompanyName(COMPANY_NAME)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE)
      .setTaxNumber(SSN)
      .setAgreeToTerms(true)
      .setSignature(SIGNATURE)
      .setSignatureDate(UNIX_TIME_NOW)
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
      .setSignatureDate(UNIX_TIME_NOW)
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

  private TaxInfoSaveApiDTO.Builder buildValidForeignBusinessDto() {
    return TaxInfoSaveApiDTO.builder()
      .setCountry(CountryEnum.OTHER)
      .setTaxNumber(FOREIGN_TAX_NUMBER)
      .setBusiness(true)
      .setCompanyName(COMPANY_NAME)
      .setCountryOfIncorporation(COUNTRY_OF_INCORPORATION)
      .setAddress(ADDRESS)
      .setCity(CITY)
      .setState(STATE)
      .setPostalCode(POSTAL_CODE)
      .setForeignStatusAccepted(true)
      .setTaxEntityTypeCode(TaxEntityTypeCodeEnum.CORP);
  }
}