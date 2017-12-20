package com.workmarket.velvetrope;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagAdapter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VenueTagTest {

	@Mock AuthenticatedGuestService authenticatedGuestService;
	@InjectMocks VenueTag tag = spy(new VenueTag());

	RopeTag ropeTag;
	TagAdapter tagAdapter;
	Guest guest;

	@Before
	public void setUp() throws Exception {
		ropeTag = mock(RopeTag.class);
		when(ropeTag.isRendered()).thenReturn(false);

		tagAdapter = mock(TagAdapter.class);
		when(tagAdapter.getAdaptee()).thenReturn(ropeTag);
		when(tag.getParent()).thenReturn(tagAdapter);

		guest = mock(Guest.class);
		when(guest.canEnter(Venue.LOBBY)).thenReturn(true);
		when(authenticatedGuestService.getGuest()).thenReturn(guest);

		tag.setName(Venue.LOBBY);
	}

	@Test
	public void doStartTagInternal_GetsTheTagsParentTagAdapter() throws Exception {
		tag.doStartTagInternal();
		verify(tag).getParent();
	}

	@Test
	public void doStartTagInternal_GetsTheTagAdaptersRopeTagAdapter() throws Exception {
		tag.doStartTagInternal();
		verify(tagAdapter).getAdaptee();
	}

	@Test(expected = JspTagException.class)
	public void doStartTagInternal_WhenThereIsNoRopeTag_Throws() throws Exception {
		when(tagAdapter.getAdaptee()).thenReturn(null);
		tag.doStartTagInternal();
	}

	@Test
	public void doStartTagInternal_WithUnrenderedRopeTag_ChecksWhetherRopeIsRendered() throws Exception {
		tag.doStartTagInternal();
		verify(ropeTag).isRendered();
	}

	@Test
	public void doStartTagInternal_WithUnrenderedRopeTag_SetsRopeTagRendered() throws Exception {
		tag.doStartTagInternal();
		verify(ropeTag).setRendered(true);
	}

	@Test
	public void doStartTagInternal_WithUnrenderedRopeTag_GetsAGuest() throws Exception {
		tag.doStartTagInternal();
		verify(authenticatedGuestService).getGuest();
	}

	@Test
	public void doStartTagInternal_WithUnrenderedRopeTag_ChecksIfGuestCanEnterAVenue() throws Exception {
		tag.doStartTagInternal();
		verify(guest).canEnter(Venue.LOBBY);
	}

	@Test
	public void doStartTagInternal_WithUnrenderedRopeTag_returnsEVAL_BODY_INCLUDE() throws Exception {
		int action = tag.doStartTagInternal();
		assertThat(action, is(VenueTag.EVAL_BODY_INCLUDE));
	}

	@Test
	public void doStartTagInternal_WithRenderedRopeTag_NeverSetsRopeTagRendered() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		tag.doStartTagInternal();
		verify(ropeTag, never()).setRendered(anyBoolean());
	}

	@Test
	public void doStartTagInternal_WithRenderedRopeTag_NeverGetsAGuest() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		tag.doStartTagInternal();
		verify(authenticatedGuestService, never()).getGuest();
	}

	@Test
	public void doStartTagInternal_WithRenderedRopeTag_NeverChecksIfGuestCanEnterAVenue() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		tag.doStartTagInternal();
		verify(guest, never()).canEnter(any(Venue.class));
	}

	@Test
	public void doStartTagInternal_WithRenderedRopeTag_returnsSKIP_BODY() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		int action = tag.doStartTagInternal();
		assertThat(action, is(VenueTag.SKIP_BODY));
	}

	@Test
	public void doStartTagInternal_WhenNoGuestCanBeFound_NeverSetsRopeTagRendered() throws Exception {
		when(authenticatedGuestService.getGuest()).thenReturn(null);
		tag.doStartTagInternal();
		verify(ropeTag, never()).setRendered(anyBoolean());
	}

	@Test
	public void doStartTagInternal_WhenNoGuestCanBeFound_NeverChecksIfGuestCanEnterAVenue() throws Exception {
		when(authenticatedGuestService.getGuest()).thenReturn(null);
		tag.doStartTagInternal();
		verify(guest, never()).canEnter(any(Venue.class));
	}

	@Test
	public void doStartTagInternal_WhenNoGuestCanBeFound_returnsSKIP_BODY() throws Exception {
		when(authenticatedGuestService.getGuest()).thenReturn(null);
		int action = tag.doStartTagInternal();
		assertThat(action, is(VenueTag.SKIP_BODY));
	}

	@Test
	public void doStartTagInternal_WhenBypassed_NeverSetsRopeTagRendered() throws Exception {
		tag.setBypass(true);
		tag.doStartTagInternal();
		verify(ropeTag, never()).setRendered(anyBoolean());
	}

	@Test
	public void doStartTagInternal_WhenBypassed_GetsAGuest() throws Exception {
		tag.setBypass(true);
		tag.doStartTagInternal();
		verify(authenticatedGuestService).getGuest();
	}

	@Test
	public void doStartTagInternal_WhenBypassed_ChecksIfGuestCanEnterAVenue() throws Exception {
		tag.setBypass(true);
		tag.doStartTagInternal();
		verify(guest).canEnter(Venue.LOBBY);
	}

	@Test
	public void doStartTagInternal_WhenBypassed_returnsSKIP_BODY() throws Exception {
		tag.setBypass(true);
		int action = tag.doStartTagInternal();
		assertThat(action, is(VenueTag.SKIP_BODY));
	}

	@Test
	public void doStartTagInternal_WithWrongVenue_NeverSetsRopeTagRendered() throws Exception {
		tag.setName(Venue.ENTERPRISE);
		tag.doStartTagInternal();
		verify(ropeTag, never()).setRendered(anyBoolean());
	}

	@Test
	public void doStartTagInternal_WithWrongVenue_GetsAGuest() throws Exception {
		tag.setName(Venue.ENTERPRISE);
		tag.doStartTagInternal();
		verify(authenticatedGuestService).getGuest();
	}

	@Test
	public void doStartTagInternal_WithWrongVenue_ChecksIfGuestCanEnterAVenue() throws Exception {
		tag.setName(Venue.ENTERPRISE);
		tag.doStartTagInternal();
		verify(guest).canEnter(Venue.ENTERPRISE);
	}

	@Test
	public void doStartTagInternal_WithWrongVenue_returnsSKIP_BODY() throws Exception {
		tag.setName(Venue.ENTERPRISE);
		int action = tag.doStartTagInternal();
		assertThat(action, is(VenueTag.SKIP_BODY));
	}
}
