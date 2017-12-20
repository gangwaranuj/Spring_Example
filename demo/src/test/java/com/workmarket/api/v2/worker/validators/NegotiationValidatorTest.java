package com.workmarket.api.v2.worker.validators;

import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.service.ValidationService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Work;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NegotiationValidatorTest {

    @Mock private MessageBundleHelper messageHelper;
    @Mock private ValidationService validationService;
    private NegotiationValidator negotiationValidator;

    private AssignmentApplicationDTO.Builder applicationDtoBuilder;
    private Work work;
    private User user;

    @Before
    public void setup() {

        applicationDtoBuilder = generateGoodAssignmentApplicationDTOBuilder();

        work = new Work();
		work.setStatus(new Status("sent","",""));
        work.setPricing(new PricingStrategy().setType(PricingStrategyType.FLAT).setFlatPrice(500.00));
        work.setCompany(new com.workmarket.thrift.core.Company());
        work.getCompany().setId(888888L);
        work.getCompany().setName("Company Test Inc.");

        user = new User();
        user.setId(12345L);
        user.setCompany(new Company());
        user.getCompany().setId(54321L);

        negotiationValidator = new NegotiationValidator();

        validationService = mock(ValidationService.class);

        when
            (
                validationService.isUserEligibilityForWork(user.getId(), work)
            )
            .thenReturn(Boolean.TRUE);

        when
            (
                validationService.isUserValidForWork(user.getId(),
                                                     user.getCompany().getId(),
                                                     work.getCompany().getId())
            )
            .thenReturn(Boolean.TRUE);

        negotiationValidator.setValidationService(validationService);

        messageHelper = mock(MessageBundleHelper.class);

        when
			(
				messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch")
			)
            .thenReturn("The submitted budget request is of different price type than the assignment.");

        when
			(
				messageHelper.getMessage("Size.budgetIncreaseForm")
			)
            .thenReturn("The amount must be greater than the existing assignment budget.");

        when
			(
				messageHelper.getMessage("assignment.budgetincrease.internal_not_valid")
			)
            .thenReturn("You cannot submit a budget increase request on an internal assignment.");

        negotiationValidator.setMessageHelper(messageHelper);
    }

    @Test
    public void validateBudgetIncrease_mismatchStrategies_errorResult() {

        List validationResults = null;

        work = new Work();

		////

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.FLAT));

        NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
          .withFlatPrice(0D)
          .withNote("testNote")
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The submitted budget request is of different price type than the assignment.",
                     validationResults.get(0));

		////

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_HOUR));

        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxHours(0.0)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The submitted budget request is of different price type than the assignment.",
                     validationResults.get(0));

		////

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_UNIT));

        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxUnits(0.0)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The submitted budget request is of different price type than the assignment.",
                     validationResults.get(0));

		////

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.BLENDED_PER_HOUR));

        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxAdditionalHours(0D)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The submitted budget request is of different price type than the assignment.",
                     validationResults.get(0));
    }

    @Test
    public void validateBudgetIncrease_internalAssignment_errorResult() {

        List validationResults = null;

        work = new Work();
        work.setPricing(new PricingStrategy().setType(PricingStrategyType.INTERNAL));

        NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
          .withFlatPrice(0D)
          .withNote("testNote")
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("You cannot submit a budget increase request on an internal assignment.",
                     validationResults.get(0));
    }

    @Test
    public void validateBudgetIncrease_requestBeneathCurrentPrice_errorResult() {

        List validationResults = null;

        work = new Work();

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.FLAT).setFlatPrice(500.00));
        NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
          .withFlatPrice(250.00)
          .withNote("testNote")
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The amount must be greater than the existing assignment budget.",
                     validationResults.get(0));

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_HOUR).setMaxNumberOfHours(16));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxHours(8D)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The amount must be greater than the existing assignment budget.",
                     validationResults.get(0));

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_UNIT).setMaxNumberOfUnits(200));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxUnits(150.0)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The amount must be greater than the existing assignment budget.",
                     validationResults.get(0));

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.BLENDED_PER_HOUR).setMaxBlendedNumberOfHours(10));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxAdditionalHours(5.000)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(1, validationResults.size());
        assertEquals("The amount must be greater than the existing assignment budget.",
                     validationResults.get(0));
    }

    @Test
    public void validateBudgetIncrease_goodData_goodResponse() {

        List validationResults = null;

        work = new Work();

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.FLAT).setFlatPrice(500.00));
        NegotiationDTO negotiationDTO = new NegotiationDTO.Builder()
          .withFlatPrice(750.00)
          .withNote("testNote")
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(0, validationResults.size());

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_HOUR).setMaxNumberOfHours(16));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxHours(20D)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(0, validationResults.size());

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.PER_UNIT).setMaxNumberOfUnits(200));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxUnits(250D)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(0, validationResults.size());

        work.setPricing(new PricingStrategy().setType(PricingStrategyType.BLENDED_PER_HOUR).setMaxBlendedNumberOfHours(10));
        negotiationDTO = new NegotiationDTO.Builder()
          .withMaxAdditionalHours(15.0)
          .build();
        validationResults = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);
        assertEquals(0, validationResults.size());
    }

    @Test
    public void validateApplication_EmptyRequestInternal_passes() {

        applicationDtoBuilder = new AssignmentApplicationDTO.Builder();

        work.getPricing().setType(PricingStrategyType.INTERNAL);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
        }
		catch (Exception e) {
            fail("Validation should have run to completion without event or exception. Exception was: " +
                 e.getClass() + "- " + e.getMessage() + ", " + e.getStackTrace());
        }
    }

    @Test
    public void validateApplication_goodNegotiation_passes() {

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
        }
		catch (Exception e) {
            fail("Validation should have run to completion without event or exception. Exception was: " +
                 e.getClass() + "- " + e.getMessage());
        }
    }

    @Test
    public void validateApplication_startAndStartWindowBeginBothPopulated_exceptionThrown() {

        applicationDtoBuilder.getSchedule().withStart(2033333333333L);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.schedule_validation.indeterminate_start_time", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_StartWindowBeginAfterWindowEnd_exceptionThrown() {

        applicationDtoBuilder.getSchedule().withStartWindowBegin(2033355555555L);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.schedule_validation.start_after_end", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_StartWindowBeginInThePast_exceptionThrown() {

        applicationDtoBuilder.getSchedule().withStartWindowBegin(20L);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.schedule_validation.start_in_past", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }

        applicationDtoBuilder.getSchedule().withStartWindowBegin(0L);
        applicationDtoBuilder.getSchedule().withStartWindowEnd(0L);
        applicationDtoBuilder.getSchedule().withStart(20L);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.schedule_validation.start_in_past", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_expirationInThePast_exceptionThrown() {

        applicationDtoBuilder.withExpirationDate(20L);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.offer_expiration.error", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_budgetDecrease_exceptionThrown() {

        applicationDtoBuilder.getPricing().withFlatPrice(300.00);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("The amount must be greater than the existing assignment budget.", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_budgetMismatch_exceptionThrown() {

        work.getPricing().setType(PricingStrategyType.BLENDED_PER_HOUR);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("The submitted budget request is of different price type than the assignment.", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_userNotValid_exceptionThrown() {

        when
			(
				validationService.isUserValidForWork(user.getId(),
													 user.getCompany().getId(),
													 work.getCompany().getId())
			)
            .thenReturn(Boolean.FALSE);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.accept.invalid_resource", msae.getMessage());
            assertEquals("You", msae.getArguments()[0]);
            assertEquals("are", msae.getArguments()[1]);
            assertEquals("you", msae.getArguments()[2]);
            assertEquals("Company Test Inc.", msae.getArguments()[3]);
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    @Test
    public void validateApplication_userIneligible_exceptionThrown() {

        when
			(
				validationService.isUserEligibilityForWork(user.getId(), work)
			)
			.thenReturn(Boolean.FALSE);

        try {
            negotiationValidator.validateApplication(applicationDtoBuilder.build(), work, user);
            fail("Expected a MessageSourceApiException to be thrown.");
        }
		catch (MessageSourceApiException msae) {
            assertEquals("assignment.apply.not_eligible.error", msae.getMessage());
        }
		catch (Exception e) {
            fail("Expected a MessageSourceApiException to be thrown. Instead, was a " + e.getClass());
        }
    }

    private AssignmentApplicationDTO.Builder generateGoodAssignmentApplicationDTOBuilder() {

        RescheduleDTO.Builder scheduleDtoBuilder = new RescheduleDTO.Builder()
          .withStartWindowBegin(2033333333333L)
          .withStartWindowEnd(2033344444444L);

        NegotiationDTO.Builder payNegotiationBuilder= new NegotiationDTO.Builder()
          .withFlatPrice(750.00)
          .withReimbursement(225.00)
          .withBonus(115.00);

        return new AssignmentApplicationDTO.Builder()
          .withPricing(payNegotiationBuilder)
          .withSchedule(scheduleDtoBuilder)
          .withExpirationDate(1700300030003L)
          .withMessage("I have many demands.");
    }
}
