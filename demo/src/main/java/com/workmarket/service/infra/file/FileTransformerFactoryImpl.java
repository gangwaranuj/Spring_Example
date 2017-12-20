package com.workmarket.service.infra.file;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.asset.AssetTransformationException;
import com.workmarket.service.infra.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class FileTransformerFactoryImpl implements FileTransformerFactory {

	@Autowired private ImageService imageService;

	@Override
	public FileTransformer newImageBatchTransformer() {
		return new ImageBatchTransformer();
	}

	@Override
	public FileTransformer newImageBatchTransformer(FileTransformer... transformers) {
		return new ImageBatchTransformer(transformers);
	}

	@Override
	public FileTransformer newImageCropTransformer(int x1, int y1, int x2, int y2) {
		return new ImageCropTransformer(x1, y1, x2, y2);
	}

	@Override
	public FileTransformer newImageResizeTransformer(int width, int height) {
		return new ImageResizeTransformer(width, height);
	}

	@Override
	public FileTransformer newImageThumbnailSmallTransformer() {
		return newImageResizeTransformer(Constants.ASSET_AVATAR_SMALL_THUMBNAIL_WIDTH, Constants.ASSET_AVATAR_SMALL_THUMBNAIL_HEIGHT);
	}

	@Override
	public FileTransformer newImageThumbnailLargeTransformer() {
		return newImageResizeTransformer(Constants.ASSET_AVATAR_LARGE_THUMBNAIL_WIDTH, Constants.ASSET_AVATAR_LARGE_THUMBNAIL_HEIGHT);
	}

	@Override
	public FileTransformer newImageWorkThumbnailSmallTransformer() {
		return newImageResizeTransformer(Constants.WORK_ASSET_SMALL_THUMBNAIL_WIDTH, Constants.WORK_ASSET_SMALL_THUMBNAIL_HEIGHT);
	}

	@Override
	public FileTransformer newImageWorkThumbnailLargeTransformer() {
		return newImageResizeTransformer(Constants.WORK_ASSET_LARGE_THUMBNAIL_WIDTH, Constants.WORK_ASSET_LARGE_THUMBNAIL_HEIGHT);
	}

	@Override
	public FileTransformer newImageWorkOrientationTransformer() {
		return new ImageOrientationTransformer();
	}

	// Image Transformers

	public class ImageBatchTransformer implements FileTransformer {
		private List<FileTransformer> transformers = Lists.newArrayList();

		public ImageBatchTransformer() {}
		public ImageBatchTransformer(FileTransformer... transformers) {
			this.transformers = Lists.newArrayList(transformers);
		}

		public ImageBatchTransformer addTransformer(FileTransformer transformer) {
			this.transformers.add(transformer);
			return this;
		}

		public boolean hasTransformers() {
			return (isNotEmpty(transformers));
		}
		@Override
		public InputStream transform(InputStream stream) throws AssetTransformationException {
			InputStream newStream = stream;
			for (FileTransformer t : transformers) {
				newStream = t.transform(newStream);
			}
			return newStream;
		}
	}

	public class ImageCropTransformer implements FileTransformer {
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		public ImageCropTransformer(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		@Override
		public InputStream transform(InputStream stream) throws AssetTransformationException {
			try {
				return imageService.cropImageFile(stream, x1, y1, x2, y2);
			} catch (Exception e) {
				throw new AssetTransformationException();
			}
		}
	}

	public class ImageResizeTransformer implements FileTransformer {
		private int width;
		private int height;

		public ImageResizeTransformer(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public InputStream transform(InputStream stream) throws AssetTransformationException {
			try {
				return imageService.resizeImageFile(stream, width, height);
			} catch (Exception e) {
				throw new AssetTransformationException(e);
			}
		}
	}

	private class ImageOrientationTransformer implements FileTransformer {
		@Override
		public InputStream transform(InputStream stream) throws AssetTransformationException {
			try {
				return imageService.orientImageFile(stream);
			} catch (Exception e) {
				throw new AssetTransformationException(e);
			}
		}
	}
}
