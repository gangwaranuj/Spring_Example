package com.workmarket.api.model.resolver;

import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.api.v1.AssignmentsCreateController;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CustomFieldGroupsArgumentResolverTest {
	private CustomFieldGroupsArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new CustomFieldGroupsArgumentResolver();
	}

	@Test
	public void evaluateArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("custom_field_groups[0][id]", "87");
		request.addParameter("custom_field_groups[0][fields][0][id]", "578");
		request.addParameter("custom_field_groups[0][fields][0][value]", "WM02240004");
		request.addParameter("custom_field_groups[0][fields][1][id]", "579");
		request.addParameter("custom_field_groups[0][fields][1][value]", "ITI193536");
		request.addParameter("custom_field_groups[0][fields][2][id]", "580");
		request.addParameter("custom_field_groups[0][fields][2][value]", "GV47LFHDTV10A");
		request.addParameter("custom_field_groups[0][fields][3][id]", "581");
		request.addParameter("custom_field_groups[0][fields][3][value]", "LHPAEAH1452757");
		request.addParameter("custom_field_groups[0][fields][4][id]", "582");
		request.addParameter("custom_field_groups[0][fields][4][value]", "M-F 0900-1800 4 Bus Day Resp");

		request.addParameter("custom_field_groups[1][id]", "92");
		request.addParameter("custom_field_groups[1][fields][0][id]", "1234");
		request.addParameter("custom_field_groups[1][fields][0][value]", "WM02241234");
		request.addParameter("custom_field_groups[1][fields][1][id]", "643");
		request.addParameter("custom_field_groups[1][fields][1][value]", "ITI193643");
		request.addParameter("custom_field_groups[1][fields][2][id]", "927");
		request.addParameter("custom_field_groups[1][fields][2][value]", "GV47LFHDTV927");
		request.addParameter("custom_field_groups[1][fields][3][id]", "812");
		request.addParameter("custom_field_groups[1][fields][3][value]", "LHPAEAH1452812");

		CustomFieldGroup[] customFieldGroups = resolver.evaluateArgument(request);
		assertNotNull(customFieldGroups);
		assertEquals(2, customFieldGroups.length);

		// assert first entry
		CustomFieldGroup customFieldGroup = customFieldGroups[0];
		assertEquals(customFieldGroup.getId(), 87L);
		assertFalse(customFieldGroup.getFields().isEmpty());
		assertEquals(customFieldGroup.getFields().get(0).getId(), 578L);
		assertEquals(customFieldGroup.getFields().get(0).getValue(), "WM02240004");
		assertEquals(customFieldGroup.getFields().get(1).getId(), 579L);
		assertEquals(customFieldGroup.getFields().get(1).getValue(), "ITI193536");
		assertEquals(customFieldGroup.getFields().get(2).getId(), 580L);
		assertEquals(customFieldGroup.getFields().get(2).getValue(), "GV47LFHDTV10A");
		assertEquals(customFieldGroup.getFields().get(3).getId(), 581L);
		assertEquals(customFieldGroup.getFields().get(3).getValue(), "LHPAEAH1452757");
		assertEquals(customFieldGroup.getFields().get(4).getId(), 582L);
		assertEquals(customFieldGroup.getFields().get(4).getValue(), "M-F 0900-1800 4 Bus Day Resp");
		assertEquals(customFieldGroup.getPosition(), Integer.valueOf(0));

		// assert second entry
		customFieldGroup = customFieldGroups[1];
		assertEquals(customFieldGroup.getId(), 92L);
		assertFalse(customFieldGroup.getFields().isEmpty());
		assertEquals(customFieldGroup.getFields().get(0).getId(), 1234L);
		assertEquals(customFieldGroup.getFields().get(0).getValue(), "WM02241234");
		assertEquals(customFieldGroup.getFields().get(1).getId(), 643L);
		assertEquals(customFieldGroup.getFields().get(1).getValue(), "ITI193643");
		assertEquals(customFieldGroup.getFields().get(2).getId(), 927L);
		assertEquals(customFieldGroup.getFields().get(2).getValue(), "GV47LFHDTV927");
		assertEquals(customFieldGroup.getFields().get(3).getId(), 812L);
		assertEquals(customFieldGroup.getFields().get(3).getValue(), "LHPAEAH1452812");
		assertEquals(customFieldGroup.getPosition(), Integer.valueOf(1));
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AssignmentsCreateController.class, "create");
		MethodParameter methodParameter = new MethodParameter(method, 51);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
