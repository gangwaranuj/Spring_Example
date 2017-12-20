package com.workmarket.utility.image;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.util.Assert;

public class GraphicsMagickConverter {

	protected GraphicsMagickConverter() {}

	private static ConvertCmd cmd = null;

	public static ConvertCmd getInstance() {

		if (cmd == null) {
			cmd = new ConvertCmd(true);
		}
		return cmd;
	}

	public static IMOperation createResizeOperation(String sourceFilePath, String destinationFilePath, int scaledWidth, int scaledHeight) {
		Assert.hasText(sourceFilePath, "sourceFilePath can't be null");
		Assert.hasText(destinationFilePath, "destinationFilePath can't be null");
		Assert.isTrue(scaledWidth > 0, "Width for resize operation should be greater than 0");
		Assert.isTrue(scaledHeight > 0, "Height for resize operation should be greater than 0");

		IMOperation op = new IMOperation();
		op.addImage(sourceFilePath);
		op.autoOrient();
		op.resize(scaledWidth, scaledHeight);
		op.autoOrient();
		op.addImage(destinationFilePath);

		return op;
	}

	public static IMOperation createCropOperation(String sourceFilePath, String destinationFilePath, int x1, int y1, int x2, int y2) {
		Assert.hasText(sourceFilePath, "sourceFilePath can't be null");
		Assert.hasText(destinationFilePath, "destinationFilePath can't be null");

		int width = (x2 > x1 ?  x2 -x1 : (x1 > x2 ? x1 - x2 : 0));
		int height = (y2 > y1 ? y2 - y1 : (y1 > y2 ? y1 - y2 : 0));

		IMOperation op = new IMOperation();
		op.addImage(sourceFilePath);
		op.crop(width, height, x1, y1);
		op.addImage(destinationFilePath);

		return op;
	}

	public static IMOperation createOrientOperation(String sourceFilePath) {
		Assert.hasText(sourceFilePath, "sourceFilePath can't be null");

		IMOperation op = new IMOperation();
		op.addImage(sourceFilePath);
		op.autoOrient();
		op.addImage(sourceFilePath);

		return op;
	}
}
