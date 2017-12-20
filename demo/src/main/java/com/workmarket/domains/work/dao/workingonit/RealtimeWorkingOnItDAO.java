package com.workmarket.domains.work.dao.workingonit;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.RealtimeWorkingOnIt;

public interface RealtimeWorkingOnItDAO extends DAOInterface<RealtimeWorkingOnIt> {

	RealtimeWorkingOnIt findOpenedByWorkId(Long workId);

}
