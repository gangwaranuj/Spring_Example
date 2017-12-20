package com.workmarket.service.business;

import com.workmarket.configuration.Constants;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupAssociationDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;


@RunWith(MockitoJUnitRunner.class)
public class CustomFieldServiceTest {

	private WorkCustomFieldGroupAssociation workCustomFieldGroupAssociation;

	@Mock WorkCustomFieldDAO workCustomFieldDAO;
	@Mock WorkCustomFieldGroupDAO workCustomFieldGroupDAO;
	@Mock AuthenticationService authenticationService;
	@Mock WorkCustomFieldGroup fieldGroup = new WorkCustomFieldGroup();
	@Mock Company company = new Company();
	@Mock User user = new User();
	@Mock WorkCustomFieldGroupAssociationDAO workCustomFieldGroupAssociationDAO;
	@Mock EventRouter eventRouter;
	@Mock WorkDAO workDAO;
	@Mock WorkCustomFieldGroupAssociation association = new WorkCustomFieldGroupAssociation();

	@InjectMocks CustomFieldServiceImpl customFieldService;

	List<WorkCustomFieldGroup> fieldGroups = new ArrayList<>();
	List<WorkCustomField> customFields = new ArrayList<>();
	WorkCustomField customField = new WorkCustomField();
	WorkCustomFieldDTO DTO = new WorkCustomFieldDTO();
	WorkCustomFieldDTO[] DTOs = new WorkCustomFieldDTO[] {DTO};

	private final static Long COMPANY_ID = 1000L, FIELD_GROUP_ID = 1L, WORK_ID = 1L;

