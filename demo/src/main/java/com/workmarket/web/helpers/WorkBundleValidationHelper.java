package com.workmarket.web.helpers;

import ch.lambdaj.group.Group;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;

@Component
public class WorkBundleValidationHelper {

	private static final Log logger = LogFactory.getLog(WorkBundleValidationHelper.class);

	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkBundleRouting workBundleRouting;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkSearchService workSearchService;

	public static final String VALIDATION_ERRORS = "validationErrors";
	public static final String VALIDATION_SUCCESSES = "validationSuccesses";
	private static final int MAX_BUNDLE_ASSIGNMENTS = 250;

	// called from Cart Controller, This is one way a bundle is sent.
	public Multimap<String, ValidateWorkResponse> readyToSend(String bundleWorkNumber, Long userId, MessageBundle messages) {
		Assert.notNull(bundleWorkNumber);
		Set<Work> workInBundle = workBundleService.getAllWorkInBundle(bundleWorkNumber);
		return readyToSend(workInBundle, userId, messages);
	}

	// called from doSaveBundle. This is the other way a bundle is sent.
	public Multimap<String, ValidateWorkResponse> readyToSend(Long bundleId, Long userId, MessageBundle messages) {
		Assert.notNull(bundleId);
		Set<Work> workInBundle = workBundleService.getAllWorkInBundle(bundleId);
		return readyToSend(workInBundle, userId, messages);
	}

	// called from the 2 above readyToSend methods. Only called when sending bundle.
	public Multimap<String, ValidateWorkResponse> readyToSend(Set<Work> workInBundle, Long userId, MessageBundle messages) {
		Assert.notNull(workInBundle);
		List<String> workNumbers = extract(workInBundle, on(Work.class).getWorkNumber());
		return readyToSend(workNumbers, userId, messages);
	}

	// called from both the send path (readyToSend immediately above), and the add path(validateWorkBundle) and from the create path (validateWorkBundle).
	public Multimap<String, ValidateWorkResponse> readyToSend(List<String> workNumbers, Long userId, MessageBundle messages) {
		Assert.notNull(workNumbers);
		Assert.notNull(userId);

		List<ValidateWorkResponse> validationResponses = workBundleService.validateAllBundledWorkForSend(workNumbers, userId);
		Group<ValidateWorkResponse> validationStatusGroup = group(validationResponses, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);
		Collection<ValidateWorkResponse> validationSuccesses = validationStatusGroup.find(true);

		if (!validationErrors.isEmpty()) {
			for (ValidateWorkResponse v : validationErrors) {
				messageHelper.setErrors(messages, v);
			}
		}

		Multimap<String, ValidateWorkResponse> results = ArrayListMultimap.create();
		results.putAll(VALIDATION_ERRORS, validationErrors);
		results.putAll(VALIDATION_SUCCESSES, validationSuccesses);
		return results;
	}

	public Multimap<String, ValidateWorkResponse> readyToAdd(List<String> workNumbers, Long parentId, Long userId, MessageBundle messages) {
		Assert.notNull(workNumbers);
		Assert.notNull(userId);

		List<ValidateWorkResponse> validationResponses = workBundleService.validateAllBundledWorkForAdd(workNumbers, parentId);
		Group<ValidateWorkResponse> validationStatusGroup = group(validationResponses, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);
		Collection<ValidateWorkResponse> validationSuccesses = validationStatusGroup.find(true);

		if (!validationErrors.isEmpty()) {
			for (ValidateWorkResponse v : validationErrors) {
				messageHelper.setErrors(messages, v);
			}
		}

		Multimap<String, ValidateWorkResponse> results = ArrayListMultimap.create();
		results.putAll(VALIDATION_ERRORS, validationErrors);
		results.putAll(VALIDATION_SUCCESSES, validationSuccesses);
		return results;
	}

	// called from create and add
	public WorkBundleValidationResult validateWorkBundle(WorkBundleDTO workBundleDTO, ExtendedUserDetails user, AjaxResponseBuilder response) {
		return validateWorkBundle(workBundleDTO, user.getId(), response);
	}

	// called from upload
	public WorkBundleValidationResult validateWorkBundle(WorkBundleDTO workBundleDTO, Long userId) {
		return validateWorkBundle(workBundleDTO, userId, new AjaxResponseBuilder());
	}

