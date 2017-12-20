package com.workmarket.domains.work.service;

import com.workmarket.dao.changelog.work.WorkNotifyChangeLogDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.changelog.work.WorkNotifyChangeLog;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Calendar;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkChangeLogServiceIT extends BaseServiceIT {
    @Autowired WorkChangeLogService workChangeLogService;

    private User employee;
    private User contractor;
    private Work work;

    private void initializeVars() throws Exception {
        employee = newFirstEmployeeWithCashBalance();
        contractor = newContractor();
        work = newWork(employee.getId());
        assertNotNull(work);
    }

    @Test
    public void shouldReturnCountZeroWithNoLogs() throws Exception {
        initializeVars();
        int count = workChangeLogService.getWorkNotifyLogCountSinceDate(work.getId(), Calendar.getInstance());
        Assert.isTrue(count == 0);
    }

    @Test
    public void shouldReturnCountZeroIfOneLogBeforeDate() throws Exception {
        initializeVars();
        workChangeLogService.saveWorkChangeLog(new WorkNotifyChangeLog(work.getId(), 1L, 1L, 1L));
        int count = workChangeLogService.getWorkNotifyLogCountSinceDate(work.getId(), Calendar.getInstance());
        Assert.isTrue(count == 0);
    }

    @Test
    public void shouldReturnCountOneIfLogSinceDate() throws Exception {
        initializeVars();
        workChangeLogService.saveWorkChangeLog(new WorkNotifyChangeLog(work.getId(), 1L, 1L, 1L));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        int count = workChangeLogService.getWorkNotifyLogCountSinceDate(work.getId(), cal);
        Assert.isTrue(count == 1);
    }
}