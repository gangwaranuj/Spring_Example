package com.workmarket.domains.model.asset.type;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Entity(name="companyAssetAssociationType")
@Table(name="company_asset_association_type")
public class CompanyAssetAssociationType extends AssetType {

	private static final long serialVersionUID = -3155824750828215843L;
	
	public static final String CLIENT_SERVICES_INTERNAL = "csrIntern";
	public static final String AVATAR = "avatar";
	public static final String RECRUITING_CAMPAIGN_LOGO = "campaign";
	public static final String INVITATION_COMPANY_LOGO = "invitation";
	public static final String ASSET_BUNDLE = "assetBundle";
	public static final String SCHEDULED_REPORT = "scheduledReport";
	
	public static final List<String> TYPES = Arrays.asList(
			NONE,
			CLIENT_SERVICES_INTERNAL,
			AVATAR,
			RECRUITING_CAMPAIGN_LOGO,
			INVITATION_COMPANY_LOGO,
			ASSET_BUNDLE,
			SCHEDULED_REPORT);

	public CompanyAssetAssociationType() {
		super();
	}
	public CompanyAssetAssociationType(String code){
		super(code);
	}
	
}
