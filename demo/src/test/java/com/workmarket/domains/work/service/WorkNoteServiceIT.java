package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkNoteServiceIT extends BaseServiceIT {

	@Autowired
	WorkNoteService workNoteService;

	final String NOTE_TEXT = "My new note for work";
	final String NOTE_TEXT_2 = "Another note";

	Work work;
	User user;
	NoteDTO noteDTO;
	NotePagination pagination;

	@Before
	public void initWorkNoteTest() {
		try {
			user = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
			authenticationService.setCurrentUser(user);
			work = newWork(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		noteDTO = new NoteDTO();
		noteDTO.setContent(NOTE_TEXT);

		pagination = new NotePagination();
		pagination.setResultsLimit(25);
		pagination.setStartRow(0);
	}

	@Test
	public void addNoteToWork_WithValidNote_NoteSavedAndPublic() {

		Note note = workNoteService.addNoteToWork(work.getId(), noteDTO);

		assertNotNull(note);
		assertEquals(note.getContent(), NOTE_TEXT);
		assertFalse(note.getIsPrivate());
		assertEquals(user.getId(), note.getCreatorId());
	}

	@Test
	public void addNoteToWork_WithValidPrivateNote_NoteSavedAndPrivate() {
		noteDTO.setIsPrivate(true);

		Note note = workNoteService.addNoteToWork(work.getId(), noteDTO);

		assertTrue(note.getIsPrivate());
	}

	@Test
	public void addNoteToWork_OnBehalfOf_NoteSavedWithOnBehalfOfOwner() throws Exception {
		noteDTO.setIsPrivate(true);
		User newUser = newCompanyEmployee(work.getCompany().getId());

		Note note = workNoteService.addNoteToWork(work.getId(), noteDTO, newUser);

		assertTrue(note.getCreatorId().equals(user.getId()));
	}

	@Test
	public void addNoteToWork_WithMultipleValidNotes_CanFilterNotesOnPrivacy() {
		workNoteService.addNoteToWork(work.getId(), noteDTO);
		NoteDTO newNoteDTO = new NoteDTO();
		newNoteDTO.setContent(NOTE_TEXT_2);
		newNoteDTO.setIsPrivate(true);
		workNoteService.addNoteToWork(work.getId(), newNoteDTO);
		pagination.addFilter(NotePagination.FILTER_KEYS.PRIVATE, "true");

		pagination = workNoteService.findAllNotesByWorkForCompany(work.getId(), work.getCompany().getId(), pagination);

		assertNotNull(pagination);
		assertEquals(1, pagination.getResults().size());
	}


	@Test
	public void findAllNotesByWorkForCompany_WithSavedNotes_CanFindAllNotes() {
		workNoteService.addNoteToWork(work.getId(), noteDTO);
		NoteDTO newNoteDTO = new NoteDTO();
		newNoteDTO.setContent(NOTE_TEXT_2);
		workNoteService.addNoteToWork(work.getId(), newNoteDTO);

		pagination = workNoteService.findAllNotesByWorkForCompany(work.getId(), work.getCompany().getId(), pagination);

		assertNotNull(pagination);
		assertEquals(pagination.getResults().size(), 2);
		assertEquals(pagination.getResults().get(0).getContent(), NOTE_TEXT);
	}

	@Test
	public void findAllNotesByWorkForCompany_WithOutsideCompany_NoResults() {
		noteDTO.setIsPrivate(true);
		workNoteService.addNoteToWork(work.getId(), noteDTO);

		pagination = workNoteService.findAllNotesByWorkForCompany(work.getId(), work.getCompany().getId() + 1, pagination);

		assertTrue(pagination.getResults().isEmpty());
	}

	@Test
	public void findAllNotesByWorkForCompany_WithOutsideCompanyAndTwoNotes_OnlyPublicNotes() {
		noteDTO.setIsPrivate(true);
		workNoteService.addNoteToWork(work.getId(), noteDTO);
		NoteDTO newNoteDTO = new NoteDTO();
		newNoteDTO.setContent(NOTE_TEXT_2);
		workNoteService.addNoteToWork(work.getId(), newNoteDTO);

		pagination = workNoteService.findAllNotesByWorkForCompany(work.getId(), work.getCompany().getId() + 1, pagination);

		assertEquals(1, pagination.getResults().size());
	}
}
