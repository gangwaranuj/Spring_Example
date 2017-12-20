package com.workmarket.web.controllers.assignments;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/assignments")
public class WorkFollowController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkFollowController.class);

	@Autowired private WorkFollowService workFollowService;

	@RequestMapping(value = "/{workNumber}/followers", method = RequestMethod.GET)
	@ResponseBody public Map<String, Object> followers(
		@PathVariable final String workNumber) throws Exception {

		if (!workService.isAuthorizedToAdminister(getCurrentUser().getId(), getCurrentUser().getCompanyId(), workNumber)) {
			return CollectionUtilities.newObjectMap();
		}

		List<WorkFollowDTO> workFollowers = workFollowService.getWorkFollowDTOs(workNumber);
		List<Map<String, Object>> results = Lists.newArrayListWithExpectedSize(workFollowers.size());
		for (WorkFollowDTO workFollowDTO : workFollowers) {
			results.add(CollectionUtilities.newObjectMap(
				"id", workFollowDTO.getId(),
				"fullName", workFollowDTO.getFollowerFullName()
			));
		}

		return CollectionUtilities.newObjectMap("results", results);
	}

	@RequestMapping(
		value = "/toggleFollow/{workNumber}",
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AjaxResponseBuilder toggleFollow(
		@PathVariable final String workNumber) {

		AjaxResponseBuilder response = new AjaxResponseBuilder();

		if (!workService.isAuthorizedToAdminister(getCurrentUser().getId(), getCurrentUser().getCompanyId(), workNumber)) {
			throw new HttpException401(messageHelper.getMessage("toggleFollow.invalid"));
		}

		return response.setSuccessful(workFollowService.toggleFollowWork(workNumber, getCurrentUser().getId()));
	}

	@RequestMapping(
		value = "/remove_follower/{workNumber}/{workFollowId}",
		method = RequestMethod.GET,
		produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AjaxResponseBuilder removeFollower(
		@PathVariable final String workNumber,
		@PathVariable final long workFollowId) {

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder();

		if (!workService.isAuthorizedToAdminister(getCurrentUser().getId(), getCurrentUser().getCompanyId(), workNumber)) {
			throw new HttpException401(messageHelper.getMessage("removeFollower.invalid"));
		}

		Optional<WorkFollow> workFollowOptional = workFollowService.removeWorkFollower(workNumber, workFollowId);
		if (workFollowOptional.isPresent()) {
			responseBody.setSuccessful(true);
			responseBody.setData(CollectionUtilities.newObjectMap(
				"removed_current_user", workFollowOptional.get().getUser().getId().equals(getCurrentUser().getId())
			));
		} else {
			responseBody.setSuccessful(false);
		}

		return responseBody;
	}

	@RequestMapping(
		value = "/add_followers/{workNumber}",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public AjaxResponseBuilder addFollowers(
		@PathVariable final String workNumber,
		@RequestParam final String followers) {

		AjaxResponseBuilder responseBody = new AjaxResponseBuilder();

		if (!workService.isAuthorizedToAdminister(getCurrentUser().getId(), getCurrentUser().getCompanyId(), workNumber)) {
			throw new HttpException401(messageHelper.getMessage("addFollowers.invalid"));
		}

		if (StringUtils.isBlank(followers)) {
			responseBody.setSuccessful(false);
			return responseBody;
		}

		List<Long> followerIds = new ArrayList<>();

		for (String followerId : ImmutableSet.copyOf(followers.split(","))) {
			try {
				followerIds.add(Long.valueOf(followerId));
			} catch (Exception e) {
				logger.debug("Attempted to add invalid follower id: " + followerId);
			}
		}

		workFollowService.saveFollowers(workNumber, followerIds, false);
		responseBody.setSuccessful(true);

		boolean addedCurrentUser = false;

		for (Long followerId : followerIds) {
			if (followerId.equals(getCurrentUser().getId())) {
				addedCurrentUser = true;
				break;
			}
		}

		responseBody.setData(CollectionUtilities.newObjectMap(
			"added_current_user", addedCurrentUser
		));

		return responseBody;
	}
}
