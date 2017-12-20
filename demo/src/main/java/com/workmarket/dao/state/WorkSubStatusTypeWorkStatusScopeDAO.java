package com.workmarket.dao.state;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeWorkStatusScope;

import java.util.List;

/**
 * Created by nick on 12/20/13 3:54 PM
 */
public interface WorkSubStatusTypeWorkStatusScopeDAO extends DAOInterface<WorkSubStatusTypeWorkStatusScope> {
	List<WorkSubStatusTypeWorkStatusScope> findAllBySubStatusId(long labelId);
}
