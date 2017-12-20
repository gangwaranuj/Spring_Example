package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.VisibilityType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by alejandrosilva on 1/19/15.
 */
@Entity(name="workAssetVisibility")
@Table(name="work_asset_visibility")
@NamedQueries({
	@NamedQuery(name="workAssetVisibility.find", query="select a from workAssetVisibility a where a.workAssetAssociation.id = :workAssetAssociationId")
})
public class WorkAssetVisibility extends AbstractEntity {

	private static final long serialVersionUID = -2800011307318293517L;

	private WorkAssetAssociation workAssetAssociation;
	private VisibilityType visibilityType;

	public WorkAssetVisibility() {}

	public WorkAssetVisibility(WorkAssetAssociation workAssetAssociation, VisibilityType visibilityType) {
		this.workAssetAssociation = workAssetAssociation;
		this.visibilityType = visibilityType;
	}

	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="work_asset_association_id", referencedColumnName="id")
	public WorkAssetAssociation getWorkAssetAssociation() {
		return workAssetAssociation;
	}

	public void setWorkAssetAssociation(WorkAssetAssociation workAssetAssociation) {
		this.workAssetAssociation = workAssetAssociation;
	}

	@OneToOne(fetch= FetchType.EAGER)
	@JoinColumn(name="visibility_type_code", referencedColumnName="code")
	public VisibilityType getVisibilityType() {
		return visibilityType;
	}

	public void setVisibilityType(VisibilityType visibilityType) {
		this.visibilityType = visibilityType;
	}

}
