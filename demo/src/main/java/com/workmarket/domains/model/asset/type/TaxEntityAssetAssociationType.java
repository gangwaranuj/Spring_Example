package com.workmarket.domains.model.asset.type;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="taxEntityAssetAssociationType")
@Table(name="tax_entity_asset_association_type")
public class TaxEntityAssetAssociationType extends AssetType {
	
	private static final long serialVersionUID = -305527414023190333L;
	
	public static String W9 = "w9";
	
	public static final List<String> TYPES = Arrays.asList(NONE, W9);
	
	public TaxEntityAssetAssociationType() {
		super();
	}
	public TaxEntityAssetAssociationType(String code){
		super(code);
	}
}