package com.workmarket.service.featuretoggle;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.featuretoggle.Feature;
import com.workmarket.domains.model.featuretoggle.FeatureSegment;
import com.workmarket.domains.model.featuretoggle.FeatureSegmentReference;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.FeatureToggleService;
import com.workmarket.test.IntegrationTest;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 8/5/13
 * Time: 8:54 AM
 */
// TODO: This iteration is just to get basic feature toggle stuff working via the
// application for the acceptance test suite. Future iterations will have better
// error handling and feedback to the browser.
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class FeatureToggleServiceIT extends BaseServiceIT {
	@Autowired FeatureToggleService featureToggleService;

	static final String FEATURE_NAME = "MyGreatFeature";

	@Test(expected = IllegalArgumentException.class)
	public void addFeature_WithNulls_Fails() {
		featureToggleService.addFeature(null, null);
	}

	@Test(expected = ConstraintViolationException.class)
	public void addFeature_Dups_Fails() {
		featureToggleService.addFeature(FEATURE_NAME, Boolean.TRUE);
		featureToggleService.addFeature(FEATURE_NAME, Boolean.TRUE);
	}

	@Test
	public void addFeature_Global_Succeeds() {
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.addFeature(FEATURE_NAME, Boolean.FALSE);
		assertEquals(true, serviceResponseBuilder.isSuccessful());
		serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);

		Map feature = (Map)serviceResponseBuilder.getData().get(FEATURE_NAME);
		assertEquals(Boolean.FALSE, feature.get("Enabled"));
		assertEquals(0, ((Map)feature.get("segments")).size());
	}

	@Test
	public void addFeature_Segments_Succeeds() {
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.addFeature(FEATURE_NAME, Boolean.TRUE);
		assertEquals(true, serviceResponseBuilder.isSuccessful());
		featureToggleService.addSegment(FEATURE_NAME, "country", "USA");
		featureToggleService.addSegment(FEATURE_NAME, "country", "CA");
		featureToggleService.addSegment(FEATURE_NAME, "id", "1");
		serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);

		Map feature = (Map)serviceResponseBuilder.getData().get(FEATURE_NAME);

		Map featureSegments = (Map)feature.get("segments");
		assertEquals(2, featureSegments.size());
		assertEquals(2, ((List)featureSegments.get("country")).size());
	}

	@Test
	public void removeFeature_Succeeds() {
		featureToggleService.addFeature(FEATURE_NAME, Boolean.TRUE);
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeFeature(FEATURE_NAME);
		assertEquals(true, serviceResponseBuilder.isSuccessful());
		serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);
		assertEquals(false, serviceResponseBuilder.isSuccessful());
	}

	private void setupRemove() {
		featureToggleService.addFeature(FEATURE_NAME, Boolean.TRUE);
		featureToggleService.addSegment(FEATURE_NAME, "country", "USA");
		featureToggleService.addSegment(FEATURE_NAME, "country", "CA");
		featureToggleService.addSegment(FEATURE_NAME, "id", "1");
	}

	@Test
	public void removeSegment_Succeeds() {
		setupRemove();
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeSegment(FEATURE_NAME, "country");
		assertEquals(true, serviceResponseBuilder.isSuccessful());
		serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);

		Map feature = (Map)serviceResponseBuilder.getData().get(FEATURE_NAME);
		Map segments = (Map)feature.get("segments");

		assertEquals(1, segments.size());
		assertEquals("1", ((ArrayList)segments.values().iterator().next()).get(0));
	}

	@Test
	public void removeReference_Succeeds() {
		setupRemove();
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.removeReferenceValue(FEATURE_NAME, "country", "USA");
		assertEquals(true, serviceResponseBuilder.isSuccessful());
		serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);

		Map feature = (Map)serviceResponseBuilder.getData().get(FEATURE_NAME);
		Map segments = (Map)feature.get("segments");
		List countries = ((List)segments.get("country"));

		assertEquals(1, countries.size());
		assertEquals("CA", countries.get(0));
	}

	@Test
	public void updateFeature_Global_Succeeds() {
		featureToggleService.addFeature(FEATURE_NAME, Boolean.FALSE);
		featureToggleService.updateFeature(FEATURE_NAME, Boolean.TRUE);
		ServiceResponseBuilder serviceResponseBuilder = featureToggleService.getFeature(FEATURE_NAME);
		Map feature = (Map)serviceResponseBuilder.getData().get(FEATURE_NAME);

		assertEquals(Boolean.TRUE, feature.get("Enabled"));
	}

	@After
	public void tearDown() {
		super.tearDown();
		featureToggleService.removeFeature(FEATURE_NAME);
	}

	@Test
	public void findCompaniesWithFeature() throws Exception {

		String featureName = "new-feature-test";

		Feature f = new Feature();
		f.setFeatureName(featureName);
		f.setAllowed(true);

		featureToggleService.addFeature(featureName, true);

		FeatureSegment fs = new FeatureSegment();
		fs.setFeatureSegmentName("companyid");
		fs.setFeature(f);
		f.getSegments().add(fs);

		User employee1 = newEmployeeWithCashBalance();
		User employee2 = newEmployeeWithCashBalance();
		User employee3 = newEmployeeWithCashBalance();

		featureToggleService.addSegment(featureName, "companyId", String.valueOf(employee1.getCompany().getId()));
		featureToggleService.addSegment(featureName, "companyId", String.valueOf(employee2.getCompany().getId()));
		featureToggleService.addSegment(featureName, "companyId", String.valueOf(employee3.getCompany().getId()));

		List<Long> companyIds = featureToggleService.getCompaniesWithFeature(featureName);
		assertEquals(3, companyIds.size());
	}
}
