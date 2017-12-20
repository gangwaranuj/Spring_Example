package com.workmarket.reporting;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.ReportingCriteriasDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.reporting.AbstractReportFilterBuilder;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.domains.model.reporting.ReportingCriteria;
import com.workmarket.domains.model.reporting.ReportingCriteriaFiltering;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomReportService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReportRequestDataIT  extends BaseServiceIT {

    @Autowired private WorkDisplay.Iface workDisplay;
    @Autowired private CustomReportService customReportService;
    @Autowired private ReportingContext reportingContext;
    @Autowired private UserService userService;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private CompanyService companyService;
    @Autowired private ReportingCriteriasDAO reportingCriteriasDAO;
    @Autowired private WorkCustomFieldGroupDAO workCustomFieldGroupDAO;

    @Before
    public void init() {
        User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
        authenticationService.setCurrentUser(currentUser);
    }

    /**
     * A bug was reported where the CSV attached to 'report' emails was missing custom fields. I investigated and
     * found that the ReportRequestData#hasCustomFields flag was only set to true if the ReportingCriteria.customFieldsReport
     * was true.
     * The ReportRequestData object was constructed differently for the email and exports. In these cases, the
     * ReportRequestData#hasCustomFieldswas set to true if ReportingCriteria.customFieldsReport was true OR if the
     * ReportingCriteria contained a 'custom field' ReportingCriteriaFiltering object
     * This test creates a report with custom field and compares the ReportRequestData object created by customReportService.getReportRequestData
     * and the one created by workDisplay.extractReportRequestData
     *
     */
    @Test
    @Transactional
    public void testCustomFieldsReportRequestData() throws Exception {

        User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
        authenticationService.setCurrentUser(employee);

        ReportingCriteria reportingCriteria = new ReportingCriteria();
        reportingCriteria.setCompany(employee.getCompany());
        reportingCriteria.setCustomFieldsReport(false);
        reportingCriteria.setDisplayKeys("workPostalCode,workCity,work.workNumber,work_milestones.paymentDate,subStatuses,clientCompanyName,work.title,work.pricingStrategy,workState,resolvedSubStatuses,projectName,templateName");
        reportingCriteria.setReportName("Test report");

        reportingCriteriasDAO.saveOrUpdate(reportingCriteria);

        // Create WorkCustomField
        WorkCustomField customField = new WorkCustomField();
        customField.setName("Test custom field 1");
        customField.setRequiredFlag(true);
        customField.setWorkCustomFieldType(new WorkCustomFieldType(WorkCustomFieldType.RESOURCE));

        WorkCustomFieldGroup workCustomFieldGroup = new WorkCustomFieldGroup();
        workCustomFieldGroup.setName("Test custom field group");
        workCustomFieldGroup.setCompany(employee.getCompany());
        workCustomFieldGroup.setWorkCustomFields(Arrays.asList(new WorkCustomField[]{customField}));

        workCustomFieldGroupDAO.saveOrUpdate(workCustomFieldGroup);

        // Add WorkCustomField to report
        List<ReportingCriteriaFiltering> reportCriteriaFiltering = new ArrayList<ReportingCriteriaFiltering>();

        ReportingCriteriaFiltering workCustomField = new ReportingCriteriaFiltering();
        workCustomField.setCreatorId(employee.getId());
        workCustomField.setCreatedOn(Calendar.getInstance());
        workCustomField.setProperty((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELD_ID));
        workCustomField.setFilteringType(FilteringType.FIELD_VALUE.getType());
        workCustomField.setFieldValue(customField.getId().toString());
        workCustomField.setFieldValueOperator(RelationalOperator.EQUAL_TO.getOperator());
        workCustomField.setModifiedOn(Calendar.getInstance());
        workCustomField.setModifierId(employee.getId());
        workCustomField.setReportingCriteria(reportingCriteria);
        reportCriteriaFiltering.add(workCustomField);

        ReportingCriteriaFiltering workCustomFields = new ReportingCriteriaFiltering();
        workCustomFields.setCreatorId(employee.getId());
        workCustomFields.setCreatedOn(Calendar.getInstance());
        workCustomFields.setProperty((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELDS));
        workCustomFields.setFilteringType(FilteringType.FIELD_VALUE.getType());
        workCustomFields.setFieldValue(customField.getId().toString());
        workCustomFields.setFieldValueOperator(RelationalOperator.EQUAL_TO.getOperator());
        workCustomFields.setModifiedOn(Calendar.getInstance());
        workCustomFields.setModifierId(employee.getId());
        workCustomFields.setReportingCriteria(reportingCriteria);
        reportCriteriaFiltering.add(workCustomFields);

        reportingCriteria.setReportingCriteriaFiltering(reportCriteriaFiltering);

        // Create ReportRequestData using CustomReportService#getReportRequestData will set 'hasWorkCustomFields' flag if a WorkCustomField has been added
        ReportRequestData reportDataFromConstructor = customReportService.getReportRequestData(reportingCriteria);
        assertTrue(reportDataFromConstructor.getHasWorkCustomFields());

        // Create ReportRequestData using WorkDisplay.Iface#extractReportRequestData does not set 'hasWorkCustomFields' flag if a WorkCustomField has been added
        ReportRequestData reportDataExtracted = workDisplay.extractReportRequestData(reportingCriteria.getId(), Locale.ENGLISH, reportingCriteria.getCompany());
        assertFalse(reportDataExtracted.getHasWorkCustomFields());

    }
}
