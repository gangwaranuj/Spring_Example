package com.workmarket.service.web.cachebusting;

import java.util.Properties;

/**
 * Created by rahul on 4/1/14
 */
public interface CacheBusterService {

	void bustCache();

	String getMediaPrefix();
}
