package com.workmarket.dao.integration.webhook;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.webhook.WebHook;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WebHookDAOImpl extends AbstractDAO<WebHook> implements WebHookDAO {

	@Override
	protected Class<?> getEntityClass() {
		return WebHook.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WebHook> findAllWebHooksByCompany(Long companyId) {
		return (List<WebHook>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("webHookClient", FetchMode.JOIN)
				.createAlias("webHookClient", "client")
				.setFetchMode("integrationEventType", FetchMode.JOIN)
				.createAlias("integrationEventType", "type")
				.add(Restrictions.eq("client.company.id", companyId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Property.forName("type.title").asc())
				.addOrder(Property.forName("callOrder").asc())
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WebHook> findAllWebHooksByCompanyAndClient(Long companyId, Long webHookClientId) {
		return (List<WebHook>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("webHookClient", FetchMode.JOIN)
				.createAlias("webHookClient", "client")
				.setFetchMode("integrationEventType", FetchMode.JOIN)
				.createAlias("integrationEventType", "type")
				.add(Restrictions.eq("client.id", webHookClientId))
				.add(Restrictions.eq("client.company.id", companyId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Property.forName("type.title").asc())
				.addOrder(Property.forName("callOrder").asc())
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WebHook> findAllEnabledWebHooksByCompany(Long companyId) {
		return (List<WebHook>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("webHookClient", FetchMode.JOIN)
				.createAlias("webHookClient", "client")
				.setFetchMode("integrationEventType", FetchMode.JOIN)
				.createAlias("integrationEventType", "type")
				.add(Restrictions.eq("client.company.id", companyId))
				.add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Property.forName("type.title").asc())
				.addOrder(Property.forName("callOrder").asc())
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	@Override
	public List<WebHook> findAllEnabledWebHooksByCompanyAndType(Long companyId, IntegrationEventType type) {
		return findAllEnabledWebHooksByCompanyAndType(companyId, type.getCode());
	}

	@SuppressWarnings("unchecked")
	@Override public List<WebHook> findAllEnabledWebHooksByCompanyAndType(Long companyId, String typeCode) {
		return (List<WebHook>) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("webHookClient", FetchMode.JOIN)
				.createAlias("webHookClient", "client")
				.createAlias("integrationEventType", "type")
				.add(Restrictions.eq("client.company.id", companyId))
				.add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("type.code", typeCode))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Property.forName("callOrder").asc())
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	@Override
	public Long findWebHookClientCompanyId(Long webHookId) {
		return (Long) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("webHookClient", "client")
				.add(Restrictions.eq("id", webHookId))
				.add(Restrictions.eq("enabled", true))
				.add(Restrictions.eq("deleted", false))
				.setProjection(Projections.property("client.company.id"))
				.uniqueResult();
	}
}
