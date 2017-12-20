package com.workmarket.data.solr.query;

import com.workmarket.search.request.user.NumericFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static com.workmarket.utility.SearchUtilities.joinWithOR;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Component
public class SearchQueryCreatorUtil {

	private SearchQueryCreatorUtil() {}

	public static void addFilterQueryStr(SolrQuery query, Field field, Collection<String> filters) {
		if (isEmpty(filters)) {
			return;
		}
		Set<String> strFilters = newHashSetWithExpectedSize(filters.size());
		for (String filterId : filters) {
			strFilters.add(field.getName() + ":" + filterId);
		}
		query.addFilterQuery("{!tag=" + field.getName() + "}" + joinWithOR(strFilters));
	}

	public static String createFilterQuery(Field field, Collection<Long> filters) {
		if (isEmpty(filters)) {
			return "";
		}
		List<String> strFilters = new ArrayList<>(filters.size());
		for (Long filterId : filters) {
			strFilters.add(field.getName() + ":" + filterId);
		}
		return "{!tag=" + field.getName() + "}" + joinWithOR(strFilters);
	}

	public static String createFacetLimitString(Field field) {
		if (field == null) {
			return StringUtils.EMPTY;
		}
		return "f." + field.getName() + ".facet.limit";
	}

	public static String createFacetSortString(Field field) {
		if (field == null) {
			return StringUtils.EMPTY;
		}
		return "f." + field.getName() + ".facet.sort";
	}

	public static String createFacetFieldString(Field field) {
		if (field == null) {
			return StringUtils.EMPTY;
		}
		return "{!ex=" + field.getName() + "}" + field.getName();
	}

	public static String createFacetFieldWithFacetPrefix(Field field, String prefix) {
		if (field == null) {
			return StringUtils.EMPTY;
		}
		return "{!ex=" + field.getName() + " key=" + field.getName() + "_" + prefix + " facet.prefix=" + prefix + "_" +"}" + field.getName();
	}

	public static String createInsuranceMinimumFilterQuery(String fieldName, NumericFilter numericFilter) {
		// If the insurance coverage amount is set to 0, return the set of users who don't have that insurance
		if (numericFilter.getFrom() == 0.0) {
			return "(-" + fieldName + ":*)";
		}
		String from = numericFilter.isSetFrom() ? "" + (long) numericFilter.getFrom() : "0";
		String to = numericFilter.isSetTo() ? "" + (long) numericFilter.getTo() : "*";
		return "(" + fieldName + ":[" + from + " TO " + to + "])";
	}

	public static String createFacetQueryWithSeparateExParam(String exParam, Field field, long value) {
		return "{!ex=" + exParam + "}" + field.getName() + ":" + value;
	}

}
