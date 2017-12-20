package com.workmarket.domains.model.contract;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Entity(name = "contract")
@Table(name = "contract")
@NamedQueries({
		@NamedQuery(name = "contract.findContractByName", query = "from contract c where c.name = :name")
})
@AuditChanges
public class Contract extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private Company company;
	private Boolean active = Boolean.TRUE;
	private Set<ContractVersion> contractVersions = Sets.newLinkedHashSet();
	private List<UserGroup> groups = Lists.newArrayList();
	private Integer contractVersionCount;
	private Long mostRecentContractVersionId;

	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToMany(mappedBy = "contract")
	public Set<ContractVersion> getContractVersions() {
		return contractVersions;
	}

	public void setContractVersions(Set<ContractVersion> contractVersions) {
		this.contractVersions = contractVersions;
	}

	@Column(name = "active", nullable = false)
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Formula(value = "(select count(1) from contract_version cv inner join contract c on cv.contract_id = c.id where cv.contract_id = id)")
	public Integer getContractVersionCount() {
		return contractVersionCount;
	}

	public void setContractVersionCount(Integer contractVersionCount) {
		this.contractVersionCount = contractVersionCount;
	}

	@Formula(value = "(select max(cv.id) from contract_version cv inner join contract c on cv.contract_id = c.id where cv.contract_id = id)")
	public Long getMostRecentContractVersionId() {
		return mostRecentContractVersionId;
	}

	public void setMostRecentContractVersionId(Long mostRecentContractVersionId) {
		this.mostRecentContractVersionId = mostRecentContractVersionId;
	}
}
