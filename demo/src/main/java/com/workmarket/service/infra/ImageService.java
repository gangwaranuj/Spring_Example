package com.workmarket.service.infra;

import com.workmarket.utility.ImageUtilitiesException;

import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
	void resizeImageFile(String sourceFilePath, String destinationFilePath, int scaledWidth, int scaledHeight) throws Exception;
	void cropImageFile(String sourceFilePath, String destinationFilePath, int x1, int y1, int x2, int y2) throws Exception;

	InputStream resizeImageFile(InputStream stream, int width, int height) throws IOException, ImageUtilitiesException;
	InputStream cropImageFile(InputStream stream, int x1, int y1, int x2, int y2) throws IOException, ImageUtilitiesException;
	InputStream orientImageFile(InputStream stream) throws IOException, ImageUtilitiesException;
}
