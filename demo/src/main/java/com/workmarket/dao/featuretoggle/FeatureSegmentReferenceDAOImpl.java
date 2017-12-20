package com.workmarket.dao.featuretoggle;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.featuretoggle.FeatureSegmentReference;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FeatureSegmentReferenceDAOImpl extends AbstractDAO<FeatureSegmentReference> implements FeatureSegmentReferenceDAO {
	@Override
	protected Class<FeatureSegmentReference> getEntityClass() {
		return FeatureSegmentReference.class;
	}

	@Override
	public void deleteReference(FeatureSegmentReference fsr) {
		getFactory().getCurrentSession().createQuery("delete from feature_segment_reference where id = :id").setParameter("id", fsr.getId()).executeUpdate();
	}

	@Override
	public FeatureSegmentReference findByFeatureAndSegmentAndReferenceNames(String featureName, String segmentName, String referenceValue) {
		return (FeatureSegmentReference)getFactory().getCurrentSession()
			.createQuery(
				"select "+
					"fsr from feature_segment_reference fsr, feature_segment fs, feature f "+
				"where "+
					"f = fs.feature and f.featureName = :featureName and "+
					"fs.featureSegmentName = :segmentName and "+
					"fsr.featureSegment = fs and "+
					"fsr.referenceValue = :referenceValue"
			)
			.setParameter("featureName", featureName)
			.setParameter("segmentName", segmentName)
			.setParameter("referenceValue", referenceValue)
			.uniqueResult();
	}

	@Override
	public List<FeatureSegmentReference> findByFeatureAndSegment(
		String featureName,
		String segmentName) {
		return getFactory().getCurrentSession()
			.createQuery(
				"select "+
					"fsr from feature_segment_reference fsr, feature_segment fs, feature f "+
					"where "+
					"f = fs.feature and f.featureName = :featureName and "+
					"fs.featureSegmentName = :segmentName and "+
					"fsr.featureSegment = fs "
			)
			.setParameter("featureName", featureName)
			.setParameter("segmentName", segmentName)
			.list();
	}
}
