package com.workmarket.common.service.wrapper;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.wrapper.response.BulkResponse;
import com.workmarket.common.service.wrapper.response.MessageResponse;
import com.workmarket.service.business.dto.TagDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 4/23/13 8:38 AM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BulkResponseTest {

	// use a simple bean - TagDTO
	private class TestResponse extends BulkResponse<String, MessageResponse> {

		@Override
		public Class<?> getKeyClass() {
			return TagDTO.class;
		}

		@Override
		public String getKeyName() {
			return "name";
		}
	}

	@Test
	public void isSuccessful_WithNewBulkResponse_SetAllResponsesSuccessful() {
		TestResponse response = new TestResponse();
		response.addResponse("response1", new MessageResponse(BaseStatus.SUCCESS));
		response.addResponse("response2", new MessageResponse(BaseStatus.SUCCESS));
		response.addResponse("response3", new MessageResponse(BaseStatus.SUCCESS));
		assertTrue(response.isSuccessful());
	}

	@Test
	public void isSuccessful_WithNewBulkResponse_SetOneResponseFail() {
		TestResponse response = new TestResponse();
		response.addResponse("response1", new MessageResponse(BaseStatus.SUCCESS));
		response.addResponse("response2", new MessageResponse(BaseStatus.SUCCESS));
		response.addResponse("response3", new MessageResponse(BaseStatus.FAILURE));
		assertFalse(response.isSuccessful());
	}

	@Test
	public void isSuccessful_WithNewBulkResponse_SetBulkResponseFail() {
		TestResponse response = new TestResponse();
		response.addResponse("response1", new MessageResponse(BaseStatus.SUCCESS));
		response.setBulkStatus(BaseStatus.FAILURE);
		assertFalse(response.isSuccessful());
	}

	@Test
	public void getResponse_WithNewBulkResponse_SetSuccessful() {
		TestResponse response = new TestResponse();
		TagDTO dto1 = new TagDTO("name1");
		response.addResponse(dto1.getName(), new MessageResponse(BaseStatus.SUCCESS));

		assertTrue(response.getResponse(dto1).isSuccessful());
	}

}
