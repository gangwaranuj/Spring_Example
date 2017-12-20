package com.workmarket.service.infra.business;

import com.google.common.collect.*;
import com.workmarket.dao.featuretoggle.FeatureSegmentDAO;
import com.workmarket.dao.featuretoggle.FeatureSegmentReferenceDAO;
import com.workmarket.dao.featuretoggle.FeatureDAO;
import com.workmarket.domains.model.featuretoggle.Feature;
import com.workmarket.domains.model.featuretoggle.FeatureSegment;
import com.workmarket.domains.model.featuretoggle.FeatureSegmentReference;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * User: micah
 * Date: 8/5/13
 * Time: 1:50 AM
 */
@Service
public class FeatureToggleServiceImpl implements FeatureToggleService {
	@Autowired
	FeatureDAO featureDAO;
	@Autowired FeatureSegmentDAO featureSegmentDAO;
	@Autowired FeatureSegmentReferenceDAO featureSegmentReferenceDAO;

	@Override
	public ServiceResponseBuilder addFeature(String featureName, Boolean isAllowed) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Feature f = setupFeature(featureName, isAllowed);
		featureDAO.saveOrUpdate(f);

		serviceResponseBuilder.setSuccessful(true);

		return serviceResponseBuilder;
	}

	@Override
	public ServiceResponseBuilder addFeature(String featureName, Boolean isAllowed, String segmentName, String referenceValue) {

		if (StringUtils.isEmpty(segmentName) || StringUtils.isEmpty(referenceValue)) {
			return addFeature(featureName, isAllowed);
		}

		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Feature f = setupFeature(featureName, isAllowed);
		setupFeatureSegment(f, segmentName, referenceValue);
		featureDAO.saveOrUpdate(f);
		serviceResponseBuilder.setSuccessful(true);

		return serviceResponseBuilder;
	}

	@Override
	public ServiceResponseBuilder addSegment(String featureName, String segmentName, String referenceValue) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Object[] args = {"featureName", featureName};
		Feature f = featureDAO.findBy(args);
		if (f != null) {
			setupFeatureSegment(f, segmentName, referenceValue);
			featureDAO.saveOrUpdate(f);
			serviceResponseBuilder.setSuccessful(true);
		}

		return serviceResponseBuilder;
	}

	@Override
	public ServiceResponseBuilder removeFeature(String featureName) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Object[] args = {"featureName", featureName};
		Feature f = featureDAO.findBy(args);
		if (f != null) {
			featureDAO.delete(f);
			serviceResponseBuilder.setSuccessful(true);
		}

		return serviceResponseBuilder;
	}

	@Override
	public ServiceResponseBuilder removeSegment(String featureName, String segmentName) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		FeatureSegment fs = featureSegmentDAO.findByFeatureAndSegmentNames(featureName, segmentName);
		if (fs == null) { return serviceResponseBuilder; }

		featureSegmentDAO.deleteSegment(fs);
		return serviceResponseBuilder.setSuccessful(true);
	}

	@Override
	public ServiceResponseBuilder removeReferenceValue(String featureName, String segmentName, String referenceValue) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		FeatureSegmentReference fsr = featureSegmentReferenceDAO.findByFeatureAndSegmentAndReferenceNames(featureName, segmentName, referenceValue);
		if (fsr == null) { return serviceResponseBuilder; }

		featureSegmentReferenceDAO.deleteReference(fsr);
		return serviceResponseBuilder.setSuccessful(true);
	}

	@Override
	public ServiceResponseBuilder updateFeature(String featureName, Boolean isAllowed) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Object[] args = {"featureName", featureName};
		Feature f = featureDAO.findBy(args);
		if (f != null) {
			f.setAllowed(isAllowed);
			featureDAO.saveOrUpdate(f);
			serviceResponseBuilder.setSuccessful(true);
		}

		return serviceResponseBuilder;
	}

	@Override
	public ServiceResponseBuilder getFeature(String featureName) {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();

		Object[] args = {"featureName", featureName};
		Feature f = featureDAO.findBy(args);
		if (f == null) { return serviceResponseBuilder; }
		Map<String, Object> data = Maps.newHashMap();
		buildSegmentMap(f, data);

		serviceResponseBuilder.setData(data);
		return serviceResponseBuilder.setSuccessful(true);
	}

	@Override
	public ServiceResponseBuilder getAllFeatures() {
		ServiceResponseBuilder serviceResponseBuilder = setupServiceResponseBuilder();
		List<Feature> features = featureDAO.getAll();
		if (features.size() == 0) { return serviceResponseBuilder; }
		Map<String, Object> data = Maps.newHashMap();
		for (Feature f : features) {
			buildSegmentMap(f, data);
		}
		serviceResponseBuilder.setData(data);
		return serviceResponseBuilder.setSuccessful(true);
	}

	private void buildSegmentMap(Feature f, Map<String, Object> data) {
		Map<String, Object> featureData = Maps.newHashMap();
		featureData.put("Enabled", f.getAllowed());
		Map<String, Object> segments = Maps.newHashMap();
		featureData.put("segments", segments);
		data.put(f.getFeatureName(), featureData);
		if (f.getSegments() != null) {
			for (FeatureSegment segment : f.getSegments()) {
				ArrayList<String> references = Lists.newArrayList();
				segments.put(segment.getFeatureSegmentName(), references);
				for (FeatureSegmentReference reference : segment.getSegmentReferences()) {
					references.add(reference.getReferenceValue());
				}
			}
		}
	}

	private ServiceResponseBuilder setupServiceResponseBuilder() {
		ServiceResponseBuilder serviceResponseBuilder = new ServiceResponseBuilder();
		serviceResponseBuilder.setSuccessful(false);
		return serviceResponseBuilder;
	}

	private Feature setupFeature(String featureName, Boolean isAllowed) {
		Assert.notNull(featureName);
		Assert.notNull(isAllowed);

		Feature f = new Feature();
		f.setFeatureName(featureName);
		f.setAllowed(isAllowed);
		return f;
	}

	private void setupFeatureSegment(Feature f, final String segmentName, final String referenceValue) {
		Assert.notNull(segmentName);
		Assert.notNull(referenceValue);

		FeatureSegment fs = null;
		for (FeatureSegment fs1 : f.getSegments()) {
			if (fs1.getFeatureSegmentName().equals(segmentName)) {
				fs = fs1;
				break;
			}
		}

		if (fs == null) {
			fs = new FeatureSegment();
			fs.setFeatureSegmentName(segmentName);
			fs.setFeature(f);
			f.addFeatureSegment(fs);
		}

		FeatureSegmentReference fsr =  new FeatureSegmentReference();
		fsr.setReferenceValue(referenceValue);

		fsr.setFeatureSegment(fs);
		fs.addSegmentReference(fsr);
	}

	@Override
	public List<Long> getCompaniesWithFeature(String featureName) {
		Set<Long> companyIds = Sets.newHashSet();
		List<FeatureSegmentReference> featureSegments = featureSegmentReferenceDAO.findByFeatureAndSegment(featureName, "companyId");
		for(FeatureSegmentReference segment : featureSegments) {
			companyIds.add(Long.valueOf(segment.getReferenceValue()));
		}
		return Lists.newArrayList(companyIds);
	}
}
