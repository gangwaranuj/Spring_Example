package com.workmarket.velvetrope;

import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.jsp.tagext.JspFragment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RopeTagIT extends BaseTagIT {
	static final String CONTENT = "WHATEVS!!!!!";
	RopeTag ropeTag;

	@Before
	public void setUp() throws Exception {
		ropeTag = new RopeTag();
		JspFragment fragment = new MockJspFragment(CONTENT);
		ropeTag.setJspBody(fragment);
		ropeTag.setJspContext(mockPageContext);
	}

	@Test
	public void doTag() throws Exception {
		ropeTag.doTag();
		String content = getContent();
		assertThat(content, is(CONTENT));
	}
}
