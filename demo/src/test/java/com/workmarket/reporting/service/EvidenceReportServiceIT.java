package com.workmarket.reporting.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.ScreeningService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class EvidenceReportServiceIT extends BaseServiceIT {

	@Autowired ScreeningService screeningService;
	@Autowired EvidenceReportService evidenceReportService;

	@Test
	public void test_bulkDownloadBackgroundChecksHandler_singleCertificate() throws Exception{
		User user = newWMEmployee();
		user.setEmail("qa@workmarket.com");
		evidenceReportService.bulkDownloadEvidenceReportHandler("qa@workmarket.com", 2223L, Screening.BACKGROUND_CHECK_TYPE);
	}

	@Test
	public void test_bulkDownloadBackgroundChecksHandler_multipleCertificates() throws Exception{
		evidenceReportService.bulkDownloadEvidenceReportHandler("qa@workmarket.com", 4804L, Screening.BACKGROUND_CHECK_TYPE);
	}

	@Test
	public void test_exportToCSVHandler_singleUser() throws Exception{
		evidenceReportService.exportToCSVHandler("qa@workmarket.com", 2223L, Screening.BACKGROUND_CHECK_TYPE);
	}

	@Test
	public void test_exportToCSVHandler_multipleUsers() throws Exception{
		evidenceReportService.exportToCSVHandler("qa@workmarket.com", 4804L, Screening.BACKGROUND_CHECK_TYPE);
	}

}
