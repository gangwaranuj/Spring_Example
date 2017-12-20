package com.workmarket.reporting;


import com.workmarket.domains.model.User;
import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.reporting.query.CSVRowBasedSQLExecutor;
import com.workmarket.reporting.query.GenericQueryBuilderSqlImpl;
import com.workmarket.reporting.query.GenericRowMapper;
import com.workmarket.reporting.util.CSVReportWriter;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.BrokenTest;
import com.workmarket.thrift.work.display.WorkDisplay;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class ReportExecutorIT extends BaseServiceIT {

    @Autowired private WorkDisplay.Iface customReportService;
    @Autowired private ReportingContext reportingContext;
    @Autowired private UserService userService;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private CompanyService companyService;


    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Before
    public void init() {
        User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
        authenticationService.setCurrentUser(currentUser);
    }


    //Dan - excuse the hardcoded data here... its quite complex to "create a report" programmatically
    //TODO - need to create a report DTO/DAO methods so I can easily create reports in tests without relying on test data
    @Test
    public void testCustomFieldsReport() throws Exception{

        ReportRequestData request = customReportService.extractReportRequestData(814, Locale.ENGLISH, companyService.findCompanyById(1L));

        Assert.isTrue(request.getHasWorkCustomFields());
        String filename = "report-dan-test";

        filename += DateUtilities.formatCalendar_MMDDYY(DateUtilities.getCalendarNow()) + Constants.CSV_EXTENSION;
        CSVReportWriter writer = new CSVReportWriter(request, reportingContext.getEntities(), filename, "/tmp");

        CSVRowBasedSQLExecutor executor = new CSVRowBasedSQLExecutor();
        executor.setJdbcTemplate(jdbcTemplate);
        executor.setRowMapper(new GenericRowMapper(reportingContext, request));
        executor.setCSVReportWriter(writer);
        executor.setSqlBuilder(new GenericQueryBuilderSqlImpl().buildQuery(reportingContext, request));
        executor.query();
    }



}
