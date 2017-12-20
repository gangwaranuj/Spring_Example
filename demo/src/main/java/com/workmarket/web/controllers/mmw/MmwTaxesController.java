package com.workmarket.web.controllers.mmw;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.EarningDetailReport;
import com.workmarket.domains.model.tax.EarningDetailReportRow;
import com.workmarket.domains.model.tax.EarningDetailReportSet;
import com.workmarket.domains.model.tax.EarningReport;
import com.workmarket.domains.model.tax.TaxForm1099;
import com.workmarket.domains.model.tax.TaxReportDetailRow;
import com.workmarket.domains.model.tax.TaxServiceReport;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.business.tax.report.TaxReportService;
import com.workmarket.service.exception.IllegalEntityAccessException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.vault.services.VaultHelper;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.views.CSVView;
import com.workmarket.web.views.HTML2PDFView;

import groovy.lang.Tuple2;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/mmw/taxes")
public class MmwTaxesController extends BaseController {

	private static final Log logger = LogFactory.getLog(MmwTaxesController.class);

	@Autowired private TaxReportService taxReportService;
	@Autowired private TaxService taxService;
	@Autowired private ResourceLoader resourceLoader;
	@Autowired private UserService userService;
	@Autowired private VaultHelper vaultHelper;

	public static final String W9_TEMPLATE = "classpath:files/w9.pdf";
	private static Properties w9FieldMap;
	private static final Character TIN_MASK_CHAR = 'x';

	private static final int FORM_PAGE_NUMBER = 1;

	private static final AddressDTO WM_RES_TAX_ADDRESS;
	private AddressDTO rcptAddress = new AddressDTO();

	private static final long RES_TAX_VIRTUAL_ID = 0;


