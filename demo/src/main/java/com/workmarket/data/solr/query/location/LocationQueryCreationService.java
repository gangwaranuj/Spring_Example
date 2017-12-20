package com.workmarket.data.solr.query.location;

import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.search.SearchWarning;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.service.external.GeocodingException;
import org.apache.solr.client.solrj.SolrQuery;

public interface LocationQueryCreationService {

	SearchWarning createGeoCodingWarning(SearchRequest request, GeocodingException e);

	void setGeoCodingWarning(AbstractSearchTransientData data, GeocodingException e, SearchQuery query);

	String addLocationQuery(AbstractSearchTransientData data, SolrQuery solrQuery) throws GeocodingException;

	GeoPoint getGeoLocationPoint(SearchRequest request) throws GeocodingException;

	GeoPoint getGeoLocationPoint(String address) throws GeocodingException;

	int findMaxMiles(SearchRequest request);

	String getBoostFunction(Integer maxMiles);
}
