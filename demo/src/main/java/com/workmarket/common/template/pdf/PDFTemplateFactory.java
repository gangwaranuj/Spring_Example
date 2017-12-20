package com.workmarket.common.template.pdf;

import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.reporting.model.EvidenceReport;

import java.util.List;

/**
 * author: rocio
 */
public interface PDFTemplateFactory {

	PDFTemplate newServiceInvoicePDFTemplate(AbstractServiceInvoice invoice);
	PDFTemplate newAssignmentInvoicePDFTemplate(AccountStatementDetailRow invoice);
	PDFTemplate earningReportPDFTemplate(EarningReport earningReport);
	PDFTemplate creditCardReceiptPDFTemplate(CreditCardTransaction creditCardTransaction);
	PDFTemplate newBackgroundEvidenceReportPDFTemplate(BackgroundCheck backgroundCheck, String filename);
	PDFTemplate newBatchBackgroundEvidenceReportPDFTemplate(List<EvidenceReport> evidenceReports, Long groupId);

}
