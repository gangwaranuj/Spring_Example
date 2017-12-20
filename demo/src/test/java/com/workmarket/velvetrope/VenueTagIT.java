package com.workmarket.velvetrope;

import com.workmarket.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.jsp.tagext.TagAdapter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VenueTagIT extends BaseTagIT {
	static final String CONTENT = "WHATEVS!!!!!";

	@Autowired VenueTag tag;

	@Before
	public void setup() throws Exception {
		authenticateSimply();

		mockPageContext.getOut().write(CONTENT);
		RopeTag rope = new RopeTag();
		TagAdapter adapter = new TagAdapter(rope);
		tag.setName(Venue.LOBBY);
		tag.setParent(adapter);
		tag.setPageContext(mockPageContext);
	}

	@Test
	public void doTagStartInternal() throws Exception {
		tag.doStartTag();
		String content = getContent();
		assertThat(content, is(CONTENT));
	}
}