	public WorkBundleValidationResult validateWorkBundle(WorkBundleDTO workBundleDTO, Long userId, AjaxResponseBuilder response) {
		WorkBundleValidationResult result = new WorkBundleValidationResult();

		boolean creating = (workBundleDTO.getId() == null);
		boolean adding = (workBundleDTO.getId() != null);
		boolean gotWork = CollectionUtils.isNotEmpty(workBundleDTO.getWorkNumbers());
		int numWork =  gotWork ? workBundleDTO.getWorkNumbers().size() : 0;
		Multimap<String, ValidateWorkResponse> readyToAdd;
		MessageBundle messages = messageHelper.newBundle();

		result.setStatus(WorkBundleValidationResult.STATUS.FAILURE);
		if (creating) {
			if (!gotWork) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.create.fail.no_work");
				return result;
			}
			if (numWork > MAX_BUNDLE_ASSIGNMENTS) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.create.fail.too_many", numWork, MAX_BUNDLE_ASSIGNMENTS);
				return result;
			}
			readyToAdd = readyToAdd(workBundleDTO.getWorkNumbers(), null, userId, messages);
		} else {
			if (!gotWork) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.no_work");
				return result;
			}
			if (numWork + workBundleDTO.getWorkCount() > MAX_BUNDLE_ASSIGNMENTS) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.too_many", numWork, workBundleDTO.getWorkCount(), MAX_BUNDLE_ASSIGNMENTS);
				return result;
			}
			if (workBundleRouting.isWorkBundlePendingRouting(workBundleDTO.getId())) {
				messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.pending_routing", StringUtilities.pluralize("assignment", workBundleDTO.getWorkNumbers().size()));
				return result;
			}
			readyToAdd = readyToAdd(workBundleDTO.getWorkNumbers(), workBundleDTO.getId(), userId, messages);
		}

		Collection<ValidateWorkResponse> validationErrors = readyToAdd.get(VALIDATION_ERRORS);
		Collection<ValidateWorkResponse> validationSuccesses = readyToAdd.get(VALIDATION_SUCCESSES);
		boolean anyValidationErrors = !validationErrors.isEmpty();
		boolean noWorkToAdd = validationSuccesses.isEmpty();
		response.setMessages(messages.getErrors());

		result.setStatus(WorkBundleValidationResult.STATUS.FAILURE);
		if (noWorkToAdd && creating) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.create.fail.no_valid_work");
			return result;
		} else if (noWorkToAdd && adding) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.no_valid_work");
			return result;
		}

		WorkBundle bundle = null;
		if (creating) {
			bundle = workBundleService.saveOrUpdateWorkBundle(userId, workBundleDTO);
		} else if (adding) {
			bundle = workBundleService.findById(workBundleDTO.getId());
		}

		result.setStatus(WorkBundleValidationResult.STATUS.FAILURE);
		if (bundle != null && !WorkStatusType.DRAFT.equals(bundle.getWorkStatusType().getCode())) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.bad_state");
			return result;
		} else if (bundle == null && creating) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.create.fail.create_failure");
			return result;
		} else if (bundle == null && adding) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.fail.bundle_not_found");
			return result;
		}

		// TODO: MICAH - bundle can be null here, so this could NPE. Plz fix.
		workBundleService.addAllToBundleByWorkNumbers(bundle.getId(), workBundleDTO.getWorkNumbers());

		// if we are here, we have success or partial success, so - time to index!
		workSearchService.workBundleUpdateSearchIndex(workBundleDTO);

		result.setBundle(bundle);
		if (anyValidationErrors && creating) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.create.partial_success", bundle.getTitle(), "" + validationSuccesses.size());
			result.setStatus(WorkBundleValidationResult.STATUS.PARTIAL_SUCCESS);
			return result;
		} else if (anyValidationErrors && adding) {
			messageHelper.addMessage(response.setSuccessful(false), "assignment_bundle.add.partial_success", bundle.getTitle(), "" + validationSuccesses.size());
			result.setStatus(WorkBundleValidationResult.STATUS.PARTIAL_SUCCESS);
			return result;
		} else if (creating) {
			messageHelper.addMessage(response.setSuccessful(true), "assignment_bundle.create.success", bundle.getTitle(), "" + validationSuccesses.size(),
					StringUtilities.pluralize("assignment", workBundleDTO.getWorkNumbers().size()));
			result.setStatus(WorkBundleValidationResult.STATUS.SUCCESS);
			return result;
		} else { //if (adding)
			messageHelper.addMessage(response.setSuccessful(true), "assignment_bundle.add.success", bundle.getTitle(), "" + validationSuccesses.size());
			result.setStatus(WorkBundleValidationResult.STATUS.SUCCESS);
			return result;
		}
	}
}
