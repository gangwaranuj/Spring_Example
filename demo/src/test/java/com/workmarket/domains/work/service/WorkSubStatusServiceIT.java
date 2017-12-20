package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.helpers.WMCallable;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class WorkSubStatusServiceIT extends BaseServiceIT {

	@Resource private EventFactory eventFactory;
	@Resource private EventRouter eventRouter;

	@Test
	public void test_CreateSubStatusType() {
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);
	}

	@Test
	public void test_CreateRemoveCreateRemoveSubStatusType() {
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);

		workSubStatusService.deleteWorkSubStatus(workSubStatusType.getId());

		workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);

		workSubStatusService.deleteWorkSubStatus(workSubStatusType.getId());

		workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);
	}

	@Test
	public void test_EditSubStatusType() {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);
		Assert.assertNotNull(workSubStatusType);

		workSubStatusTypeDTO.setWorkSubStatusTypeId(workSubStatusType.getId());
		workSubStatusTypeDTO.setRemoveOnVoidOrCancelled(true);
		workSubStatusTypeDTO.setNoteRequired(false);
		workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);
		Assert.assertTrue(workSubStatusType.isRemoveOnVoidOrCancelled());
		assertFalse(workSubStatusType.isNoteRequired());

		workSubStatusTypeDTO.setRemoveOnVoidOrCancelled(false);
		workSubStatusTypeDTO.setNoteRequired(true);
		workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);
		assertFalse(workSubStatusType.isRemoveOnVoidOrCancelled());
		Assert.assertTrue(workSubStatusType.isNoteRequired());
	}

	@Test
	public void test_AddSubStatusType() throws Exception {
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);

		User employee = newWMEmployee();
		Work work = newWork(employee.getId());
		Assert.assertNotNull(work);

		workSubStatusService.addSubStatus(work.getId(), workSubStatusType.getId(), "");
		Assert.assertTrue(workSubStatusService.findAllUnResolvedSubStatuses(work.getId()).contains(workSubStatusType));
	}

	@Test
	public void test_ResolveSubStatusType() throws Exception {
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(newWorkSubStatusTypeDTO());
		Assert.assertNotNull(workSubStatusType);

		User employee = newWMEmployee();
		Work work = newWork(employee.getId());
		Assert.assertNotNull(work);

		workSubStatusService.addSubStatus(work.getId(), workSubStatusType.getId(), "new " + RandomUtilities.generateNumericString(5));
		Assert.assertTrue(workSubStatusService.findAllUnResolvedSubStatuses(work.getId()).contains(workSubStatusType));

		workSubStatusService.resolveSubStatus(authenticationService.getCurrentUser().getId(), work.getId(), workSubStatusType.getId(), "new " + RandomUtilities.generateNumericString(5));

		assertFalse(workSubStatusService.findAllUnResolvedSubStatuses(work.getId()).contains(workSubStatusType));
	}

	@Test
	public void test_EditSubStatusTypeThenValidateSubStatusType() throws Exception {
		String[] workStatusTypes = { WorkStatusType.CANCELLED };

		WorkSubStatusTypeDTO subStatusTypeDTO = newWorkSubStatusTypeDTO();
		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(subStatusTypeDTO);
		Assert.assertNotNull(workSubStatusType);

		User employee = newWMEmployee();
		Work work = newWork(employee.getId());
		Assert.assertNotNull(work);

		workSubStatusService.addSubStatus(work.getId(), workSubStatusType.getId(), "new " + RandomUtilities.generateNumericString(5));
		Assert.assertTrue(workSubStatusService.findAllUnResolvedSubStatuses(work.getId()).contains(workSubStatusType));

		subStatusTypeDTO.setWorkSubStatusTypeId(workSubStatusType.getId());
		subStatusTypeDTO.setWorkStatusCodes(workStatusTypes);
		workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(subStatusTypeDTO);
		eventRouter.sendEvent(eventFactory.buildWorkSubStatusTypeUpdatedEvent(employee.getId(), workSubStatusType.getId()));

		await().atMost(JMS_DELAY, MILLISECONDS).until(workStatusIsResolved(work.getId(), workSubStatusType));
		assertFalse(workSubStatusService.findAllUnResolvedSubStatuses(work.getId()).contains(workSubStatusType));
	}

	@Test
	public void saveOrUpdateCustomWorkSubStatus_labelContainsRecipientIds_idsAddedToDaoInWorkSubStatusService() throws Exception {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();
		User employee1 = newWMEmployee();
		User employee2 = newWMEmployee();
		User employee3 = newWMEmployee();

		String[] recipientUserNumbers = {employee1.getUserNumber(), employee2.getUserNumber(), employee3.getUserNumber()};
		Long[] recipientIds ={employee1.getId(), employee2.getId(), employee3.getId()};

		workSubStatusTypeDTO.setWorkSubStatusTypeRecipientIds(recipientUserNumbers);

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);

		Assert.assertNotNull(workSubStatusType);

		for (Long id : recipientIds) {
			Assert.assertTrue(
				workSubStatusService.findAllRecipientsByWorkSubStatusId(workSubStatusType.getId()).contains(id));
		}
	}

	@Test
	public void findAllRecipientsByWorkSubStatusCodeAndCompany_passWorkSubStatusCodeAndCompanyId_idsAddedToDaoInWorkSubStatusService() throws Exception {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();
		User employee1 = newWMEmployee();
		User employee2 = newWMEmployee();
		User employee3 = newWMEmployee();

		String[] recipientUserNumbers = {employee1.getUserNumber(), employee2.getUserNumber(), employee3.getUserNumber()};
		Long[] recipientIds ={employee1.getId(), employee2.getId(), employee3.getId()};

		workSubStatusTypeDTO.setWorkSubStatusTypeRecipientIds(recipientUserNumbers);

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);

		Assert.assertNotNull(workSubStatusType);

		for (Long id : recipientIds) {
			Assert.assertTrue(
				workSubStatusService.findAllRecipientsByWorkSubStatusCodeAndCompany(workSubStatusType.getCode(), workSubStatusType.getCompany().getId()).contains(id));
		}
	}

	@Test
	public void deleteWorkSubStatusTypeRecipientAssociation_passRecipientIdAndWorkSubStatusTypeId_recipientIsDeleted() throws Exception {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();
		User employee1 = newWMEmployee();
		User employee2 = newWMEmployee();
		User employee3 = newWMEmployee();

		String[] recipientUserNumbers = {employee1.getUserNumber(), employee2.getUserNumber(), employee3.getUserNumber()};
		Long[] recipientIds ={employee1.getId(), employee2.getId(), employee3.getId()};

		workSubStatusTypeDTO.setWorkSubStatusTypeRecipientIds(recipientUserNumbers);

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);

		Assert.assertNotNull(workSubStatusType);

		workSubStatusService.deleteWorkSubStatusTypeRecipientAssociation(recipientIds[0], workSubStatusType.getId());

		Assert.assertFalse(workSubStatusService.findAllRecipientsByWorkSubStatusId(workSubStatusType.getId()).contains(recipientIds[0]));
	}

	@Test
	public void deleteAllWorkSubStatusTypeRecipientAssociationsByWorkSubStatusId_passWorkSubStatusId_allRecipientsDeleted() throws Exception {
		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();
		User employee1 = newWMEmployee();
		User employee2 = newWMEmployee();
		User employee3 = newWMEmployee();

		String[] recipientUserNumbers = {employee1.getUserNumber(), employee2.getUserNumber(), employee3.getUserNumber()};
		Long[] recipientIds ={employee1.getId(), employee2.getId(), employee3.getId()};

		workSubStatusTypeDTO.setWorkSubStatusTypeRecipientIds(recipientUserNumbers);

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);

		Assert.assertNotNull(workSubStatusType);

		workSubStatusService.deleteAllWorkSubStatusTypeRecipientAssociationsByWorkSubStatusId(workSubStatusType.getId());

		for (Long id : recipientIds) {
			Assert.assertFalse(
				workSubStatusService.findAllRecipientsByWorkSubStatusId(workSubStatusType.getId()).contains(id));
		}
	}

	@Test
	public void findWorkUploadWorkSubStatuses() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);

		WorkSubStatusTypeDTO workSubStatusTypeDTO = newWorkSubStatusTypeDTO();

		WorkSubStatusType workSubStatusType = workSubStatusService.saveOrUpdateCustomWorkSubStatus(workSubStatusTypeDTO);

		List<WorkSubStatusType> workSubStatusTypes = workSubStatusService.findAllWorkUploadSubStatuses();

		assertEquals(1, workSubStatusTypes.size());
	}

	private Callable<Boolean> workStatusIsResolved(final Long workId, final WorkSubStatusType workSubStatusType) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				return !workSubStatusService.findAllUnResolvedSubStatuses(workId).contains(workSubStatusType);
			}
		};
	}
}