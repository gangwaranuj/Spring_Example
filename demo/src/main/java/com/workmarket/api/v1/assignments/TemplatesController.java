
package com.workmarket.api.v1.assignments;

import com.google.common.collect.ImmutableSet;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.ApiHelper;
import com.workmarket.api.v1.ApiV1Response;
import com.workmarket.api.v1.model.ApiTemplateDTO;
import com.workmarket.api.v1.model.ApiTemplateListItemDTO;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.service.WorkTemplateService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException403;
import com.workmarket.web.exceptions.HttpException404;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(tags = "Assignments")
@Controller("apiTemplatesController")
@RequestMapping(value = {"/v1/employer/assignments/templates", "/api/v1/assignments/templates"})
public class TemplatesController extends ApiBaseController {

	private static final Logger logger = LoggerFactory.getLogger(TemplatesController.class);

	private static final Set<WorkRequestInfo> TEMPLATE_INCLUDES = ImmutableSet.<WorkRequestInfo>builder()
		.add(WorkRequestInfo.CONTEXT_INFO)
		.add(WorkRequestInfo.COMPANY_INFO)
		.add(WorkRequestInfo.STATUS_INFO)
		.add(WorkRequestInfo.INDUSTRY_INFO)
		.add(WorkRequestInfo.PROJECT_INFO)
		.add(WorkRequestInfo.CLIENT_COMPANY_INFO)
		.add(WorkRequestInfo.BUYER_INFO)
		.add(WorkRequestInfo.LOCATION_CONTACT_INFO)
		.add(WorkRequestInfo.SUPPORT_CONTACT_INFO)
		.add(WorkRequestInfo.LOCATION_INFO)
		.add(WorkRequestInfo.SCHEDULE_INFO)
		.add(WorkRequestInfo.PRICING_INFO)
		.add(WorkRequestInfo.ASSETS_INFO)
		.add(WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO)
		.add(WorkRequestInfo.PARTS_INFO)
		.add(WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO)
		.add(WorkRequestInfo.PAYMENT_INFO)
		.add(WorkRequestInfo.REQUIRED_ASSESSMENTS_INFO)
		.add(WorkRequestInfo.DELIVERABLES_INFO)
		.build();

	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkTemplateService workTemplateService;
	@Autowired private ApiHelper apiHelper;
	@Autowired private AuthenticationService authenticationService;

	/**
	 * List templates.
	 * @return ApiResponse
	 */
	@ApiOperation(value = "List templates")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiTemplateListItemDTO>> listAssignmentTemplatesAction(
			@RequestParam(required = false, value = "client_id") final Long clientId) {
		ApiV1Response<List<ApiTemplateListItemDTO>> apiResponse = new ApiV1Response<>();
		Long companyId = authenticationService.getCurrentUser().getCompany().getId();

		logger.debug("retrieving template list for companyId={}", companyId);

		Map<String, Map<String, Object>> templates =
				workTemplateService.findAllActiveWorkTemplatesWorkNumberNameMap(companyId, clientId);

		if (logger.isDebugEnabled()) {
			logger.debug("found {} templates for companyId={}", templates.size(), companyId);
		}

		List<ApiTemplateListItemDTO> templateList = new ArrayList<>();

		for (Map.Entry<String, Map<String, Object>> entry : templates.entrySet()) {
			templateList.add(new ApiTemplateListItemDTO.Builder()
				.withTemplateId(entry.getKey())
				.withName((String) entry.getValue().get("template_name"))
				.withClientId((Long) entry.getValue().get("client_id"))
				.build()
			);
		}

		apiResponse.setResponse(templateList);
		return apiResponse;
	}

	/**
	 * Get a template.
	 * @param workNumber is work assignment number
	 * @return Ajax response
	 */
	@ApiOperation(value = "Get template")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/get", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<ApiTemplateDTO> getAssignmentTemplateAction(@RequestParam(value="id", required=false) String workNumber) {
		logger.debug("retrieving template for requested workNumber={}", workNumber);

		workNumber = StringUtilities.defaultString(workNumber, null);

		// Make sure the Id was passed
		if (StringUtils.isEmpty(workNumber)) {
			throw new HttpException400("Missing template number.");
		}

		WorkResponse workResponse =
				loadWorkWithIncludes(authenticationService.getCurrentUserId(), authenticationService.getCurrentUserCompanyId(), workNumber, TEMPLATE_INCLUDES);

		Work work = workResponse.getWork();
		ApiTemplateDTO template = new ApiTemplateDTO.Builder(apiHelper.buildAssignmentForApi(work))
			.withTemplateName(work.getTemplate().getName())
			.withTemplateDescription(work.getTemplate().getDescription())
			.build();

		ApiV1Response<ApiTemplateDTO> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(template);

		return apiResponse;
	}

	protected WorkResponse loadWorkWithIncludes(
			final Long currentUserId,
			final Long currentUserCompanyId,
			final String workNumber,
			final Set<WorkRequestInfo> includes) {
		WorkRequest workRequest = new WorkRequest();
		workRequest.setUserId(currentUserId);
		workRequest.setWorkNumber(workNumber);
		workRequest.setIncludes(includes);

		WorkResponse workResponse;

		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		}
		catch (Exception ex) {
			throw new HttpException404("The template you are looking for does not exist.", ex);
		}

		// If you found nuts, error out.
		if (workResponse == null) {
			throw new HttpException404("The template you are looking for does not exist.");
		}

		// Does the current user have access to this work assignment?
		if (!currentUserCompanyId.equals(workResponse.getWork().getCompany().getId())) {
			throw new HttpException403("You do not have access to the requested template.");
		}

		// If buyer had selected "I want to spend", then we need to convert everything back to the buyer's perception of spend limit.
		if (workResponse.getWork().getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
			double workFee = workResponse.getWork().getPayment().getBuyerFeePercentage() / 100D;
			long pricingStrategy = workResponse.getWork().getPricing().getId();

			if ((PricingStrategyType.FLAT.ordinal() + 1) == pricingStrategy) {
				double price = workResponse.getWork().getPricing().getFlatPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setFlatPrice( Math.round(price) );
				
			} else if ((PricingStrategyType.PER_HOUR.ordinal() + 1) == pricingStrategy) {
				double price = workResponse.getWork().getPricing().getPerHourPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setPerHourPrice( Math.round(price) );
				
			} else if ((PricingStrategyType.PER_UNIT.ordinal() + 1) == pricingStrategy) {
				double price = workResponse.getWork().getPricing().getPerUnitPrice() * (1D + workFee);
				workResponse.getWork().getPricing().setPerUnitPrice( Math.round(price) );
				
			} else if ((PricingStrategyType.BLENDED_PER_HOUR.ordinal() + 1) == pricingStrategy) {
				double price = workResponse.getWork().getPricing().getInitialPerHourPrice() * (1D + workFee);
				price = ((long)(price * 100D))/100D;
				workResponse.getWork().getPricing().setInitialPerHourPrice( Math.round(price) );

				price = workResponse.getWork().getPricing().getAdditionalPerHourPrice() * (1 + workFee);
				price = ((long)(price * 100D))/100D;
				workResponse.getWork().getPricing().setAdditionalPerHourPrice(Math.round(price));
			}
		}

		return workResponse;
	}
}
