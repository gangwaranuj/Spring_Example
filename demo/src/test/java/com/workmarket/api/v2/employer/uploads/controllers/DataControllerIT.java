package com.workmarket.api.v2.employer.uploads.controllers;


import com.workmarket.api.v2.employer.uploads.models.DataDTO;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import com.workmarket.api.v2.employer.uploads.services.PreviewStorageService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DataControllerIT extends BaseUploadsControllerIT {
	@Autowired PreviewStorageService previewStorageService;

	@Test
	public void getData() throws Exception {
		String uuid = upload(this.csv);
		processData(uuid);

		DataDTO dataDTO = getData(uuid);
		assertThat(dataDTO, hasProperty("count", is(2L)));
		assertThat(dataDTO, hasProperty("data", hasSize(2)));
	}

	@Test
	public void getRowDataFromRedis() throws Exception {
		String uuid = upload(this.csv);
		processData(uuid);

		List<PreviewDTO> rowPreviews = previewStorageService.get(uuid, 0, -1);
		assertThat(rowPreviews, hasSize(2));
		assertThat(rowPreviews.get(0).getRowData(), is(notNullValue()));
	}
}
