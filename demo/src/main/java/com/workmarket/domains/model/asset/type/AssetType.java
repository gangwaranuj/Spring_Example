package com.workmarket.domains.model.asset.type;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.workmarket.domains.model.LookupEntity;

@Entity(name = "asset_type")
@Table(name = "asset_type")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AssetType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static String NONE = "none";

	public AssetType() {
		super();
	}

	public AssetType(String code) {
		super(code);
	}
}