package com.workmarket.api.v2.worker.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.worker.fulfillment.AssignmentFulfillmentProcessor;
import com.workmarket.api.v2.worker.fulfillment.FulfillmentPayloadDTO;
import com.workmarket.api.v2.worker.model.FeedRequestDTO;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.velvetrope.guest.WebGuest;
import com.workmarket.domains.velvetrope.rope.HideWorkFeedRope;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.utility.StringUtilities;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.forms.feed.FeedRequestParams;
import com.workmarket.web.validators.FeedRequestParamsValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = {"Assignments"})
@RequestMapping("/worker/v2/feed")
@Controller(value = "workerFeedController")
public class FeedController extends ApiBaseController {

	private static final Log logger = LogFactory.getLog(FeedController.class);

	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private FeedRequestParamsValidator feedRequestParamsValidator;
	@Autowired private AssignmentFulfillmentProcessor assignmentFulfillmentProcessor;
	@Autowired @Qualifier("hideWorkFeedDoorman") Doorman doorman;

	@ApiOperation("Get the assignment feed for the currently logged in user")
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getFeed(@ApiParam @ApiArgumentResolver FeedRequestDTO feedRequestDTO, final HttpServletRequest request) {

		ExtendedUserDetails user = getCurrentUser();

		final FeedRequestParams params = convertDTOToFeedRequestParams(user, feedRequestDTO);

		final FulfillmentPayloadDTO results = assignmentFulfillmentProcessor.getFeedPage(user, request, params);

		return new ApiV2Response(new ApiJSONPayloadMap(), results.getPayload(), results.getPagination());
	}

	private FeedRequestParams convertDTOToFeedRequestParams(ExtendedUserDetails user, FeedRequestDTO feedRequestDTO) {

		ProfileDTO profile = profileService.findProfileDTO(user.getId());
		Assert.notNull(profile);

		FeedRequestParams params = new FeedRequestParams();

		doorman.welcome(
			new WebGuest(user),
			new HideWorkFeedRope(params, user.getCompanyId())
		);

		params.setK(feedRequestDTO.getKeyword());
		if (feedRequestDTO.getIndustryId() != null) {
			params.setI(feedRequestDTO.getIndustryId().toString());
		}
		if (feedRequestDTO.getLatitude() != null) {
			params.setLat(feedRequestDTO.getLatitude().toString());
		}
		if (feedRequestDTO.getLongitude() != null) {
			params.setLon(feedRequestDTO.getLongitude().toString());
		}
		if (feedRequestDTO.getRadius() != null) {
			params.setD(feedRequestDTO.getRadius().toString());
		}
		if (feedRequestDTO.getVirtual() != null) {

			params.setV(feedRequestDTO.getVirtual());

			if (feedRequestDTO.getVirtual() == false) {

				params.setPostalCode(profile.getPostalCode());

				if (!StringUtilities.all(params.getLatitude(), params.getLongitude())) {


					Coordinate coords = profileService.findLatLongForUser(user.getId());

					if (coords != null) {

						params.setLatitude(coords.getLatitude() != null ? coords.getLatitude().toString() : "");
						params.setLongitude(coords.getLongitude() != null ? coords.getLongitude().toString() : "");
					}
				}
			}
		}
		params.setW(feedRequestDTO.getWhen());
		// TODO API - see how I handled this in FeedRequestDTO(builder) constructor
		//		if (feedRequestDTO.getPageSize() == null) {
		//			feedRequestDTO.setPageSize(MobileDashboardService.DEFAULT_ASSIGNMENT_FEED_PAGE_SIZE);
		//		}

		final int start = (feedRequestDTO.getPage() - 1) * feedRequestDTO.getPageSize();

		params.setS(start);
		params.setL(feedRequestDTO.getPageSize());
		params.setStartDate(feedRequestDTO.getStartDate());
		params.setEndDate(feedRequestDTO.getEndDate());
		params.setFilter(feedRequestDTO.getFilter());
		params.setFilterOutApplied(feedRequestDTO.isFilterOutApplied());

		if (CollectionUtils.isEmpty(feedRequestDTO.getSort())) {
			params.setSort(ImmutableList.of("-" + WorkSearchableFields.SEND_DATE.getName()));
		} else {
			params.setSort(feedRequestDTO.getSort());
		}

		/*
					BindingResult binding = new DataBinder(params).getBindingResult();
          feedRequestParamsValidator.validate(params, binding);
        */

		return params;
	}
}
