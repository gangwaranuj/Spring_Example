package com.workmarket.domains.payments.controllers;

import com.google.common.collect.Lists;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Statement;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.project.ProjectInvoiceBundle;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.HTML2PDFView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/payments/statements")
public class StatementsController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(StatementsController.class);

	@Autowired private BillingService billingService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private CompanyService companyService;


	@RequestMapping(
		value = "/pay/{statementId}",
		method = GET)
	@PreAuthorize("!principal.isMasquerading()")
	public String showPay(
		@PathVariable(value = "statementId") Long id,
		Model model) {

		MessageBundle bundle = messageHelper.newBundle(model);

		if (id == null) {
			messageHelper.addError(bundle, "payments.statements.pay.not_selected");
		} else {
			Statement statement = billingService.findStatementById(id);

			// Group invoices by project
			if (companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId())) {
				List<ProjectInvoiceBundle> projectInvoiceBundleList = billingService.groupInvoicesByProject(Lists.newArrayList(id), getCurrentUser().getCompanyId());
				List<? extends AbstractInvoice> generalInvoices = billingService.findInvoicesWithoutProjectBudget(Lists.newArrayList(id), getCurrentUser().getCompanyId());
				BigDecimal generalTotalDue = billingService.findGeneralTotalDue(Lists.newArrayList(id), getCurrentUser().getCompanyId());
				model.addAttribute("general_cash", getGeneralCash());
				model.addAttribute("project_list", projectInvoiceBundleList);
				model.addAttribute("general_invoices", generalInvoices);
				model.addAttribute("general_totalDue", generalTotalDue);
				model.addAttribute("doesCompanyHaveReservedFundsEnabledProject", String.valueOf(companyService.doesCompanyHaveReservedFundsEnabledProject(getCurrentUser().getCompanyId())));
			}

			if (statement == null) {
				messageHelper.addError(bundle, "payments.statements.pay.not_found");
			} else {
				model.addAttribute("id", id);
				model.addAttribute("statement", statement);
			}
		}

		return "web/pages/payments/statements/pay";
	}


	@RequestMapping(
		value = "/pay/{statementId}",
		method = POST)
	@PreAuthorize("!principal.isMasquerading()")
	public String pay(
		@PathVariable(value = "statementId") Long id,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);

		if (id == null) {
			messageHelper.addError(bundle, "payments.statements.pay.not_selected");
			return "web/pages/payments/statements/pay";
		}

		Statement statement = billingService.findStatementById(id);

		if (statement == null) {
			messageHelper.addError(bundle, "payments.statements.pay.not_found");
			return "web/pages/payments/statements/pay";
		}

		try {
			billingService.payStatement(id);

			messageHelper.addSuccess(bundle, "payments.statements.pay.paid");
			return "redirect:/payments/invoices";

		} catch (InsufficientFundsException ex) {
			logger.warn("insufficient funds to pay for statement with id={}", new Object[]{id}, ex);
			messageHelper.addError(bundle, "payments.statements.pay.insufficient.funds");
		} catch (Exception ex) {
			logger.error("error paying statement with id={}", new Object[]{id}, ex);
			messageHelper.addError(bundle, "payments.statements.pay.error");
		}

		return "redirect:/payments/invoices";
	}


	@RequestMapping(
		value = "/print/{statementId}",
		method = GET)
	public ModelAndView print(
		@PathVariable("statementId") Long id,
		Model model,
		MessageBundle bundle) {

		Statement statement = billingService.findStatementById(id);

		model.addAttribute("company", companyService.findCompanyById(getCurrentUser().getCompanyId()));
		model.addAttribute("bundle", bundle);
		if (statement == null || !billingService.validateAccessToInvoice(id)) {
			messageHelper.addError(bundle, "payments.statements.print.not_found");
			return new ModelAndView("forward:/payments/invoices", model.asMap());
		}
		AccountStatementDetailPagination pagination = new AccountStatementDetailPagination();
		pagination.setReturnAllRows(false);
		pagination.setSortColumn(AccountStatementDetailPagination.SORTS.INVOICE_CREATED_ON);
		pagination.setSortDirection(AccountStatementDetailPagination.SORT_DIRECTION.DESC);

		AccountStatementFilters filters = new AccountStatementFilters();
		filters.setStatementId(id);

		AccountStatementDetailPagination results = billingService.getStatementDashboard(filters, pagination);

		ModelAndView mav = new ModelAndView();
		mav.addObject("invoices", results.getResults());
		mav.addObject("statement", statement);
		mav.setView(new HTML2PDFView("pdf/statement"));

		return mav;
	}
}
