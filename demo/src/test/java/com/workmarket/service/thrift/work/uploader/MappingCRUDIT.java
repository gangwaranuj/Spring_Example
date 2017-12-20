package com.workmarket.service.thrift.work.uploader;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.test.IntegrationTest;
import com.workmarket.service.business.upload.parser.ParseUtils;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.thrift.work.uploader.DeleteMappingRequest;
import com.workmarket.thrift.work.uploader.FieldCategory;
import com.workmarket.thrift.work.uploader.FieldMapping;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.thrift.work.uploader.FindMappingsRequest;
import com.workmarket.thrift.work.uploader.FindMappingsResponse;
import com.workmarket.thrift.work.uploader.RenameMappingRequest;
import com.workmarket.thrift.work.uploader.SaveMappingRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class MappingCRUDIT extends BaseServiceIT {

	private User user;

	@Autowired private UserService userService;
	@Autowired private TWorkUploadService workUploader;

	@Before
	public void before() {
		user = userService.findUserById(ANONYMOUS_USER_ID);
	}

	@Test
	public void testGetFieldCategories() throws Exception {
		List<FieldCategory> categories = workUploader.getFieldCategories();

		Assert.assertFalse(categories.isEmpty());
	}

	@Test
	@Ignore
	public void testFindMappings() throws Exception {
		FindMappingsRequest request = new FindMappingsRequest()
			.setCompanyId(user.getCompany().getId())
			.setStartRow(0)
			.setResultsLimit(100);
		FindMappingsResponse response = workUploader.findMappings(request);
		List<FieldMappingGroup> mappingGroups = response.getMappingGroups();

		Assert.assertFalse(mappingGroups.isEmpty());
	}

	@Test
	@Ignore
	public void testCreateMapping() throws Exception {

		FieldMappingGroup mappingGroup = new FieldMappingGroup();
		mappingGroup.setName("Test Mapping");
		mappingGroup.addToMappings(
			new FieldMapping()
				.setColumnIndex(0)
				.setColumnName("title")
				.setType(new FieldType().setCode(WorkUploadColumn.TITLE.getUploadColumnName()))
		);
		mappingGroup.addToMappings(
			new FieldMapping()
				.setColumnIndex(1)
				.setColumnName("description")
				.setType(new FieldType().setCode(WorkUploadColumn.DESCRIPTION.getUploadColumnName()))
		);
		mappingGroup.addToMappings(
			new FieldMapping()
				.setColumnIndex(2)
				.setColumnName("instructions")
				.setType(new FieldType().setCode(WorkUploadColumn.INSTRUCTIONS.getUploadColumnName()))
		);

		SaveMappingRequest saveRequest = new SaveMappingRequest();
		saveRequest.setUserNumber(user.getUserNumber());
		saveRequest.setMappingGroup(mappingGroup);

		FieldMappingGroup savedMappingGroup = workUploader.saveMapping(saveRequest);

		Assert.assertTrue(savedMappingGroup.isSetName());
		Assert.assertEquals(mappingGroup.getName(), savedMappingGroup.getName());
		Assert.assertEquals(mappingGroup.getMappingsSize(), savedMappingGroup.getMappingsSize());
	}

	@Test
	@Ignore
	public void testRenameMapping() throws Exception {
		FindMappingsRequest request = new FindMappingsRequest()
			.setCompanyId(user.getCompany().getId())
			.setStartRow(0)
			.setResultsLimit(100);
		FindMappingsResponse response = workUploader.findMappings(request);
		List<FieldMappingGroup> mappingGroups = response.getMappingGroups();

		String newName = String.format("%s (renamed)", mappingGroups.get(0).getName());

		RenameMappingRequest renameRequest = new RenameMappingRequest();
		renameRequest.setUserNumber(user.getUserNumber());
		renameRequest.setMappingGroupId(mappingGroups.get(0).getId());
		renameRequest.setName(newName);

		workUploader.renameMapping(renameRequest);

		response = workUploader.findMappings(request);
		mappingGroups = response.getMappingGroups();

		Assert.assertEquals(newName, mappingGroups.get(0).getName());
	}

	@Test
	@Ignore
	public void testDeleteMapping() throws Exception {
		FindMappingsRequest request = new FindMappingsRequest()
			.setCompanyId(user.getCompany().getId())
			.setStartRow(0)
			.setResultsLimit(100);
		FindMappingsResponse response = workUploader.findMappings(request);
		List<FieldMappingGroup> mappingGroups = response.getMappingGroups();

		int count = mappingGroups.size();

		DeleteMappingRequest deleteRequest = new DeleteMappingRequest();
		deleteRequest.setUserNumber(user.getUserNumber());
		deleteRequest.setMappingGroupId(mappingGroups.get(0).getId());

		workUploader.deleteMapping(deleteRequest);

		response = workUploader.findMappings(request);
		mappingGroups = response.getMappingGroups();

		Assert.assertEquals(count - 1, mappingGroups.size());
	}

	@Test
	public void testVerifyMappings() throws Exception {
		FieldMappingGroup mappingGroup = new FieldMappingGroup();
		mappingGroup.addToMappings(
			new FieldMapping()
				.setColumnIndex(0)
				.setColumnName("title")
				.setType(WorkUploadColumn.TITLE.createFieldType())
		);
		mappingGroup.addToMappings(
			new FieldMapping()
				.setColumnIndex(1)
				.setColumnName("description")
				.setType(WorkUploadColumn.DESCRIPTION.createFieldType())
		);

		// All good

		List<String[]> rows = Lists.<String[]>newArrayList(
			new String[] {"title", "description"}
		);

		Assert.assertEquals(0, ParseUtils.verifyMapping(mappingGroup, rows, true).size());

		// Mismatched headers

		rows = Lists.<String[]>newArrayList(
			new String[] {"name", "information"}
		);

		Assert.assertEquals(1, ParseUtils.verifyMapping(mappingGroup, rows, true).size());

		// Mismatched headers and too much data

		rows = Lists.<String[]>newArrayList(
			new String[] {"name", "description", "instructions"}
		);

		Assert.assertEquals(2, ParseUtils.verifyMapping(mappingGroup, rows, true).size());

		// Too little data

		rows = Lists.<String[]>newArrayList(
			new String[] {"Printer Repair"}
		);

		Assert.assertEquals(1, ParseUtils.verifyMapping(mappingGroup, rows, false).size());

		// Too much data

		rows = Lists.<String[]>newArrayList(
			new String[] {"Printer Repair", "Repair some printers.", "Bring tools. And stuff."}
		);

		Assert.assertEquals(1, ParseUtils.verifyMapping(mappingGroup, rows, false).size());
	}
}
