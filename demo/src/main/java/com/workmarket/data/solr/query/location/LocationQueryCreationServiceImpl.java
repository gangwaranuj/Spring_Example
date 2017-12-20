package com.workmarket.data.solr.query.location;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Point;
import com.workmarket.data.solr.configuration.BoostConfiguration;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.search.SearchWarning;
import com.workmarket.search.SearchWarningType;
import com.workmarket.search.cache.StateLookupCache;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.request.user.Constants;
import com.workmarket.service.external.GeocodingException;
import com.workmarket.service.infra.business.GeocodingService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.workmarket.utility.NumberUtilities.milesToKilometers;
import static org.apache.commons.lang.StringUtils.trim;
import static org.apache.solr.common.params.SolrParams.toSolrParams;

@Component
public class LocationQueryCreationServiceImpl implements LocationQueryCreationService {

	private static final Log logger = LogFactory.getLog(LocationQueryCreationServiceImpl.class);
	private final String BOOST_FUNCTION_TEMPLATE;

	@Autowired private GeocodingService geocodingService;
	@Autowired private InvariantDataService invariantDataService;

	private final BoostConfiguration boostConfig;
	private final StateLookupCache stateLookupCache;

	@Autowired
	public LocationQueryCreationServiceImpl(BoostConfiguration boostConfig, StateLookupCache stateLookupCache) {
		this.boostConfig = boostConfig;
		this.stateLookupCache = stateLookupCache;
		stateLookupCache.populateMap();
		BOOST_FUNCTION_TEMPLATE = "recip(geodist(),2,200,20)^ recip(map(geodist(),%d,500000,50000),2,200,200)^" + this.boostConfig.getDistanceBoost();
	}

	@Override
	public String addLocationQuery(AbstractSearchTransientData data, SolrQuery query) throws GeocodingException {
		SearchRequest request = data.getOriginalRequest();
		if (!request.isSetLocationFilter() || !request.getLocationFilter().isSetWillingToTravelTo()) {
			return StringUtils.EMPTY;
		}

		String willingToTravelTo = request.getLocationFilter().getWillingToTravelTo();
		NamedList<String> queryParameterlist = new NamedList<>();

		if (stateLookupCache.isStateQuery(willingToTravelTo)) {
			String stateKeyword = stateLookupCache.getStateCode(willingToTravelTo);
			query.add("fq", UserSearchableFields.STATE.getName() + ":" + SearchUtilities.escapeReservedWords(stateKeyword));
			return StringUtils.EMPTY;
		}

		GeoPoint point = getGeoLocationPoint(request);
		data.setGeopoint(point);

		if (point == null) {
			logger.warn("search request didn't resolve a geopoint " + request);
			return StringUtils.EMPTY;
		}

		// geo location required fields
		queryParameterlist.add("pt", point.getLatitude() + "," + point.getLongitude());
		queryParameterlist.add("sfield", UserSearchableFields.LOCATION.getName());

		int maxMiles = findMaxMiles(request);
		boolean filterByDistance = (!request.getLocationFilter().isAnywhere());

		// this is now a filter, as well as a relevancy algorithm
		if (filterByDistance) {
			query.addFilterQuery("{!geofilt}");
			queryParameterlist.add("d", String.valueOf(milesToKilometers(maxMiles)));
		}
		query.add(toSolrParams(queryParameterlist));

		/**
		 * The closer the user, the higher the boost.
		 * The distance query will get boosted by the given radius
		 */
		return getBoostFunction(maxMiles);
	}

	@Override
	public SearchWarning createGeoCodingWarning(SearchRequest request, GeocodingException e) {
		if (e.getErrorType() != null) {
			SearchWarning warning = new SearchWarning();
			switch (e.getErrorType()) {
				case INVALID_REQUEST:
					warning.setSearchWarning(SearchWarningType.LOCATION_INVALID_REQUEST);
					logger.error("Location invalid for geocode lookup: " + request);
					break;
				case OVER_QUERY_LIMIT:
					warning.setSearchWarning(SearchWarningType.LOCATION_OVER_QUERY_LIMIT);
					logger.error("Over query limit geocode lookup: " + request);
					break;
				case REQUEST_DENIED:
					logger.error("Request denied from geocode lookup: " + request);
					warning.setSearchWarning(SearchWarningType.LOCATION_REQUEST_DENIED);
					break;
				case ZERO_RESULTS:
					warning.setSearchWarning(SearchWarningType.LOCATION_ZERO_RESULTS);
					break;
			}
			warning.setWarningMessage("Exception thrown: " + e.getMessage());
			return warning;
		}
		return null;
	}

	@Override
	public int findMaxMiles(SearchRequest request) {
		if (request.isSetLocationFilter() && request.getLocationFilter().isSetMaxMileFromResourceToLocation()) {
			return request.getLocationFilter().getMaxMileFromResourceToLocation();
		}
		return Constants.MAX_MILES;
	}

	@Override
	public GeoPoint getGeoLocationPoint(SearchRequest request) throws GeocodingException {
		// Set lat/lng have precedence
		if (request.getLatitude() != null && request.getLongitude() != null) {
			return new GeoPoint(request.getLatitude(), request.getLongitude());
		} else if (request.getLocationFilter() != null) {
			// we have a location set.. look for zip code
			return getGeoLocationPoint(request.getLocationFilter().getWillingToTravelTo());
		} else {
			return null;
		}
	}

	@Override
	public GeoPoint getGeoLocationPoint(final String address) throws GeocodingException {

		if (StringUtils.isBlank(address)) {
			return null;
		}
		PostalCode postalCode = PostalCode.fromString(trim(address));
		if (postalCode != null) {
			Country country = postalCode.getCountry();
			postalCode = invariantDataService.getPostalCodeByCodeAndCounry(postalCode.getPostalCode(), country);
			if (postalCode != null) {
				return new GeoPoint(postalCode.getLatitude(), postalCode.getLongitude());
			}
		}
		Point point = geocodingService.geocode(address);
		if (point != null) {
			return new GeoPoint(point.getY(), point.getX());
		}
		return null;
	}

	@Override
	public void setGeoCodingWarning(AbstractSearchTransientData data, GeocodingException e, SearchQuery query) {
		SearchWarning warning = createGeoCodingWarning(data.getOriginalRequest(), e);
		List<SearchWarning> searchWarnings = query.getSearchWarnings();
		if (searchWarnings == null) {
			searchWarnings = Lists.newArrayList(warning);
		}
		query.setSearchWarnings(searchWarnings);
	}

	@Override
	public String getBoostFunction(Integer maxMiles) {
		return String.format(BOOST_FUNCTION_TEMPLATE, maxMiles);
	}
}