	static {
		//TODO: Refactor this to display the wm data from the tax.
		WM_RES_TAX_ADDRESS = new AddressDTO();
		WM_RES_TAX_ADDRESS.setAddress1(Constants.WM_TAX_ADDRESS_LINE_1);
		WM_RES_TAX_ADDRESS.setAddress2(Constants.WM_TAX_ADDRESS_LINE_2);
		WM_RES_TAX_ADDRESS.setCity(Constants.WM_TAX_CITY);
		WM_RES_TAX_ADDRESS.setState(Constants.WM_TAX_STATE);
		WM_RES_TAX_ADDRESS.setPostalCode(Constants.WM_TAX_POSTAL_CODE);
		WM_RES_TAX_ADDRESS.setCountry(Constants.WM_TAX_COUNTRY);

		try {
			w9FieldMap = PropertiesLoaderUtils.loadAllProperties("files/w9.field.properties");
		} catch (IOException e) {
			logger.error("Failed to load assignment detail navigation mappings", e);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) throws Exception {
		List<TaxForm1099> forms1099 = taxReportService.findAvailable1099s(getCurrentUser().getId());
		List<EarningReport> earningReport = taxReportService.findAvailableEarningReports(getCurrentUser().getId());
		List<EarningDetailReportSet> earningDetailReport = taxReportService.findAvailableEarningDetailReportSets(getCurrentUser().getId());
		List<TaxServiceReport> taxServiceReport = taxReportService.findAvailableTaxServiceDetailReports(getCurrentUser().getId());
		List<? extends AbstractTaxEntity> formsEntities = taxService.findAllTaxEntities(getCurrentUser().getId());

		boolean doesUserHaveRESTaxDetailReport = taxReportService.doesUserHaveRESTaxDetailReport(getCurrentUser().getId());
		final Calendar RES_TAX_REPORT_CREATE_ON = Calendar.getInstance();
		RES_TAX_REPORT_CREATE_ON.set(2015, Calendar.JANUARY, 31);

		List<Map<String, Object>> forms = Lists.newArrayList();

		boolean masquerading = this.getCurrentUser().isMasquerading();

		for (TaxForm1099 f : forms1099) {
			String createdBy = f.getTaxEntity().getCompany().getCreatedBy() != null ? f.getTaxEntity().getCompany().getCreatedBy().getFullName() : "";
			forms.add(CollectionUtilities.newObjectMap(
					"id", f.getId(),
					"type", "1099-MISC",
					"status", "Issued", // all 1099 are Issued status
					"createdBy", createdBy,
					"createdOn", f.getCreatedOn(),
					"active", true
			));
		}

		for (TaxServiceReport f : taxServiceReport) {
			forms.add(CollectionUtilities.newObjectMap(
					"id", f.getId(),
					"type", "1099-MISC(B)", // Put tax service company name here
					"status", "Issued", // all 1099 are Issued status
					"createdBy", "Work Market",
					"createdOn", f.getCreatedOn(),
					"active", true
			));
		}

		if (doesUserHaveRESTaxDetailReport) {
			forms.add(CollectionUtilities.newObjectMap(
					"id", RES_TAX_VIRTUAL_ID,
					"type", "1099-MISC(B)", // Put tax service company name here
					"status", "Issued", // all 1099 are Issued status
					"createdBy", "Work Market",
					"createdOn", RES_TAX_REPORT_CREATE_ON,
					"active", true
			));
		}

		for (EarningReport f : earningReport) {
			String createdBy = f.getTaxEntity().getCompany().getCreatedBy() != null ? f.getTaxEntity().getCompany().getCreatedBy().getFullName() : "";
			forms.add(CollectionUtilities.newObjectMap(
					"id", f.getId(),
					"type", "Earnings Report",
					"status", "Issued",
					"createdBy", createdBy,
					"createdOn", f.getCreatedOn(),
					"active", true
			));
		}

		if(CollectionUtils.isNotEmpty(earningDetailReport)) {
			for (EarningDetailReportSet reportSet : earningDetailReport) {
				Map<String, Object> creatorProps = userService.getProjectionMapById(reportSet.getCreatorId(), "firstName", "lastName");
				forms.add(CollectionUtilities.newObjectMap(
						"id", reportSet.getId(),
						"type", "1099-MISC Data",
						"status", "Issued",
						"createdBy", StringUtilities.fullName((String) creatorProps.get("firstName"), (String) creatorProps.get("lastName")),
						"createdOn", reportSet.getCreatedOn(),
						"active", true
				));
			}
		}
		for (AbstractTaxEntity e : formsEntities) {
			if (AbstractTaxEntity.COUNTRY_USA.equals(e.getCountry())) {
				String taxNumber = vaultHelper.get(e, "taxNumber", e.getRawTaxNumber()).getValue();
				String tin = StringUtilities.showLastNDigits(e.getBusinessFlag() ?
						StringUtilities.formatEin(taxNumber) :
						StringUtilities.formatSsn(taxNumber), TIN_MASK_CHAR, 4);
				String createdBy = e.getCompany().getCreatedBy() != null ? e.getCompany().getCreatedBy().getFullName() : "";
				forms.add(CollectionUtilities.newObjectMap(
						"id", e.getId(),
						"type", "W9",
						"status", e.getStatus().getDisplayableStatus(),
						"createdBy", createdBy,
						"createdOn", e.getActiveDate(),
						"tin", tin,
						"active", e.getActiveFlag()
				));
			} else if (AbstractTaxEntity.COUNTRY_CANADA.equals(e.getCountry())) {
				String taxNumber = vaultHelper.get(e, "taxNumber", e.getRawTaxNumber()).getValue();
				String tin = StringUtilities.showLastNDigits(e.getBusinessFlag() ?
						StringUtilities.formatCanadaBn(taxNumber) :
						StringUtilities.formatCanadaSin(taxNumber), TIN_MASK_CHAR, 4);
				String createdBy = e.getCompany().getCreatedBy() != null ? e.getCompany().getCreatedBy().getFullName() : "";
				forms.add(CollectionUtilities.newObjectMap(
						"id", e.getId(),
						"type", "T4A",
						"status", "Issued", // all T4 are Issued status
						"createdBy", createdBy,
						"createdOn", e.getActiveDate(),
						"tin", tin,
						"active", e.getActiveFlag()
				));
			} else if (AbstractTaxEntity.COUNTRY_OTHER.equals(e.getCountry())) {
				String taxNumber = vaultHelper.get(e, "taxNumber", e.getSecureTaxNumber()).getValue();
				String tin = StringUtilities.showLastNDigits(taxNumber, TIN_MASK_CHAR, 4);
				String createdBy = e.getCompany().getCreatedBy() != null ? e.getCompany().getCreatedBy().getFullName() : "";
				forms.add(CollectionUtilities.newObjectMap(
						"id", e.getId(),
						"type", "OTHER",
						"status", "Issued", // all other are Issued status
						"createdBy", createdBy,
						"createdOn", e.getActiveDate(),
						"tin", tin,
						"active", e.getActiveFlag()
				));
			}
		}

		// sort by date
		Collections.sort(forms, new Ordering<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> map1, Map<String, Object> map2) {
				return ((Calendar) map1.get("createdOn")).compareTo((Calendar) map2.get("createdOn"));
			}
		});

