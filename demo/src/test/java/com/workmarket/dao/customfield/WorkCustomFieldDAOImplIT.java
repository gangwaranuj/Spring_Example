package com.workmarket.dao.customfield;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.reports.model.CustomReportCustomFieldDTO;
import com.workmarket.domains.reports.model.CustomReportCustomFieldGroupDTO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkCustomFieldDAOImplIT extends BaseServiceIT {
    private static final String VALUE = "value";
    @Autowired @Qualifier("workCustomFieldDAOImpl") WorkCustomFieldDAO workCustomFieldDAO;
    @Autowired CustomFieldService customFieldService;

    private User user;
    private Work work;
    private WorkCustomFieldGroup workCustomFieldGroup;

    @Test
    public void shouldFetchAllCustomFields() throws Exception {
        initializeWorkWithCustomFields();

        CustomFieldReportFilters filters = new CustomFieldReportFilters();
        filters.setWorkIds(ImmutableList.of(work.getId()));
        List<CustomFieldReportRow> rows = workCustomFieldDAO.findAllWorkCustomFields(user.getId(), user.getCompany().getId(), filters);
        assertTrue(rows.size() == 1);
    }

    @Test
    public void shouldFetchTheCorrectCustomFields() throws Exception {
        initializeWorkWithCustomFields();

        CustomFieldReportFilters filters = new CustomFieldReportFilters();
        filters.setWorkIds(ImmutableList.of(work.getId()));
        Map<Long, List<CustomFieldReportRow>> fields = workCustomFieldDAO.getWorkCustomFieldsMap(user.getId(), user.getCompany().getId(), filters);
        assertTrue(fields.get(work.getId()).get(0).getFieldValue().equals(VALUE));
    }

    @Test
    public void shouldFilterOutMissingCustomFields() throws Exception {
        initializeWorkWithCustomFields();

        CustomFieldReportFilters filters = new CustomFieldReportFilters();
        filters.setWorkIds(ImmutableList.of(work.getId()));
        filters.setWorkCustomFieldIds(ImmutableList.of(-1L));
        List<CustomFieldReportRow> rows = workCustomFieldDAO.findAllWorkCustomFields(user.getId(), user.getCompany().getId(), filters);
        assertTrue(rows.size() == 0);
    }

    @Test
    public void shouldFetchTheRequestedCorrectCustomFields() throws Exception {
        initializeWorkWithCustomFields();

        CustomFieldReportFilters filters = new CustomFieldReportFilters();
        filters.setWorkIds(ImmutableList.of(work.getId()));
        filters.setWorkCustomFieldIds(ImmutableList.of(workCustomFieldGroup.getWorkCustomFields().get(0).getId()));
        List<CustomFieldReportRow> rows = workCustomFieldDAO.findAllWorkCustomFields(user.getId(), user.getCompany().getId(), filters);
        assertTrue(rows.size() == 1);
    }

    @Test
    public void shouldReturnCustomFieldGroupsForCompany() throws Exception {
        initializeWorkWithCustomFields();

        List<CustomReportCustomFieldGroupDTO> customFieldGroups = workCustomFieldDAO.findCustomReportCustomFieldGroupsForCompanyAndReport(work.getCompany().getId(), null);
        CustomReportCustomFieldGroupDTO foundGroup = Iterables.find(customFieldGroups, new Predicate<CustomReportCustomFieldGroupDTO>() {
            @Override
            public boolean apply(CustomReportCustomFieldGroupDTO customReportCustomFieldGroupDTO) {
                return customReportCustomFieldGroupDTO.getId() == workCustomFieldGroup.getId();
            }
        }, null);

        assertNotNull(foundGroup);
    }

    private void initializeWorkWithCustomFields() throws Exception {
        user = userService.getUser(ANONYMOUS_USER_ID);
        work = newWork(user.getId());
        workCustomFieldGroup = addCustomFieldsToWork(work.getId());
        WorkCustomFieldDTO workCustomFieldDTO = new WorkCustomFieldDTO();
        workCustomFieldDTO.setId(workCustomFieldGroup.getWorkCustomFields().get(0).getId());
        workCustomFieldDTO.setValue(VALUE);
        customFieldService.saveWorkCustomFieldForWork(workCustomFieldDTO, work.getId());
    }
}