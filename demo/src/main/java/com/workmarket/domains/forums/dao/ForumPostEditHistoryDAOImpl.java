package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.forums.model.ForumPostEditHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ForumPostEditHistoryDAOImpl extends DeletableAbstractDAO<ForumPostEditHistory> implements ForumPostEditHistoryDAO {
	private static final Log logger = LogFactory.getLog(ForumPostEditHistoryDAOImpl.class);

	@Override
	protected Class<ForumPostEditHistory> getEntityClass() {
		return ForumPostEditHistory.class;
	}

}
