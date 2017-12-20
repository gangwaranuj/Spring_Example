package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.changelog.work.WorkChangeLog;
import com.workmarket.domains.model.changelog.work.WorkChangeLogPagination;
import com.workmarket.domains.model.changelog.work.WorkCreatedChangeLog;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.BrokenTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.RandomUtilities;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class WorkChangeLogIT extends BaseServiceIT {

	@Autowired private WorkService workService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private LaneService laneService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private WorkNoteService workNoteService;

	@Test
	@Transactional
	public void test_findAllChangeLogByWorkId() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(ONE, pagination.getRowCount());

		for (WorkChangeLog changeLog : pagination.getResults()) {
			assertNotNull(changeLog.getCreatedOn());
			assertNotNull(changeLog.getWorkId());
			assertNotNull(changeLog.getActorId());
		}
	}

	@Test
	@Transactional
	public void test_workCreated() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(1, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_workUpdated() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		work1 = workService.findWork(work1.getId());
		workService.updateWorkProperties(work1.getId(),
				CollectionUtilities.newStringMap("title", "new title " + RandomUtilities.nextLong()));

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(1, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_workQuestionAsked() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		workQuestionService.saveQuestion(work1.getId(),
				employee.getId(), "question" + RandomUtilities.nextLong());

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(3, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_workQuestionAnswered() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		WorkQuestionAnswerPair saveQuestion = workQuestionService.saveQuestion(work1.getId(),
				employee.getId(), "question" + RandomUtilities.nextLong());

		workQuestionService.saveAnswerToQuestion(saveQuestion.getId(), employee.getId(), "answer" + RandomUtilities.nextLong(), work1.getId());

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(5, pagination.getResults().size());
	}


	@Test
	@Transactional
	public void test_workResourceStatusChange() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		User contractor1 = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(contractor1.getId(), employee.getCompany().getId());

		Work work1 = newWork(employee.getId());

		workRoutingService.addToWorkResources(work1.getId(), contractor1.getId());

		workService.acceptWork(contractor1.getId(), work1.getId());

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(6, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_workNoteCreatedChangeLog() throws Exception {
		User employee = newFirstEmployeeWithCashBalance();

		Work work1 = newWork(employee.getId());

		NoteDTO dto = new NoteDTO();
		dto.setContent("Content " + RandomUtilities.generateAlphaString(10));
		workNoteService.addNoteToWork(work1.getId(), dto);

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		assertEquals(3, pagination.getResults().size());
	}

	@Test
	@Transactional
	public void test_WorkPropertyChangeLog_masquerade() throws Exception {
		User internal = newInternalUser();

		User employee = newFirstEmployeeWithCashBalance();

		authenticationService.startMasquerade(internal.getId(), employee.getId());

		Work work1 = newWork(employee.getId());

		workService.updateWorkProperties(work1.getId(), CollectionUtilities.newStringMap("title", RandomUtilities.generateAlphaString(10)));

		WorkChangeLogPagination pagination = new WorkChangeLogPagination();
		pagination.setReturnAllRows();

		pagination = workChangeLogService.findAllChangeLogsByWorkId(work1.getId(), pagination);

		WorkCreatedChangeLog changeLog = null;

		for (WorkChangeLog log : pagination.getResults()) {
			if (log instanceof WorkCreatedChangeLog) {
				changeLog = (WorkCreatedChangeLog) log;
				break;
			}
		}
		assertNotNull(changeLog);
		assertEquals(employee.getId(), changeLog.getActorId());
		assertEquals(internal.getId(), changeLog.getMasqueradeActorId());
	}

}

