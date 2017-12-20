package com.workmarket.domains.model.asset.type;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="attemptResponseAssetAssociationType")
@Table(name="assessment_attempt_response_asset_association_type")
public class AttemptResponseAssetAssociationType extends AssetType {
	private static final long serialVersionUID = -1512371291474059507L;
	
	public static final List<String> TYPES = Arrays.asList(new String[] {NONE});
	
	public AttemptResponseAssetAssociationType() {}
	public AttemptResponseAssetAssociationType(String code){
		super(code);
	}
}