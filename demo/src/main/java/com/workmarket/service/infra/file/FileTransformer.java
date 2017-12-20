package com.workmarket.service.infra.file;

import java.io.InputStream;

import com.workmarket.service.exception.asset.AssetTransformationException;

public interface FileTransformer {
	public InputStream transform(InputStream stream) throws AssetTransformationException;
}