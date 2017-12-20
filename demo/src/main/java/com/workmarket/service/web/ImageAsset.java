package com.workmarket.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: micah
 * Date: 9/25/13
 * Time: 7:58 AM
 */
public enum ImageAsset {
	AVAILABLE("images/assignments/stillavailable.png"),
	NOT_AVAILABLE("images/assignments/notavailable.png"),
	NOT_ALLOWED("images/assignments/cannotaccess.png"),
	BUNDLE_AVAILABLE("images/assignments/bundle_stillavailable.png"),
	BUNDLE_NOT_AVAILABLE("images/assignments/bundle_notavailable.png"),
	BUNDLE_NOT_ALLOWED("images/assignments/bundle_cannotaccess.png");

	private final String fileName;
	private byte[] imageData;

	private static final Logger logger = LoggerFactory.getLogger(ImageAsset.class);

	ImageAsset(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		if (imageData == null) { loadImage(); }
		return imageData;
	}

	public int size() {
		if (imageData == null) { loadImage(); }
		return imageData.length;
	}

	private void loadImage() {
		try {
			File f = new File(ClassUtils.getDefaultClassLoader().getResource(fileName).getFile());
			FileInputStream fi = new FileInputStream(f);
			imageData = new byte[(int) f.length()];
			fi.read(imageData);
			fi.close();
		} catch (IOException e) {
			logger.error("Could not read file", e);
		}
	}
}
