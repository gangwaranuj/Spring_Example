package com.workmarket.api.v2.controllers;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.BaseApiControllerTest;
import com.workmarket.api.exceptions.GenericApiException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.orgstructure.OrgStructureService;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrgStructureControllerTest extends BaseApiControllerTest {

	private static final String ORG_UNIT_UUID = "org-unit-uuid";
	private static final String ORG_FEATURE = "org_structures";
	private static final String SELECTED_ORG_MODE = "orgUuid";

	@Mock
	private FeatureEntitlementService featureEntitlementService;
	@Mock
	private OrgStructureService orgStructureService;

	@InjectMocks
	private OrgStructureController controller = new OrgStructureController();

	@Before
	public void setup() throws Exception {
		super.setup(controller);
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(true);

		when(orgStructureService.setOrgModeSettingForUser(anyLong(), anyString())).thenReturn(BaseStatus.SUCCESS);
		when(orgStructureService.getOrgModeSetting(anyLong())).thenReturn(SELECTED_ORG_MODE);
		when(orgStructureService.getOrgUnitMembers(anyList())).thenReturn(ImmutableList.of(new UserDTO()));
	}

	@Test
	public void getSubtreePaths_toggleOff_emptyList() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		final ApiV2Response<OrgUnitDTO> emptyResponse = controller.getSubtreePaths(ORG_UNIT_UUID);

		assertEquals(Integer.valueOf(HttpStatus.SC_OK), emptyResponse.getMeta().getStatusCode());
		assertThat(emptyResponse.getResults(), Matchers.<OrgUnitDTO>empty());
	}

	@Test
	public void getSubtreePaths_emptyOrgUnitUuid_emptyList() {
		final ApiV2Response<OrgUnitDTO> emptyResponse = controller.getSubtreePaths(ORG_UNIT_UUID);

		assertEquals(Integer.valueOf(HttpStatus.SC_OK), emptyResponse.getMeta().getStatusCode());
		assertThat(emptyResponse.getResults(), Matchers.<OrgUnitDTO>empty());
	}

	@Test
	public void getSubtreePaths_success_pathList() {
		final OrgUnitDTO pathDTOPhoto = buildOrgUnitPathDTO("Photo", "1231",
			ImmutableList.of("COMPANY B"));
		final OrgUnitDTO pathDTOLocal = buildOrgUnitPathDTO("Local", "3324",
			ImmutableList.of("COMPANY B", "Magazine", "Magazine Supervision"));
		final OrgUnitDTO pathDTOMagT = buildOrgUnitPathDTO("Mag-T", "9695",
			ImmutableList.of("COMPANY B", "Magazine", "Magazine Supervision", "Mag"));
		final List<OrgUnitDTO> expectedResult =
			ImmutableList.of(pathDTOPhoto, pathDTOLocal, pathDTOMagT);
		when(orgStructureService.getSubtreePaths(anyLong(), anyLong(), eq(ORG_UNIT_UUID)))
			.thenReturn(expectedResult);

		final ApiV2Response<OrgUnitDTO> response = controller.getSubtreePaths(ORG_UNIT_UUID);
		final List<OrgUnitDTO> actualPaths = response.getResults();

		assertEquals(Integer.valueOf(HttpStatus.SC_OK), response.getMeta().getStatusCode());
		assertEquals(expectedResult.size(), actualPaths.size());
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("uuid", equalTo(pathDTOPhoto.getUuid()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("name", equalTo(pathDTOPhoto.getName()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("paths", equalTo(pathDTOPhoto.getPaths()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("uuid", equalTo(pathDTOLocal.getUuid()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("name", equalTo(pathDTOLocal.getName()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("paths", equalTo(pathDTOLocal.getPaths()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("uuid", equalTo(pathDTOMagT.getUuid()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("name", equalTo(pathDTOMagT.getName()))));
		assertThat(actualPaths,
			hasItem(Matchers.<OrgUnitDTO>hasProperty("paths", equalTo(pathDTOMagT.getPaths()))));
	}

	@Test
	public void getOrgMode_toggleOff_emptyString() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		final ApiV2Response<String> emptyResponse = controller.getOrgMode();

		final List<String> results = emptyResponse.getResults();
		assertEquals(Integer.valueOf(HttpStatus.SC_OK), emptyResponse.getMeta().getStatusCode());
		assertTrue(StringUtils.isBlank(results.get(0)));
	}

	@Test
	public void getOrgMode_toggleOff_orgServiceNotCalled() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		final ApiV2Response<String> emptyResponse = controller.getOrgMode();

		verify(orgStructureService, never()).getOrgModeSetting(anyLong());
	}

	@Test
	public void getOrgMode_toggleOn_orgServiceCalled() {
		controller.getOrgMode();

		verify(orgStructureService).getOrgModeSetting(anyLong());
	}

	@Test(expected = GenericApiException.class)
	public void setOrgMode_toggleOff_exceptionThrown() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		controller.setOrgMode(SELECTED_ORG_MODE);
	}

	@Test
	public void setOrgMode_toggleOff_orgServiceNotCalled() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		try {
			controller.setOrgMode(SELECTED_ORG_MODE);
		} catch (GenericApiException ex) {
			// ignore exception
		}

		verify(orgStructureService, never()).getOrgModeSetting(anyLong());
	}

	@Test
	public void setOrgMode_toggleOn_orgServiceCalled() {
		controller.setOrgMode(SELECTED_ORG_MODE);

		verify(orgStructureService).setOrgModeSettingForUser(anyLong(), anyString());
	}

	@Test
	public void getOrgModeOptions_toggleOff_emptyList() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		final ApiV2Response<List<OrgUnitPath>> response = controller.getOrgModeOptions();

		final List<OrgUnitPath> orgModeOptions = response.getResults().get(0);
		assertTrue(CollectionUtils.isEmpty(orgModeOptions));
	}

	@Test
	public void getOrgModeOptions_toggleOff_orgServiceNotCalled() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
			.thenReturn(false);

		controller.getOrgModeOptions();

		verify(orgStructureService, never()).getOrgModeOptions(anyLong());
	}

	@Test
	public void getOrgModeOptions_toggleOn_orgServiceCalled() {
		controller.getOrgModeOptions();

		verify(orgStructureService).getOrgModeOptions(anyLong());
	}

	private OrgUnitDTO buildOrgUnitPathDTO(final String uuid, final String name, final List<String> paths) {
		return new OrgUnitDTO.Builder(OrgUnitPath.newBuilder()
			.setName(name)
			.setUuid(uuid)
			.addAllPath(paths)
			.build()).build();
	}

	@Test
	public void getOrgUnitMembers_toggleOn_emptyList() {
		final ApiV2Response<UserDTO> response = controller.getOrgUnitMembers(Collections.EMPTY_LIST);
		final List<UserDTO> users = response.getResults();
		assertTrue(CollectionUtilities.isEmpty(users));
	}

	@Test
	public void getOrgUnitMembers_toggleOn_nonEmptyList() {
		final ApiV2Response<UserDTO> response = controller.getOrgUnitMembers(ImmutableList.of(ORG_UNIT_UUID));
		final List<UserDTO> users = response.getResults();
		assertEquals(users.size(), 1);
	}

	@Test
	public void getOrgUnitMembers_toggleOn_orgServiceCalled() {
		final List<String> orgUnitUuids = ImmutableList.of(ORG_UNIT_UUID);
		controller.getOrgUnitMembers(orgUnitUuids);

		verify(orgStructureService).getOrgUnitMembers(orgUnitUuids);
	}

	@Test
	public void getOrgUnitMembers_toggleOff_emptyList() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
				.thenReturn(false);

		final ApiV2Response<UserDTO> response = controller.getOrgUnitMembers(Collections.EMPTY_LIST);
		final List<UserDTO> users = response.getResults();
		assertTrue(CollectionUtilities.isEmpty(users));
	}

	@Test
	public void getOrgUnitMembers_toggleOff_orgServiceNotCalled() {
		when(featureEntitlementService.hasFeatureToggle(anyLong(), eq(ORG_FEATURE)))
				.thenReturn(false);

		final List<String> orgUnitUuids = ImmutableList.of(ORG_UNIT_UUID);
		controller.getOrgUnitMembers(orgUnitUuids);

		verify(orgStructureService, never()).getOrgUnitMembers(orgUnitUuids);
	}
}
