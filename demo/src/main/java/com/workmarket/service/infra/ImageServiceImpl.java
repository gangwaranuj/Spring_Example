package com.workmarket.service.infra;

import com.workmarket.utility.FileUtilities;
import com.workmarket.utility.ImageUtilities;
import com.workmarket.utility.ImageUtilitiesException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service

public class ImageServiceImpl implements ImageService {
	@Override
	public void resizeImageFile(String sourceFilePath, String destinationFilePath, int scaledWidth, int scaledHeight) throws Exception {
		ImageUtilities.scaleImage(sourceFilePath, destinationFilePath, scaledWidth, scaledHeight);
	}

	@Override
	public void cropImageFile(String sourceFilePath, String destinationFilePath, int x1, int y1, int x2, int y2) throws Exception {
		ImageUtilities.cropImage(sourceFilePath, destinationFilePath, x1, y1, x2, y2);
	}

	@Override
	public InputStream resizeImageFile(InputStream stream, int width, int height) throws IOException, ImageUtilitiesException {
		String original = FileUtilities.generateTemporaryFileName();
		String modified = FileUtilities.generateTemporaryFileName();

		IOUtils.copy(stream, new FileOutputStream(original));
		ImageUtilities.scaleImage(original, modified, width, height);

		FileUtils.deleteQuietly(new File(original));
		return new DeletableFileInputStream(modified);
	}

	@Override
	public InputStream cropImageFile(InputStream stream, int x1, int y1, int x2, int y2) throws IOException, ImageUtilitiesException {
		String original = FileUtilities.generateTemporaryFileName();
		String modified = FileUtilities.generateTemporaryFileName();

		IOUtils.copy(stream, new FileOutputStream(original));
		ImageUtilities.cropImage(original, modified, x1, y1, x2, y2);

		FileUtils.deleteQuietly(new File(original));
		return new DeletableFileInputStream(modified);
	}

	@Override
	public InputStream orientImageFile(InputStream stream) throws IOException, ImageUtilitiesException {
		String original = FileUtilities.generateTemporaryFileName();

		IOUtils.copy(stream, new FileOutputStream(original));
		ImageUtilities.orientImage(original);

		return new DeletableFileInputStream(original);
	}
}
