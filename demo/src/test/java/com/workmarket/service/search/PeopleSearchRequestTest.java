package com.workmarket.service.search;

import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.utility.SerializationUtilities;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PeopleSearchRequestTest {

	@Test
	public void writeObject() throws Exception {
		final long expectedUserId = 1234L;

		PeopleSearchRequest originalRequest = new PeopleSearchRequest();
		originalRequest.setUserId(expectedUserId);
		byte[] bytes = SerializationUtilities.serialize(originalRequest);

		PeopleSearchRequest clonedRequest = (PeopleSearchRequest)SerializationUtilities.deserialize(bytes);
		assertEquals(expectedUserId, clonedRequest.getUserId());
		assertTrue(clonedRequest.isSetUserId());
	}

}
