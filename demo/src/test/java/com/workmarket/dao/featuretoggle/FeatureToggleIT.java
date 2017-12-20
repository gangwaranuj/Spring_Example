package com.workmarket.dao.featuretoggle;

import com.workmarket.data.aggregate.FeatureAggregate;
import com.workmarket.domains.model.featuretoggle.Feature;
import com.workmarket.domains.model.featuretoggle.FeatureSegment;
import com.workmarket.domains.model.featuretoggle.FeatureSegmentReference;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.sql.SQLBuilder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: micah
 * Date: 8/4/13
 * Time: 3:04 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class FeatureToggleIT extends BaseServiceIT {
	@Autowired FeatureDAO featureDAO;

	@Resource(name = "readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	final static String FEATURE_NAME = "MyGreatFeature";

	private Feature findMyGreatFeature() {
		return featureDAO.findBy("featureName", FEATURE_NAME);
	}

	@Test
	@Transactional
	public void initialize_Verify() {
		Feature f = findMyGreatFeature();
		assertEquals(null, f);
		f = new Feature();
		f.setFeatureName(FEATURE_NAME);
		f.setAllowed(true);
		featureDAO.saveOrUpdate(f);

		f = findMyGreatFeature();
		assertEquals("MyGreatFeature", f.getFeatureName());
		assertEquals(Boolean.TRUE, f.getAllowed());
	}

	@Test
	@Transactional
	public void fullFeature_WithSegments_Verify() {
		Feature f = new Feature();
		f.setFeatureName(FEATURE_NAME);
		f.setAllowed(true);

		FeatureSegment fs = new FeatureSegment();
		fs.setFeatureSegmentName("country");
		fs.setFeature(f);

		FeatureSegmentReference fsr = new FeatureSegmentReference();
		fsr.setReferenceValue("USA");
		fsr.setFeatureSegment(fs);

		fs.getSegmentReferences().add(fsr);
		f.getSegments().add(fs);

		featureDAO.saveOrUpdate(f);

		f = findMyGreatFeature();
		fs = f.getSegments().toArray(new FeatureSegment[0])[0];
		fsr = fs.getSegmentReferences().toArray(new FeatureSegmentReference[0])[0];

		assertEquals("country", fs.getFeatureSegmentName());
		assertEquals("USA", fsr.getReferenceValue());
	}

	@Test
	@Transactional
	public void featureWithSegments_independentDatabaseConfirmation() {
		Feature f = new Feature();
		f.setFeatureName(FEATURE_NAME);
		f.setAllowed(true);

		FeatureSegment fs = new FeatureSegment();
		fs.setFeatureSegmentName("country");

		FeatureSegmentReference fsr = new FeatureSegmentReference();
		fsr.setReferenceValue("USA");

		fsr.setFeatureSegment(fs);
		fs.getSegmentReferences().add(fsr);

		fs.setFeature(f);
		f.getSegments().add(fs);

		featureDAO.saveOrUpdate(f);

		// verify with direct database access
		SQLBuilder sql = new SQLBuilder();
		sql
			.addColumns(
				"f.feature_name as featureName",
				"f.is_allowed as isAllowed",
				"fs.segment_name as segmentName",
				"group_concat(fsr.reference_value separator ',') as referenceValue"
			)
			.addJoin("left join feature_segment fs on f.id = fs.feature_id")
			.addJoin("left join feature_segment_reference fsr on fs.id = fsr.feature_segment_id")
			.addTable("feature f")
			.addGroupColumns("featureName", "segmentName")
			.addWhereClause("feature_name = '" + FEATURE_NAME + "'");

		RowMapper<FeatureAggregate> mapper = new RowMapper<FeatureAggregate>() {
			public FeatureAggregate mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeatureAggregate feature = new FeatureAggregate();

				feature.setFeatureName(rs.getString("featureName"));
				feature.setAllowed(rs.getBoolean("isAllowed"));
				feature.setSegmentName(rs.getString("segmentName"));
				feature.setReferenceValue(rs.getString("referenceValue"));

				return feature;
			}
		};

		List<FeatureAggregate> results = jdbcTemplate.query(sql.build(), sql.getParams(), mapper);

		assertEquals(1, results.size());

		FeatureAggregate result = results.get(0);

		assertEquals(FEATURE_NAME, result.getFeatureName());
		assertEquals("country", result.getSegmentName());
		assertEquals("USA", result.getReferenceValue());
	}
}
