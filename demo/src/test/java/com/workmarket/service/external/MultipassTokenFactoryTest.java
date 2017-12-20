package com.workmarket.service.external;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.LinkedHashMap;

@RunWith(BlockJUnit4ClassRunner.class)
public class MultipassTokenFactoryTest {

	private MultipassTokenFactory multipassFactory;

	private final static String JSON;
	private final static String ENCODED_JSON = "cyvf5GEygj9EfEXxxARA66GgIjqtVSWNpXRKQdnzBlGxB_1NMbcRoYXPxLNfGdxTxbhpn2dHAD51ewDtLahxslM5MPMUyIaS3Plq4S8kRqQCMOakPEXLKc0PNjn4-SMKtykC_GpJrYegogcZuMGF9lXo7zcXi53qBqMegphpZhg";
	private final static String SIGNATURE = "PaRrGCUoqFkM4dojbJFNtoWTjOM=";

	static {
		try {
			JSON = new JSONObject(new LinkedHashMap())
					.put("uid", "123456789")
					.put("expires", "2012-08-02T12:00Z")
					.put("customer_name", "Timmy Tester")
					.put("customer_email", "test@test.com")
					.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Before
	public void before() {
		multipassFactory = new MultipassTokenFactoryImpl("workmarket", "670e6b60b978012f34d512313e008572");
	}

	@Test
	public void encode() throws Exception {
		String encoded = multipassFactory.encode(JSON);
		Assert.assertEquals(ENCODED_JSON, encoded);
	}

	@Test
	public void sign() throws Exception {
		String encoded = multipassFactory.sign(ENCODED_JSON);
		Assert.assertEquals(SIGNATURE, encoded);
	}
}
