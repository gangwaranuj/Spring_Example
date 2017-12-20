package com.workmarket.web.controllers.lms.admin;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.thrift.assessment.*;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;
import java.util.Set;

public class BaseAssessmentAdminController extends BaseController {

	@Autowired protected ProfileService profileService;
	@Autowired protected FormOptionsDataHelper formOptionsDataHelper;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired protected AssessmentServiceFacade thriftAssessmentService;
	@Autowired protected AssessmentService assessmentService;

	@ModelAttribute("AssessmentType")
	private Map<String,Object> getAssessmentTypes() {
		return ModelEnumUtilities.assessmentTypes;
	}

	@ModelAttribute("AssessmentItemType")
	private Map<String,Object> getAssessmentItemTypes() {
		return ModelEnumUtilities.assessmentItemTypes;
	}

	protected Assessment getAssessment(Long id, AssessmentRequestInfo... info) {
		Set<AssessmentRequestInfo> requestInfo = (ArrayUtils.isEmpty(info)) ?
			Sets.newHashSet(
				AssessmentRequestInfo.CONTEXT_INFO,
				AssessmentRequestInfo.ITEM_INFO
			) : Sets.newHashSet(info);

		AssessmentRequest request = new AssessmentRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(id)
			.setIncludes(requestInfo);

		try {
			AssessmentResponse response = thriftAssessmentService.findAssessment(request);
			if (response.getAssessment().getStatus().getCode().equals(AssessmentStatusType.REMOVED)) {
				throw new HttpException404();
			}
			if (!CollectionUtilities.containsAny(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
				throw new HttpException401();
			}
			return response.getAssessment();
		} catch (AssessmentRequestException e) {
			throw new HttpException404(e);
		} catch (HttpException401 e){
			throw e;
		} catch (Exception e) {
			throw new HttpException404(e);
		}
	}

	public FormOptionsDataHelper getDataHelper() {
		return formOptionsDataHelper;
	}

}
