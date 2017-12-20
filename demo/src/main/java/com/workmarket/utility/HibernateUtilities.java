package com.workmarket.utility;

import com.google.common.collect.ImmutableList;
import com.workmarket.data.annotation.TrackChanges;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class HibernateUtilities {
	private static final Log logger = LogFactory.getLog(HibernateUtilities.class);

	private static final List<String> IRRELEVANT_PROPERTIES = ImmutableList.of("modifierId", "modifier", "modifiedOn", "version");

	private HibernateUtilities() {
	}

	@SuppressWarnings("unchecked")
    public static <T> List<T> listAndCast(Criteria q) {
	    @SuppressWarnings("rawtypes")
		List list = q.list();
	    return list;
	}

	/**
	 * Make sure this is the last call in the criteria chain
	 *
	 * @param criteria
	 * @return
	 */
	public static Long getRowCount(Criteria criteria) {
		Assert.notNull(criteria);
		criteria.setProjection(Projections.rowCount());
		@SuppressWarnings("rawtypes")
		List list = criteria.list();

		if (logger.isDebugEnabled()) {
			if (list == null) {
				logger.error("Criteria rowCount projection returned null list");
				Assert.isTrue(false, "Criteria rowCount projection returned null list");
			} else {
				if (list.size() == 0) {
					logger.error("Criteria rowCount projection returned 0 results");
					Assert.isTrue(false, "Criteria rowCount projection returned 0 results");
				}
			}
		}

		return (Long) criteria.list().get(0);
	}

	public static Criteria addRestrictionsEq(Criteria criteria, Object... restrictions) {
		Assert.notNull(criteria, "Criteria must not be null");
		Assert.state(restrictions.length > 0, "Restrictions are missing");

		int i = 0;
		while (i < restrictions.length) {
			Assert.state(restrictions[i] != null, "Property name must not be null");
			Assert.state(restrictions[i] instanceof String, "Property name must be a string");
			criteria.add(Restrictions.eq((String)restrictions[i++], restrictions[i++]));
		}
		return criteria;
	}

	public static Criteria setupPagination(Criteria criteria, Integer startRow, Integer limit, int maxRows) {
		Assert.notNull(criteria, "Criteria must not be null");
		Assert.notNull(startRow, "Start row cannot be null");

		if (limit == null) { limit = maxRows; }
		Assert.notNull(limit, "Result limit cannot be null");

		criteria.setFirstResult(startRow);
		criteria.setMaxResults(limit);

		return criteria;
	}

	public static boolean isRelevantPropertyChange(Object bean, String propertyName, Object oldValue, Object newValue) throws Exception {
		if (isBlank(propertyName)) {
			return false;
		}
		if (IRRELEVANT_PROPERTIES.contains(propertyName)) {
			return false;
		}

		if (propertyName.endsWith("OldValue") || propertyName.endsWith("ModifiedOn")) {
			return false;
		}

		if (oldValue != null && !Hibernate.isInitialized(oldValue)) {
			return false;
		}
		if (newValue != null && !Hibernate.isInitialized(newValue)) {
			return false;
		}
		if (oldValue == null && newValue == null) {
			return false;
		}
		if (oldValue != null && newValue != null && newValue.equals(oldValue)) {
			return false;
		}
		if (bean == null) {
			return true;
		}

		// We're going to use the TrackChanges annotation to specify fields that we care about
		// for reporting in the change log. The mere existence of the annotation means we care.

		PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(bean, propertyName);
		if (propertyDescriptor == null || propertyDescriptor.getWriteMethod() == null) {
			return false;
		}
		return propertyDescriptor.getWriteMethod().isAnnotationPresent(TrackChanges.class);
	}

	public static boolean hasRelevantPropertyChange(Object bean, String[] propertyNames, Object[] oldState, Object[] newState){
		for (int i = 0; i < propertyNames.length; i++) {
			if (oldState != null && newState != null) {
				Object oldValue = oldState[i];
				Object newValue = newState[i];
				String property = propertyNames[i];
				try {
					if (HibernateUtilities.isRelevantPropertyChange(bean, property, oldValue, newValue)) {
						return true;
					}
				} catch (Exception e) {
					logger.error("[hasRelevantPropertyChange]", e);
					return false;
				}
			}
		}
		return false;
	}

	public static Criteria addJoins(Criteria criteria, int joinType, String ... properties) {
		for (String p : properties) {
			criteria
				.setFetchMode(p, FetchMode.JOIN)
				.createAlias(p, p, joinType);
		}
		return criteria;
	}

	public static Criteria addSorts(Criteria criteria, List<String> properties, boolean isAscending) {
		return addSorts(criteria, isAscending, properties.toArray(new String[properties.size()]));
	}

	private static Criteria addSorts(Criteria criteria, boolean isAscending, String... properties) {
		if (isAscending) {
			for (String p : properties)
				criteria.addOrder(Order.asc(p));
		} else {
			for (String p : properties)
				criteria.addOrder(Order.desc(p));
		}
		return criteria;
	}
}