	@Before
	public void setup() {

		customField.setId(1L);
		customField.setName("Test Custom Field");
		customField.setDefaultValue("Some Value");
		customField.setWorkCustomFieldGroup(fieldGroup);
		DTO.setId(1L);

		when(workCustomFieldDAO.get(anyLong())).thenReturn(customField);

		when(workCustomFieldDAO.findAllFieldsForCustomFieldGroup(FIELD_GROUP_ID)).thenReturn(customFields);
		when(workCustomFieldGroupDAO.findWorkCustomFieldGroups(anyLong())).thenReturn(fieldGroups);
		when(workCustomFieldGroupDAO.get(FIELD_GROUP_ID)).thenReturn(fieldGroup);
		when(fieldGroup.getCompany()).thenReturn(company);
		when(company.getId()).thenReturn(COMPANY_ID);
		when(fieldGroup.getWorkCustomFields()).thenReturn(customFields);

		when(user.getId()).thenReturn(Constants.WORKMARKET_SYSTEM_USER_ID);
		when(authenticationService.getCurrentUser()).thenReturn(user);

		workCustomFieldGroupAssociation = mock(WorkCustomFieldGroupAssociation.class);
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(anyLong(), anyLong())).thenReturn(workCustomFieldGroupAssociation);
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(null, null)).thenReturn(null);
	}

	@Test
	public void testFindWorkCustomFieldGroups_success() {
		customFieldService.findWorkCustomFieldGroups(COMPANY_ID);
		verify(workCustomFieldGroupDAO).findWorkCustomFieldGroups(COMPANY_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindWorkCustomFieldGroups_withNull_throwsException() {
			customFieldService.findWorkCustomFieldGroups(null);
	}

	@Test
	public void findWorkCustomFieldGroup_success() {
		WorkCustomFieldGroup workCustomFieldGroup = customFieldService.findWorkCustomFieldGroup(FIELD_GROUP_ID);
		verify(workCustomFieldGroupDAO).get(FIELD_GROUP_ID);
		assertEquals(fieldGroup, workCustomFieldGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindWorkCustomFieldGroup_withNull_throwsException() {
		customFieldService.findWorkCustomFieldGroup(null);
	}

	@Test
	public void testFindWorkCustomFieldGroupByCompany_success() {
		WorkCustomFieldGroup workCustomFieldGroup = customFieldService.findWorkCustomFieldGroupByCompany(FIELD_GROUP_ID, COMPANY_ID);
		verify(workCustomFieldGroupDAO).get(FIELD_GROUP_ID);
		assertEquals(fieldGroup, workCustomFieldGroup);
	}

	@Test
	public void testFindWorkCustomFieldGroupByCompany_withNull_returns_null() {
		WorkCustomFieldGroup group = customFieldService.findWorkCustomFieldGroupByCompany(null, COMPANY_ID);
		assertNull(group);
	}

	@Test
	public void testFindAllFieldsForCustomFieldGroup_withFieldGroupId_success() {
		customFieldService.findAllFieldsForCustomFieldGroup(FIELD_GROUP_ID);
		verify(workCustomFieldDAO, times(1)).findAllFieldsForCustomFieldGroup(FIELD_GROUP_ID);
	}

	@Test
	public void testFindAllByWork_success() {
		customFieldService.findAllByWork(WORK_ID);
		verify(workCustomFieldGroupAssociationDAO, times(1)).findAllByWork(WORK_ID);
	}

	@Test
	public void testAddWorkCustomFieldGroupToWork_success() {
		customFieldService.addWorkCustomFieldGroupToWork(FIELD_GROUP_ID, WORK_ID, 1);
		verify(workCustomFieldGroupAssociationDAO, times(1)).findByWorkAndWorkCustomFieldGroup(WORK_ID, FIELD_GROUP_ID);
	}

	@Test
	public void testRemoveWorkCustomFieldGroupForWork_success() {
		customFieldService.removeWorkCustomFieldGroupForWork(FIELD_GROUP_ID, WORK_ID);
		verify(workCustomFieldGroupAssociationDAO, times(1)).findByWorkAndWorkCustomFieldGroup(WORK_ID, FIELD_GROUP_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveWorkCustomFieldGroupForWork_withNullFieldGroup_throwsException() {
		customFieldService.removeWorkCustomFieldGroupForWork(null, WORK_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveWorkCustomFieldGroupForWork_withNulls_throwsException() {
		customFieldService.removeWorkCustomFieldGroupForWork(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveWorkCustomFieldGroupForWork_withNull_throwsException() {
		customFieldService.removeWorkCustomFieldGroupForWork(FIELD_GROUP_ID, null);
	}

	@Test
	public void testSaveOrUpdateWorkCustomField_withCustomField_success() {
		customFieldService.saveOrUpdateWorkCustomField(customField);
		verify(workCustomFieldDAO, times(1)).saveOrUpdate(customField);
	}

	@Test
	public void testSaveOrUpdateWorkCustomField_withNullCustomField_success() {
		customFieldService.saveOrUpdateWorkCustomField(null);
		verify(workCustomFieldDAO, times(1)).saveOrUpdate(null);
	}

	@Test
	public void testCopyCustomFieldGroupByCompany_withData_success()  {
		customFieldService.copyCustomFieldGroupByCompany(FIELD_GROUP_ID, "Test Copy", COMPANY_ID);
		verify(workCustomFieldGroupDAO, times(1)).saveOrUpdate(Matchers.<WorkCustomFieldGroup>anyObject());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCopyCustomFieldGroupByCompany_withNull_throwsException()  {
		customFieldService.copyCustomFieldGroupByCompany(FIELD_GROUP_ID, null, COMPANY_ID);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_callsDAO_success() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, 456L);
		verify(workCustomFieldGroupAssociationDAO, times(1)).findByWorkAndWorkCustomFieldGroup(123L, 456L);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_found_success() throws Exception {
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(123L, 456L)).thenReturn(workCustomFieldGroupAssociation);
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, 456L);
		verify(workCustomFieldGroupAssociation, times(1)).setDeleted(true);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_notFound_doesNotDelete() throws Exception {
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(123L, 456L)).thenReturn(null);
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, 456L);
		verify(workCustomFieldGroupAssociation, never()).setDeleted(true);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_nullWork_doesNotDelete() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(null, 456L);
		verify(workCustomFieldGroupAssociation, never()).setDeleted(true);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_nullFieldGroup_doesNotDelete() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, null);
		verify(workCustomFieldGroupAssociation, never()).setDeleted(true);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_bothNull_doesNotDelete() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(null, null);
		verify(workCustomFieldGroupAssociation, never()).setDeleted(true);
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_found_sendsEvent() throws Exception {
		ArgumentCaptor<WorkUpdateSearchIndexEvent> argument = ArgumentCaptor.forClass(WorkUpdateSearchIndexEvent.class);
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(123L, 456L)).thenReturn(workCustomFieldGroupAssociation);
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, 456L);
		verify(eventRouter, times(1)).sendEvent(argument.capture());
		assertThat(argument.getValue().getWorkIds(), hasItem(123L));
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_notFound_doesNotSendEvent() throws Exception {
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroup(123L, 456L)).thenReturn(null);
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, 456L);
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_nullWork_doesNotSendEvent() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(null, 456L);
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_nullFieldGroup_doesNotSendEvent() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(123L, null);
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void testDeleteWorkCustomFieldGroupFromWork_bothNull_doesNotSendEvent() throws Exception {
		customFieldService.deleteWorkCustomFieldGroupFromWork(null, null);
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void testFindWorkCustomFieldGroup_AbleToReturnWithValidData() throws Exception{
		when(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroupPosition(123L, 123)).thenReturn(workCustomFieldGroupAssociation);
		customFieldService.findWorkCustomFieldGroup(123L,123L,123);
	}

	@Test
	public void testCustomFieldGroupLookupWithEmptyParams_doesNotSendEvent() throws Exception {
		Assert.isNull(workCustomFieldGroupAssociationDAO.findByWorkAndWorkCustomFieldGroupPosition(null, null));
	}

	@Test
	public void test_saveWorkCustomFieldsForWork_doesntindexe() {
		customFieldService.saveWorkCustomFieldsForWork(DTOs, WORK_ID, user, null);
		verify(eventRouter, never()).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}

	@Test
	public void test_saveWorkCustomFieldsForWorkAndIndex_indexes() {
		customFieldService.saveWorkCustomFieldsForWorkAndIndex(DTOs, WORK_ID);
		verify(eventRouter).sendEvent(any(WorkUpdateSearchIndexEvent.class));
	}
}