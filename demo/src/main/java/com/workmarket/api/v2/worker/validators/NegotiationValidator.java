package com.workmarket.api.v2.worker.validators;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import com.workmarket.api.v2.worker.model.RescheduleDTO;
import com.workmarket.api.v2.worker.service.ValidationService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.thrift.work.Work;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Business logic level validation
 */
@Service
public class NegotiationValidator {

    @Autowired private ValidationService validationService;
    @Autowired protected MessageBundleHelper messageHelper;

    public List<String> validateBudgetIncrease(NegotiationDTO negotiationDTO,
                                               Work work) {

        List<String> errors = new LinkedList<String>();

        switch (work.getPricing().getType()) {
        case FLAT:
            if (negotiationDTO.getFlatPrice() == null || negotiationDTO.getFlatPrice() <= 0) {
                errors.add(messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch"));
                break;
            }
            if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(),
                                                                             negotiationDTO.getFlatPrice()) != -1) {
                errors.add(messageHelper.getMessage("Size.budgetIncreaseForm"));
            }
            break;
        case PER_HOUR:
            if (negotiationDTO.getMaxHours() == null || negotiationDTO.getMaxHours() <= 0) {
                errors.add(messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch"));
                break;
            }
            if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(),
                                                                             negotiationDTO.getMaxHours().doubleValue()) != -1) {
                errors.add(messageHelper.getMessage("Size.budgetIncreaseForm"));
            }
            break;
        case PER_UNIT:
            if (negotiationDTO.getMaxUnits() == null || negotiationDTO.getMaxUnits() <= 0) {
                errors.add(messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch"));
                break;
            }
            if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(),
                                                                             negotiationDTO.getMaxUnits().doubleValue()) != -1) {
                errors.add(messageHelper.getMessage("Size.budgetIncreaseForm"));
            }
            break;
        case BLENDED_PER_HOUR:
            if (negotiationDTO.getMaxAdditionalHours() == null || negotiationDTO.getMaxAdditionalHours() <= 0) {
                errors.add(messageHelper.getMessage("assignment.budgetincrease.pricing_strategy_mismatch"));
                break;
            }
            if (PricingStrategyUtilities.compareThriftPricingStrategyToValue(work.getPricing(),
                                                                             negotiationDTO.getMaxAdditionalHours().doubleValue()) != -1) {
                errors.add(messageHelper.getMessage("Size.budgetIncreaseForm"));
            }
            break;
        case INTERNAL:
            errors.add(messageHelper.getMessage("assignment.budgetincrease.internal_not_valid"));
            break;
        }

        return errors;
    }

    public void validateApplication(AssignmentApplicationDTO applyForm,
                                    Work work,
                                    User user) {

        if (applyForm == null) {
            return;
        }

        if (!WorkStatusType.SENT.equals(work.getStatus().getCode())) {
            throw new MessageSourceApiException("assignment.apply.failure");
        }

        String scheduleValidationError = validateNegotiationSchedule(applyForm.getSchedule());
        if (StringUtils.isNotBlank(scheduleValidationError)) {
            throw new MessageSourceApiException(scheduleValidationError);
        }

        if (!validateOfferExpiration(applyForm.getExpirationDate())) {
            throw new MessageSourceApiException("assignment.apply.offer_expiration.error");
        }

        if (applyForm.getPricing() != null && applyForm.getPricing().hasBudgetNegotiation()) {
            List<String> messages = validateBudgetIncrease(applyForm.getPricing(), work);
            if (CollectionUtils.isNotEmpty(messages)) {
                throw new MessageSourceApiException(messages.get(0));
            }
        }

        if (!isUserValidForWork(user, work)) {
            throw new MessageSourceApiException("assignment.accept.invalid_resource", ImmutableList.of("You", "are", "you",
                                                                                                       work.getCompany().getName()).toArray());
        }
        if (!isUserEligibleForWork(user, work)) {
            throw new MessageSourceApiException("assignment.apply.not_eligible.error");
        }
    }

    public String validateNegotiationSchedule(RescheduleDTO scheduleForm) {

        if (scheduleForm == null ||
            ( (scheduleForm.getStart() == null || scheduleForm.getStart() == 0) &&
              (scheduleForm.getStartWindowBegin() == null || scheduleForm.getStartWindowBegin() == 0))) {
            return null;
        }

        if (scheduleForm.getStart() != null && scheduleForm.getStart() > 0 &&
            scheduleForm.getStartWindowBegin() != null && scheduleForm.getStartWindowBegin() > 0) {
            return "assignment.apply.schedule_validation.indeterminate_start_time";
        }

        boolean isStartWindow = false;
        if (scheduleForm.getStart() == null || scheduleForm.getStart() == 0) {
            isStartWindow = true;
        }
        if (isStartWindow && scheduleForm.getStartWindowBegin() >= scheduleForm.getStartWindowEnd()) {
            return "assignment.apply.schedule_validation.start_after_end";
        }

        Long startTime = isStartWindow ? scheduleForm.getStartWindowBegin() : scheduleForm.getStart();
        Long now = new Date().getTime();
        if (now >= startTime) {
            return "assignment.apply.schedule_validation.start_in_past";
        }
        return null;
    }

    public Boolean validateOfferExpiration(Long expirationTimeInMillis) {

        if (expirationTimeInMillis == null || expirationTimeInMillis <= 0) {
            return Boolean.TRUE;
        }

        Long now = new Date().getTime();
        if (now > expirationTimeInMillis) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public Boolean isUserValidForWork(User user, Work work) {
        return validationService.isUserValidForWork(user.getId(), user.getCompany().getId(), work.getCompany().getId());
    }

    public Boolean isUserEligibleForWork(User user, Work work) {
        return validationService.isUserEligibilityForWork(user.getId(), work);
    }

    /**
     * Utility methods for unit tests
     */
    protected void setMessageHelper(MessageBundleHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    protected void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
