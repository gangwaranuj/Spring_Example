package com.workmarket.api.v2.employer.uploads.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.uploads.models.SettingsDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UploadsSettingsControllerIT extends BaseUploadsControllerIT {

	private static final TypeReference<ApiV2Response<SettingsDTO>> settingsType = new TypeReference<ApiV2Response<SettingsDTO>>() {};

	@Test
	public void postAndGetSettings() throws Exception {
		String uuid = String.valueOf(System.currentTimeMillis());

		postSettings(uuid, new SettingsDTO.Builder()
			.setLabelId(111L)
			.setTemplateId("222").build());
		MvcResult mvcResult =  get("settings", uuid);

		SettingsDTO settingsDTO = getFirstResult(mvcResult, settingsType);
		assertThat(settingsDTO, hasProperty("labelId", is(111L)));
		assertThat(settingsDTO, hasProperty("templateId", is("222")));
	}

	@Test
	public void postAndGetSettings_templateOnly() throws Exception {
		String uuid = String.valueOf(System.currentTimeMillis());

		postSettings(uuid, new SettingsDTO.Builder()
			.setLabelId(111L).build());
		MvcResult mvcResult =  get("settings", uuid);

		SettingsDTO settingsDTO = getFirstResult(mvcResult, settingsType);
		assertThat(settingsDTO, hasProperty("labelId", is(111L)));
	}

	@Test
	public void postAndGetSettings_labelOnly() throws Exception {
		String uuid = String.valueOf(System.currentTimeMillis());

		postSettings(uuid, new SettingsDTO.Builder()
			.setTemplateId("222").build());
		MvcResult mvcResult =  get("settings", uuid);

		SettingsDTO settingsDTO = getFirstResult(mvcResult, settingsType);
		assertThat(settingsDTO, hasProperty("templateId", is("222")));
	}
}
