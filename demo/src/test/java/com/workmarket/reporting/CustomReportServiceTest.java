package com.workmarket.reporting;

import com.workmarket.dao.ReportingCriteriasDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomReportServiceImpl;
import com.workmarket.service.business.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomReportServiceTest {

    @Mock CompanyService companyService;
    @Mock ReportingCriteriasDAO reportingCriteriasDAO;
    @Mock UserService userService;
    @InjectMocks CustomReportServiceImpl customReportService;

    private ReportingCriteria reportingCriteria;
    private Company company;

    @Before
    public void init() {

        reportingCriteria = mock(ReportingCriteria.class);
        company = mock(Company.class);

        when(reportingCriteria.getCompany()).thenReturn(company);
        when(company.getId()).thenReturn(1L);
        when(reportingCriteriasDAO.get(anyLong())).thenReturn(reportingCriteria);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateSavedCustomReport_withNullReportId_fails() {
        customReportService.generateSavedCustomReport(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateAdhocCustomReport_withNullArguments_fails() {
        customReportService.generateAdhocCustomReport(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDataTableColumnHeaders_withNullArguments_fails() {
        customReportService.createDataTableColumnHeaders(null, null);
    }

    @Test
    public void validateAccessToCustomReport_withSameCompany_success() {
        assertTrue(customReportService.hasAccessToCustomReport(1L, 1L));
    }

    @Test
    public void validateAccessToCustomReport_withDifferentCompany_success() {
        assertFalse(customReportService.hasAccessToCustomReport(1L, 1000L));
    }
}
