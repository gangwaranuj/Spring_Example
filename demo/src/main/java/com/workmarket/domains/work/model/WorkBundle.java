package com.workmarket.domains.work.model;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Entity(name = "workBundle")
@DiscriminatorValue("B")
@NamedQueries({
	@NamedQuery(name = "workBundle.findAllInByWorkNumbers", query = "FROM work w WHERE w.workNumber IN (:workNumbers)"),
	@NamedQuery(name = "workBundle.findAllInByIds", query = "FROM work w WHERE w.id IN (:ids)")
})
@AuditChanges
@Access(AccessType.FIELD)
public class WorkBundle extends Work {

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	private Set<Work> bundle;

	@Transient
	private BigDecimal bundleBudget = BigDecimal.ZERO;

	public Set<Work> getBundle() {
		return bundle;
	}

	public void setBundle(Set<Work> bundle) {
		this.bundle = bundle;
	}

	@Transient
	public boolean isWorkBundle() {
		return true;
	}

	@Transient
	public BigDecimal getBundleBudget() {
		return bundleBudget;
	}

	@Transient
	public void setBundleBudget(BigDecimal bundleBudget) {
		this.bundleBudget = bundleBudget;
	}

	@Transient
	public Set<Long> getBundleIds() {
		if (isNotEmpty(bundle)) {
			Set<Long> ids = Sets.newHashSetWithExpectedSize(bundle.size());
			for (Work work : bundle) {
				ids.add(work.getId());
			}
			return ids;
		}
		return Collections.emptySet();
	}
}
