package com.workmarket.velvetrope;

import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.jsp.tagext.JspFragment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DefaultTagIT extends BaseTagIT {
	static final String CONTENT = "WHATEVS!!!!!";
	private DefaultTag tag;

	@Before
	public void setUp() throws Exception {
		JspFragment fragment = new MockJspFragment(CONTENT);
		RopeTag rope = new RopeTag();
		tag = new DefaultTag();
		tag.setParent(rope);
		tag.setJspBody(fragment);
		tag.setJspContext(mockPageContext);
	}

	@Test
	public void doTag() throws Exception {
		tag.doTag();
		String content = getContent();
		assertThat(content, is(CONTENT));
	}
}
