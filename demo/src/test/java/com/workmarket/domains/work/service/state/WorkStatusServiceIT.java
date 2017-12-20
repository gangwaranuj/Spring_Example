package com.workmarket.domains.work.service.state;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.filter.WorkSubStatusTypeFilter;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusType.TriggeredBy;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeDashboard;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeCompanySettingDTO;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.TimeTrackingRequest;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkStatusServiceIT extends BaseServiceIT {

	@Autowired private WorkSubStatusService workSubStatusService;

	@Autowired private WorkService workService;
	@Autowired private LaneService laneService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkNegotiationService workNegotiationService;

	@Test
	@Ignore
	public void test_resolveSubStatusByAction() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		authenticationService.setCurrentUser(employee);

		workSubStatusService.addSystemSubStatus(work.getId(), WorkSubStatusType.RESOURCE_NO_SHOW, StringUtils.EMPTY);

		List<WorkSubStatusType> list = workSubStatusService.findAllUnResolvedSubStatuses(work.getId());
		Assert.assertTrue(list.size() == 1);

		authenticationService.setCurrentUser(contractor);

		workService.checkInActiveResource(new TimeTrackingRequest().setWorkId(work.getId()).setDate(DateUtilities.getCalendarNow()));

		list = workSubStatusService.findAllUnResolvedSubStatuses(work.getId());

		Assert.assertTrue(list.contains(new WorkSubStatusType(WorkSubStatusType.RESOURCE_CHECKED_IN)));
		Assert.assertFalse(list.contains(new WorkSubStatusType(WorkSubStatusType.RESOURCE_NO_SHOW)));

	}

	@Test
	@Ignore
	public void test_resolveSubStatus() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());

		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		authenticationService.setCurrentUser(employee);

		workSubStatusService.addSystemSubStatus(work.getId(), WorkSubStatusType.GENERAL_PROBLEM, StringUtils.EMPTY);

		List<WorkSubStatusType> list = workSubStatusService.findAllUnResolvedSubStatuses(work.getId());
		Assert.assertTrue(list.size() == 1);

		//authenticationService.setCurrentUser(contractor);

		workSubStatusService.resolveSubStatus(employee.getId(), work.getId(), workSubStatusService.findSystemWorkSubStatus(WorkSubStatusType.GENERAL_PROBLEM).getId(), "Resolved!!!");

		list = workSubStatusService.findAllUnResolvedSubStatuses(work.getId());

		Assert.assertFalse(list.contains(new WorkSubStatusType(WorkSubStatusType.GENERAL_PROBLEM)));

	}

	@Test
	public void test_findAllSubStatuses() throws Exception {
		List<WorkSubStatusType> list = workSubStatusService.findAllSubStatuses(true, false);
		for (WorkSubStatusType w : list) {
			Assert.assertTrue(w.getTriggeredBy().equals(TriggeredBy.CLIENT_OR_RESOURCE));
		}

		list = workSubStatusService.findAllSubStatuses(true, true);
		for (WorkSubStatusType w : list) {
			Assert.assertTrue(w.getTriggeredBy().equals(TriggeredBy.CLIENT) || w.getTriggeredBy().equals(TriggeredBy.CLIENT_OR_RESOURCE));
		}
	}

	@Test
	public void testSaveCustomSubStatus() throws Exception {
		WorkSubStatusTypeDTO workSubStatusDTO = new WorkSubStatusTypeDTO();
		workSubStatusDTO.setCode("new " + RandomUtilities.generateNumericString(5));
		workSubStatusDTO.setDescription("some description");
		workSubStatusDTO.setAlert(true);
		workSubStatusDTO.setCompanyId(COMPANY_ID);
		workSubStatusDTO.setNotifyResourceEnabled(true);

		WorkSubStatusType subStatus = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusDTO);
		Assert.assertNotNull(subStatus);
		Assert.assertTrue(subStatus.isAlert());
		Assert.assertTrue(subStatus.isCustom());
		Assert.assertTrue(subStatus.isNotifyResourceEnabled());
		Assert.assertTrue(subStatus.isNotifyClientEnabled());
		Assert.assertTrue(subStatus.isActive());

		WorkSubStatusTypeCompanySettingDTO dto = new WorkSubStatusTypeCompanySettingDTO();
		dto.setWorkSubStatusTypeId(subStatus.getId());
		dto.setColorRgb("000000");
		WorkSubStatusTypeCompanySetting setting = workSubStatusService.saveWorkSubStatusTypeCompanySetting(COMPANY_ID, dto);
		Assert.assertNotNull(setting);
		Assert.assertEquals(setting.getColorRgb(), "000000");

		subStatus = workSubStatusService.deleteWorkSubStatus(subStatus.getId());
		Assert.assertTrue(subStatus.getDeleted());
	}

	@Test
	public void getSubStatusDashboard() {
		WorkSubStatusTypeDashboard dashboard = workSubStatusService.findWorkSubStatusDashboardByCompany(new WorkSubStatusTypeFilter());
		Assert.assertNotNull(dashboard);
		Assert.assertNull(dashboard.getSettingsByWorkSubStatus(1L));
	}

	@Test
	public void testFindUnresolvedSubStatus() throws Exception {
		User employee = newEmployeeWithCashBalance();
		Work work = newWork(employee.getId());
		List<WorkSubStatusType> unresolvedSubStatusesWithColor = workSubStatusService.findAllUnresolvedSubStatusWithColor(work.getId());
		List<WorkSubStatusType> unresolvedSubStatuses = workSubStatusService.findAllUnresolvedSubStatusWithColor(work.getId());
		Assert.assertNotNull(unresolvedSubStatuses);
		Assert.assertTrue(unresolvedSubStatuses.isEmpty());
		Assert.assertNotNull(unresolvedSubStatusesWithColor);
		Assert.assertTrue(unresolvedSubStatusesWithColor.isEmpty());
	}

	@Test
	public void testFindSubstatuses() throws Exception {
		authenticationService.setCurrentUser(ANONYMOUS_USER_ID);
		// WorkSubStatusTypeFilter [clientVisible=false, resourceVisible=true, showDeactivated=false, showCustomSubStatus=true,
		//showSystemSubStatus=true, triggeredBy=[CLIENT_OR_RESOURCE]]
		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setClientVisible(false);
		filter.setResourceVisible(true);
		filter.setShowDeactivated(false);
		filter.setShowSystemSubStatus(true);
		filter.setShowCustomSubStatus(true);
		List<WorkSubStatusType> sList = workSubStatusService.findAllSubStatuses(filter);
		for (WorkSubStatusType t : sList) {
			Assert.assertNotNull(t);
			Assert.assertTrue(t.isResourceEditable());
		}
	}

	@Test
	public void testWorkNegotiationAndSubStatus() throws Exception {
		User employee = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee);

		WorkSubStatusTypeDTO workSubStatusDTO = new WorkSubStatusTypeDTO();
		workSubStatusDTO.setCode("new " + RandomUtilities.generateNumericString(5));
		workSubStatusDTO.setDescription("some description");
		workSubStatusDTO.setAlert(true);
		workSubStatusDTO.setScheduleRequired(true);
		workSubStatusDTO.setCompanyId(employee.getCompany().getId());
		workSubStatusDTO.setNotifyResourceEnabled(true);

		WorkSubStatusType subStatus = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusDTO);
		Assert.assertNotNull(subStatus);

		User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		authenticationService.setCurrentUser(employee);
		Work work = newWork(employee.getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId());
		workService.acceptWork(contractor.getId(), work.getId());

		workSubStatusService.addSubStatus(work.getId(), subStatus.getId(), StringUtils.EMPTY);

		Calendar reschedule = DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0);
		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setAssociatedWorkSubStatusTypeId(subStatus.getId());
		dto.setScheduleFromString(DateUtilities.getISO8601(reschedule));

		authenticationService.setCurrentUser(employee);

		WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), dto);
		Assert.assertNotNull(negotiation.getWorkSubStatusTypeAssociation());
	}

	@Test
	public void testAddDefaultWorkSubStatusToCompany() throws Exception {
		User employee = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(employee.getId());
		WorkSubStatusTypeFilter filter = new WorkSubStatusTypeFilter();
		filter.setClientVisible(false);
		filter.setResourceVisible(true);
		filter.setShowDeactivated(false);
		filter.setShowSystemSubStatus(false);
		filter.setShowCustomSubStatus(true);
		List<WorkSubStatusType> sList = workSubStatusService.findAllSubStatuses(filter);
		for (WorkSubStatusType t : sList) {
			if (t.getCode().equals(WorkSubStatusType.GENERAL_PROBLEM)) {
				Assert.assertTrue(t.isCustom());
			}
		}
	}
}

