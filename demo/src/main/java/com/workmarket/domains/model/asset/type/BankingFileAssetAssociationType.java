package com.workmarket.domains.model.asset.type;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="bankingFileAssetAssociationType")
@Table(name="banking_file_asset_association_type")
public class BankingFileAssetAssociationType extends AssetType {
		
	private static final long serialVersionUID = -3015339364830018767L;
	
	public static final List<String> TYPES = Arrays.asList(new String[] {NONE});
	
	public BankingFileAssetAssociationType() {
		super();
	}
	public BankingFileAssetAssociationType(String code){
		super(code);
	}
}