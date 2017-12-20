package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.aggregate.CompanyAggregatePagination;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.service.business.CompanyService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping("/admin/locks")
public class LocksController extends BaseController {

	@Autowired CompanyService companyService;

	@RequestMapping(
		value={"", "/", "/index", "/companies"},
		method = GET)
	public String index() {

		return "web/pages/admin/locks/companies";
	}

	@RequestMapping(
		value="/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void list(HttpServletRequest httpRequest, Model model) throws Exception {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(CollectionUtilities.<Integer,String>newTypedObjectMap(
			0, CompanyAggregatePagination.SORTS.COMPANY_NAME.toString()
		));

		CompanyAggregatePagination pagination = request.newPagination(CompanyAggregatePagination.class);
		pagination.addFilter(CompanyAggregatePagination.FILTER_KEYS.COMPANY_STATUS, CompanyStatusType.LOCKED);

		pagination = companyService.findAllCompanies(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);

		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");

		for (CompanyAggregate company : pagination.getResults()) {
			List<String> row = Lists.newArrayList(
				company.getCompanyName(),
				String.valueOf(company.getLane0Users()),
				String.valueOf(company.getLane1Users()),
				String.valueOf(company.getLane2Users()),
				String.valueOf(company.getLane3Users()),
				sdf.format(company.getCreatedOn().getTime())
			);

			Map<String,Object> meta = CollectionUtilities.newObjectMap(
				"company_id", company.getCompanyId()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}
}