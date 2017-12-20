package com.workmarket.dao.featuretoggle;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.featuretoggle.FeatureSegment;
import org.springframework.stereotype.Repository;

/**
 * User: micah
 * Date: 8/7/13
 * Time: 1:38 PM
 */
@Repository
public class FeatureSegmentDAOImpl extends AbstractDAO<FeatureSegment> implements FeatureSegmentDAO {
	@Override
	protected Class<FeatureSegment> getEntityClass() {
		return FeatureSegment.class;
	}

	@Override
	public void deleteSegment(FeatureSegment fs) {
		getFactory().getCurrentSession().createQuery("delete from feature_segment_reference where featureSegment.id = :id").setParameter("id", fs.getId()).executeUpdate();
		getFactory().getCurrentSession().createQuery("delete from feature_segment where id = :id").setParameter("id", fs.getId()).executeUpdate();
	}

	@Override
	public FeatureSegment findByFeatureAndSegmentNames(String featureName, String segmentName) {
		return (FeatureSegment)getFactory().getCurrentSession()
			.createQuery("select fs from feature_segment fs, feature f where f.featureName = :featureName and f = fs.feature and fs.featureSegmentName = :segmentName")
			.setParameter("featureName", featureName)
			.setParameter("segmentName", segmentName)
			.uniqueResult();
	}
}
