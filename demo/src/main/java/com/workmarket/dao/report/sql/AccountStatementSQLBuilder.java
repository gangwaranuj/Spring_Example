package com.workmarket.dao.report.sql;

import com.workmarket.domains.model.invoice.*;
import com.workmarket.data.report.work.AccountStatementFilters;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class AccountStatementSQLBuilder extends SQLBuilder {
	private static final List<String> PAID_PENDING_AND_VOID_INVOICE_STATUS_TYPE_FILTER = Arrays.asList(InvoiceStatusType.PAID,InvoiceStatusType.PAYMENT_PENDING,InvoiceStatusType.VOID);
	private static final List<String> INVOICE_TYPE_FILTER = Arrays.asList(Invoice.INVOICE_TYPE);
	private static final List<String> ALL_PAYABLES_INVOICES_TYPE_FILTER = Arrays.asList(Invoice.INVOICE_TYPE, InvoiceSummary.INVOICE_SUMMARY_TYPE, 
			SubscriptionInvoice.SUBSCRIPTION_INVOICE_TYPE, AdHocInvoice.AD_HOC_INVOICE_TYPE);

	private Long userId;
	private Long companyId;
	private boolean adminOrController;
	private AccountStatementFilters accountStatementFilters = new AccountStatementFilters();

	public AccountStatementSQLBuilder() {
	}

	public AccountStatementSQLBuilder(Long userId, Long companyId, boolean isAdminOrController, AccountStatementFilters accountStatementFilters) {
		super();
		this.userId = userId;
		this.companyId = companyId;
		this.adminOrController = isAdminOrController;

		if (accountStatementFilters != null) {
			setAccountStatementFilters(accountStatementFilters);
		}

		String[] COLUMNS = new String[]{"DISTINCT invoice.id as invoiceId",
				"invoice.invoice_number",
				"invoice.description AS invoiceDescription",
				"IF( (invoice.invoice_status_type_code = 'paid' AND invoice.payment_fulfillment_status_type_code = 'pendingFulfillment') " +
						" OR EXISTS(SELECT parent.id FROM invoice parent " +
						" INNER JOIN invoice_summary_detail d on d.invoice_summary_id = parent.id " +
						" WHERE parent.invoice_status_type_code = 'paid' " +
						" AND parent.payment_fulfillment_status_type_code = 'pendingFulfillment' AND invoice_id = invoice.id), 1, 0) AS isPendingFulfillment",
				"IF(invoice.type = 'invoice', 0, (SELECT count(*) FROM invoice_summary_detail WHERE invoice_summary_id = invoice.id)) AS numberOfInvoices ",
				"invoice.balance",
				"invoice.due_date AS invoiceDueDate",
				"invoice.payment_date AS invoicePaymentDate",
				"invoice.type AS invoiceType",
				"invoice.bundled AS invoiceIsBundled",
				"invoice.payment_fulfillment_status_type_code AS invoiceFulfillmentStatus",
				"invoice.remaining_balance AS invoiceRemainingBalance",
				"invoice.company_id",
				"invoice.void_on AS invoiceVoidDate",
				"invoice.created_on AS invoiceCreatedDate",
				"invoice.downloaded_on AS invoiceDownloadedDate",
				"assignedUser.first_name as resourceFirstName",
				"assignedUser.last_name as resourceLastName",
				"assignedUserCompany.effective_name as resourceCompanyName",
				"COALESCE(cancelWork.id, work.id) AS workId",
				"COALESCE(cancelWork.title, work.title) AS workTitle",
				"COALESCE(cancelWork.work_number, work.work_number) AS workNumber",
				"work.work_status_type_code",
				"work.due_on AS dueDate",
				"work.closed_on AS closeDate",
				"work.schedule_from AS workDate",
				"work.payment_terms_days",
				"work.payment_terms_enabled",
				"time_zone.time_zone_id AS timeZoneId",
				"client_company.name AS clientCompanyName",
				"client_contact.first_name as clientFirstName",
				"client_contact.last_name as clientLastName",
				"work_address.city as workCity",
				"work_address.country as workCountry",
				"state.short_name as workState",
				"work_address.postal_code as workPostalCode",
				"buyer.id AS buyerId",
				"buyer.last_name AS buyerLastName",
				"buyer.first_name AS buyerFirstName",
				"buyer_profile.work_phone as buyer_work_phone",
				"buyer_profile.work_phone_extension as buyer_work_phone_extension",
				"work.amount_earned",
				"COALESCE(work.buyer_fee, 0) AS buyer_fee",
				"COALESCE(work.buyer_total_cost, 0) AS buyer_total_cost",
				"company.name AS companyName",
				"IF (buyer.id = :userId OR invoice.company_id = :companyId, 1, 0) AS isOwner",
				"work_unique_id.display_name AS uniqueIdDisplayName",
				"work_unique_id.id_value AS uniqueIdValue"};
		addColumns(COLUMNS);

		if (accountStatementFilters.hasAnyInvoiceSummaryFilter()) {
			addColumns(
					// If the viewer is the buyer or from the buyer company, show the status as it is.
					"IF (buyer.id = :userId OR invoice.company_id = :companyId, invoice.invoice_status_type_code, " +
						// Otherwise if the invoice is bundled, show its parent (bundle) status.
						" IF(invoice.bundled = true, bundle.invoice_status_type_code, invoice.invoice_status_type_code) " +
						") AS invoiceStatusTypeCode",

					"bundle.id AS invoiceSummaryId",
					"bundle.invoice_number AS invoiceSummaryNumber",
					"bundle.description AS invoiceSummaryDescription",
					"bundle.due_date AS invoiceSummaryDueDate",
					"IF ((bundle.last_sent_to IS NOT NULL OR bundle.last_sent_on IS NOT NULL OR bundle.downloaded_on IS NOT NULL" +
						" OR invoice.last_sent_to IS NOT NULL OR invoice.last_sent_on IS NOT NULL OR invoice.downloaded_on IS NOT NULL), 0, 1) AS editable"
			);
		} else {
			addColumns(
					"IF(invoice.bundled = 1 AND invoice.invoice_status_type_code != 'void', (SELECT bundle.invoice_status_type_code FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type IN  ('bundle', 'statement')), invoice.invoice_status_type_code) AS invoiceStatusTypeCode",

					"(SELECT bundle.id FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type IN  ('bundle', 'statement')) AS invoiceSummaryId",

					"(SELECT bundle.invoice_number FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type IN  ('bundle', 'statement')) AS invoiceSummaryNumber",

					"(SELECT bundle.description FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type IN  ('bundle', 'statement')) AS invoiceSummaryDescription",

					"(SELECT bundle.due_date FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type IN  ('bundle', 'statement')) AS invoiceSummaryDueDate",

					"IF(invoice.bundled = 0, IF(invoice.last_sent_to IS NOT NULL OR invoice.last_sent_on IS NOT NULL OR invoice.downloaded_on IS NOT NULL, 0, 1)," +
					"(SELECT IF(bundle.last_sent_to IS NOT NULL OR bundle.last_sent_on IS NOT NULL OR bundle.downloaded_on IS NOT NULL, 0, 1) FROM invoice_summary_detail " +
					"INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id " +
					"WHERE invoice.id = invoice_summary_detail.invoice_id AND bundle.type = 'bundle')) AS editable"
			);
		}
		addTable("invoice");

		buildJoin();
		buildFilters();
	}

	public AccountStatementFilters getAccountStatementFilters() {
		return accountStatementFilters;
	}

	public void setAccountStatementFilters(AccountStatementFilters accountStatementFilters) {
		this.accountStatementFilters = accountStatementFilters;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public boolean isAdminOrController() {
		return adminOrController;
	}

	public void setAdminOrController(boolean isAdminOrController) {
		adminOrController = isAdminOrController;
	}

	private void buildJoin() {
		addJoin("INNER JOIN company ON invoice.company_id = company.id ");

		if (accountStatementFilters.hasReportTypeFilter() && !accountStatementFilters.getPayables()) {
			addJoin("INNER JOIN work_resource ON invoice.active_work_resource_id = work_resource.id");
			addJoin("INNER JOIN user AS assignedUser ON work_resource.user_id = assignedUser.id");
		} else {
			addJoin("LEFT JOIN work_resource ON invoice.active_work_resource_id = work_resource.id");
			addJoin("LEFT JOIN user AS assignedUser ON work_resource.user_id = assignedUser.id");
		}

		if (accountStatementFilters.hasAnyInvoiceSummaryFilter()) {
			addJoin("INNER JOIN invoice_summary_detail ON invoice_summary_detail.invoice_id = invoice.id");
			addJoin("INNER JOIN invoice bundle ON bundle.id = invoice_summary_detail.invoice_summary_id");
		}

		addJoin("LEFT JOIN company as assignedUserCompany ON assignedUser.company_id = assignedUserCompany.id");
		addJoin("LEFT JOIN work ON invoice.id = work.invoice_id");
		addJoin("LEFT JOIN work cancelWork ON invoice.cancel_payment_work_id = cancelWork.id");
		addJoin("LEFT JOIN time_zone ON time_zone.id = work.time_zone_id", false);
		addJoin("LEFT JOIN user as buyer ON work.buyer_user_id = buyer.id");
		addJoin("LEFT JOIN profile as buyer_profile ON buyer.id = buyer_profile.user_id", false);
		addJoin("LEFT JOIN client_contact ON work.client_contact_id = client_contact.id");
		addJoin("LEFT JOIN client_company ON work.client_company_id = client_company.id", accountStatementFilters.hasClientCompanyFilter());
		addJoin("LEFT JOIN address as work_address ON work.address_id = work_address.id");
		addJoin("LEFT JOIN state  ON state.id = work_address.state");
		addJoin("LEFT JOIN project_work_association project ON project.work_id = work.id", accountStatementFilters.hasProjectFilter());
		addJoin("LEFT JOIN work_unique_id ON work.id = work_unique_id.work_id");
	}

	private void buildFilters() {
		// By default show only paid and pending status, exclude bundled unless a specific call is made
		List<String> invoiceStatusTypeFilter = PAID_PENDING_AND_VOID_INVOICE_STATUS_TYPE_FILTER;
		
		// By default show only invoices
		List<String> invoiceTypeFilter = INVOICE_TYPE_FILTER;
		
		/**
		 * Selecting the right value for invoiceTypeFilter and invoiceStatusTypeFilter. Bundled invoices are only relevant to buyers.
		 */
		if (!CollectionUtils.isEmpty(accountStatementFilters.getInvoiceTypes())) {
			invoiceTypeFilter = accountStatementFilters.getInvoiceTypes();
		}
		else if (StringUtilities.isNotEmpty(accountStatementFilters.getInvoiceType())){
			// Show invoices or bundles when there is a invoice type filter
			invoiceTypeFilter = Arrays.asList(accountStatementFilters.getInvoiceType());		
		} else if ((accountStatementFilters.hasReportTypeFilter()) && (accountStatementFilters.getPayables()) ) {
			// Show both invoices and bundles together for buyers when there is no invoice type filter
			invoiceTypeFilter = ALL_PAYABLES_INVOICES_TYPE_FILTER;
		}

		if (!accountStatementFilters.hasInvoiceIdFilter()) {
			// Exclude statements
			addWhereClause("invoice.type IN (:invoiceTypeFilter)")
					.addWhereClause("invoice.invoice_status_type_code IN (:invoiceStatusTypeFilter)");
			
			addParam("invoiceTypeFilter", invoiceTypeFilter);
			addParam("invoiceStatusTypeFilter", invoiceStatusTypeFilter);
		}

		addWhereClause("invoice.deleted = 0");

		if (getUserId() != null) {
			addParam("userId", getUserId());
		}

		if (getCompanyId() != null) {
			addParam("companyId", getCompanyId());
		}

		buildDefaultFilter();
		buildWorkResourceFilter();
		buildInvoiceIdFilter();
		buildStatementFilter();
		buildBundledInvoicesFilter();
		buildWorkFilter();
		buildDateFilter();
		buildWorkDateFilter();
		buildPaidStatusFilter();
		buildBuyerFilter();
		buildClientCompanyFilter();
		buildProjectFilter();
		buildAssignedResourceFilter();
		buildPaidDateFilter();
		buildDueDateFilter();
	}

	private void buildBuyerFilter() {
		if (accountStatementFilters.hasBuyerFilter()) {
			addWhereInClause("buyer.id", "buyerUserId", accountStatementFilters.getBuyerId());
		}
	}

	private void buildClientCompanyFilter() {
		if (accountStatementFilters.hasClientCompanyFilter()) {
			addWhereInClause("client_company.id", "clientCompanyId", accountStatementFilters.getClientCompanyId());
		}
	}

	private void buildAssignedResourceFilter() {
		if (accountStatementFilters.hasAssignedResourceIdFilter()) {
			addWhereInClause("assignedUser.id", "assignedResourceId", accountStatementFilters.getAssignedResourceId());
		}
	}

	private void buildWorkDateFilter() {
		if (accountStatementFilters.hasWorkDateFilter()) {
			if (accountStatementFilters.getFromWorkDate() != null) {
				addWhereClause("work.schedule_from >= :fromDate")
					.addParam("fromDate", accountStatementFilters.getFromWorkDate());
			}
			if (accountStatementFilters.getToWorkDate() != null) {
				addWhereClause("work.schedule_from <= :toDate")
					.addParam("toDate", accountStatementFilters.getToWorkDate());
			}
		}
	}

	private void buildDateFilter() {
		if (accountStatementFilters.hasDateFilter()) {
			if (accountStatementFilters.getFromDate() != null) {
				addWhereClause("invoice.created_on >= :fromDate")
						.addParam("fromDate", accountStatementFilters.getFromDate());
			}
			if (accountStatementFilters.getToDate() != null) {
				addWhereClause("invoice.created_on <= :toDate")
						.addParam("toDate", accountStatementFilters.getToDate());
			}
		}
	}

	private void buildPaidDateFilter() {
		if (accountStatementFilters.hasPaidDateFilter()) {
			if (accountStatementFilters.getFromPaidDate() != null) {
				addWhereClause("invoice.payment_date >= :fromPaidDate")
						.addParam("fromPaidDate", accountStatementFilters.getFromPaidDate());
			}
			if (accountStatementFilters.getToPaidDate() != null) {
				addWhereClause("invoice.payment_date <= :toPaidDate")
						.addParam("toPaidDate", accountStatementFilters.getToPaidDate());
			}
		}
	}

	private void buildDueDateFilter() {
		if (accountStatementFilters.hasDueDateFilter()) {
			if (accountStatementFilters.getFromDueDate() != null) {
				addWhereClause("invoice.due_date >= :fromDueDate")
						.addParam("fromDueDate", accountStatementFilters.getFromDueDate());
			}
			if (accountStatementFilters.getToDueDate() != null) {
				addWhereClause("invoice.due_date <= :toDueDate")
						.addParam("toDueDate", accountStatementFilters.getToDueDate());
			}
		}
	}

	private void buildPaidStatusFilter() {
		if (accountStatementFilters.hasPaidStatusFilter()) {
			if (accountStatementFilters.getPaidStatus()) {
				addWhereClause("invoice.invoice_status_type_code = :invoiceStatusTypeCode")
						.addParam("invoiceStatusTypeCode", InvoiceStatusType.PAID);
			} else {
				addWhereClause("invoice.invoice_status_type_code = :invoiceStatusTypeCode")
						.addParam("invoiceStatusTypeCode", InvoiceStatusType.PAYMENT_PENDING);
			}
		}
	}

	private void buildProjectFilter() {
		if (accountStatementFilters.hasProjectFilter()) {
			addWhereInClause("project.project_id", "projectId", accountStatementFilters.getProjectId());
		}
	}

	private void buildStatementFilter() {
		if (accountStatementFilters.hasStatementFilter()) {
			addWhereClause("bundle.id = :statementId")
					.addParam("statementId", accountStatementFilters.getStatementId());
		} else if (accountStatementFilters.hasInvoiceSummaryIdFilter()) {
			addWhereClause("bundle.id = :invoiceSummaryId")
					.addParam("invoiceSummaryId", accountStatementFilters.getInvoiceSummaryId());
		} else if (accountStatementFilters.hasInvoiceSummaryIdsFilter()) {
			addWhereClause("bundle.id IN (" + CollectionUtilities.join(accountStatementFilters.getInvoiceSummaryIds(), ",") + ")");
		} else if (accountStatementFilters.isIgnoreStatementsFilter()) {
			addWhereClause("NOT EXISTS (SELECT bundle.id FROM invoice_summary_detail detail " +
					"INNER JOIN invoice bundle ON bundle.id = detail.invoice_summary_id " +
					"WHERE detail.invoice_id = invoice.id AND bundle.type = :statementTypeCode)")
				.addParam("statementTypeCode", Statement.STATEMENT_TYPE);
		}
	}

	private void buildInvoiceIdFilter() {
		if (accountStatementFilters.hasInvoiceIdFilter()) {
			addWhereClause("invoice.id = :invoiceId");
			addParam("invoiceId", accountStatementFilters.getInvoiceId());
		}
	}

	private void buildWorkFilter() {
		if (accountStatementFilters.hasWorkNumbersFilter()) {
			addWhereClause("work.work_number IN (:workNumbers)")
					.addParam("workNumbers", accountStatementFilters.getWorkNumbers());
		}
	}

	private void buildWorkResourceFilter() {
		if (accountStatementFilters.hasReportTypeFilter()) {
			// Payables
			if (accountStatementFilters.getPayables()) {
				if (isAdminOrController() && getCompanyId() != null) {
					addWhereClause("invoice.company_id = :companyId");
				} else if (getUserId() != null) {
					addWhereClause("buyer.id = :userId");
				}
				// Receivables
			} else {
				if (isAdminOrController() && getCompanyId() != null) {
					addWhereClause("assignedUser.company_id = :companyId");
				} else if (getUserId() != null) {
					addWhereClause("(work_resource.user_id = :userId OR work_resource.dispatcher_id = :userId)");
				}
			}
		}
	}

	private void buildDefaultFilter() {
		// Default filter
		if (!accountStatementFilters.hasReportTypeFilter()) {
			if (isAdminOrController() && getCompanyId() != null) {
				addWhereClause("(invoice.company_id = :companyId OR assignedUser.company_id =:companyId)");
			} else if (getUserId() != null) {
				addWhereClause("(buyer.id = :userId OR assignedUser.id =:userId)");
			}
		}
	}

	private void buildBundledInvoicesFilter() {
		if (accountStatementFilters.getPayables() != null && !accountStatementFilters.getPayables()) {
			return;
		}
		if (accountStatementFilters.hasBundledInvoicesFilter()) {
			addWhereClause("invoice.bundled = :showBundledInvoices");
			addParam("showBundledInvoices", accountStatementFilters.getBundledInvoices());
		}
	}

	@Override
	public String buildCount() {
		return super.buildCount("DISTINCT invoice.id");
	}
}
