package com.workmarket.data.solr.query.keyword;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.repository.UserSearchableFields;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.SearchUser;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.SearchUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static com.workmarket.utility.EmailUtilities.containsEmail;
import static com.workmarket.utility.EmailUtilities.findEmail;

@Component
public class SearchKeywordServiceImpl implements SearchKeywordService {

	@Value(value = "${people.search.keyword.maxlength}")
	private Integer maximumKeywordLength;
	private static final String QUOTE = "\"";
	private static final String UNDERSCORE = "_";
	static final String FULL_NAME_BOOST = "^10";
	private static final String WARP_KEYWORD = "warp_";

	@Override
	public String addKeywordQueryString(String keywordInputString, SolrQuery query, SearchUser currentUser, SearchType searchType) {
		Assert.notNull(currentUser);
		Assert.notNull(currentUser.getCompanyId());
		Assert.notNull(query);
		if (StringUtils.isBlank(keywordInputString)) {
			return null;
		}

		if(keywordInputString.startsWith(WARP_KEYWORD)){
			query.addFilterQuery(UserSearchableFields.WARP_REQUISITION_ID.getName() + ":" + keywordInputString.replaceFirst(WARP_KEYWORD, ""));
			return null;
		}

		List<String> keywords = Lists.newArrayList(StringUtils.split(keywordInputString));

		if (containsEmail(keywords)) {
			String email = findEmail(keywords);
			query.setQuery(email);
			query.add("qf", UserSearchableFields.EMAIL.getName());
			return null;
		}
		if (containsUserNumber(keywords)) {
			String userNumber = keywords.get(0);
			query.setQuery(userNumber);
			query.add("qf", UserSearchableFields.USER_NUMBER.getName());
			return null;
		}

		if (SearchType.PEOPLE_SEARCH_ASSIGNMENT_FULL_NAME.equals(searchType)) {
			query.setQuery(String.format("%s:%s", UserSearchableFields.FULL_NAME.getName(), SearchUtilities.extractKeywords(keywordInputString, maximumKeywordLength)));
			query.add("qf", UserSearchableFields.FULL_NAME.getName() + FULL_NAME_BOOST);
			return null;
		}

		//Private tags have the company id as prefix
		List<String> extendedKeywords = Lists.newArrayList();
		if (StringUtilities.isSurroundedBy(keywordInputString, QUOTE)) {
			extendedKeywords.add(QUOTE + currentUser.getCompanyId() + UNDERSCORE + StringUtilities.extractSurroundedString(keywordInputString, QUOTE) + QUOTE);
		} else {
			for (String kw : keywords) {
				extendedKeywords.add(currentUser.getCompanyId() + UNDERSCORE + kw);
			}
		}

		// The edismax query handler expects all keywords to exist
		// in at least one of the fields mentioned in the qf parameter.
		query.setQuery(String.format("%s %s", SearchUtilities.extractKeywords(keywordInputString, maximumKeywordLength), Joiner.on(" ").skipNulls().join(extendedKeywords)));
		return query.getQuery();

	}

	private static boolean containsUserNumber(List<String> keywords) {
		if (keywords.size() != 1) {
			return false;
		}
		String keyword = keywords.get(0);
		return StringUtils.isNumeric(keyword) && keyword.length() <= Constants.USER_NUMBER_IDENTIFIER_LENGTH;
	}

}
