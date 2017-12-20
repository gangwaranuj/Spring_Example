package com.workmarket.domains.work.facade.service;

import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.thrift.work.WorkResponse;

import java.util.List;

/**
 * Provides a single interface of top-level operations to be delegated to the various work-related services underneath
 */
public interface WorkFacadeService {
	Work saveOrUpdateWork(Long userId, WorkDTO workDTO);

	List<ConstraintViolation> cancelWork(CancelWorkDTO cancelWorkDTO);
	List<ConstraintViolation> voidWork(long workId, String message);
}
