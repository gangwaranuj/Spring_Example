package com.workmarket.service.security.features;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.authentication.features.EntitledSegment;
import com.workmarket.domains.authentication.features.Feature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 5/3/13
 * Time: 12:42 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FeatureUnitTest {
	//HashMap<String, EntitledSegment<?>> entitledSegments;
	Feature feature;

	@Before
	public void setup() {
		//entitledSegments = new HashMap<String, EntitledSegment<?>>();
		feature = new Feature("MyFeature");
		//feature.setEntitledSegments(entitledSegments);
		feature.setEnabled(true);
	}

	@Test
	public void isEntitledFor_With_Long() {
		feature.addSegment("company", new EntitledSegment<Long>(ImmutableSet.of(1L, 2L, 3L)));
		assertTrue(feature.isEnabledFor("company", 3L));
		assertFalse(feature.isEnabledFor("company", 4L));
	}

	@Test
	public void isEntitledFor_With_String() {
		feature.addSegment("country", new EntitledSegment<String>(ImmutableSet.of("One", "Two", "Three")));
		assertTrue(feature.isEnabledFor("country", "Two"));
		assertFalse(feature.isEnabledFor("country", "Four"));
	}

	@Test
	public void isEntitledFor_With_Long_And_String() {
		feature.addSegment("company", new EntitledSegment<Long>(ImmutableSet.of(1L, 2L, 3L)));
		feature.addSegment("country", new EntitledSegment<String>(ImmutableSet.of("One", "Two", "Three")));

		assertTrue(feature.isEnabledFor("company", 3L));
		assertFalse(feature.isEnabledFor("company", 4L));
		assertTrue(feature.isEnabledFor("country", "Two"));
		assertFalse(feature.isEnabledFor("country", "Four"));
	}
}
