package com.workmarket.service.business.template;

import com.workmarket.common.template.Template;
import com.workmarket.common.template.pdf.PDFTemplate;
import com.workmarket.domains.model.changelog.user.UserChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.invoice.AbstractInvoice;

public interface TemplateService {

	String render(Template template);

	String renderSubject(Template template);

	<T extends WorkChangeLog> String renderChangeLogTemplate(T changeLog);

	<T extends UserChangeLog> String renderChangeLogTemplate(T changeLog);

	String renderWorkCalendarTemplate(Object work);

	String renderPDFTemplate(PDFTemplate pdfTemplate);

	String renderPDFTemplate(PDFTemplate pdfTemplate, AbstractInvoice invoice);
}
