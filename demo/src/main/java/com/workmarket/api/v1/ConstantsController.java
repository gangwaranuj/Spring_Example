package com.workmarket.api.v1;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v1.model.ApiDressCodeDTO;
import com.workmarket.api.v1.model.ApiIndustryDTO;
import com.workmarket.api.v1.model.ApiLocationTypeDTO;
import com.workmarket.api.v1.model.ApiSubstatusDTO;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.LocationTypeDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(tags = "Constants")
@Controller("apiConstantsController")
@RequestMapping(value = {"/v1/employer/constants", "/api/v1/constants"})
public class ConstantsController extends ApiBaseController{

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private IndustryService industryService;

	/**
	 * List location types.
	 * @return response
	 */
	@ApiOperation(value = "Location types")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/location_types", method=RequestMethod.GET)
	@ResponseBody
	public ApiV1Response<List<ApiLocationTypeDTO>> locationTypes() {

		List<LocationTypeDTO> locationTypes = invariantDataService.getLocationTypeDTOs();
		List<ApiLocationTypeDTO> apiLocationTypeDTOs = Lists.transform(locationTypes, new Function<LocationTypeDTO, ApiLocationTypeDTO>() {
			@Override
			public ApiLocationTypeDTO apply(LocationTypeDTO locationTypeDTO) {
				return new ApiLocationTypeDTO.Builder()
					.withId(locationTypeDTO.getId())
					.withName(locationTypeDTO.getDescription())
					.build();
			}
		});

		ApiV1Response<List<ApiLocationTypeDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(apiLocationTypeDTOs);
		return apiResponse;
	}

	/**
	 * List dress codes.
	 * @return response
	 */
	@ApiOperation(value = "List dress codes")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@Deprecated
	@RequestMapping(value="/dress_codes", method=RequestMethod.GET)
	public @ResponseBody
	ApiV1Response<List<ApiDressCodeDTO>> dressCodes() {

		List<ApiDressCodeDTO> dressCodeDTOs = Lists.transform(invariantDataService.getDressCodes(), new Function<DressCode, ApiDressCodeDTO>() {
			@Override
			public ApiDressCodeDTO apply(DressCode dressCode) {
				return new ApiDressCodeDTO.Builder()
					.withId(dressCode.getId())
					.withName(dressCode.getDescription())
					.build();
			}
		});

		ApiV1Response<List<ApiDressCodeDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(dressCodeDTOs);
		return apiResponse;
	}

	/**
	 * List industries.
	 * @return response
	 */
	@ApiOperation(value = "List industry IDs")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/industries", method=RequestMethod.GET)
	public @ResponseBody
	ApiV1Response<List<ApiIndustryDTO>> industries() {

		List<ApiIndustryDTO> apiIndustryDTOs = Lists.transform(industryService.getAllIndustryDTOs(), new Function<IndustryDTO, ApiIndustryDTO>() {
			@Override
			public ApiIndustryDTO apply(IndustryDTO industryDTO) {
				return new ApiIndustryDTO.Builder()
					.withId(industryDTO.getId())
					.withName(industryDTO.getName())
					.build();
			}
		});

		ApiV1Response<List<ApiIndustryDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(apiIndustryDTOs);
		return apiResponse;
	}

	/**
	 * List substatuses.
	 * @return ApiResponse
	 * @deprecated
	 */
	@ApiOperation(value = "List substatuses")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@Deprecated
	@RequestMapping(value="/substatuses", method=RequestMethod.GET)
	public @ResponseBody
	ApiV1Response<List<ApiSubstatusDTO>> subStatuses() {
		Long companyId = authenticationService.getCurrentUser().getCompany().getId();

		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		filter.setShowDeactivated(false);
		filter.setClientVisible(true);
		filter.setResourceVisible(true);

		List<WorkSubStatusType> subStatuses = workSubStatusService.findAllSubStatuses(companyId, filter);

		List<ApiSubstatusDTO> apiSubstatusDTOs = Lists.transform(subStatuses, new Function<WorkSubStatusType, ApiSubstatusDTO>() {
			@Override
			public ApiSubstatusDTO apply(WorkSubStatusType workSubStatusType) {
				return new ApiSubstatusDTO.Builder()
					.withId(workSubStatusType.getCode())
					.withName(workSubStatusType.getDescription())
					.build();
			}
		});

		ApiV1Response<List<ApiSubstatusDTO>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(apiSubstatusDTOs);
		return apiResponse;
	}


	@ApiOperation(value = "List cancellation reason codes")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/cancellation_codes", method=RequestMethod.GET)
	public @ResponseBody
	ApiV1Response<List<String>> cancellationReasonCodes() {
		ApiV1Response<List<String>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(CancellationReasonType.cancellationReasons);
		return apiResponse;
	}

	@ApiOperation(value = "List unassign reason codes")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value="/unassign_codes", method=RequestMethod.GET)
	public @ResponseBody
	ApiV1Response<List<String>> unassignReasonCodes() {
		// Unassign only allows a subset of the cancellation codes
		List<String> unassignCodes = ImmutableList.of(CancellationReasonType.RESOURCE_ABANDONED, CancellationReasonType.RESOURCE_CANCELLED);
		ApiV1Response<List<String>> apiResponse = new ApiV1Response<>();
		apiResponse.setResponse(unassignCodes);
		return apiResponse;
	}
}
