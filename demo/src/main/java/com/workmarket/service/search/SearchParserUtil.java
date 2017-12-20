package com.workmarket.service.search;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class SearchParserUtil {

	private static final Logger logger = LoggerFactory.getLogger(SearchParserUtil.class);

	private SearchParserUtil() {}

	public static Set<Long> parseLongSetFromSolr(String fieldName, SolrDocument doc) {
		Collection<Object> collection = doc.getFieldValues(fieldName);
		if (isNotEmpty(collection)) {
			return Sets.newHashSet(Iterables.filter(collection, Long.class));
		}
		return Collections.emptySet();
	}

	public static Set<Long> parseStringSetToLongSetFromSolr(String fieldName, SolrDocument doc) {
		Collection<Object> collection = doc.getFieldValues(fieldName);
		Set<Long> longSet = Sets.newHashSet();
		if (isNotEmpty(collection)) {
			for (Object string : collection) {
				try {
					longSet.add(Long.parseLong((String) string));
				} catch (NumberFormatException | ClassCastException ex) {
					logger.error("Error parsing String set from solr", ex);
				}
			}
			return longSet;
		}
		return Collections.emptySet();
	}

	public static Set<Integer> parseIntegerSetFromSolr(String fieldName, SolrDocument doc) {
		Collection<Object> collection = doc.getFieldValues(fieldName);
		if (isNotEmpty(collection)) {
			return Sets.newHashSet(Iterables.filter(collection, Integer.class));
		}
		return Collections.emptySet();
	}

	public static Long[] parseLongArrayFromSolr(String fieldName, SolrDocument doc) {
		Collection<Object> collection = doc.getFieldValues(fieldName);
		if (isNotEmpty(collection)) {
			return collection.toArray(new Long[collection.size()]);
		}
		return new Long[0];
	}
}
