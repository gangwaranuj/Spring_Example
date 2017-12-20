package com.workmarket.service.search;

import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.workmarket.service.search.SearchResultConstants.STATIC_FACET_FIELD_NAMES;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
public abstract class SearchResultParserImpl {

	private static final Log logger = LogFactory.getLog(SearchResultParserImpl.class);

	protected static String[] parseStringArrayFromSolr(String fieldName, SolrDocument doc) {
		Collection<Object> collection = doc.getFieldValues(fieldName);
		if (CollectionUtils.isNotEmpty(collection)) {
			return collection.toArray(new String[collection.size()]);
		}
		return new String[0];
	}

	@Async
	protected void parseFacetResult(FacetField facetField, SearchResponse searchResponse, boolean includeZero) {
		Enum facetResultType = findFacetResultType(facetField);
		if (facetResultType == null) {
			logger.warn("Unidentified result type for facet.  Facet not parsed." + facetField);
			return;
		}
		List<FacetResult> facetResults = new ArrayList<>(facetField.getValueCount());
		for (Count value : facetField.getValues()) {
			if ((value.getCount() > 0 || includeZero) && !isBlank(value.getName())) {
				FacetResult result = new FacetResult().setFacetCount(value.getCount()).setFacetId(value.getName());
				facetResults.add(result);
			}
		}
		if (CollectionUtils.isNotEmpty(facetResults) || includeZero) {
			searchResponse.putToFacets(facetResultType, facetResults);
		}
	}

	protected abstract Enum findFacetResultType(FacetField facetField);

	protected boolean isStaticFacetFieldName(String fieldName) {
		return ArrayUtils.contains(STATIC_FACET_FIELD_NAMES, fieldName);
	}

}
