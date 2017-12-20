package com.workmarket.dao.integration.webhook;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.webhook.WebHookHeader;
import org.springframework.stereotype.Repository;

@Repository
public class WebHookHeaderDAOImpl extends AbstractDAO<WebHookHeader> implements WebHookHeaderDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WebHookHeader.class;
	}

}
