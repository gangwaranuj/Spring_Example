package com.workmarket.api;

/**
 * Created by joshlevine on 12/23/16.
 */
public interface ApiBaseResponseMeta {
	/**
	 * ApiV0ResponseMeta is just a map at the end of the day.  It should support get.
	 * @param key
	 * @return
	 */
	Object get(String key);
}
