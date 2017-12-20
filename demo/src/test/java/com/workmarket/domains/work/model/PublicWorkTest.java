package com.workmarket.domains.work.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicWorkTest {
	private PublicWork publicWork;
	private AbstractWork work;

	@Before
	public void setUp() throws Exception {
		publicWork = new PublicWork();
		work = mock(AbstractWork.class);
	}

	@Test
	public void copy_withAbstractWork_getsWorkNumber() throws Exception {
		publicWork.copy(work);
		verify(work).getWorkNumber();
	}

	@Test
	public void copy_withAbstractWork_getsTitle() throws Exception {
		publicWork.copy(work);
		verify(work).getTitle();
	}

	@Test
	public void copy_withAbstractWork_truncatesTitle() throws Exception {
		when(work.getTitle()).thenReturn("123456789 123456789 123456789 123456789 123456789 123456789 123456789 YOU SHOULD NOT SEE ME");
		publicWork.copy(work);
		assertEquals(PublicWork.MAX_TITLE_LENGTH, publicWork.getTitle().length());
	}

	@Test
	public void copy_withAbstractWork_getsAddress() throws Exception {
		publicWork.copy(work);
		verify(work).getAddress();
	}

	@Test
	public void copy_withAbstractWork_getsDescription() throws Exception {
		publicWork.copy(work);
		verify(work, times(2)).getDescription();
	}

	@Test
	public void copy_withAbstractWork_stripsUnsafeTagsInDescription() throws Exception {
		when(work.getDescription()).thenReturn("<p>This is <script>blah</script>cool.</p>");
		publicWork.copy(work);
		assertEquals("<p>This is cool.</p>", publicWork.getDescription());
	}

	@Test
	public void copy_withAbstractWork_getsPricingStrategy() throws Exception {
		publicWork.copy(work);
		verify(work).getPricingStrategy();
	}

	@Test
	public void copy_withAbstractWork_getsScheduleRangeFlag() throws Exception {
		publicWork.copy(work);
		verify(work).getScheduleRangeFlag();
	}

	@Test
	public void copy_withAbstractWork_getsSchedule() throws Exception {
		publicWork.copy(work);
		verify(work).getSchedule();
	}

	@Test
	public void copy_withAbstractWork_getsTimeZone() throws Exception {
		publicWork.copy(work);
		verify(work).getTimeZone();
	}

	@Test
	public void copy_withAbstractWork_getsDesiredSkills() throws Exception {
		publicWork.copy(work);
		verify(work).getDesiredSkills();
	}

	@Test
	public void copy_withAbstractWork_getsWorkStatusType() throws Exception {
		publicWork.copy(work);
		verify(work).getWorkStatusType();
	}
}
