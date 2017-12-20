package com.workmarket.service.thrift;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TWorkFacadeServiceIT extends BaseServiceIT {

    @Autowired private TWorkFacadeService tWorkFacadeService;

    @Test
    public void workResourceAcceptsWork_withDateTimeSet_appointmentSet() throws Exception {
        User employee = newFirstEmployeeWithCashBalance();
        Work work = newWork(employee.getId());
        User user1 = newContractorIndependentlane4Ready();

        assertNotNull(user1);
        assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

        laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());

        workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(user1.getUserNumber()));
        tWorkFacadeService.acceptWork(user1.getId(), work.getId());

        work = workService.findWork(work.getId());
        WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());

        assertNotNull(workResource.getAppointment().getFrom());
        assertEquals(workResource.getAppointment().getFrom(), work.getScheduleFrom());
    }

    @Test
    public void workResourceAcceptsWork_withDateRange_appointmentNotSet() throws Exception {
        User employee = newFirstEmployeeWithCashBalance();
        Calendar fiveDaysAgo = Calendar.getInstance();
        fiveDaysAgo.add(Calendar.DAY_OF_MONTH, -5);
        Calendar today = Calendar.getInstance();
        Work work = newWorkWithDateRange(employee.getId(), new WorkDTO(), fiveDaysAgo, today);
        User user1 = newContractorIndependentlane4Ready();

        assertNotNull(user1);
        assertEquals(WorkStatusType.DRAFT, work.getWorkStatusType().getCode());

        laneService.addUserToCompanyLane2(user1.getId(), work.getCompany().getId());

        workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(user1.getUserNumber()));
        tWorkFacadeService.acceptWork(user1.getId(), work.getId());

        work = workService.findWork(work.getId());
        WorkResource workResource = workService.findWorkResource(user1.getId(), work.getId());

        assertNull(workResource.getAppointment());
    }

    @Test
    public void workResourceAcceptsWorkOnBehalfOf_withDateTimeSet_appointmentSet() throws Exception {
        User dispatcher = createDispatcher();
        User worker = newCompanyEmployeeSharedWorkerConfirmed(dispatcher.getCompany().getId());

        User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
        authenticationService.setCurrentUser(buyer);
        WorkDTO dto = new WorkDTO();
        dto.setAssignToFirstResource(false);
        Work work = newWork(buyer.getId(), dto);
        laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
        workRoutingService.addToWorkResources(work.getId(), worker.getId());

        authenticationService.setCurrentUser(dispatcher);

        tWorkFacadeService.acceptWorkOnBehalf(createAcceptWorkOfferRequest(work, worker, dispatcher.getUserNumber()));

        work = workService.findWork(work.getId());

        WorkResource workResource = workService.findWorkResource(worker.getId(), work.getId());

        assertNotNull(workResource.getAppointment().getFrom());
        assertEquals(workResource.getAppointment().getFrom(), work.getScheduleFrom());
    }

    @Test
    public void workResourceAcceptsWorkOnBehalfOf_withoutDateRange_appointmentNotSet() throws Exception {
        User dispatcher = createDispatcher();
        User worker = newCompanyEmployeeSharedWorkerConfirmed(dispatcher.getCompany().getId());

        User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
        authenticationService.setCurrentUser(buyer);
        Calendar fiveDaysAgo = Calendar.getInstance();
        fiveDaysAgo.add(Calendar.DAY_OF_MONTH, -5);
        Calendar today = Calendar.getInstance();
        Work work = newWorkWithDateRange(buyer.getId(), new WorkDTO(), fiveDaysAgo, today);
        laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());
        workRoutingService.addToWorkResources(work.getId(), worker.getId());

        authenticationService.setCurrentUser(dispatcher);

        tWorkFacadeService.acceptWorkOnBehalf(createAcceptWorkOfferRequest(work, worker, dispatcher.getUserNumber()));

        work = workService.findWork(work.getId());

        WorkResource workResource = workService.findWorkResource(worker.getId(), work.getId());

        assertNull(workResource.getAppointment());
    }


}
