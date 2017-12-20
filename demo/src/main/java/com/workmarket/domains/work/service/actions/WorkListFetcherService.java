package com.workmarket.domains.work.service.actions;

import com.workmarket.helpers.ResponseBuilderBase;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;

import java.util.List;

public interface WorkListFetcherService {
	List<String> fetchWorkNumbers(final Long userId, final List<String> workNumbers);
	<T extends ResponseBuilderBase> List<Work> fetchValidatedWork(final User user, final List<String> workNumbers,T response, String messageKey);
	<T extends ResponseBuilderBase> List<Work> fetchWork(List<String> workNumbers, T response, String messageKey);
}
