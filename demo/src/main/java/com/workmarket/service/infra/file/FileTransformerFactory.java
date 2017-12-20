package com.workmarket.service.infra.file;

public interface FileTransformerFactory {
	FileTransformer newImageBatchTransformer();
	FileTransformer newImageBatchTransformer(FileTransformer... transformers);
	FileTransformer newImageCropTransformer(int x1, int y1, int x2, int y2);
	FileTransformer newImageResizeTransformer(int width, int height);
	FileTransformer newImageThumbnailSmallTransformer();
	FileTransformer newImageThumbnailLargeTransformer();
	FileTransformer newImageWorkThumbnailSmallTransformer();
	FileTransformer newImageWorkThumbnailLargeTransformer();
	FileTransformer newImageWorkOrientationTransformer();
}
