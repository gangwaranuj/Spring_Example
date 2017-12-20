package com.workmarket.velvetrope;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.jsp.tagext.JspFragment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RopeTagTest {

	RopeTag tag;
	JspFragment body;

	@Before
	public void setUp() throws Exception {
		tag = new RopeTag();
		body = mock(JspFragment.class);
		tag.setJspBody(body);
	}

	@Test
	public void doTag_InvokesNullOnTheBody() throws Exception {
		tag.doTag();
		verify(body).invoke(null);
	}

	@Test
	public void isRendered_CanBeSetTrue() throws Exception {
		tag.setRendered(true);
		assertThat(tag.isRendered(), is(true));
	}

	@Test
	public void isRendered_IsFalseByDefault() throws Exception {
		assertThat(tag.isRendered(), is(false));
	}
}
