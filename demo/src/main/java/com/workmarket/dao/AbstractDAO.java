package com.workmarket.dao;

import com.google.api.client.util.Maps;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.integration.autotask.util.StringUtil;
import com.workmarket.dto.SuggestionDTO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDAO<T> implements DAOInterface<T> {

	private static final Log logger = LogFactory.getLog(AbstractDAO.class);

	@Resource(name = "sessionFactory")
	private SessionFactory youShouldUseGetFactory; // don't use this directly, call getFactory() instead.

	@Value("${database.batch_size}")
	public int batchSize;

	private static ThreadLocal<Session> overrideSession = new ThreadLocal<>();

	public void saveOrUpdate(T entity) {
		Assert.notNull(entity);
		getFactory().getCurrentSession().saveOrUpdate(entity);
	}

	public static void setSession(final Session session) {
		overrideSession.set(session);
	}

	public T getOrInitializeBy(Object... objects) {
		Criteria criteria = createDynamicCriteria(objects);

		T entity = (T) criteria.uniqueResult();

		if (entity == null) {
			try {
				entity = (T) getEntityClass().newInstance();
				for (int i = 0; i < objects.length / 2; i++) {
					PropertyUtils.setProperty(entity, (String) objects[2 * i], objects[2 * i + 1]);
				}
			} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				logger.error(e);
			}
		}

		return entity;
	}

	public T findBy(Object... objects) {
		Criteria criteria = createDynamicCriteria(objects);
		return (T) criteria.setMaxResults(1).uniqueResult();
	}

	public List<T> findAllBy(Object... objects) {
		Criteria criteria = createDynamicCriteria(objects);
		return (List<T>) criteria.list();
	}

	@Override
	public boolean existsBy(Object... objects) {
		Criteria criteria = createDynamicCriteria(objects);
		criteria.setMaxResults(1);
		return criteria.uniqueResult() != null;
	}

	@Override
	public List<Long> findAllEntityIdsBy(Object... objects) {
		Criteria criteria = createDynamicCriteria(objects);
		criteria.setProjection(Projections.property("id"));
		return (List<Long>) criteria.list();
	}

	private Criteria createDynamicCriteria(Object[] objects) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass());
		for (int i = 0; i < objects.length / 2; i++) {
			if (objects[2 * i + 1] == null) {
				criteria.add(Restrictions.isNull((String)objects[2 * i]));
			} else {
				criteria.add(Restrictions.eq((String) objects[2 * i], objects[2 * i + 1]));
			}
		}
		return criteria;
	}

	@Override
	public List<Long> getAllIds() {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.setProjection(Projections.property("id"));
		return criteria.list();
	}

	@Override
	public Map<String, Object> getProjectionMapById(Long id, String... properties) {
		Map<String, Object> result = Maps.newHashMap();

		if (id == null) {
			return result;
		}

		result = getProjectionMapByIds(Lists.newArrayList(id), properties).get(id);

		if (result == null) {
			result = Maps.newHashMap();
		}

		return result;
	}

	@Override
	public Map<Long, Map<String, Object>> getProjectionMapByIds(List<Long> ids, String... properties) {
		Assert.notEmpty(properties);

		if (CollectionUtils.isEmpty(ids)) {
			return com.google.common.collect.Maps.newHashMap(); // return empty
		}

		List<String> props = new ArrayList<>(Arrays.asList(properties));

		// Add id by default as the first element
		props.remove("id");
		props.add(0, "id");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("id", ids));

		setProjections(criteria, props);

		List values = criteria.list();
		Map<Long, Map<String, Object>> result = Maps.newHashMap();

		if (CollectionUtils.isEmpty(values)) {
			return result;
		}

		for (Long ident : ids) {
			if (ident == null) {
				continue;
			}

			final Long id = ident;
			Map<String, Object> propMap = Maps.newHashMap();
			Object vls = Iterables.find(values, new Predicate() {
				@Override
				public boolean apply(@Nullable Object o) {
					if (o instanceof Object[]) {
						Object[] oArray = (Object[])o;
						return oArray[0].equals(id);
					} else {
						return o.equals(id);
					}
				}
			});

			if (props.size() == 1) {
				propMap.put(props.get(0), vls);
			} else {
				Object[] vs = (Object[]) vls;
				for (int j = 0; j < vs.length; j++) {
					propMap.put(props.get(j), vs[j]);
				}
			}

			result.put(id, propMap);
		}

		return result;
	}

	protected void setProjections(Criteria criteria, List<String> properties) {
		ProjectionList list = Projections.projectionList();

		for (String prop : properties) {
			if (!StringUtil.isNullOrEmpty(prop)) {
				list.add(Projections.property(prop), prop);
			}
		}

		criteria.setProjection(list);
	}

	@SuppressWarnings("unchecked")
	public T get(Long primaryKey) {
		if (primaryKey == null) {
			return null;
		}

		return (T) getFactory().getCurrentSession().get(getEntityClass(), primaryKey);
	}

	@SuppressWarnings("unchecked")
	public List<T> get(Long... primaryKeys) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("id", primaryKeys))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> get(Collection<Long> primaryKeys) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.in("id", primaryKeys))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return getFactory().getCurrentSession().createCriteria(getEntityClass()).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(int start, int limit) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFirstResult(start)
				.setMaxResults(limit)
				.list();
	}

	public void initialize(T entity) {
		Hibernate.initialize(entity);
	}

	public void initialize(Collection<? extends T> collection) {
		Hibernate.initialize(collection);
	}

	protected abstract Class<?> getEntityClass();

	public SessionFactory getFactory() {
		if (overrideSession.get() != null) {
			return new DelegatingSessionFactory(youShouldUseGetFactory, overrideSession.get());
		}
		return youShouldUseGetFactory;
	}

	public void setFactory(SessionFactory factory) {
		this.youShouldUseGetFactory = factory;
	}

	public void persist(T t) {
		getFactory().getCurrentSession().persist(t);
	}

	public void refresh(T t) {
		getFactory().getCurrentSession().refresh(t);
	}

	// TODO review anybody calling deletes
	public void delete(T entity) {
		getFactory().getCurrentSession().delete(entity);
	}

	//BE SUPER CAREFUL!! when you use this.
	public void delete(Set<T> entities) {
		for (T t : entities) {
			delete(t);
		}
	}

	@SuppressWarnings("unchecked")
	public List<SuggestionDTO> suggest(String prefix, String property) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.ilike(property, prefix, MatchMode.START))
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.setMaxResults(10)
				.setProjection(Projections.projectionList()
						.add(Projections.property("id"), "id")
						.add(Projections.property(property), "value")
				)
				.setResultTransformer(Transformers.aliasToBean(SuggestionDTO.class))
				.list();
	}

	public void saveAll(Collection<T> entities) {
		int i = 0;
		for (T entity : entities) {
			getFactory().getCurrentSession().save(entity);
			i++;
			if (i % batchSize == 0) {
				getFactory().getCurrentSession().flush();
			}
		}
	}
}
