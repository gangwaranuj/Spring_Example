package com.workmarket.utility;

import com.workmarket.utility.image.GraphicsMagickConverter;
import org.im4java.core.IM4JavaException;

import java.io.IOException;

public class ImageUtilities {

	private ImageUtilities() {}

	public static void scaleImage(String sourceFilePath, String destinationFilePath, int scaledWidth, int scaledHeight) throws IOException, ImageUtilitiesException {
		try {
			GraphicsMagickConverter.getInstance().run(GraphicsMagickConverter.createResizeOperation(sourceFilePath, destinationFilePath, scaledWidth, scaledHeight));
		} catch (InterruptedException e) {
			throw new ImageUtilitiesException("Couldn't perform resize operation with GraphicsMagick", e);
		} catch (IM4JavaException e) {
			throw new ImageUtilitiesException("Couldn't perform resize operation with GraphicsMagick", e);
		}
	}

	public static void cropImage(String sourceFilePath, String destinationFilePath, int x1, int y1, int x2, int y2) throws IOException, ImageUtilitiesException {

		try {
			GraphicsMagickConverter.getInstance().run(GraphicsMagickConverter.createCropOperation(sourceFilePath, destinationFilePath, x1, y1, x2, y2));
		} catch (InterruptedException e) {
			throw new ImageUtilitiesException("Couldn't perform crop operation with GraphicsMagick", e);
		} catch (IM4JavaException e) {
			throw new ImageUtilitiesException("Couldn't perform crop operation with GraphicsMagick", e);
		}
	}

	public static void orientImage(String sourceFilePath) throws IOException, ImageUtilitiesException {
		try {
			GraphicsMagickConverter.getInstance().run(GraphicsMagickConverter.createOrientOperation(sourceFilePath));
		} catch (InterruptedException e) {
			throw new ImageUtilitiesException("Couldn't perform resize operation with GraphicsMagick", e);
		} catch (IM4JavaException e) {
			throw new ImageUtilitiesException("Couldn't perform resize operation with GraphicsMagick", e);
		}
	}
}
