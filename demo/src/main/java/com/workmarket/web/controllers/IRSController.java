package com.workmarket.web.controllers;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.workmarket.domains.model.MimeType;
import com.workmarket.dto.AddressDTO;
import com.workmarket.web.views.HTML2PDFView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;

@Controller
@RequestMapping("/irs")
public class IRSController extends BaseController {
	private static final String F1099_PATH = "src/main/resources/files/f1099msc_12.pdf";
	private static final int FORM_PAGE_NUMBER = 4;

	@Autowired private ResourceLoader resourceLoader;

	private final AddressDTO wmAddress = new AddressDTO();
	private final AddressDTO rcptAddress = new AddressDTO();


	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody void index(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		// Set response type to PDF
		response.setContentType(MimeType.PDF.getMimeType());

		// Fill mockup data
		setModelData(model);

		// Render template view with field values
		HTML2PDFView templateView = new HTML2PDFView("web/pages/irs/template", "");
		String tmplFile = templateView.renderToFile(model.asMap(), request);

		Resource formPDF = resourceLoader.getResource("file:" + F1099_PATH);
		PdfReader pdfReader = new PdfReader(formPDF.getInputStream());
		PdfReader tmplReader = new PdfReader(new FileInputStream(tmplFile));

		// Overlay form with template
		PdfStamper stamper = new PdfStamper(pdfReader, response.getOutputStream());
		PdfContentByte content = stamper.getOverContent(FORM_PAGE_NUMBER);
		content.getInternalBuffer().append(tmplReader.getPageContent(1));

		stamper.close();
	}


	private void setModelData(Model model) {
		wmAddress.setAddress1("20 West 20th Street");
		wmAddress.setAddress2("Suite 402");
		wmAddress.setCity("New York");
		wmAddress.setState("NY");
		wmAddress.setPostalCode("10011");
		wmAddress.setCountry("USA");

		model.addAttribute("wm_name", "WORK MARKET INC.");
		model.addAttribute("wm_address", wmAddress);
		model.addAttribute("wm_phone", "1-212-229-9675");

		model.addAttribute("wm_ein", "27-2580820");
		model.addAttribute("rcpt_ssn_ein", "XX-XXXXXXX");
		model.addAttribute("amount", 123456.78);

		rcptAddress.setAddress1("242 West 30th Street, 15th Floor");
		rcptAddress.setCity("New York");
		rcptAddress.setState("NY");
		rcptAddress.setPostalCode("10001");

		model.addAttribute("rcpt_name", "Johnson Lorenzo");
		model.addAttribute("rcpt_address", rcptAddress);
	}
}
