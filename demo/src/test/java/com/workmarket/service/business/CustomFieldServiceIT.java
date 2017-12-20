package com.workmarket.service.business;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkCustomFieldGroupDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 2013-11-16 3:07 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class CustomFieldServiceIT extends BaseServiceIT {


	@Test
	public void copyCustomFieldGroupByCompany_existingFieldSet_success() throws Exception {
		final String NEW_NAME = "New name";
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());
		WorkCustomFieldGroup newGroup = customFieldService.copyCustomFieldGroupByCompany(fieldGroup.getId(), NEW_NAME, user.getCompany().getId());

		assertEquals(fieldGroup.getWorkCustomFields().size(), newGroup.getWorkCustomFields().size());
		assertEquals(newGroup.getName(), NEW_NAME);
		assertFalse(fieldGroup.getId().equals(newGroup.getId()));

		for (int i = 0; i < fieldGroup.getWorkCustomFields().size(); i++) {
			WorkCustomField oldField = fieldGroup.getWorkCustomFields().get(i);
			WorkCustomField newField = newGroup.getWorkCustomFields().get(i);

			assertEquals(oldField.getName(), newField.getName());
			assertEquals(oldField.getDefaultValue(), newField.getDefaultValue());
			assertFalse(oldField.getId().equals(newField.getId()));
		}
	}

	@Test
	public void findClientFieldSetIdsMap_newCompany_returnsZero() throws Exception {
		User user = newFirstEmployee();
		Map<Long, String> map = customFieldService.findClientFieldSetIdsMap(user.getCompany().getId());
		assertTrue(map.isEmpty());
	}

	@Test
	public void findClientFieldSetIdsMap_newCompanyAndWorkerCustomFieldGroup_returnsZero() throws Exception {
		User user = newFirstEmployee();

		WorkCustomFieldGroupDTO dto = new WorkCustomFieldGroupDTO();
		dto.setName("Test CF Group");
		dto.setRequired(false);
		dto.setPosition(0);

		List<WorkCustomFieldDTO> customFieldDTOs = new LinkedList<>();

		WorkCustomFieldDTO ownerCustomFieldDTO = new WorkCustomFieldDTO();
		ownerCustomFieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.OWNER);
		ownerCustomFieldDTO.setName("Owner Field");
		ownerCustomFieldDTO.setPosition(0);
		customFieldDTOs.add(ownerCustomFieldDTO);

		WorkCustomFieldDTO resourceCustomFieldDTO = new WorkCustomFieldDTO();
		resourceCustomFieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.RESOURCE);
		resourceCustomFieldDTO.setName("Resource Field");
		resourceCustomFieldDTO.setPosition(1);
		customFieldDTOs.add(resourceCustomFieldDTO);

		dto.setWorkCustomFields(customFieldDTOs);

		customFieldService.saveOrUpdateWorkFieldGroup(user.getId(), dto);

		Map<Long, String> map = customFieldService.findClientFieldSetIdsMap(user.getCompany().getId());
		assertTrue(map.isEmpty());
	}

	@Test
	public void findClientFieldSetIdsMap_newCompanyAndClientOnlyCustomFieldGroup_returnsOne() throws Exception {
		User user = newFirstEmployee();

		WorkCustomFieldGroupDTO dto = new WorkCustomFieldGroupDTO();
		dto.setName("Test CF Group");
		dto.setRequired(false);
		dto.setPosition(0);

		List<WorkCustomFieldDTO> customFieldDTOs = new LinkedList<>();

		WorkCustomFieldDTO ownerCustomFieldDTO = new WorkCustomFieldDTO();
		ownerCustomFieldDTO.setWorkCustomFieldTypeCode(WorkCustomFieldType.OWNER);
		ownerCustomFieldDTO.setName("Owner Field");
		ownerCustomFieldDTO.setPosition(0);
		customFieldDTOs.add(ownerCustomFieldDTO);

		dto.setWorkCustomFields(customFieldDTOs);

		customFieldService.saveOrUpdateWorkFieldGroup(user.getId(), dto);

		Map<Long, String> map = customFieldService.findClientFieldSetIdsMap(user.getCompany().getId());
		assertTrue(map.size() == 1);
	}

	@Test
	public void deleteCustomFieldGroupByCompany_existingFieldSet_Success() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		customFieldService.deleteWorkCustomFieldGroupFromWork(work.getId(), fieldGroup.getId());

		Set<WorkCustomFieldGroupAssociation> map = customFieldService.findAllByWork(work.getId());
		assertTrue(map.isEmpty());
	}

	@Test
	public void addCustomFieldGroup_findWorkCustomFieldGroups_success() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		final WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		List<WorkCustomFieldGroup> dto = customFieldService.findWorkCustomFieldGroups(user.getCompany().getId());
		final WorkCustomFieldGroup foundDTO = Iterables.find(dto, new Predicate<WorkCustomFieldGroup>() {
			@Override
			public boolean apply(WorkCustomFieldGroup workCustomFieldGroup) {
				return workCustomFieldGroup.getId().equals(fieldGroup.getId());
			}
		});
		WorkCustomField foundWorkCustomFieldDTO = Iterables.find(foundDTO.getWorkCustomFields(), new Predicate<WorkCustomField>() {
			@Override
			public boolean apply(WorkCustomField workCustomField) {
				return workCustomField.getId().equals(foundDTO.getWorkCustomFields().get(0).getId());
			}
		});

		assertNotNull(foundWorkCustomFieldDTO);
	}

	@Test
	public void findAllFieldsForCustomFieldGroup_existingFieldGroup_success() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		List<WorkCustomField> customFieldGroups = customFieldService.findAllFieldsForCustomFieldGroup(fieldGroup.getId());
		Assert.assertNotNull(customFieldGroups);
	}

	@Test
	public void deactivateWorkCustomFieldGroupByCompany_existingFieldGroupAndCompany_success() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		customFieldService.deleteWorkCustomFieldGroupByCompany(fieldGroup.getId(), user.getId());
		Assert.assertTrue(!fieldGroup.getDeleted());
	}

	@Test
	public void removeWorkCustomFieldGroupForWork_existingFieldGroupAndWork_success() throws Exception {
		User user = userService.getUser(ANONYMOUS_USER_ID);
		Work work = newWork(user.getId());
		WorkCustomFieldGroup fieldGroup = addCustomFieldsToWork(work.getId());

		customFieldService.addWorkCustomFieldGroupToWork(fieldGroup.getId(), work.getId(), 0);

		Set<WorkCustomFieldGroupAssociation> savedFieldGroups = customFieldService.findAllByWork(work.getId());
		if (savedFieldGroups.isEmpty()) {
			customFieldService.removeWorkCustomFieldGroupForWork(fieldGroup.getId(), work.getId());
		}

		Set<WorkCustomFieldGroupAssociation> removedFieldGroups = customFieldService.findAllByWork(work.getId());
		Assert.assertTrue(removedFieldGroups.isEmpty());
	}

}