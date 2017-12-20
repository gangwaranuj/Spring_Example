package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class SelectServiceImpl implements SelectService {

	@Autowired SearchService searchService;
	@Autowired WorkSearchRequestRepository workSearchRequestRepository;

	private static final int MAX_SELECT_SIZE=10000;

	@NotNull
	public List<String> fetchAllWorkBySearchFilter(Long userId){

		Optional<WorkSearchRequest> requestOption = workSearchRequestRepository.get(userId);
		if(!requestOption.isPresent()){
			return Lists.newArrayList();
		}
		WorkSearchRequest request = requestOption.get();
		request.setPageSize(MAX_SELECT_SIZE);
		WorkSearchResponse response = searchService.searchAllWork(userId,request);
		if(response == null){
			return Lists.newArrayList();
		}

		List<String> resultWorkNumbers = Lists.newArrayList();
		for(SolrWorkData workData:response.getResults()){
			if(workData == null) continue;
			resultWorkNumbers.add(workData.getWorkNumber());
		}

		return resultWorkNumbers;
	}
}
