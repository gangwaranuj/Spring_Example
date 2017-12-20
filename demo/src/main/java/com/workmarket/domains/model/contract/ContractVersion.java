package com.workmarket.domains.model.contract;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ContractVersionAssetAssociation;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "contractVersion")
@Table(name = "contract_version")
@AuditChanges
public class ContractVersion extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private Contract contract;
	private Set<ContractVersionAssetAssociation> contractVersionAssetAssociations = Sets.newLinkedHashSet();
	private Set<ContractVersionUserSignature> contractVersionUserSignatures = Sets.newLinkedHashSet();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@OneToMany(mappedBy = "contractVersion")
	public Set<ContractVersionUserSignature> getContractVersionUserSignatures() {
		return contractVersionUserSignatures;
	}

	public void setContractVersionUserSignatures(Set<ContractVersionUserSignature> contractVersionUserSignatures) {
		this.contractVersionUserSignatures = contractVersionUserSignatures;
	}

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_version_id")
	public Set<ContractVersionAssetAssociation> getContractVersionAssetAssociations() {
		return contractVersionAssetAssociations;
	}

	public void setContractVersionAssetAssociations(Set<ContractVersionAssetAssociation> contractVersionAssetAssociations) {
		this.contractVersionAssetAssociations = contractVersionAssetAssociations;
	}

	@Transient
	public Set<Asset> getContractVersionAssets() {
		Set<Asset> assets = Sets.newHashSet();
		for (ContractVersionAssetAssociation a : contractVersionAssetAssociations)
			assets.add(a.getAsset());
		return assets;
	}
}
