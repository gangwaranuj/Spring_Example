package com.workmarket.search.cache;

public interface StateLookupCache {

	public boolean isStateQuery(String keywords);

	public String getStateCode(String keywords);

	public void populateMap();
}
