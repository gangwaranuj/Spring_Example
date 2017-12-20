package com.workmarket.web.helpers;

import com.google.common.collect.Sets;
import com.workmarket.utility.MimeTypeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class UploadHelper {
	private static final Log logger = LogFactory.getLog(UploadHelper.class);

	private final static Set<String> validMimeTypes;

	static {
		validMimeTypes = Sets.newHashSet();
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("valid-upload-mimes.properties");
			validMimeTypes.addAll(MimeTypeUtilities.getMimeTypesByExtension(props.stringPropertyNames()));
		} catch (IOException e) {
			logger.error("Failed to load mime types", e);
		}
	}

	public static Set<String> getValidMimeTypes() {
		return validMimeTypes;
	}
	
	public static Set<String> getValidMimeTypeExtensions() {
		return MimeTypeUtilities.getMimeTypeExtensions(validMimeTypes);
	}
	
	public static boolean isValidMimeType(String mimeType) {
		return validMimeTypes.contains(mimeType);
	}
	
	public static String getMimeTypeIcon(String mimeType) {
		return MimeTypeUtilities.getMimeIconName(mimeType);
	}
}
