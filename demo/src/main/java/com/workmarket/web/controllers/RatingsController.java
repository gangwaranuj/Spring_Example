
package com.workmarket.web.controllers;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkPagination;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.RatingService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.web.helpers.RatingStarsHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/ratings")
public class RatingsController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);

	@Autowired private RatingService ratingService;
	@Autowired private MessageSource messageSource;
	@Autowired private JsonSerializationService jsonSerializationService;

	@RequestMapping(
		value = {"", "/outstanding"},
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String getPendingResourceRatings(Model model) throws IllegalAccessException, InstantiationException {

		model.addAttribute("rating_stars", RatingStarsHelper.STAR_VALUES);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "ratings",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/ratings/ratings";
	}

	@RequestMapping(
		value = "",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public String getPendingResourceRatingsData(Model model, HttpServletRequest httpRequest) throws IllegalAccessException, InstantiationException {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		WorkPagination pagination = request.newPagination(WorkPagination.class);
		Calendar fromDate = Calendar.getInstance();
		fromDate.add(Calendar.DAY_OF_YEAR, -180);

		Map<String, String> filters = CollectionUtilities.newStringMap(
			WorkPagination.FILTER_KEYS.FROM_DATE.toString(), String.valueOf(fromDate.getTimeInMillis()));
		pagination.setFilters(filters);
		pagination.setResultsLimit(request.getLimit());

		try {
			pagination = ratingService.findAllWorkPendingRatingByResource(getCurrentUser().getId(), pagination);
		} catch (Exception e) {
			logger.error("Trouble finding work pending ratings", e);
		}

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		int counter = 0;
		for (Work e : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				"<title/>", "<rating/>"
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"index", counter++,
				"work_id", e.getId(),
				"title", e.getTitle(),
				"schedule_date", DateUtilities.format("MM/dd/yyyy", e.getScheduleFrom()),
				"paid_date", e.getInvoice().getPaymentDate() == null ? "" : DateUtilities.format("MM/dd/yyyy", e.getInvoice().getPaymentDate()),
				"due_date", DateUtilities.format("MM/dd/yyyy", e.getInvoice().getDueDate()),
				"status", e.getWorkStatusType(),
				"price", NumberUtilities.currency(e.getFulfillmentStrategy().getAmountEarned()),
				"work_number", e.getWorkNumber(),
				"address", (e.getAddressOnsiteFlag() && null != e.getAddress()) ? e.getAddress().getFullAddress() : false,
				"location", (null != e.getLocation()) ? e.getLocation().getName() : false,
				"company", e.getCompany().getEffectiveName(),
				"buyer", e.getBuyer().getFullName(),
				"buyer_id", e.getBuyer().getId(),
				"resource_id", getCurrentUser().getId()
			);

			response.addRow(data, meta);
		}

		model.addAttribute("response", response);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "ratings",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return "web/pages/ratings/ratings";
	}

	@RequestMapping(
		value = "/flag/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map flag(
		@PathVariable Long id,
		HttpServletResponse response) {

		String message;

		try {
			ratingService.flagRatingForReview(id, true);
			message = messageSource.getMessage("rating.flag.success", null, null);
		} catch (Exception e) {
			logger.error("Trouble flagging review", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			message = messageSource.getMessage("rating.flag.error", null, null);
		}

		return CollectionUtilities.newObjectMap(
			"message", message
		);
	}
}
