package com.workmarket.velvetrope;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTagTest {

	DefaultTag tag;
	RopeTag ropeTag;
	JspFragment body;

	@Before
	public void setUp() throws Exception {
		tag = spy(new DefaultTag());

		ropeTag = mock(RopeTag.class);
		when(ropeTag.isRendered()).thenReturn(false);
		when(tag.getParent()).thenReturn(ropeTag);

		body = mock(JspFragment.class);
		tag.setJspBody(body);
	}

	@Test
	public void doTag_GetsTheTagsParentRopeTag() throws Exception {
		tag.doTag();
		verify(tag).getParent();
	}

	@Test
	public void doTag_WhenRopeTagHasNotBeenRendered_SetsItRendered() throws Exception {
		tag.doTag();
		verify(ropeTag).setRendered(true);
	}

	@Test
	public void doTag_WhenRopeTagHasNotBeenRendered_InvokesNullOnTheBody() throws Exception {
		tag.doTag();
		verify(body).invoke(null);
	}

	@Test
	public void doTag_WhenRopeTagHasBeenRendered_NeverSetsItRendered() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		tag.doTag();
		verify(ropeTag, never()).setRendered(true);
	}

	@Test
	public void doTag_WhenRopeTagHasNotBeenRendered_NeverInvokesNullOnTheBody() throws Exception {
		when(ropeTag.isRendered()).thenReturn(true);
		tag.doTag();
		verify(body, never()).invoke(null);
	}

	@Test(expected = JspTagException.class)
	public void doTag_WhenTheTagsParentIsNull_Throws() throws Exception {
		when(tag.getParent()).thenReturn(null);
		tag.doTag();
	}
}
