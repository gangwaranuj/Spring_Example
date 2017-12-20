package com.workmarket.domains.model.asset;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.assessment.AttemptResponse;
import com.workmarket.domains.model.asset.type.AssetType;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="attemptResponseAssetAssociation")
@Table(name="assessment_attempt_response_asset_association")
@AuditChanges
public class AttemptResponseAssetAssociation extends AbstractEntityAssetAssociation<AttemptResponse> implements EntityAssetAssociation<AttemptResponse> {
	private static final long serialVersionUID = 1L;

	private Asset transformedSmallAsset;
	private Asset transformedLargeAsset;

	public AttemptResponseAssetAssociation() {}
	public AttemptResponseAssetAssociation(AttemptResponse entity, Asset asset, AssetType type) {
		super(entity, asset, type);
	}

	@ManyToOne
	@JoinColumn(name="transformed_small_asset_id", updatable = false)
	public Asset getTransformedSmallAsset() {
		return transformedSmallAsset;
	}

	public void setTransformedSmallAsset(Asset transformedSmallAsset) {
		this.transformedSmallAsset = transformedSmallAsset;
	}

	@ManyToOne
	@JoinColumn(name="transformed_large_asset_id", updatable = false)
	public Asset getTransformedLargeAsset() {
		return transformedLargeAsset;
	}

	public void setTransformedLargeAsset(Asset transformedLargeAsset) {
		this.transformedLargeAsset = transformedLargeAsset;
	}
}
