package com.workmarket.service.business;

import com.workmarket.dao.ReportingCriteriasDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.reporting.AbstractReportFilterBuilder;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.domains.model.reporting.ReportingCriteriaFiltering;
import com.workmarket.reporting.service.WorkReportGeneratorServiceImpl;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by timothy on 12/8/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomReportServiceTest {

    @Mock AuthenticationService authenticationService;
    @Mock private ReportingCriteriasDAO reportingCriteriasDAO;
    @Mock private WorkReportGeneratorServiceImpl workReportGeneratorService;
    @Mock private WorkDisplay.Iface workDisplayHandler;
    @Mock private ReportingContext workReportingContext;

    @InjectMocks CustomReportServiceImpl customReportService;

    private User user;
    private Company company;
    private WorkCustomField customField;

    @Before
    public void setup() {

        user = mock(User.class);
        company = mock(Company.class);

        when(user.getId()).thenReturn(123L);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(user.getCompany()).thenReturn(company);
        when(user.isAdminOrManager()).thenReturn(true);
        when(company.getId()).thenReturn(5L);

        customField = mock(WorkCustomField.class);
        when(customField.getId()).thenReturn(123L);
    }

    /**
     * reportRequestData.hasWorkCustomFields should be set to true if reportingCriteria.isCustomFieldReport or if
     * reportingCriteria contains a custom field filter. This test covers the case where the reportingCriteria
     * contains a custom field filter
     */
    @Test
    public void getReportRequestData_withCustomFields_hasWorkCustomFieldsFlagIsTrue() {

        ReportingCriteria reportingCriteria = new ReportingCriteria();
        reportingCriteria.setCustomFieldsReport(false);
        reportingCriteria.setCompany(company);

        // Add WorkCustomField to report
        List<ReportingCriteriaFiltering> reportCriteriaFiltering = new ArrayList<ReportingCriteriaFiltering>();

        ReportingCriteriaFiltering workCustomField = new ReportingCriteriaFiltering();
        workCustomField.setProperty((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELD_ID));
        workCustomField.setFieldValue(customField.getId().toString());
        workCustomField.setReportingCriteria(reportingCriteria);
        reportCriteriaFiltering.add(workCustomField);

        ReportingCriteriaFiltering workCustomFields = new ReportingCriteriaFiltering();
        workCustomFields.setProperty((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELDS));
        workCustomFields.setFieldValue(customField.getId().toString());
        workCustomFields.setReportingCriteria(reportingCriteria);
        reportCriteriaFiltering.add(workCustomFields);

        reportingCriteria.setReportingCriteriaFiltering(reportCriteriaFiltering);

        ReportRequestData reportRequestData = customReportService.getReportRequestData(reportingCriteria);
        assertTrue(reportRequestData.getHasWorkCustomFields());
    }

    /**
     * reportRequestData.hasWorkCustomFields should be set to true if reportingCriteria.isCustomFieldReport or if
     * reportingCriteria contains a custom field filter. This test covers the case where reportingCriteria.hasWorkCustomFields is true
     */
    @Test
    public void getReportRequestData_customFieldReport_hasWorkCustomFieldsFlagIsTrue() {

        ReportingCriteria reportingCriteria = new ReportingCriteria();
        reportingCriteria.setCustomFieldsReport(true);
        reportingCriteria.setCompany(company);

        // Add WorkCustomField to report
        List<ReportingCriteriaFiltering> reportCriteriaFiltering = new ArrayList<ReportingCriteriaFiltering>();

        ReportRequestData reportRequestData = customReportService.getReportRequestData(reportingCriteria);
        assertTrue(reportRequestData.getHasWorkCustomFields());
    }
}
