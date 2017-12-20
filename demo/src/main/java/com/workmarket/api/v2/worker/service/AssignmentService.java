package com.workmarket.api.v2.worker.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;

import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.controllers.mobile.MobileWorkController;
import com.workmarket.web.exceptions.MobileHttpException401;
import com.workmarket.web.exceptions.MobileHttpException403;

import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.CustomFieldGroupSet;

import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.business.dto.PaymentSummaryDTO;
import com.workmarket.thrift.work.PaymentSummary;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain assignment related data and make
 * assignment related calls. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should give
 * way to service classes that call on microservices for this type of work.
 */
@Service
public class AssignmentService {

    @Autowired private PaymentSummaryService paymentSummaryService;
    @Autowired private MobileWorkController mobileWorkController;
    @Autowired private PartService partService;
    @Autowired private WorkBundleService workBundleService;

    public Map<String, Object> getList(final WorkStatusType workStatusType,
                                       final String[] requestedFields,
                                       final Integer page,
                                       final Integer pageSize,
                                       final String sort) {

        return mobileWorkController
            .getList
            (
                workStatusType,
                //requestedFields,
                page,
                pageSize,
                sort
            );
    }

    public Map<String, Object> getWorkFeed(final FeedRequestParams params) {

        return mobileWorkController
            .getWorkFeed
            (
                params
            );
    }

    public Map<String, Object> getWorkFeed(final String keyword,
                                           final Integer industryId,
                                           final Double latitude,
                                           final Double longitude,
                                           final Double radius,
                                           final Boolean virtual,
                                           final String[] requestedFields,
                                           final Integer page,
                                           final Integer pageSize) {

        return mobileWorkController
            .getWorkFeed
            (
                keyword,
                industryId,
                latitude != null ? String.valueOf(latitude) : "",
                longitude != null ? String.valueOf(longitude) : "",
                radius,
                virtual,
                requestedFields,
                page,
                pageSize
            );
    }

    public WorkResponse getAssignmentDetails(final String workNumber) {

        try {

            return mobileWorkController
                .getWorkDetails
                (
                    workNumber
                );
        }
        catch (MobileHttpException401 ex) {

            throw new MobileHttpException403().setMessageKey("assignment.mobile.notallowed");
        }
    }

    public List<CustomField> getHeaderCustomFieldsForAssignmentDetails
		(
			final WorkResponse workResponse
		) {

        final CustomFieldGroupSet customFieldGroupSet = new CustomFieldGroupSet();

		final com.workmarket.thrift.work.Work work = workResponse.getWork();

        customFieldGroupSet.setCustomFieldGroupSet(work.getCustomFieldGroups());

        if (CollectionUtils.isNotEmpty(work.getCustomFieldGroups())) {

            final List<CustomField> headerDisplayFields = Lists.newArrayList();

            for (CustomFieldGroup fieldGroup : work.getCustomFieldGroups()) {

                if (CollectionUtils.isEmpty(fieldGroup.getFields())) {
                    continue;
                }

                for (final CustomField field : fieldGroup.getFields()) {

                    if (field == null) {
                        continue;
                    }

                    if (field.isShowInAssignmentHeader()) {
                        headerDisplayFields.add(field);
                    }
                }
            }

            return headerDisplayFields;
        }

		return null;
    }

    public List<PartDTO> getPartsForAssignmentDetails
		(
			final WorkResponse workResponse
		) {

        if (workResponse == null ||
            workResponse.getWork() == null ||
            workResponse.getWork().getPartGroup() == null ||
            StringUtils.isBlank(workResponse.getWork().getPartGroup().getUuid())) {

            return null;
        }

        final String partGroupUuid = workResponse.getWork().getPartGroup().getUuid();

        return partService.getPartsByGroupUuid(partGroupUuid);
    }

    public boolean authorizeBundleView
		(
			final long bundleId,
			final ExtendedUserDetails user
		) {

        return workBundleService.authorizeBundleView(bundleId, user);
    }

    public boolean authorizeBundlePendingRouting
		(
			final long bundleId,
			final long userId
		) {

        return workBundleService.authorizeBundlePendingRouting(bundleId, userId);
    }

