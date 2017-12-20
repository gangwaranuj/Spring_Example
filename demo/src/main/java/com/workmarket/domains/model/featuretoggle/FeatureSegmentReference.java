package com.workmarket.domains.model.featuretoggle;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: micah
 * Date: 8/4/13
 * Time: 2:53 PM
 */
@Entity(name = "feature_segment_reference")
@Table(name = "feature_segment_reference")
@AuditChanges
public class FeatureSegmentReference extends AuditedEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private FeatureSegment featureSegment;
	private String referenceValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feature_segment_id", referencedColumnName = "id", nullable = false)
	public FeatureSegment getFeatureSegment() {
		return featureSegment;
	}

	public void setFeatureSegment(FeatureSegment featureSegment) {
		this.featureSegment = featureSegment;
	}

	@Column(name = "reference_value", nullable = false, length = 50)
	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		else if (o instanceof FeatureSegmentReference) {
			FeatureSegmentReference fsr = (FeatureSegmentReference)o;
			if (
				fsr.getFeatureSegment().equals(featureSegment) &&
				fsr.getReferenceValue().equals(referenceValue)
			) { return true; }
		}
		return false;
	}

	public int hashcode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
