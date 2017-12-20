package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class NotificationTemplateIT extends BaseServiceIT {
    @Autowired private NotificationDispatcher notificationDispatcher;

    @Test
    public void sendWorkResourceCheckInNotification() throws Exception{
        Calendar schedule = Calendar.getInstance();
        schedule.add(Calendar.MINUTE, 10); //must be in future
        User employee = this.newEmployeeWithCashBalance();
        User contractor = this.newContractor();
        Work work = this.createWorkAndSendToResourceNoPaymentTermsAndAccept(employee, contractor, schedule);
        WorkResourceCheckInNotificationTemplate template = new WorkResourceCheckInNotificationTemplate(contractor.getId(), work);
        notificationDispatcher.dispatchNotification(template);
    }

}
