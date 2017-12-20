package com.workmarket.service.security.features;

import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.features.Feature;
import com.workmarket.domains.authentication.features.FeatureEvaluatorConfiguration;
import com.workmarket.domains.authentication.features.FeatureEvaluatorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.security.core.Authentication;

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: micah
 * Date: 5/6/13
 * Time: 6:19 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FeatureEvaluatorImplUnitTest {
	FeatureEvaluatorConfiguration config = mock(FeatureEvaluatorConfiguration.class);
	Authentication authentication = mock(Authentication.class);
	ExtendedUserDetails user = mock(ExtendedUserDetails.class);
	Feature feature = mock(Feature.class);

	FeatureEvaluatorImpl featureEvaluator;

	@Before
	public void setup() {
		featureEvaluator = new FeatureEvaluatorImpl();
		featureEvaluator.setConfig(config);

		when(authentication.getPrincipal()).thenReturn(user);
	}

	@Test
	public void hasFeature_DoesntExist_False() {
		when(config.get("noexist")).thenReturn(null);
		assertEquals(false, featureEvaluator.hasFeature(authentication, "noexist"));
	}

	@Test
	public void hasFeature_ByCompanyId_True() {
		when(user.getCompanyId()).thenReturn(1L);
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("companyId", 1L)).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("companyId"));

		assertEquals(true, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_ByCompanyId_False() {
		when(user.getCompanyId()).thenReturn(1L);
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("companyId", 2L)).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("companyId"));

		assertEquals(false, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_ByCountry_True() {
		when(user.getCountry()).thenReturn("USA");
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("country", "USA")).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("country"));

		assertEquals(true, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_ById_True() {
		when(user.getId()).thenReturn(1234L);
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("id", 1234L)).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("id"));

		assertEquals(true, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_Global_False() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.FALSE);

		assertEquals(false, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_Global_True() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);

		assertEquals(true, featureEvaluator.hasFeature(authentication, "feed"));
	}

	@Test
	public void hasFeature_SpecialCompanyId_True() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("companyId", 1L)).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("companyId"));

		assertEquals(true, featureEvaluator.hasFeature(1L, "feed"));
	}

	@Test
	public void hasFeature_SpecialCompanyId_False() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.isEnabledFor("companyId", 1L)).thenReturn(Boolean.FALSE);
		when(feature.getEntitledSegmentKeys()).thenReturn(Sets.newHashSet("companyId"));

		assertEquals(false, featureEvaluator.hasFeature(1L, "feed"));
	}

	@Test
	public void hasFeature_SpecialCompanyId_Global_True() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.TRUE);
		when(feature.getEntitledSegmentKeys()).thenReturn(new HashSet<String>());

		assertEquals(true, featureEvaluator.hasFeature(1L, "feed"));
		assertEquals(true, featureEvaluator.hasFeature(1L, "feed"));
	}

	@Test
	public void hasFeature_SpecialCompanyId_Global_False() {
		when(config.get("feed")).thenReturn(feature);
		when(feature.isEnabled()).thenReturn(Boolean.FALSE);
		when(feature.getEntitledSegmentKeys()).thenReturn(new HashSet<String>());

		assertEquals(false, featureEvaluator.hasFeature(1L, "feed"));
		assertEquals(false, featureEvaluator.hasFeature(1L, "feed"));
	}
}
