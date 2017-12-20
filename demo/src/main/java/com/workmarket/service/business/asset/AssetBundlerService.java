package com.workmarket.service.business.asset;

import com.google.common.base.Optional;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.ZipAssetBundle;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.thrift.work.WorkResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface AssetBundlerService {
	void sendAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException;

	Optional<Asset> downloadAsset(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException;

	ZipAssetBundle createAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException;

	ZipAssetBundle createAssignmentsAssetBundle(AssetBundle bundle) throws IOException, HostServiceException, AssetTransformationException;

	AssetAssignmentBundle createAssignmentAssetBundle(List<WorkResponse> workResponses, Set<String> types, boolean filterForDeliverables, Long deliverableRequirementId);
}
