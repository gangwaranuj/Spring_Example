package com.workmarket.api;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.ImmutableSet;
import com.workmarket.api.v2.ApiV2Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by joshlevine on 1/31/17.
 */
@org.junit.runner.RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ApiBaseHttpMessageConverterTest extends ExpectApiV2Support {

	private static final TypeReference<ApiV2Response<TestDTO>> testDTOResponseType = new TypeReference<ApiV2Response<TestDTO>>() {};


	@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
	@JsonDeserialize(builder = TestDTO.Builder.class)
	static class TestDTO {
		private String fieldOne;
		private String fieldTwo;
		private String fieldThree;
		private NestedTestDTO nestedDTO;

		@JsonProperty("fieldOne")
		public String getFieldOne() {
			return fieldOne;
		}

		@JsonProperty("fieldTwo")
		public String getFieldTwo() {
			return fieldTwo;
		}

		@JsonProperty("fieldThree")
		public String getFieldThree() {
			return fieldThree;
		}

		@JsonProperty("nestedDTO")
		public NestedTestDTO getNestedDTO() {
			return nestedDTO;
		}


		private TestDTO(Builder builder) {
			this.fieldOne = builder.fieldOne;
			this.fieldTwo = builder.fieldTwo;
			this.fieldThree = builder.fieldThree;
			this.nestedDTO = builder.nestedDTO;
		}


		public static class Builder {
			private String fieldOne;
			private String fieldTwo;
			private String fieldThree;
			private NestedTestDTO nestedDTO;

			@JsonProperty("fieldOne")
			public Builder fieldOne(String fieldOne) {
				this.fieldOne = fieldOne;
				return this;
			}

			@JsonProperty("fieldTwo")
			public Builder fieldTwo(String fieldTwo) {
				this.fieldTwo = fieldTwo;
				return this;
			}

			@JsonProperty("fieldThree")
			public Builder fieldThree(String fieldThree) {
				this.fieldThree = fieldThree;
				return this;
			}

			@JsonProperty("nestedDTO")
			public Builder nestedDTO(NestedTestDTO nestedDTO) {
				this.nestedDTO = nestedDTO;
				return this;
			}

			public Builder fromPrototype(TestDTO prototype) {
				fieldOne = prototype.fieldOne;
				fieldTwo = prototype.fieldTwo;
				fieldThree = prototype.fieldThree;
				nestedDTO = prototype.nestedDTO;
				return this;
			}

			public TestDTO build() {
				return new TestDTO(this);
			}
		}
	}


	@JsonFilter(ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS)
	@JsonDeserialize(builder = NestedTestDTO.Builder.class)
	static class NestedTestDTO {
		private String fieldOne;
		private String fieldTwo;
		private String fieldThree;

		@JsonProperty("fieldOne")
		public String getFieldOne() {
			return fieldOne;
		}

		@JsonProperty("fieldTwo")
		public String getFieldTwo() {
			return fieldTwo;
		}

		@JsonProperty("fieldThree")
		public String getFieldThree() {
			return fieldThree;
		}

		private NestedTestDTO(Builder builder) {
			this.fieldOne = builder.fieldOne;
			this.fieldTwo = builder.fieldTwo;
			this.fieldThree = builder.fieldThree;
		}


		public static class Builder {
			private String fieldOne;
			private String fieldTwo;
			private String fieldThree;

			@JsonProperty("fieldOne")
			public Builder fieldOne(String fieldOne) {
				this.fieldOne = fieldOne;
				return this;
			}

			@JsonProperty("fieldTwo")
			public Builder fieldTwo(String fieldTwo) {
				this.fieldTwo = fieldTwo;
				return this;
			}

			@JsonProperty("fieldThree")
			public Builder fieldThree(String fieldThree) {
				this.fieldThree = fieldThree;
				return this;
			}

			public Builder fromPrototype(TestDTO prototype) {
				fieldOne = prototype.fieldOne;
				fieldTwo = prototype.fieldTwo;
				fieldThree = prototype.fieldThree;
				return this;
			}

			public NestedTestDTO build() {
				return new NestedTestDTO(this);
			}
		}
	}

	@Test
	public void testProjectionSerialization() throws Exception {
		NestedTestDTO nestedDTO = new NestedTestDTO.Builder().fieldOne("nf1").fieldTwo("nf2").fieldThree("nf3").build();
		TestDTO testDTO = new TestDTO.Builder().fieldOne("f1").fieldTwo("f2").fieldThree("f3").nestedDTO(nestedDTO).build();
		ApiV2Response<TestDTO> apiResponse = ApiV2Response.valueWithResult(testDTO);

		ObjectMapper mapper = new ObjectMapper();
		String[] fieldsToInclude = { "fieldOne", "fieldThree", "nestedDTO.fieldTwo"};

		ApiBaseHttpMessageConverter.APIProjectionsFilter filter =
			new ApiBaseHttpMessageConverter.APIProjectionsFilter(ImmutableSet.copyOf(fieldsToInclude));

		FilterProvider filters = new SimpleFilterProvider().addFilter(
			ApiBaseHttpMessageConverter.FILTER_API_PROJECTIONS,
			filter
		);

		ObjectWriter writer = mapper.writer(filters);
		String json = writer.writeValueAsString(apiResponse);

		ApiV2Response<TestDTO> deserializedResponse = expectApiV2Response(json, testDTOResponseType);

		assertEquals("Expect fieldOne to match",
			deserializedResponse.getResults().get(0).getFieldOne(),
			testDTO.getFieldOne());

		assertNull("Expect fieldTwo to be suppressed",
			deserializedResponse.getResults().get(0).getFieldTwo());

		assertEquals("Expect fieldThree to match",
			deserializedResponse.getResults().get(0).getFieldThree(),
			testDTO.getFieldThree());

		assertNull("Expect nestedDTO.fieldOne to be suppressed",
			deserializedResponse.getResults().get(0).getNestedDTO().getFieldOne());

		assertEquals("Expect nestedDTO.fieldTwo to match",
			deserializedResponse.getResults().get(0).getNestedDTO().getFieldTwo(),
			testDTO.getNestedDTO().getFieldTwo());

		assertNull("Expect nestedDTO.fieldThree to be suppressed",
			deserializedResponse.getResults().get(0).getNestedDTO().getFieldThree());
	}
}
