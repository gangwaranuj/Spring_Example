package com.workmarket.domains.model.asset.type;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="companyAssetLibraryAssociationType")
@Table(name="company_asset_library_association_type")
public class CompanyAssetLibraryAssociationType extends AssetType {

	private static final long serialVersionUID = -665401775889529675L;

	public static final List<String> TYPES = Arrays.asList(new String[] {NONE});
	
	public CompanyAssetLibraryAssociationType() {
		super();
	}
	public CompanyAssetLibraryAssociationType(String code){
		super(code);
	}
	
}