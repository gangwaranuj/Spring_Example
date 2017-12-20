
package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.data.report.internal.BuyerSummary;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.reporting.DailySummary;
import com.workmarket.domains.model.reporting.DailySummaryPagination;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.service.business.DailySummaryService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.reports.DailySummaryReportForm;
import com.workmarket.web.views.CSVView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/admin/reporting")
public class ReportingController extends BaseController {

	@Autowired
	@Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;

	@Autowired private DailySummaryService dailySummaryService;

	@RequestMapping(
		value = {"", "/", "/index"},
		method = GET)
	public String index(Model model) {

		List<DailySummary> summaries = dailySummaryService.findAllSummaries();
		model.addAttribute("summaries", summaries);

		DailySummary summary = summaries.iterator().next();
		model.addAttribute("summary", summary);

		return "web/pages/admin/reporting/index";
	}

	@RequestMapping(
		value = "/dailysummary/export",
		method = POST)
	public CSVView export(
		Model model,
		@ModelAttribute("filterForm") DailySummaryReportForm form,
		HttpServletRequest httpRequest) throws Exception {

		DailySummaryPagination pagination = new DailySummaryPagination();
		pagination.setFromDate(form.getFromDate());
		pagination.setToDate(form.getToDate());
		DailySummaryPagination results = dailySummaryService.findAllSummaries(pagination);

		List<String> columns = Lists.newArrayList(
			"Date",
			"Day Unique Creators",
			"Day Unique Routers",
			"Sent Revenue",
			"Draft Created Potential Revenue",
			"Paid Revenue",
			"Average price of Created",
			"# of Assignments Sent",
			"# of Assignments Created",
			"# of assignmnets Closed",
			"# of assignments Paid",
			"# canceled",
			"# voided",
			"Total Cash on Platform",
			"Total Terms Exposed",
			"#of new buyers (Buyers that created first assignments)"
		);

		List<String[]> rows = Lists.newArrayList();
		rows.add(columns.toArray(new String[0]));

		for (DailySummary summary : results.getResults()) {
			List<String> data = Lists.newArrayList(
				DateUtilities.format("MM/dd/YYYY", summary.getCreatedOn()),
				ObjectUtils.nullSafeToString(summary.getUniqueCreators()),
				ObjectUtils.nullSafeToString(summary.getUniqueRouters()),
				ObjectUtils.nullSafeToString(summary.getTotalRoutedToday()),
				ObjectUtils.nullSafeToString(summary.getDraftsCreated()),
				ObjectUtils.nullSafeToString(summary.getTotalAssignmentCost()),
				ObjectUtils.nullSafeToString(summary.getAveragePriceCreatedAssignments()),
				ObjectUtils.nullSafeToString(summary.getRouted()),
				ObjectUtils.nullSafeToString(summary.getAssignments()),
				ObjectUtils.nullSafeToString(summary.getClosedAssignments()),
				ObjectUtils.nullSafeToString(summary.getPaidAssignments()),
				ObjectUtils.nullSafeToString(summary.getCancelledAssignments()),
				ObjectUtils.nullSafeToString(summary.getVoidAssignments()),
				ObjectUtils.nullSafeToString(summary.getCashOnPlatform()),
				ObjectUtils.nullSafeToString(summary.getTotalMoneyExposedOnTerms()),
				ObjectUtils.nullSafeToString(summary.getNewBuyers())
			);
			rows.add(data.toArray(new String[0]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format(
			"daily-summary-export-%s-%s.csv",
			DateUtilities.format("YYYYMMdd", form.getFromDate()),
			DateUtilities.format("YYYYMMdd", form.getToDate())
		));

		return view;
	}

	@RequestMapping(
		value = "/index/{id}",
		method = GET)
	public String indexLookup(Model model, @PathVariable("id") Long id) {

		List<DailySummary> summaries = dailySummaryService.findAllSummaries();
		model.addAttribute("summaries", summaries);

		DailySummary summary = dailySummaryService.findSummary(id);
		model.addAttribute("summary", summary);

		return "web/pages/admin/reporting/index";
	}

	@RequestMapping(
		value="/unique_buyers/{id}",
		method = GET)
	public String uniqueRouters(
		Model model,
		@PathVariable("id") Long id) throws Exception {

		List<BuyerSummary> summary_buyers = dailySummaryService.findUniqueBuyersForSummary(id);
		model.addAttribute("summary_buyers", summary_buyers);

		return "web/pages/admin/reporting/summary_buyers";
	}

	@RequestMapping(
		value = "/routed_detail/{id}",
		method = GET)
	public String routedDetail(
		Model model,
		@PathVariable("id") Long id) {

		List<Object[]> summary_routed = dailySummaryService.findRoutedAssignmentsPerCompany(id);
		model.addAttribute("summary_routed", summary_routed);

		return "web/pages/admin/reporting/routed_detail";
	}

	@RequestMapping(
		value = "/funding_transactions",
		method = GET)
	public String fundingTransactions(
		Model model,
		HttpServletRequest httpServletRequest) throws Exception {

		DateFilter datefilter = new DateFilter();

		if(StringUtilities.isNotEmpty(httpServletRequest.getParameter("fromDate"))) {
			datefilter.setFromDate(DateUtilities.getCalendarFromDateString(httpServletRequest.getParameter("fromDate"), getCurrentUser().getTimeZoneId()));
		} else {
			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.DAY_OF_MONTH, -2);
			datefilter.setFromDate(fromDate);
		}

		if(StringUtilities.isNotEmpty(httpServletRequest.getParameter("toDate"))) {
			datefilter.setToDate(DateUtilities.getCalendarFromDateString(httpServletRequest.getParameter("toDate"), getCurrentUser().getTimeZoneId()));
		} else {
			Calendar toDate = Calendar.getInstance();
			toDate.add(Calendar.DAY_OF_MONTH, 1);
			datefilter.setToDate(toDate);
		}

		List<RegisterTransaction> fundingTransactions = accountRegisterService.findFundingTransactionsByDate(datefilter);
		model.addAttribute("funding_transactions", fundingTransactions);

		return "web/pages/admin/reporting/fundingtransactions";
	}

}