		model.addAttribute("yearNow", String.valueOf(DateUtilities.getCalendarNow().get(Calendar.YEAR)));
		model.addAttribute("forms", forms);
		model.addAttribute("email", getCurrentUser().getEmail());

		return "web/pages/mmw/taxes/index";
	}

	@RequestMapping(value = "/download_1099/{taxFormId}", method = RequestMethod.GET)
	public void download1099(@PathVariable("taxFormId") Long taxFormId, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		// Get tax form and tax form information
		Optional<TaxForm1099> form1099Opt = getForm1099FromId(taxFormId);

		int taxYear = 2012;
		String filename = "";
		if (form1099Opt.isPresent()) {
			TaxForm1099 form1099 = form1099Opt.get();
			taxYear = form1099.getTaxForm1099Set().getTaxYear();
			filename = form1099.getTaxFormPDFName();
			form1099.getTaxEntity().setTaxNumber(vaultHelper.get(form1099.getTaxEntity(), "taxNumber", form1099.getTaxEntity().getTaxNumberSanitized()).getValue());
		}

		// Fill mockup data
		setTaxInfoToModelData(model, form1099Opt);

		// Render template view with field values
		HTML2PDFView templateView = new HTML2PDFView("web/pages/irs/template");
		String tmplFile = templateView.renderToFile(model.asMap(), request);

		Resource formPDF = resourceLoader.getResource(StringUtilities.getPdfTemplateFilename(taxYear));

		PdfReader pdfReader = new PdfReader(formPDF.getInputStream());
		PdfReader tmplReader = new PdfReader(new FileInputStream(tmplFile));

		// Overlay form with template
		File file = File.createTempFile(filename, "");
		PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(file));
		PdfImportedPage page = stamper.getImportedPage(tmplReader, 1);
		PdfContentByte content = stamper.getOverContent(FORM_PAGE_NUMBER);
		content.addTemplate(page, 0, 0);

		stamper.close();

		response.setContentType(MimeType.PDF.getMimeType());
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", filename));
		FileInputStream inputStream = new FileInputStream(file);
		response.setContentLength(inputStream.available());
		IOUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	private Optional<TaxForm1099> getForm1099FromId(Long taxFormId) {
		Optional<TaxForm1099> form1099Opt;

		try {
			form1099Opt = taxReportService.get1099Form(taxFormId);
		} catch (IllegalEntityAccessException e) {
			logger.warn("Illegal 1099 download access: ", e);
			throw new HttpException401();
		} catch (Exception ex) {
			logger.error("", ex);
			throw new HttpException404();
		}

		return form1099Opt;
	}

	private void setTaxInfoToModelData(Model model, Optional<TaxForm1099> form1099Opt) {
		if (form1099Opt.isPresent()) {

			TaxForm1099 form1099 = form1099Opt.get();

			model.addAttribute("wm_name", Constants.WM_TAX_NAME);
			model.addAttribute("wm_address", WM_RES_TAX_ADDRESS);
			model.addAttribute("wm_phone", Constants.WM_TAX_PHONE);

			model.addAttribute("wm_ein", Constants.WM_TAX_EIN);
			model.addAttribute("rcpt_ssn_ein", form1099.getTaxEntity().getFormattedTaxNumberForForm1099());
			model.addAttribute("amount", form1099.getAmount());

			rcptAddress.setAddress1(StringUtils.upperCase(form1099.getAddress()));
			rcptAddress.setCity(StringUtils.upperCase(form1099.getCity()));
			rcptAddress.setState(StringUtils.upperCase(form1099.getState()));
			rcptAddress.setPostalCode(form1099.getPostalCode());

			model.addAttribute("rcpt_name", StringUtils.upperCase(StringEscapeUtils.unescapeHtml3(StringEscapeUtils.escapeXml(form1099.getFullName()))));
			model.addAttribute("rcpt_address", rcptAddress);
		}
	}

	private void setTaxServiceInfoToModelData(Model model, Optional<TaxServiceReport> taxServiceDetailReport) {

		AddressDTO address = new AddressDTO();

		if (taxServiceDetailReport.isPresent()) {
			TaxServiceReport report = taxServiceDetailReport.get();

			AbstractTaxEntity workerTaxEntity = report.getTaxEntity();
			if (report.isUseWMTaxEntity()) {
				model.addAttribute("wm_name", Constants.WM_TAX_NAME);
				model.addAttribute("wm_address", WM_RES_TAX_ADDRESS);
				model.addAttribute("wm_phone", Constants.WM_TAX_PHONE);
				model.addAttribute("wm_ein", Constants.WM_TAX_EIN);

			} else {
				AbstractTaxEntity buyerTaxEntity = taxService.findActiveTaxEntityByCompany(report.getBuyerCompanyId());
				vaultHelper.unobfuscateEntityFields(buyerTaxEntity);
				address.setAddress1(StringUtils.upperCase(buyerTaxEntity.getAddress()));
				address.setCity(StringUtils.upperCase(buyerTaxEntity.getCity()));
				address.setState(StringUtils.upperCase(buyerTaxEntity.getState()));
				address.setPostalCode(buyerTaxEntity.getPostalCode());
				address.setCountry(StringUtils.upperCase(buyerTaxEntity.getCountry()));

				model.addAttribute("wm_name", StringUtils.upperCase(buyerTaxEntity.getTaxName()));
				model.addAttribute("wm_address", address); // Filling address to 1099 pdf file
				model.addAttribute("wm_phone", buyerTaxEntity.getPhoneNumber());
				model.addAttribute("wm_ein", buyerTaxEntity.getFormattedTaxNumber());
			}

			model.addAttribute("rcpt_ssn_ein", workerTaxEntity.getFormattedTaxNumberForForm1099());
			model.addAttribute("amount", report.getFormattedNetEarnings());

			rcptAddress.setAddress1(StringUtils.upperCase(report.getAddress()));
			rcptAddress.setCity(StringUtils.upperCase(report.getCity()));
			rcptAddress.setState(StringUtils.upperCase(report.getState()));
			rcptAddress.setPostalCode(report.getPostalCode());

			model.addAttribute("rcpt_name", StringUtils.upperCase(StringEscapeUtils.unescapeHtml3(StringEscapeUtils.escapeXml(report.getFullName()))));
			model.addAttribute("rcpt_address", rcptAddress);
		}


	}

	@RequestMapping(value = "/download_earning_report/{earningReportId}", method = RequestMethod.GET)
	public ModelAndView downloadEarningReport(@PathVariable("earningReportId") Long earningReportId) throws Exception {

		HTML2PDFView view = new HTML2PDFView(StringUtils.EMPTY);
		try {
			view.setHtml(taxReportService.getEarningReportPdfView(earningReportId));
		} catch (IllegalEntityAccessException e) {
			logger.warn("Illegal Earning Report download access: ", e);
			throw new HttpException401();
		} catch (Exception ex) {
			logger.error("", ex);
			throw new HttpException404();
		}

		return new ModelAndView(view);
	}

	@RequestMapping(value="/download_earning_detail_report/{id}", method=RequestMethod.GET)
	public CSVView downloadEarningDetailReport(@PathVariable("id") Long earningDetailResultSetId, Model model) throws Exception {

		List<EarningDetailReport> earningDetailReports = taxReportService.findAvailableEarningDetailReportsByUserIdAndReportSetId(getCurrentUser().getId(), earningDetailResultSetId);
		List<String[]> rows = Lists.newArrayList();
		final String[] header = {"TIN", "First Name", "Last Name", "Address", "City", "State",
				"Postal Code", "Country", "Gross Earnings", "Expenses", "Net Earnings"};
		rows.add(header);

		String taxYear = String.valueOf(DateUtilities.getCalendarNow().get(Calendar.YEAR) - 1);

		Map<Long, String> taxEntityIdTaxNumberMap = taxService.getTaxIdToTaxNumberMapFromVault(earningDetailReports);

		for (EarningDetailReport earningDetailReport : earningDetailReports) {
			taxYear = earningDetailReport.getTaxYear();
			List<String> data = Lists.newArrayList(
				taxService.getFormattedTaxNumber(earningDetailReport.getTaxEntity(), taxEntityIdTaxNumberMap.get(earningDetailReport.getTaxEntity().getId())),
				(earningDetailReport.getBusinessFlag() ? StringUtils.EMPTY : StringUtils.upperCase(earningDetailReport.getFirstName())),
				(StringUtils.upperCase(earningDetailReport.getLastName())),
				StringUtils.upperCase(earningDetailReport.getAddress()),
				StringUtils.upperCase(earningDetailReport.getCity()),
				StringUtils.upperCase(earningDetailReport.getState()),
				earningDetailReport.getPostalCode(),
				StringUtils.upperCase(earningDetailReport.getCountry()),
				earningDetailReport.getEarnings().setScale(2, RoundingMode.HALF_UP).toString(),
				earningDetailReport.getExpenses().setScale(2, RoundingMode.HALF_UP).toString(),
				(earningDetailReport.getEarnings().add(earningDetailReport.getExpenses())).setScale(2, RoundingMode.HALF_UP).toString()
			);
			rows.add(data.toArray(new String[0]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("1099-MISC-Data-report-%s.csv", taxYear));

		return view;
	}

	@RequestMapping(value="/download_earning_detail_report_data/{taxYear}", method=RequestMethod.GET)
	public CSVView downloadEarningDetailReportData(@PathVariable("taxYear") Integer taxYear, Model model) throws Exception {

		List<String[]> rows = Lists.newArrayList();

		final String VOR_COMPANY_NAME = "Resource Enterprise Services LLC" ;
		final String[] header = {"Company", "Gross Earnings", "Expenses", "Net Earnings"};
		rows.add(header);

		// Earnings from VOR
		List<EarningReport> earningReport = taxReportService.findAvailableEarningReports(getCurrentUser().getId());

		for (EarningReport report: earningReport) {
			if(String.valueOf(taxYear).equals(report.getTaxYear())) {
				List<String> data = Lists.newArrayList(
						VOR_COMPANY_NAME,
						report.getFormattedTotalVorEarningPaymentReversalAndMarketing(),
						report.getFormattedVorExpenses(),
						report.getFormattedVorNetEarnings()
				);
				rows.add(data.toArray(new String[0]));
			}
		}

		// Earnings from NON-VOR
		List<EarningDetailReportRow> earningDetailReportRowList = taxReportService.getEarningDetailReportForUserInYear(getCurrentUser().getId(), taxYear);

		for (EarningDetailReportRow e : earningDetailReportRowList) {
			List<String> data = Lists.newArrayList(
					e.getBuyerCompanyName(),
					e.getEarnings().setScale(2, RoundingMode.HALF_UP).toString(),
					e.getExpenses().setScale(2, RoundingMode.HALF_UP).toString(),
					(e.getEarnings().add(e.getExpenses())).setScale(2, RoundingMode.HALF_UP).toString()
			);
			rows.add(data.toArray(new String[0]));
		}

		// Earnings Tax Service
		List<TaxReportDetailRow> taxReportDetailRowList = taxReportService.getTaxDetailReportForUserInYear(getCurrentUser().getId(), taxYear);

		BigDecimal earnings = BigDecimal.ZERO;
		BigDecimal expenses = BigDecimal.ZERO;

		for (TaxReportDetailRow e : taxReportDetailRowList) {
			BigDecimal netEarnings = e.getEarnings().add(e.getExpenses());
			if (netEarnings.compareTo(Constants.TAX_FORM_1099_GENERATION_AMOUNT_THRESHOLD) >= 0 && e.isUseWMTaxEntity()) {
				earnings = earnings.add(e.getEarnings());
				expenses = expenses.add(e.getExpenses());
				continue;
			} else {
				List<String> data = Lists.newArrayList(
						e.getBuyerCompanyName(),
						e.getEarnings().setScale(2, RoundingMode.HALF_UP).toString(),
						e.getExpenses().setScale(2, RoundingMode.HALF_UP).toString(),
						(e.getEarnings().add(e.getExpenses())).setScale(2, RoundingMode.HALF_UP).toString()
				);
				rows.add(data.toArray(new String[0]));
			}
		}

		if (earnings.compareTo(BigDecimal.ZERO) == 1) {
			List<String> aggregateRESData = Lists.newArrayList(
					VOR_COMPANY_NAME,
					earnings.setScale(2, RoundingMode.HALF_UP).toString(),
					expenses.setScale(2, RoundingMode.HALF_UP).toString(),
					(earnings.add(expenses)).setScale(2, RoundingMode.HALF_UP).toString()
			);
			rows.add(aggregateRESData.toArray(new String[0]));
		}

		model.addAttribute(CSVView.CSV_MODEL_KEY, rows);

		CSVView view = new CSVView();
		view.setFilename(String.format("earnings-report-data-%s.csv", String.valueOf(taxYear)));

		return view;
	}

	@RequestMapping(value = "/download_tax_service_detail_report/{taxServiceDetailReportId}", method = RequestMethod.GET)
	public void downloadTaxServiceDetailReport(@PathVariable("taxServiceDetailReportId") Long taxServiceDetailReportId, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		// Get tax form and tax form information
		Optional<TaxServiceReport> taxServiceDetailReport = getTaxServiceDetailReportFromId(taxServiceDetailReportId);

		int taxYear = 2012;
		String filename = "";
		if (taxServiceDetailReport.isPresent()) {
			TaxServiceReport report = taxServiceDetailReport.get();
			taxYear = report.getTaxServiceReportSet().getTaxYear();
			filename = report.getTaxFormPDFName();
			report.getTaxEntity().setTaxNumber(vaultHelper.get(report.getTaxEntity(), "taxNumber", report.getTaxEntity().getTaxNumberSanitized()).getValue());
		}

		// Fill mockup data
		setTaxServiceInfoToModelData(model, taxServiceDetailReport);

		// Render template view with field values
		HTML2PDFView templateView = new HTML2PDFView("web/pages/irs/template");
		String tmplFile = templateView.renderToFile(model.asMap(), request);

		Resource formPDF = resourceLoader.getResource(StringUtilities.getPdfTemplateFilename(taxYear));

		PdfReader pdfReader = new PdfReader(formPDF.getInputStream());
		PdfReader tmplReader = new PdfReader(new FileInputStream(tmplFile));

		// Overlay form with template
		File file = File.createTempFile(filename, "");
		PdfStamper stamper = new PdfStamper(pdfReader, new FileOutputStream(file));
		PdfImportedPage page = stamper.getImportedPage(tmplReader, 1);
		PdfContentByte content = stamper.getOverContent(FORM_PAGE_NUMBER);
		content.addTemplate(page, 0, 0);

		stamper.close();

		response.setContentType(MimeType.PDF.getMimeType());
		response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", filename));
		FileInputStream inputStream = new FileInputStream(file);
		response.setContentLength(inputStream.available());
		IOUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	private Optional<TaxServiceReport> getTaxServiceDetailReportFromId(Long id) {
		Assert.notNull(id);
		Optional<TaxServiceReport> taxServiceDetailReport;

		try {
			if (id == RES_TAX_VIRTUAL_ID) {
				// Generate RES Tax Report for 2014
				taxServiceDetailReport = taxReportService.getRESTaxServiceReportForYear(2014, getCurrentUser().getCompanyId());
			} else {
				taxServiceDetailReport = taxReportService.getTaxServiceDetailReport(id);
			}
		} catch (IllegalEntityAccessException e) {
			logger.warn("Illegal tax service detail report download access: ", e);
			throw new HttpException401();
		} catch (Exception ex) {
			logger.error("", ex);
			throw new HttpException404();
		}

		return taxServiceDetailReport;
	}

	@RequestMapping(value = "/download_tax_form/{taxFormId}", method = RequestMethod.GET)
	public void downloadW9(
			@PathVariable("taxFormId") Long taxFormId,
			final HttpServletResponse response) throws Exception {
		if (taxFormId == null) {
			return;
		}

		Tuple2<File, String> fileAndName = null;
		try {
			fileAndName = taxService.buildPdfForTaxForm(taxService.findTaxEntityByIdAndCompany(taxFormId, getCurrentUser().getCompanyId()));
			if (fileAndName != null) {
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", String.format("inline; filename=%s", fileAndName.getSecond()));
				int bytesCopied = IOUtils.copy(new FileInputStream(fileAndName.getFirst()), response.getOutputStream());
				response.setContentLength(bytesCopied);
				response.flushBuffer();
			}
		} catch (final Exception e) {
			logger.error("Error building pdf file", e);
		} finally {
			try {
				if (fileAndName != null && fileAndName.getFirst() != null) {
					fileAndName.getFirst().delete();
				}
			} catch (final Exception e) {
				logger.error("Error closing pdf file", e);
			}
		}
	}
}
