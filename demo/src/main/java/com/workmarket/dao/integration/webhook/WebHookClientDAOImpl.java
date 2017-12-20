package com.workmarket.dao.integration.webhook;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.webhook.AbstractWebHookClient;
import com.workmarket.domains.model.integration.webhook.GenericWebHookClient;
import com.workmarket.domains.model.integration.webhook.SalesforceWebHookClient;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class WebHookClientDAOImpl extends AbstractDAO<AbstractWebHookClient> implements WebHookClientDAO {

	@Override
	protected Class<?> getEntityClass() {
		return AbstractWebHookClient.class;
	}

	@SuppressWarnings("unchecked")
	@Override public Optional<SalesforceWebHookClient> findSalesforceWebHookClientByCompany(Long companyId) {
		return Optional.fromNullable(findWebHookClientByCompanyAndType(companyId, SalesforceWebHookClient.class));
	}

	@Override
	public Optional<GenericWebHookClient> findGenericWebHookClientByCompany(Long companyId) {
		return Optional.fromNullable(findWebHookClientByCompanyAndType(companyId, GenericWebHookClient.class));
	}

	public Optional<AbstractWebHookClient> findWebHookClientById(Long id) {
		return Optional.fromNullable(get(id));
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractWebHookClient> T findWebHookClientByCompanyAndType(Long companyId, Class<T> type) {
		return (T) getFactory().getCurrentSession().createCriteria(type)
				.add(Restrictions.eq("company.id", companyId))
				.uniqueResult();
	}
}
