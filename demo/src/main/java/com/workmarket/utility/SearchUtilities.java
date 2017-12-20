package com.workmarket.utility;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

import static org.apache.commons.lang.ArrayUtils.contains;
import static org.apache.commons.lang.StringUtils.isBlank;

public class SearchUtilities {

	private SearchUtilities() {}
	static final String ENCODE_STRING = "id__id";

	private static char[] solrEscapeChars = {'=', '+', '-', '&', '|', '!', '(', ')', '{', '}', '[', ']', '^',  '~', '?', ':', '\\', '\n', '\t', '/'};

	private static char[] solrRespectedChars = {'\\', '*', '"'};

	public static String encodeId(Long id) {
		if (id == null) {
			return ENCODE_STRING;
		}
		return " id_" + id + "_id ";
	}

	public static Long decodeId(String id) {
		if (StringUtils.isNotBlank(id)) {
			String idString = StringUtils.replace(StringUtils.replace(id, "id", ""), "_", "").trim();
			if (StringUtils.isNumeric(idString)) {
				return Long.valueOf(idString);
			}
		}
		return null;
	}

	public static String joinWithOR(Collection entities) {
		if (CollectionUtils.isEmpty(entities)) return StringUtils.EMPTY;
		return StringUtils.join(entities, " OR ");
	}

	public static String joinWithAND(Collection entities) {
		if (CollectionUtils.isEmpty(entities)) return StringUtils.EMPTY;
		return StringUtils.join(entities, " AND ");
	}

	public static String sanitizeKeywords(String keywords) {
		if (StringUtils.isBlank(keywords))
			return "";
		return keywords.replaceAll("[^a-zA-Z0-9\"]", " ");
	}

	public static String extractKeywords(String keywords, Integer maxLength) {
		if (StringUtils.isBlank(keywords)) {
			return StringUtils.EMPTY;
		}
		if (keywords.length() > maxLength) {
			return encode(StringUtils.substring(keywords, 0, maxLength - 1));
		}
		return encode(keywords);
	}

	public static String encode(String encodeMe) {
		if (isBlank(encodeMe)) {
			return StringUtils.EMPTY;
		}
		return strip(encodeMe);
	}

	private static String strip(String encodeMe) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		boolean stillStar = false;
		for (char charBit : encodeMe.toCharArray()) {
			//remove all beginning "*" chars
			if (i == 0 || stillStar) {
				if (charBit == '*') {
					stillStar = true;
					continue;
				} else {
					stillStar = false;
				}
			}
			if (!contains(solrEscapeChars, charBit)) {
				builder.append(charBit);
			} else if (contains(solrRespectedChars, charBit)) {
				// RESPECT THE SOLR!!!
				builder.append('\\').append(charBit);
			}
		}
		return builder.toString();
	}

	public static String escapeReservedWords(String reservedWord) {
		if (StringUtils.isNotBlank(reservedWord)) {
			return "\\" + reservedWord;
		}
		return reservedWord;
	}

}