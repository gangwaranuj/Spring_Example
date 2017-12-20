package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;

import java.util.List;

public interface WorkBundleDAO extends DAOInterface<WorkBundle> {
	List<Work> findAllInByWorkNumbers(List<String> workNumbers);

	List<Work> findAllInByIds(List<Long> ids);

	WorkBundleDTO findByChildId(Long childId);

	@SuppressWarnings("unchecked")
	List<WorkBundleSuggestionDTO> suggest(String prefix, Long userId);

	boolean isAssignmentBundle(String workNumber);
}
