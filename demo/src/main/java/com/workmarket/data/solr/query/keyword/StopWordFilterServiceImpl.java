package com.workmarket.data.solr.query.keyword;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static java.util.Arrays.asList;
import static java.util.Collections.binarySearch;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class StopWordFilterServiceImpl implements StopWordFilterService {

	private final List<String> stopWords;

	@Autowired
	public StopWordFilterServiceImpl(@Value(value = "${search.service.stopword.file}") String stopwordFile) throws IOException {
		Scanner scanner = null;
		try {
			checkNotNull(stopwordFile);
			ClassPathResource resource = new ClassPathResource(stopwordFile);
			scanner = new Scanner(resource.getInputStream());
			ArrayList<String> stopwordList = newArrayListWithExpectedSize(200);
			while (scanner.hasNext()) {
				stopwordList.add(scanner.next().toLowerCase().trim());
			}
			Collections.sort(stopwordList);
			stopWords = stopwordList;
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private boolean isStopWord(String possibleStopword) {
		return possibleStopword != null && binarySearch(stopWords, possibleStopword.toLowerCase().trim()) >= 0;
	}

	private List<String> filterStopWords(List<String> wordList) {
		if (isEmpty(wordList)) {
			return emptyList();
		}
		List<String> response = Lists.newArrayListWithExpectedSize(wordList.size());
		for (String possibleStopword : wordList) {
			if (!isStopWord(possibleStopword)) {
				response.add(possibleStopword);
			}
		}
		return response;
	}

	@Override
	public List<String> filterStopWords(String words) {
		if (isBlank(words)) {
			return emptyList();
		}
		String[] wordArray = words.split("\\s+");
		return filterStopWords(asList(wordArray));
	}

}
