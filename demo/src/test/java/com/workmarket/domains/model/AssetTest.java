package com.workmarket.domains.model;

import com.workmarket.domains.model.asset.Asset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AssetTest {
	Asset asset;
	String uuid =  "84d05a46-cdc5-4982-8b72-84fec9317769";
	String remoteURI = "https://s3.amazonaws.com/workmarket-private-dev/84/d0/5a/46/cd/84d05a46-cdc5-4982-8b72-84fec9317769";


	@Before
	public void setup() {
		asset = new Asset();
		asset.setAvailability(new AvailabilityType(AvailabilityType.ALL));
		asset.setUUID(uuid);
		asset.setRemoteUri(remoteURI);
		asset.setActive(Boolean.TRUE);
		asset.setName("confirmation");
	}

	@Test
	public void general_ShowContentOfTheUrlInline(){
		assertEquals("/asset/84d05a46-cdc5-4982-8b72-84fec9317769",asset.getUri());
	}

	@Test
	public void general_ShowContentAsAttachment(){
		assertEquals("/asset/download/84d05a46-cdc5-4982-8b72-84fec9317769",asset.getDownloadableUri());
	}

	@Test
	public void general_ReturnNullIfNoIDPresent(){
		asset.setUUID(null);
		assertNull(asset.getUri());
	}
}
