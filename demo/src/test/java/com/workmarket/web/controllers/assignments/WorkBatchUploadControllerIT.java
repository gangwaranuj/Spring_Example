package com.workmarket.web.controllers.assignments;

import com.workmarket.api.v2.employer.uploads.controllers.BaseUploadsControllerIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkBatchUploadControllerIT extends BaseUploadsControllerIT {

	@Test
	public void mapPreview() throws Exception {
		MockMultipartFile upload = mockCsv("upload",
			"Title,Description\n",
			"awesome title,awesome description\n",
			"blarg,blurg\n"
		);

		MvcResult mvcResult =  mockMvc.perform(
			MockMvcRequestBuilders
				.fileUpload("/assignments/upload/map")
				.file(upload)
				.param("preview", "true")
				.param("headersProvided", "true"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("response", instanceOf(WorkUploadResponse.class)))
			.andExpect(model().attribute(
				"response", hasProperty(
					"uploads", allOf(
						hasItem(
							hasProperty(
								"values", allOf(
									hasItem(
										allOf(
											hasProperty("value", equalTo("awesome title")),
											hasProperty("type", hasProperty("description", equalTo("Title")))
										)
									),
									hasItem(
										allOf(
											hasProperty("value", equalTo("awesome description")),
											hasProperty("type", hasProperty("description", equalTo("Description")))
										)
									)
								)
							)
						),
						hasItem(
							hasProperty(
								"values", allOf(
									hasItem(
										allOf(
											hasProperty("value", equalTo("blarg")),
											hasProperty("type", hasProperty("description", equalTo("Title")))
										)
									),
									hasItem(
										allOf(
											hasProperty("value", equalTo("blurg")),
											hasProperty("type", hasProperty("description", equalTo("Description")))
										)
									)
								)
							)
						)
					)
				)
			))
			.andExpect(view().name("tiles:web/pages/assignments/upload/map"))

			.andDo(print())
			.andReturn();

		assertThat(mvcResult.getResponse().getContentAsString(), equalTo(""));
	}

	@Test
	public void mapNotPreview() throws Exception {
		MockMultipartFile upload = mockCsv("upload",
			"Title,Description\n",
			"awesome title,awesome description\n",
			"blarg,blurg\n"
		);

		MvcResult mvcResult =  mockMvc.perform(
			MockMvcRequestBuilders
				.fileUpload("/assignments/upload/map")
				.file(upload)
				.param("preview", "false")
				.param("headersProvided", "true"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("response", instanceOf(WorkUploadResponse.class)))
			.andExpect(model().attribute(
				"response", hasProperty(
					"uploads", allOf(
						hasItem(
							hasProperty(
								"values", allOf(
									hasItem(
										allOf(
											hasProperty("value", equalTo("awesome title")),
											hasProperty("type", hasProperty("description", equalTo("Title")))
										)
									),
									hasItem(
										allOf(
											hasProperty("value", equalTo("awesome description")),
											hasProperty("type", hasProperty("description", equalTo("Description")))
										)
									)
								)
							)
						),
						hasItem(
							hasProperty(
								"values", allOf(
									hasItem(
										allOf(
											hasProperty("value", equalTo("blarg")),
											hasProperty("type", hasProperty("description", equalTo("Title")))
										)
									),
									hasItem(
										allOf(
											hasProperty("value", equalTo("blurg")),
											hasProperty("type", hasProperty("description", equalTo("Description")))
										)
									)
								)
							)
						)
					)
				)
			))
			.andExpect(view().name("tiles:web/pages/assignments/upload/map"))

			.andDo(print())
			.andReturn();

		assertThat(mvcResult.getResponse().getContentAsString(), equalTo(""));
	}
}
