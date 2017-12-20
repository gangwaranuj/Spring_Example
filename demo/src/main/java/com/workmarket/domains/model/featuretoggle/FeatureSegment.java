package com.workmarket.domains.model.featuretoggle;


import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * User: micah
 * Date: 8/4/13
 * Time: 2:42 PM
 */
@Entity(name = "feature_segment")
@Table(name = "feature_segment")
@AuditChanges
public class FeatureSegment extends AuditedEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Feature feature;
	private String featureSegmentName;
	private Set<FeatureSegmentReference> segmentReferences = Sets.newHashSet();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feature_id", referencedColumnName = "id", nullable = false)
	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	@Column(name = "segment_name", nullable = false, length = 50)
	public String getFeatureSegmentName() {
		return featureSegmentName;
	}

	public void setFeatureSegmentName(String featureSegmentName) {
		this.featureSegmentName = featureSegmentName;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "featureSegment", cascade = CascadeType.ALL)
	public Set<FeatureSegmentReference> getSegmentReferences() {
		return segmentReferences;
	}

	public void setSegmentReferences(Set<FeatureSegmentReference> segmentReferences) {
		if (segmentReferences == null) { this.segmentReferences = Sets.newHashSet(); }
		else { this.segmentReferences = segmentReferences; }
	}

	public void addSegmentReference(FeatureSegmentReference fsr) {
		if (fsr != null) { this.segmentReferences.add(fsr); }
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		else if (
			o instanceof FeatureSegment &&
			((FeatureSegment)o).getFeatureSegmentName().equals(featureSegmentName)
		) { return true; }
		return false;
	}

	public int hashcode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
