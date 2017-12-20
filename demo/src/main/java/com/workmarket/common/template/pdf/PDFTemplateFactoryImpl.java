package com.workmarket.common.template.pdf;

import com.workmarket.data.report.work.AccountStatementDetailRow;
import com.workmarket.domains.model.account.CreditCardTransaction;
import com.workmarket.domains.model.invoice.AbstractServiceInvoice;
import com.workmarket.domains.model.invoice.AdHocInvoice;
import com.workmarket.domains.model.invoice.CreditMemo;
import com.workmarket.domains.model.invoice.SubscriptionInvoice;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.template.pdf.AssignmentInvoicePDFTemplate;
import com.workmarket.reporting.model.EvidenceReport;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * author: rocio
 */
@Service
public class PDFTemplateFactoryImpl implements PDFTemplateFactory {

	@Override
	public PDFTemplate newServiceInvoicePDFTemplate(AbstractServiceInvoice invoice) {
		if (invoice instanceof SubscriptionInvoice) {
			return new SubscriptionInvoicePDFTemplate((SubscriptionInvoice)invoice);
		} else if (invoice instanceof AdHocInvoice) {
			return new AdHocInvoicePDFTemplate((AdHocInvoice)invoice);
		} else if (invoice instanceof CreditMemo) {
			return new CreditMemoInvoicePDFTemplate((CreditMemo) invoice);
		}
		return null;
	}

	@Override
	public PDFTemplate newAssignmentInvoicePDFTemplate(AccountStatementDetailRow invoice) {
		checkNotNull(invoice);
		return new AssignmentInvoicePDFTemplate(invoice);
	}

	@Override
	public PDFTemplate earningReportPDFTemplate(EarningReport earningReport) {
		if(Integer.valueOf(earningReport.getTaxYear()) < 2013) {
			return new TaxEarningsReportPDFTemplate(earningReport);
		} else {
			return new NewTaxEarningsReportPDFTemplate(earningReport);
		}
	}


	@Override
	public PDFTemplate creditCardReceiptPDFTemplate(CreditCardTransaction creditCardTransaction) {
		return new CreditCardReceiptPDFTemplate(creditCardTransaction);
	}

	@Override
	public PDFTemplate newBackgroundEvidenceReportPDFTemplate(BackgroundCheck backgroundCheck, String filename){
		return new BackgroundEvidenceReportPDFTemplate(backgroundCheck,filename);
	}

	@Override
	public PDFTemplate newBatchBackgroundEvidenceReportPDFTemplate(List<EvidenceReport> evidenceReports, Long groupId){
		return new BatchBackgroundEvidenceReportPDFTemplate(evidenceReports, groupId);
	}

}
