
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.screening.ScreenedUser;
import com.workmarket.domains.model.screening.ScreenedUserPagination;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/admin/screening")
public class AdminScreeningController extends BaseController {

	@Autowired ScreeningService screeningService;

	@RequestMapping(
		value={"", "/", "/index"},
		method = GET)
	public String index(Model model) {

		model.addAttribute("current_type", "screening");

		return "web/pages/admin/screening/index";
	}

	@RequestMapping(
		value="/listusers",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void listusers(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(new HashMap<Integer, String>() {{
			put(0, ScreenedUserPagination.SORTS.USER_FIRSTNAME.toString());
			put(1, ScreenedUserPagination.SORTS.USER_LASTNAME.toString());
			put(2, ScreenedUserPagination.SORTS.COMPANY_NAME.toString());
			put(3, ScreenedUserPagination.SORTS.BACKGROUND_CHECK_STATUS.toString());
			put(4, ScreenedUserPagination.SORTS.DRUGTEST_STATUS.toString());
			put(5, ScreenedUserPagination.SORTS.CREDITCHECK_STATUS.toString());
		}});

		ScreenedUserPagination pagination = new ScreenedUserPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		pagination = screeningService.findAllScreenedUsers(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ScreenedUser item : pagination.getResults()) {
			String backgroundCheckedDate = DateUtilities.format("MM/dd/yyyy", item.getBackgroundCheckRequestDate(), getCurrentUser().getTimeZoneId());
			String drugTestedDate = DateUtilities.format("MM/dd/yyyy", item.getDrugTestRequestDate(), getCurrentUser().getTimeZoneId());
			String creditCheckedDate = DateUtilities.format("MM/dd/yyyy", item.getCreditCheckRequestDate(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
				item.getFirstName(),
				item.getLastName(),
				item.getCompanyName(),
				item.getBackgroundCheckStatus() + ((!item.getBackgroundCheckStatus().equals("Not requested")) ? " (" + backgroundCheckedDate + ")" : ""),
				item.getDrugTestStatus() + ((!item.getDrugTestStatus().equals("Not requested")) ? " (" + drugTestedDate + ")" : ""),
				item.getCreditCheckStatus() + ((!item.getCreditCheckStatus().equals("Not requested")) ? " (" + creditCheckedDate + ")" : "")
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", item.getUserNumber()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

}
