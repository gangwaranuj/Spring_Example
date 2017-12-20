package com.workmarket.service.web.cachebusting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Properties;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by rahul on 4/1/14
 */
@Service
@ManagedResource(objectName="bean:name=cachebuster", description="Manages cachebuster for front-end assets")
public class CacheBusterServiceImpl implements CacheBusterService {

	private static final Log logger = LogFactory.getLog(CacheBusterServiceImpl.class);

	private StringBuilder mediaPrefix = null;
	private final String CACHE_BUSTER_SOURCE = "git.properties";
	private final String CACHE_BUSTER_KEY = "git.commit.id.abbrev";
	@Value("${cdn.use.cachebuster}")
	private String useCacheBuster;
	@Value("${cdn.static.url.prefix}")
	private String cdnStaticUrlPrefix;

	// Initialize mediaPrefix when properties are available
	@PostConstruct
	public void init() {
		try {
			// Load the git file
			Properties properties = PropertiesLoaderUtils.loadAllProperties(CACHE_BUSTER_SOURCE);
			// Use the abbreviated commit id for cache busting
			String cacheBuster = properties.getProperty(CACHE_BUSTER_KEY);

			// Build mediaPrefix with CDN url and the cache busting value
			mediaPrefix = new StringBuilder(cdnStaticUrlPrefix);
			if (Boolean.valueOf(useCacheBuster) && isNotBlank(cacheBuster)) {
				mediaPrefix.append('/' + cacheBuster);
			}

			logger.info(String.format("CDN and cache busting values initialized with mediaPrefix: %s", mediaPrefix));
		} catch (IOException e) {
			logger.error("Failed initial load of cache buster", e);
		}
	}

	@ManagedOperation(description="Bust cache for front-end assets")
	@Override
	public void bustCache() {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties(CACHE_BUSTER_SOURCE);
			String cacheBuster = properties.getProperty(CACHE_BUSTER_KEY);

			// Reset and rebuild mediaPrefix
			mediaPrefix.setLength(0);
			mediaPrefix.append(cdnStaticUrlPrefix);
			if (Boolean.valueOf(useCacheBuster) && isNotBlank(cacheBuster)) {
				mediaPrefix.append('/' + cacheBuster);
			}

			logger.info(String.format("Cache busted and reinitialized with mediaPrefix: %s", mediaPrefix));
		} catch (IOException e) {
			logger.error("Failed to reload cache buster", e);
		}
	}

	@Override
	public String getMediaPrefix() {
		return mediaPrefix.toString();
	}
}
