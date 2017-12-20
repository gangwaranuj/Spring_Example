package com.workmarket.web.controllers.assignments;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.resource.LiteResource;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.dto.AddressDTOUtilities;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.SpamSlayer;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.RatingStarsHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.workmarket.utility.NumberUtilities.defaultValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller for generating the Workers tab on the assignment detail page
 */
@Controller
@RequestMapping("/assignments")
public class WorkDetailsWorkerController extends BaseWorkController {

	@RequestMapping(
		value = "/{workNumber}/workers",
		method = GET)
	public @ResponseBody Map<String, Object> resources(
		@PathVariable String workNumber,
		@RequestParam(defaultValue = "0") Integer start,
		@RequestParam(defaultValue = "10") Integer limit,
		@RequestParam(defaultValue = "ACTIVE_AND_LAST_NAME") String sortColumn,
		@RequestParam(defaultValue = "ASC") String sortDirection) throws Exception {

		final WorkResponse workResponse;
		try {
			workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN,
				AuthorizationContext.DISPATCHER
			), "resource");
		} catch (Exception e) {
			return CollectionUtilities.newObjectMap();
		}

		Long workId = workResponse.getWork().getId();

		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();
		pagination.setStartRow(start);
		pagination.setResultsLimit(limit);
		pagination.setSortDirection(Pagination.SORT_DIRECTION.valueOf(sortDirection));
		pagination.setSortColumn(WorkResourceDetailPagination.SORTS.valueOf(sortColumn));
		pagination.setIncludeApplyNegotiation(true);
		pagination.setIncludeLabels(true);

		if (workResponse.getRequestContexts().contains(RequestContext.DISPATCHER)) {
			if (!WorkStatusType.SENT.equals(workResponse.getWork().getStatus().getCode()) &&
				!WorkStatusType.DECLINED.equals(workResponse.getWork().getStatus().getCode())) {
				return CollectionUtilities.newObjectMap();
			}
			pagination.addFilter(
				WorkResourceDetailPagination.FILTER_KEYS.WORK_RESOURCE_COMPANY_ID, getCurrentUser().getCompanyId()
			);
		}

		ResourceRowMapper rowMapper = new ApplicantResourceRowMapper();
		rowMapper.setWorkResponse(workResponse);

		pagination = workResourceService.findAllResourcesForWork(workId, pagination);

		return CollectionUtilities.newObjectMap(
			"results", Lists.transform(pagination.getResults(), rowMapper),
			"total_results", pagination.getRowCount()
		);
	}

	// This loads just the lat, lng of the resources.
	@RequestMapping(
		value = "/{workNumber}/lite-resources",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder liteResources(@PathVariable String workNumber) throws Exception {
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();
		if (workNumber == null) {
			return response;
		}
		List<LiteResource> results = workService.findLiteResourceByWorkNumber(workNumber);

		return response.setSuccessful(true).addData("results", results);
	}

	// This load all the workers with full info
	@RequestMapping(
		value = "/{workNumber}/full-workers",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder fullResources(@PathVariable String workNumber) throws Exception {
		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		final WorkResponse workResponse;
		try {
			workResponse = getWork(workNumber, ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.STATUS_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
			), ImmutableSet.of(
				AuthorizationContext.ADMIN
			), "resource");
		} catch (Exception e) {
			return response;
		}

		Long workId = workResponse.getWork().getId();
		boolean requiresApplication = !workResponse.getWork().getConfiguration().isAssignToFirstResource();
		boolean isSentStatus = WorkStatusType.SENT.equals(workResponse.getWork().getStatus().getCode());

		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();
		pagination.setReturnAllRows(true);

		ResourceRowMapper rowMapper;

		if (requiresApplication && isSentStatus) {
			pagination.setIncludeApplyNegotiation(true);
			rowMapper = new ApplicantResourceRowMapper();
		} else {
			pagination
				.setIncludeLabels(true)
				.setIncludeNotes(true);
			rowMapper = new DefaultResourceRowMapper();
		}

		rowMapper.setWorkResponse(workResponse);

		pagination = workResourceService.findAllResourcesForWork(workId, pagination);

		return response
			.setSuccessful(true)
			.addData("results", Lists.transform(pagination.getResults(), rowMapper))
			.addData("total_results", pagination.getRowCount());

	}

	// Utility Guava Function classes for transforming a WorkResourceDetail to a Map
	// for JSON responses to build the resource lists on an assignment detail.

	private abstract class ResourceRowMapper implements Function<WorkResourceDetail, Object> {
		WorkResponse workResponse;

		public void setWorkResponse(WorkResponse workResponse) {
			this.workResponse = workResponse;
		}
	}

	private class DefaultResourceRowMapper extends ResourceRowMapper {
		@Override
		public Object apply(@Nullable WorkResourceDetail res) {
			if (res != null) {
				List<Object> notes = Lists.transform(res.getNotes(), new Function<ResourceNote, Object>() {
					@Override
					public Object apply(@Nullable ResourceNote n) {
						if (n != null) {
							return CollectionUtilities.newObjectMap(
								"type", n.getHoverType(),
								"note", n.getNote(),
								"action_code", n.getActionCodeDescription(),
								"date", DateUtilities.formatMillis("m/d/Y g:ia T", n.getDateOfNote(), workResponse.getWork().getTimeZone()),
								"deputy", CollectionUtilities.newObjectMap(
									"first_name", n.getOnBehalfOfUser().getName().getFirstName(),
									"last_name", n.getOnBehalfOfUser().getName().getLastName(),
									"is_employee", n.getOnBehalfOfUser().isIsWorkMarketEmployee())
							);
						}
						return Collections.EMPTY_MAP;
					}
				});

				String companyRatingText = RatingStarsHelper.convertRatingValueToText(
					defaultValue(res.getResourceCompanyScoreCard().getRating().getSatisfactionRate()).intValue());

				return CollectionUtilities.newObjectMap(
					"user_id", res.getUserId(),
					"user_number", res.getUserNumber(),
					"name", StringUtilities.fullName(res.getFirstName(), res.getLastName()),
					"company_name", res.getCompanyName(),
					"email", SpamSlayer.slay(res.getEmail()),
					"avatar_uri", res.getAvatarUri(),
					"lane", res.getLaneType().getColumn(),
					"status", res.getWorkResourceStatusTypeCode(),
					"sent_on", DateUtilities.format("MM/d/yyyy h:mm a z", res.getInvitedOn(), workResponse.getWork().getTimeZone()),
					"declined_on", DateUtilities.format("MM/d/yyyy @ h:mm a z", res.getDeclinedDate(), workResponse.getWork().getTimeZone()),
					"work_phone", StringUtilities.formatPhoneNumber(res.getWorkPhone()),
					"mobile_phone", StringUtilities.formatPhoneNumber(res.getMobilePhone()),
					"address", AddressDTOUtilities.formatAddressShort(res.getAddress()),
					"latitude", res.getLatitude(),
					"longitude", res.getLongitude(),
					"distance", res.getDistance(),
					"question_pending", res.isQuestionPending(),
					"latest_negotiation_pending", res.isLatestNegotiationPending(),
					"latest_negotiation_declined", res.isLatestNegotiationDeclined(),
					"latest_negotiation_expired", res.isLatestNegotiationExpired(),
					"notes", notes,
					"labels", res.getLabels(),
					"rating_text", RatingStarsHelper.convertRatingValueToText(res.getRating()),
					"company_rating_text", companyRatingText,
					"resource_scorecard", res.getResourceScoreCard(),
					"resource_scorecard_for_company", res.getResourceCompanyScoreCard(),
					"schedule_conflict", res.hasScheduleConflict()
				);
			}
			return null;
		}
	}

	private class ApplicantResourceRowMapper extends ResourceRowMapper {
		@Override
		public Object apply(@Nullable WorkResourceDetail res) {
			if (res != null) {
				WorkNegotiation n = res.getApplyNegotiation();
				Map<String, Object> negotiation = null;
				boolean hasNegotiation = false;

				if (n != null) {
					negotiation = CollectionUtilities.newObjectMap(
						"encrypted_id", n.getEncryptedId(),
						"approval_status", n.getApprovalStatus().ordinal(),
						"requested_on", n.getRequestedOn().getTime(),
						"requested_on_date", DateUtilities.format("MM/d/yyyy h:mm a", n.getRequestedOn().getTime(), workResponse.getWork().getTimeZone()),
						"requested_on_fuzzy", DateUtilities.fuzzySpan(n.getRequestedOn().getTime()),
						"expires_on", n.hasExpirationDate() ? DateUtilities.format("E, MM/d/yyyy h:mm a z", n.getExpiresOn(), workResponse.getWork().getTimeZone()) : "",
						"is_expired", DateUtilities.isInPast(n.getExpiresOn()),
						"is_price_negotiation", n.isPriceNegotiation(),
						"is_schedule_negotiation", n.isScheduleNegotiation(),
						"is_best_price", res.isBestPrice(),
						"note", res.getApplyNegotiationNote()
					);

					hasNegotiation = true;

					if (n.isPriceNegotiation()) {
						FullPricingStrategy p = n.getFullPricingStrategy();
						CollectionUtilities.addToObjectMap(negotiation, "pricing", CollectionUtilities.newObjectMap(
							"id", n.getPricingStrategy().getId(),
							"type", n.getFullPricingStrategy().getPricingStrategyType().name(),
							"spend_limit", res.getApplyNegotiationSpendLimit(),
							"fee", res.getApplyNegotiationFee(),
							"total_cost", res.getApplyNegotiationTotalCost(),
							"flat_price", p.getFlatPrice(),
							"per_hour_price", p.getPerHourPrice(),
							"max_number_of_hours", p.getMaxNumberOfHours(),
							"per_unit_price", p.getPerUnitPrice(),
							"max_number_of_units", p.getMaxNumberOfUnits(),
							"initial_per_hour_price", p.getInitialPerHourPrice(),
							"initial_number_of_hours", p.getInitialNumberOfHours(),
							"additional_per_hour_price", p.getAdditionalPerHourPrice(),
							"max_blended_number_of_hours", p.getMaxBlendedNumberOfHours(),
							"additional_expenses", p.getAdditionalExpenses()
						));
					}

					if (n.isScheduleNegotiation()) {
						CollectionUtilities.addToObjectMap(
							negotiation, "schedule",
							DateRangeUtilities.format("MM/d/yyyy h:mm a", "MM/d/yyyy h:mm a z", " - ", n.getSchedule(), workResponse.getWork().getTimeZone()));
					}
				}

				String companyRatingText = RatingStarsHelper.convertRatingValueToText(defaultValue(res.getResourceCompanyScoreCard().getRating().getSatisfactionRate()).intValue());

				Map<String, Object> result = CollectionUtilities.newObjectMap(
					"user_id", res.getUserId(),
					"user_number", res.getUserNumber(),
					"name", StringUtilities.fullName(res.getFirstName(), res.getLastName()),
					"company_name", res.getCompanyName(),
					"email", res.getEmail(),
					"avatar_uri", res.getAvatarUri(),
					"lane", res.getLaneType() != null ? res.getLaneType().ordinal() : null,
					"status", res.getWorkResourceStatusTypeCode(),
					"sent_on", DateUtilities.format("MM/d/yyyy h:mm a z", res.getInvitedOn(), workResponse.getWork().getTimeZone()),
					"declined_on", DateUtilities.format("MM/d/yyyy @ h:mm a z", res.getDeclinedDate(), workResponse.getWork().getTimeZone()),
					"work_phone", StringUtilities.formatPhoneNumber(res.getWorkPhone()),
					"mobile_phone", StringUtilities.formatPhoneNumber(res.getMobilePhone()),
					"address", AddressDTOUtilities.formatAddressShort(res.getAddress()),
					"distance", res.getDistance(),
					"latitude", res.getLatitude(),
					"longitude", res.getLongitude(),
					"question_pending", res.isQuestionPending(),
					"has_negotiation", hasNegotiation,
					"negotiation", negotiation,
					"targeted", res.isTargeted(),
					"blocked", res.isBlocked(),
					"new_user", DateUtilities.getDaysBetweenFromNow(res.getJoinedOn()) < Constants.NEW_USER_DAYS,
					"rating_text", RatingStarsHelper.convertRatingValueToText(res.getRating()),
					"company_rating_text", companyRatingText,
					"resource_scorecard", res.getResourceScoreCard(),
					"resource_scorecard_for_company", res.getResourceCompanyScoreCard(),
					"labels", res.getLabels(),
					"schedule_conflict", res.hasScheduleConflict(),
					"assign_to_first_to_accept", res.isAssignToFirstToAccept(),
					"dispatcher", res.getDispatcher()
				);

				if (workResponse.getRequestContexts().contains(RequestContext.DISPATCHER)) {
					result.put("eligibility", eligibilityService.getEligibilityFor(res.getUserId(), workResponse.getWork()));
				}

				return result;
			}
			return null;
		}
	}
}
