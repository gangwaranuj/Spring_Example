package com.workmarket.api.v2.employer.uploads.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.workmarket.api.v2.employer.uploads.models.RowsDTO;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RowsControllerIT extends BaseUploadsControllerIT {
	private static final TypeReference<ApiV2Response<RowsDTO>> rowsType = new TypeReference<ApiV2Response<RowsDTO>>() {};

	@Test
	public void getRows() throws Exception {
		String uuid = upload(csv);
		processRows(uuid);

		RowsDTO getResult = getRows(uuid);
		assertThat(getResult, hasProperty("uuid", is(uuid)));
		assertThat(getResult, hasProperty("count", is(2L)));
		assertThat(getResult, hasProperty("rows", hasSize(2)));
	}
}
