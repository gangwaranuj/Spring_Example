package com.workmarket.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

	public static String BASEURL;

	public static String ASSET_PATH;

	public static String COMPANY_ASSET_PATH;

	public static String CONTRACT_VERSION_ASSET_PATH;

	public static String USER_ASSET_PATH;

	public static String WORK_ASSET_PATH;

	public static String BANKING_ASSET_PATH;

	public static String TAX_ENTITY_ASSET_PATH;

	public static String CAMPAIGN_DETAILS_URL;
	public static String WORK_DETAILS_URL;

	public static String WM_SUPPORT_PHONE_NUMBER;

	@Value("${baseurl}")
	public void setBaseURL(String baseURL) {
		BASEURL = baseURL;
	}

	@Value("${asset.path}")
	public void setAssetPath(String assetPath){
		ASSET_PATH = assetPath;
	}

	@Value("${asset.company.path}")
	public void setCompanyAssetPath(String companyAssetPath) {
		COMPANY_ASSET_PATH = companyAssetPath;
	}

	@Value("${asset.contract.path}")
	public void setContractVersionAssetPath(
			String contractVersionAssetPath) {
		CONTRACT_VERSION_ASSET_PATH = contractVersionAssetPath;
	}

	@Value("${asset.user.path}")
	public void setUserAssetPath(String userAssetPath) {
		USER_ASSET_PATH = userAssetPath;
	}

	@Value("${asset.work.path}")
	public void setWorkAssetPath(String workAssetPath) {
		WORK_ASSET_PATH = workAssetPath;
	}

	@Value("${asset.banking.path}")
	public void setBankingAssetPath(String bankingAssetPath) {
		BANKING_ASSET_PATH = bankingAssetPath;
	}

	@Value("${asset.taxentity.path}")
	public void setTaxEntityAssetPath(String taxEntityAssetPath) {
		TAX_ENTITY_ASSET_PATH = taxEntityAssetPath;
	}

	@Value("${campaign.details.url}")
	public void setCampaignDetailsUrl(String campaignDetailsUrl) {
		CAMPAIGN_DETAILS_URL = campaignDetailsUrl;
	}

	@Value("${assignment.details.url}")
	public void setWorkDetailsUrl(String workDetailsUrl) {
		WORK_DETAILS_URL = workDetailsUrl;
	}

	@Value("${WM_SUPPORT_PHONE_NUMBER}")
	public void setWmSupportPhoneNumber(String wmSupportPhoneNumber) {
		WM_SUPPORT_PHONE_NUMBER = wmSupportPhoneNumber;
	}
}