    public PaymentSummary buildPayment
		(
			final Long workId
		) {

        final PaymentSummaryDTO paymentDTO = paymentSummaryService.generatePaymentSummaryForWork(workId);

        final PaymentSummary paymentSummary = new PaymentSummary();

        if (paymentDTO.getMaxSpendLimit() != null) {
            paymentSummary.setMaxSpendLimit(paymentDTO.getMaxSpendLimit().doubleValue());
        }
        if (paymentDTO.getActualSpendLimit() != null) {
            paymentSummary.setActualSpendLimit(paymentDTO.getActualSpendLimit().doubleValue());
        }
        if (paymentDTO.getBuyerFee() != null) {
            paymentSummary.setBuyerFee(paymentDTO.getBuyerFee().doubleValue());
        }
        if (paymentDTO.getBuyerFeePercentage() != null) {
            paymentSummary.setBuyerFeePercentage(paymentDTO.getBuyerFeePercentage().doubleValue());
        }
        if (paymentDTO.getBuyerFeeBand() != null) {
            paymentSummary.setBuyerFeeBand(paymentDTO.getBuyerFeeBand());
        }
        if (paymentDTO.getTotalCost() != null) {
            paymentSummary.setTotalCost(paymentDTO.getTotalCost().doubleValue());
        }

        if (paymentDTO.getLegacyBuyerFee() != null) {
            paymentSummary.setLegacyBuyerFee(paymentDTO.getLegacyBuyerFee());
        }

        if (paymentDTO.getHoursWorked() != null) {
            paymentSummary.setHoursWorked(paymentDTO.getHoursWorked().doubleValue());
        }
        if (paymentDTO.getUnitsProcessed() != null) {
            paymentSummary.setUnitsProcessed(paymentDTO.getUnitsProcessed().doubleValue());
        }
        if (paymentDTO.getAdditionalExpenses() != null) {
            paymentSummary.setAdditionalExpenses(paymentDTO.getAdditionalExpenses().doubleValue());
        }
        if (paymentDTO.getAdditionalExpensesWithFee() != null) {
            paymentSummary.setAdditionalExpensesWithFee(paymentDTO.getAdditionalExpensesWithFee().doubleValue());
        }
        if (paymentDTO.getBonus() != null) {
            paymentSummary.setBonus(paymentDTO.getBonus().doubleValue());
        }
        if (paymentDTO.getBonusWithFee() != null) {
            paymentSummary.setBonusWithFee(paymentDTO.getBonusWithFee().doubleValue());
        }

        if (paymentDTO.getSalesTaxCollectedFlag() != null) {
            paymentSummary.setSalesTaxCollectedFlag(paymentDTO.getSalesTaxCollectedFlag());
        }
        if (paymentDTO.getSalesTaxCollected() != null) {
            paymentSummary.setSalesTaxCollected(paymentDTO.getSalesTaxCollected().doubleValue());
        }
        if (paymentDTO.getSalesTaxRate() != null) {
            paymentSummary.setSalesTaxRate(paymentDTO.getSalesTaxRate().doubleValue());
        }

        if (paymentDTO.getPaidOn() != null) {
            paymentSummary.setPaidOn(paymentDTO.getPaidOn().getTimeInMillis());
        }
        if (paymentDTO.getPaymentDueOn() != null) {
            paymentSummary.setPaymentDueOn(paymentDTO.getPaymentDueOn().getTimeInMillis());
        }

        if (paymentDTO.getPerHourPriceWithFee() != null) {
            paymentSummary.setPerHourPriceWithFee(paymentDTO.getPerHourPriceWithFee().doubleValue());
        }
        if (paymentDTO.getPerUnitPriceWithFee() != null) {
            paymentSummary.setPerUnitPriceWithFee(paymentDTO.getPerUnitPriceWithFee().doubleValue());
        }
        if (paymentDTO.getInitialPerHourPriceWithFee() != null) {
            paymentSummary.setInitialPerHourPriceWithFee(paymentDTO.getInitialPerHourPriceWithFee().doubleValue());
        }
        if (paymentDTO.getAdditionalPerHourPriceWithFee() != null) {
            paymentSummary.setAdditionalPerHourPriceWithFee(paymentDTO.getAdditionalPerHourPriceWithFee().doubleValue());
        }

        return paymentSummary;
    }
}
