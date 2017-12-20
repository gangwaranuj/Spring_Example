package com.workmarket.service.business.asset;


import java.util.List;

public class AssetAssignmentBundle  extends AssetBundle{
	private List<String> assetAssignments;


	public List<String> getAssetAssignments() {
		return assetAssignments;
	}

	public void setAssetAssignments(List<String> assetAssignments) {
		this.assetAssignments = assetAssignments;
	}

	public String getName(int index) {
		return  assetAssignments.get(index);
	}

}
