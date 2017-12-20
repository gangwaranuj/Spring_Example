package com.workmarket.utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MimeTypeUtilities {

	private static final Log logger = LogFactory.getLog(MimeTypeUtilities.class);

	private static final Map<String, Set<String>> mimeTypeToExtensionMapping;
	private static final Map<String, List<String>> extensionToMimeTypeMapping;
	private static final Map<String, List<String>> validMimeIconMap;
	private static final Map<String, List<String>> pageFiletypeMap;

	private final static Set<String> IMAGE_MIME_TYPES = ImmutableSet.of(
			"image/gif",
			"image/jpeg",
			"image/png",
			"image/bmp",
			"image/tiff",
			"image/pjpeg" // IE8 sends this - it seems to be a valid type
	);

	private final static Set<String> MEDIA_MIME_TYPES = ImmutableSet.of(
			"video/x-flv",
			"application/mp4",
			"video/mp4",
			"video/x-sgi-movie",
			"video/quicktime",
			"audio/mpeg"
	);

	static {
		extensionToMimeTypeMapping = Maps.newHashMap();
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("mimes.properties");
			for (String mime : props.stringPropertyNames()) {
				extensionToMimeTypeMapping.put(mime, Lists.newArrayList(StringUtils.split(props.getProperty(mime), ", ")));
			}
		} catch (IOException e) {
			logger.error("Failed to load mime types", e);
		}

		validMimeIconMap = Maps.newHashMap();
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("mime-groups.properties");
			for (String mime : props.stringPropertyNames()) {
				validMimeIconMap.put(mime, getMimeTypesByExtension(StringUtils.split(props.getProperty(mime), ",")));
			}
		} catch (IOException e) {
			logger.error("Failed to load mime groups", e);
		}

		mimeTypeToExtensionMapping = Maps.newHashMap();
		for (Map.Entry<String, List<String>> entry : extensionToMimeTypeMapping.entrySet())
			for (String mimeType : entry.getValue()) {
				if (!mimeTypeToExtensionMapping.containsKey(mimeType))
					mimeTypeToExtensionMapping.put(mimeType, Sets.<String>newHashSet());
				mimeTypeToExtensionMapping.get(mimeType).add(entry.getKey());
			}

		pageFiletypeMap = Maps.newHashMap();
		try {
			Properties props = PropertiesLoaderUtils.loadAllProperties("upload-page-mapping-types.properties");
			Properties groups = PropertiesLoaderUtils.loadAllProperties("mime-groups.properties");
			for (String page : props.stringPropertyNames()) {
				List types = Lists.newArrayList();
				for(String m : props.getProperty(page).split(",") ) {
					types.addAll(Lists.newArrayList(groups.getProperty(m).split(",")));
				}
				pageFiletypeMap.put(page, types);
			}
		} catch (IOException e) {
			logger.error("Failed to load page type mappings", e);
		}
	}



	public static boolean isImage(String mimeType) {
		return IMAGE_MIME_TYPES.contains(mimeType);
	}

	public static boolean isMedia(String mimeType) {
		return MEDIA_MIME_TYPES.contains(mimeType);
	}


	public static List<String> getMimeTypesByExtension(Collection<String> exts) {
		return getMimeTypesByExtension(exts.toArray(new String[exts.size()]));
	}

	public static List<String> getMimeTypesByExtension(String... exts) {
		List<String> types = Lists.newArrayList();
		for (String e : exts) {
			types.addAll(extensionToMimeTypeMapping.get(e.trim()));
		}
		return types;
	}

	public static Set<String> getMimeTypeExtensions(Collection<String> mimeTypes) {
		return getMimeTypeExtensions(mimeTypes.toArray(new String[mimeTypes.size()]));
    }

	public static Set<String> getMimeTypeExtensions(String... mimeTypes) {
		Set<String> ext = Sets.newHashSet();
		for (String m : mimeTypes)
			ext.addAll(mimeTypeToExtensionMapping.get(m));
		return ext;
	}

	public static String getMimeIconName(String mimeType) {
		for (String key : validMimeIconMap.keySet()) {
			if (validMimeIconMap.get(key).contains(mimeType))
				return key;
		}
		return "misc";
	}

	public static Set<String> getMimeTypesForPage(String page) {
		Set<String> types = Sets.newHashSet();
		String singlePage = page.replaceAll("/[0-9]+", ""); //for pesky edit campaign pages with ids
		for (String key : pageFiletypeMap.keySet()) {
			if (key.equals(singlePage)) {
				types.addAll(pageFiletypeMap.get(key));
			}
		}
		return types;
	}


	/**
	 * Guesses the MIME type of a filename in two different ways. This is because the URLConnection utility is not able to guess
	 * a few extension types like Office.
	 *
	 * @param filename
	 * @return
	 */
	public static String guessMimeType(String filename) {
		String guess = null;
		try {
			guess = URLConnection.guessContentTypeFromName(filename);
		} catch (Exception e) {
			logger.debug("Error guessing mime type", e);
		}
		
		if (StringUtils.isNotEmpty(guess))
			return guess;

		String ext = StringUtilities.getFileExtension(filename);
		if (StringUtils.isNotEmpty(ext)) {
			ext = ext.toLowerCase();
			List<String> exts = extensionToMimeTypeMapping.get(ext);
			if (exts != null)
				return exts.get(0); // this assumes they are ordered according to preference in the properties file
		}

		return "";
	}
}