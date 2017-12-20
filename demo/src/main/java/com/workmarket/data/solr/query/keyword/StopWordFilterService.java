package com.workmarket.data.solr.query.keyword;

import java.util.List;

public interface StopWordFilterService {

	List<String> filterStopWords(String words);

}