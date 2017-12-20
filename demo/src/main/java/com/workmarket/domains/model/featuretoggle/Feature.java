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
 * Time: 2:26 PM
 */
@Entity(name = "feature")
@Table(name = "feature")
@AuditChanges
public class Feature extends AuditedEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String featureName;
	private Boolean isAllowed = Boolean.FALSE;
	private Set<FeatureSegment> segments = Sets.newHashSet();

	@Column(name = "feature_name", nullable = false, length = 50)
	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	@Column(name = "is_allowed", nullable = false, length = 50)
	public Boolean getAllowed() {
		return isAllowed;
	}

	public void setAllowed(Boolean allowed) {
		isAllowed = allowed;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feature", cascade = CascadeType.ALL)
	public Set<FeatureSegment> getSegments() {
		return segments;
	}

	public void setSegments(Set<FeatureSegment> segments) {
		if (segments == null) { this.segments = Sets.newHashSet(); }
		else { this.segments = segments; }
	}

	public void addFeatureSegment(FeatureSegment fs) {
		if (fs != null) { this.segments.add(fs); }
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		else if (
			o instanceof Feature &&
			((Feature) o).getFeatureName().equals(featureName)
		) { return true; }
		return false;
	}

	public int hashcode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
