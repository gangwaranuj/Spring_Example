package com.workmarket.service.business.screening;

import com.workmarket.screening.model.ScreeningStatusCode;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class ExternalScreeningServiceMockImpl implements ExternalScreeningService {
	@Override
	public String getVendorId(String results) throws Exception {
 		return (String) evaluateXpath(
				results,
				"//BackgroundReports/BackgroundReportPackage/ClientReferenceId/IdValue",
				XPathConstants.STRING
		);
	}

	@Override
	public com.workmarket.screening.model.ScreeningStatusCode getScreeningStatus(String results) throws Exception {
		String resultStatus = (String) evaluateXpath(
				results,
				"//BackgroundReports/BackgroundReportPackage/ScreeningStatus/ResultStatus",
				XPathConstants.STRING
		);

		String orderStatus = (String) evaluateXpath(
				results,
				"//BackgroundReports/BackgroundReportPackage/ScreeningStatus/OrderStatus",
				XPathConstants.STRING
		);

		// For available response values:
		// @see http://ns.hr-xml.org/2_3/HR-XML-2_3/Screening/BackgroundReports.html
		// TODO Followup with Acxiom as to what conditions would return any of the following that we're not handling.
		// OrderStatus: New, InProgress, Cancelled, Suspended, Completed, Fulfilled, Delayed, Hold
		// ResultStatus: Pass, Fail, Review, Hit, Clear, UnableToContact, UnableToVerify

		if (orderStatus.equals("Cancelled"))
			return ScreeningStatusCode.CANCELLED;

		if (orderStatus.equals("Completed")) {
			if (resultStatus.equals("Clear") || resultStatus.equals("Pass"))
				return ScreeningStatusCode.PASSED;
			if (resultStatus.equals("Hit") || resultStatus.equals("Fail"))
				return ScreeningStatusCode.FAILED;
		}

		return ScreeningStatusCode.REVIEW;
	}

	private Object evaluateXpath(String source, String expression, QName type) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return xpath.evaluate(expression, builder.parse(new InputSource(new StringReader(source))), type);
	}
}