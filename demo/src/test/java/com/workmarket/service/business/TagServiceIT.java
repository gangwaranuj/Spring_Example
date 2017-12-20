package com.workmarket.service.business;

import com.workmarket.domains.model.tag.Tag;
import com.workmarket.domains.model.tag.TagPagination;
import com.workmarket.service.business.dto.TagDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class TagServiceIT extends BaseServiceIT {

	@Autowired private TagService tagService;

	@Test
	@Ignore
	public void test_findTagById() throws Exception {
		Tag tag = tagService.findTagById(TAG_ID);

		Assert.assertNotNull(tag);
		Assert.assertEquals("tag1", tag.getName());
		Assert.assertNotNull(tag.getCreatedOn());
	}

	@Test
	public void test_findAllTags() throws Exception {
		TagPagination tagPagination = new TagPagination();
		tagPagination.setStartRow(0);
		tagPagination.setResultsLimit(250);

		tagPagination = tagService.findAllTags(tagPagination);

		Assert.assertTrue(tagPagination.getRowCount() > 0);
	}

	@Test
	public void test_findTagByName() throws Exception {
		Tag tag = tagService.findTagByName("tag1");

		Assert.assertNotNull(tag);
		Assert.assertEquals("tag1", tag.getName());
		Assert.assertNotNull(tag.getCreatedOn());
	}

	@Test
	@Transactional
	public void test_createTag() throws Exception {
		TagDTO tagDTO = new TagDTO();
		String tagName = "tag" + RandomUtilities.nextLong();
		tagDTO.setName(tagName);
		Tag tag = tagService.createTag(tagDTO);

		Assert.assertNotNull(tag);
		Assert.assertEquals(tagName, tag.getName());
		Assert.assertNotNull(tag.getCreatedOn());
	}

	@Test
	@Transactional
	@Ignore
	public void test_deleteTagById() throws Exception {

		Tag tag = tagService.findTagById(TAG_ID);
		Assert.assertEquals(Boolean.FALSE, tag.getDeleted());

		tagService.deleteTagById(TAG_ID);

		tag = tagService.findTagById(TAG_ID);
		Assert.assertNotNull(tag);
		Assert.assertNotNull(tag);
		Assert.assertEquals("tag1", tag.getName());
		Assert.assertNotNull(tag.getCreatedOn());


	}
}

