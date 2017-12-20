package com.workmarket.service.security.features;

import com.workmarket.data.aggregate.FeatureAggregate;
import com.workmarket.domains.authentication.features.Feature;
import com.workmarket.domains.authentication.features.FeatureEvaluatorConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 5/6/13
 * Time: 3:32 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class FeatureEvaluatorConfigurationUnitTest {
	@Mock NamedParameterJdbcTemplate jdbcTemplate;
	@InjectMocks FeatureEvaluatorConfiguration featureEvaluatorConfiguration;

	List<FeatureAggregate> results = new ArrayList<FeatureAggregate>();

	private FeatureAggregate setupFeature(String name, boolean allowed, String segment, String referenceValue) {
		FeatureAggregate f = new FeatureAggregate();
		f.setFeatureName(name);
		f.setAllowed(allowed);
		f.setSegmentName(segment);
		f.setReferenceValue(referenceValue);

		return f;
	}

	private void addToResults(String name, boolean allowed, String segment, String referenceValue) {
		results.add(setupFeature(name, allowed, segment, referenceValue));
	}

	@Before
	public void setup() throws Exception {
		// allow all example
		addToResults("feed", true, null, null);

		// multiple references example
		addToResults("bundles", true, "id", "37588,32570,30526,13792,8845,44251,1003");
		addToResults("bundles", true, "companyId", "1340");

		// string example
		addToResults("screening", true, "country", "USA");

		when(jdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class), any(RowMapper.class)))
			.thenReturn(results);
		featureEvaluatorConfiguration.reload();
	}

	@Test
	public void get_BasicWithString() {
		Feature f = featureEvaluatorConfiguration.get("screening");
		assertEquals(true, f.isEnabled());
		assertEquals(true, f.isEnabledFor("country", "USA"));
		assertEquals(false, f.isEnabledFor("country", "FR"));
	}

	@Test
	public void get_BasicWithLong() {
		Feature f = featureEvaluatorConfiguration.get("bundles");
		assertEquals(true, f.isEnabledFor("companyId", 1340L));
		assertEquals(false, f.isEnabledFor("companyId", 4L));
	}

	@Test
	public void get_ConfirmAllAllowed() {
		Feature f = featureEvaluatorConfiguration.get("feed");
		assertEquals(true, f.isEnabled());
	}

	@Test
	public void get_ConfirmMultipleValues() {
		Feature f = featureEvaluatorConfiguration.get("bundles");
		assertEquals(true, f.isEnabledFor("id", 1003L));
		assertEquals(true, f.isEnabledFor("id", 37588L));
	}
}
