package com.workmarket.thrift;

import com.workmarket.search.request.user.PeopleSearchRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ThriftUtilitiesTest {

	@Test
	public void testRoundtripString() throws Exception {
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		searchRequest.setUserId(1000L);
		searchRequest.addToGroupFilter(1001L);
		searchRequest.addToGroupFilter(1002L);
		searchRequest.addToGroupFilter(1003L);

		String serialization = ThriftUtilities.serializeToString(searchRequest);

		PeopleSearchRequest deserialized = ThriftUtilities.deserialize(serialization, PeopleSearchRequest.class);

		Assert.assertNotNull(deserialized);
		Assert.assertEquals(1000L, deserialized.getUserId());
		Assert.assertEquals(3, deserialized.getGroupFilterSize());
		Assert.assertTrue(deserialized.getGroupFilter().contains(1001L));
		Assert.assertTrue(deserialized.getGroupFilter().contains(1002L));
		Assert.assertTrue(deserialized.getGroupFilter().contains(1003L));
	}

	@Test
	public void testRoundtripBytes() throws Exception {
		PeopleSearchRequest searchRequest = new PeopleSearchRequest();
		searchRequest.setUserId(1000L);
		searchRequest.addToGroupFilter(1001L);
		searchRequest.addToGroupFilter(1002L);
		searchRequest.addToGroupFilter(1003L);

		byte[] serialization = ThriftUtilities.serialize(searchRequest);

		PeopleSearchRequest deserialized = ThriftUtilities.deserialize(serialization, PeopleSearchRequest.class);

		Assert.assertNotNull(deserialized);
		Assert.assertEquals(1000L, deserialized.getUserId());
		Assert.assertEquals(3, deserialized.getGroupFilterSize());
		Assert.assertTrue(deserialized.getGroupFilter().contains(1001L));
		Assert.assertTrue(deserialized.getGroupFilter().contains(1002L));
		Assert.assertTrue(deserialized.getGroupFilter().contains(1003L));
	}
}
