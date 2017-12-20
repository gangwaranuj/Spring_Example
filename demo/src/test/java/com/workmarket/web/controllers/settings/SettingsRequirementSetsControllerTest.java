package com.workmarket.web.controllers.settings;

import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.requirementsets.RequirementSetsSerializationService;
import com.workmarket.service.business.requirementsets.RequirementSetsService;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsRequirementSetsControllerTest extends BaseControllerUnitTest {

	@Mock private RequirementSetsService requirementSetsService;
	@Mock private RequirementSetsSerializationService jsonService;
	@Mock private UserService userService;

	@InjectMocks SettingsRequirementSetsController controller;

	private RequirementSet requirementSet;

	final private static long REQUIREMENT_SET_ID = 1L;
	final private static String JSON_STRING = "{\"requirements\":[{\"$type\":\"TestRequirement\",\"requirable\":{\"name\":\"asd\",\"id\":1288},\"mandatory\":true}],\"active\":true,\"$type\":\"RequirementSet\",\"name\":\"asfd\",\"required\":false,\"creatorName\":\"Jeff Wald\"}";
	final private static String ESCAPED_JSON_STRING = "{&quot;requirements&quot;:[{&quot;$type&quot;:&quot;TestRequirement&quot;,&quot;requirable&quot;:{&quot;name&quot;:&quot;asd&quot;,&quot;id&quot;:1288},&quot;mandatory&quot;:true}],&quot;active&quot;:true,&quot;$type&quot;:&quot;RequirementSet&quot;,&quot;name&quot;:&quot;asfd&quot;,&quot;required&quot;:false,&quot;creatorName&quot;:&quot;Jeff Wald&quot;}";

	@Before
	public void setUp() {
		requirementSet = mock(RequirementSet.class);

		when(requirementSetsService.find(anyLong())).thenReturn(requirementSet);

		when(jsonService.fromJson(ESCAPED_JSON_STRING)).thenReturn(null);
		when(jsonService.toJson((RequirementSet) null)).thenReturn(null);

		when(jsonService.fromJson(JSON_STRING)).thenReturn(requirementSet);
		when(jsonService.toJson(requirementSet)).thenReturn(JSON_STRING);

		when(jsonService.mergeJson(requirementSet, JSON_STRING)).thenReturn(requirementSet);
		when(jsonService.mergeJson(requirementSet, ESCAPED_JSON_STRING)).thenReturn(null);
	}

	@Test
	public void create_unescapesEscapedStringParam() {
		String result = controller.create(ESCAPED_JSON_STRING);
		assertEquals(JSON_STRING, result);
	}

	@Test
	public void update_unescapesEscapedStringParam() {
		String result = controller.update(REQUIREMENT_SET_ID, ESCAPED_JSON_STRING);
		assertEquals(JSON_STRING, result);
	}

}
