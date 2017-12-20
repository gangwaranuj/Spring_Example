
package com.workmarket.web.controllers.admin.manage.screenings;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.screening.ScreeningPagination;
import com.workmarket.screening.model.Screening;
import com.workmarket.service.business.ScreeningService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.business.screening.ScreeningAndUser;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.screening.ScreeningForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/admin/manage/screenings/drug")
@PreAuthorize("hasAnyRole('ACL_WM_CSVC MANAGER', 'ACL_WM_ADMINISTRATOR','ROLE_WM_QUEUES','ROLE_WM_ADMIN')")
public class AdminDrugController extends BaseController {

	@Autowired private InvariantDataService invariantDataService;
	@Autowired private ScreeningService screeningService;
	@Autowired private UserService userService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MessageSource messageSource;

	@RequestMapping(
		value = "/request",
		method = GET)
	public String request(Model model) {

		model.addAttribute("states", invariantDataService.getStateDTOs());
		model.addAttribute("countries", invariantDataService.getCountryDTOs());

		return "web/pages/admin/manage/screenings/drug/request";
	}

	@RequestMapping(
		value="/request",
		method = POST)
	public String requestSubmit(
		Model model,
		HttpServletRequest httpRequest,
		@Valid @ModelAttribute("screeningForm") ScreeningForm form,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {

		MessageBundle bundle = MessageBundle.newInstance();

		if (bindingResult.hasFieldErrors()) {
			for (FieldError fe : bindingResult.getFieldErrors()) {
				bundle.addError(messageSource.getMessage(fe, null));
			}
		} else {
			try {
				Long uid = userService.findUserId(httpRequest.getParameter("user_number"));

				ScreeningDTO dto = new ScreeningDTO();
				dto.setFirstName(form.getFirst_name());
				dto.setLastName(form.getLast_name());
				dto.setMiddleName(form.getMiddle_name());
				dto.setMaidenName(form.getMaiden_name());
				dto.setDateOfBirth(
					form.getBirth_year() + "-" +
						String.format("%02d", Integer.parseInt(form.getBirth_month())) + "-" +
						String.format("%02d", Integer.parseInt(form.getBirth_day()))
				);
				dto.setWorkIdentificationNumber(form.getSsn());
				dto.setState(form.getState());
				dto.setAddress1(form.getAddress1());
				dto.setAddress2(form.getAddress1());
				dto.setEmail(form.getEmail());
				dto.setCity(form.getCity());
				dto.setCountry(form.getCountry());
				dto.setPostalCode(form.getPostal_code());

				screeningService.requestFreeDrugTest(uid, dto);

				bundle.addSuccess("admin.screening.drug.request.success");
				redirectAttributes.addFlashAttribute("bundle", bundle);

				return "redirect:/screening/bkgrnd";
			} catch (Exception e) {
				bundle.addError("admin.screening.drug.request.error");
			}
		}

		model.addAttribute("bundle", bundle);
		model.addAttribute("states", invariantDataService.getStateDTOs());
		model.addAttribute("countries", invariantDataService.getCountryDTOs());

		return "web/pages/admin/manage/screenings/drug/request";
	}

	@RequestMapping(
		value = "/queue",
		method = GET)
	public String queue(Model model) {

		model.addAttribute("current_type", "drug_queue");

		return "web/pages/admin/manage/screenings/drug/queue";
	}

	@RequestMapping(
		value = "/queue_list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void queueList(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		ScreeningPagination pagination = new ScreeningPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		final List<ScreeningAndUser> results = screeningService.findDrugTestsByStatus(
		        httpRequest.getParameter("status"), pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		for (final ScreeningAndUser item : results) {
			Screening screening = item.getScreening();
			String requestedDate = DateUtilities.format("MM/dd/yyyy",
				screening.getCreatedOn().toGregorianCalendar(), getCurrentUser().getTimeZoneId());

			List<String> row = Lists.newArrayList(
				screening.getUuid(),
				screening.getUuid(),
				item.getUser().getFullName(),
				item.getUser().getCompany().getName(),
				requestedDate,
				""
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", screening.getUuid(),
				"vendor_id", screening.getUuid(),
				"user_number", item.getUser().getUserNumber()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value = "/update_status",
		method = GET)
	public String updateStatus(RedirectAttributes redirectAttributes, HttpServletRequest httpRequest) {

		// Create message container.
		MessageBundle bundle = messageHelper.newBundle();

		try {
			screeningService.updateScreeningStatus(
				httpRequest.getParameter("id"),
				httpRequest.getParameter("status")
			);

			messageHelper.addSuccess(bundle, "admin.screening.drug.status.success");
		} catch (Exception e) {
			messageHelper.addError(bundle, "admin.screening.drug.status.error");
		}

		redirectAttributes.addFlashAttribute("bundle", bundle);

		return "redirect:/admin/manage/screenings/drug/queue";
	}

}
