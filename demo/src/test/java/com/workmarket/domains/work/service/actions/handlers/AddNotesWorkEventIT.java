package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.actions.AddNotesWorkEvent;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class AddNotesWorkEventIT extends BaseServiceIT {

	@Autowired AddNotesWorkEventHandler addNotesWorkEventHandler;
	@Autowired WorkNoteService workNoteService;
	@Autowired AuthenticationService authenticationService;


	AddNotesWorkEvent event;
	Work work1;
	Work work2;
	List<Work> works;
	User user;
	NotePagination pagination;
	AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
	List<String> workNumbers = Lists.newArrayList();

	@Before
	public void setup() throws Exception{
		user = newEmployeeWithCashBalance();
		work1 = newWork(user.getId());
		work2 = newWork(user.getId());
		works = Lists.newArrayList();
		works.add(work1);

		event = (AddNotesWorkEvent) new AddNotesWorkEvent.Builder(workNumbers,user,"test","add_note","hey",true)
				.work(works)
				.workEventHandler(addNotesWorkEventHandler)
				.response(response)
				.build();

		pagination = new NotePagination();
		pagination.setResultsLimit(1000);
		pagination.setStartRow(0);
		authenticationService.setCurrentUser(user);
	}

	@Test
	public void test_addNote_single(){
		addNotesWorkEventHandler.handleEvent(event);
		pagination = workNoteService.findAllNotesByWorkForCompany(work1.getId(),user.getCompany().getId(),pagination);
		Assert.assertNotNull(pagination.getResults());
		Assert.assertTrue(pagination.getResults().size() == 1);
	}

	@Test
	public void test_addNote_multiple(){
		works.add(work2);
		addNotesWorkEventHandler.handleEvent(event);

		pagination = workNoteService.findAllNotesByWorkForCompany(work1.getId(),user.getCompany().getId(),pagination);
		Assert.assertNotNull(pagination.getResults());
		Assert.assertTrue(pagination.getResults().size() == 1);

		pagination = workNoteService.findAllNotesByWorkForCompany(work2.getId(),user.getCompany().getId(),pagination);
		Assert.assertNotNull(pagination.getResults());
		Assert.assertTrue(pagination.getResults().size() == 1);
	}

	@Test
	public void test_addEmptyNote(){
		event = (AddNotesWorkEvent) new AddNotesWorkEvent.Builder(workNumbers,user,"test","add_note","",true)
				.work(works)
				.workEventHandler(addNotesWorkEventHandler)
				.response(response)
				.build();
		AjaxResponseBuilder response = addNotesWorkEventHandler.handleEvent(event);
		Assert.assertFalse(response.isSuccessful());
	}

	@Test(expected=Exception.class)
	public void test_addEmptyWorkList(){
		List<Work> tWorks = Lists.newArrayList();
		event = (AddNotesWorkEvent) new AddNotesWorkEvent.Builder(workNumbers,user,"test","add_note","hey",true)
				.work(tWorks)
				.workEventHandler(addNotesWorkEventHandler)
				.response(response)
				.build();
		AjaxResponseBuilder response = addNotesWorkEventHandler.handleEvent(event);
		Assert.assertFalse(response.isSuccessful());
	}

	@Test
	public void test_isPrivate_false(){
		event = (AddNotesWorkEvent) new AddNotesWorkEvent.Builder(workNumbers,user,"test","add_note","hey",false)
				.work(works)
				.workEventHandler(addNotesWorkEventHandler)
				.response(response)
				.build();
		AjaxResponseBuilder response = addNotesWorkEventHandler.handleEvent(event);
		Assert.assertTrue(response.isSuccessful());
		pagination = workNoteService.findAllNotesByWorkForCompany(work1.getId(),user.getCompany().getId(),pagination);
		Note note = pagination.getResults().get(0);
		Assert.assertFalse(note.getIsPrivate());
	}

	@Test
	public void test_isPrivate_true(){
		AjaxResponseBuilder response = addNotesWorkEventHandler.handleEvent(event);
		Assert.assertTrue(response.isSuccessful());
		pagination = workNoteService.findAllNotesByWorkForCompany(work1.getId(),user.getCompany().getId(),pagination);
		Note note = pagination.getResults().get(0);
		Assert.assertTrue(note.getIsPrivate());
	}

}
