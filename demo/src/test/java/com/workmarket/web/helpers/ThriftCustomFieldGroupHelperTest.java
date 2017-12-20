package com.workmarket.web.helpers;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThriftCustomFieldGroupHelperTest {
	Work work;
	WorkCustomFieldGroup requiredGroup;
	WorkCustomField field1;
	CustomFieldGroup customFieldGroup1, customFieldGroup2;
	WorkCustomFieldType type;

	@Before
	public void setup() {
		work = mock(Work.class);
		requiredGroup = mock(WorkCustomFieldGroup.class);
		customFieldGroup1 = mock(CustomFieldGroup.class);
		customFieldGroup2 = mock(CustomFieldGroup.class);
		field1 = mock(WorkCustomField.class);
		type = mock(WorkCustomFieldType.class);
		when(field1.getWorkCustomFieldType()).thenReturn(type);
		when(requiredGroup.getWorkCustomFields()).thenReturn(ImmutableList.of(field1));
		when(requiredGroup.getId()).thenReturn(1L);
		when(customFieldGroup1.getId()).thenReturn(1L);
		when(customFieldGroup2.getId()).thenReturn(2L);
	}

	@Test
	public void requiredGroupNotInEmptyWorkAddedToWork() {
		ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);

		verify(work).addToCustomFieldGroups((CustomFieldGroup) anyObject());
	}

	@Test
	public void requiredGroupNotInWorkAddedToWork() {
		List<CustomFieldGroup> customFieldGroups = Lists.newArrayList();
		customFieldGroups.add(customFieldGroup2);
		when(work.getCustomFieldGroups()).thenReturn(customFieldGroups);
		ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);

		verify(work).addToCustomFieldGroups((CustomFieldGroup)anyObject());
	}

	@Test
	public void requiredGroupInWorkNotAddedToWork() {
		List<CustomFieldGroup> customFieldGroups = Lists.newArrayList();
		customFieldGroups.add(customFieldGroup1);
		when(work.getCustomFieldGroups()).thenReturn(customFieldGroups);
		ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);

		verify(work, never()).addToCustomFieldGroups((CustomFieldGroup)anyObject());
	}

	@Test
	public void emptyRequiredGroupNotAddedToWork() {
		List<CustomFieldGroup> customFieldGroups = Lists.newArrayList();
		customFieldGroups.add(customFieldGroup1);
		when(work.getCustomFieldGroups()).thenReturn(customFieldGroups);
		when(field1.getDeleted()).thenReturn(true);
		ThriftCustomFieldGroupHelper.setRequiredThriftCustomFieldGroup(requiredGroup, work);

		verify(work, never()).addToCustomFieldGroups((CustomFieldGroup)anyObject());
	}
}
