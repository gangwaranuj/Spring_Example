package com.workmarket.domains.model.asset.type;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick on 12/2/12 11:21 AM
 */
@Entity(name="taxVerificationRequestAssetAssociationType")
@Table(name="tax_verification_request_asset_association_type")
public class TaxVerificationRequestAssetAssociationType extends AssetType {

	public static String IRS_MATCH = "irs_match";

	public static final List<String> TYPES = Arrays.asList(NONE, IRS_MATCH);

	public TaxVerificationRequestAssetAssociationType() {
		super();
	}
	public TaxVerificationRequestAssetAssociationType(String code){
		super(code);
	}
}
