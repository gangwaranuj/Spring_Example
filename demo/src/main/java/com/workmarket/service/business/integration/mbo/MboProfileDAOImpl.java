package com.workmarket.service.business.integration.mbo;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class MboProfileDAOImpl extends DeletableAbstractDAO<MboProfile> implements MboProfileDAO {

	private final static String FILTER_MBO_FROM_LIST_SQL =
		"SELECT mp.user_id " +
		"FROM mbo_profile mp " +
		"WHERE mp.user_id IN (:userIds)";

	@Autowired @Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<?> getEntityClass() {
		return MboProfile.class;
	}

	@Override
	public MboProfile findMboProfile(Long userId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(MboProfile.class)
				.add(Restrictions.eq("userId", userId));

		return (MboProfile) criteria.uniqueResult();
	}

	@Override
	public MboProfile findMboProfileByGUID(String objectGUID) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(MboProfile.class)
				.add(Restrictions.eq("objectId", objectGUID));

		return (MboProfile) criteria.uniqueResult();
	}

	@Override
	public Set<Long> filterMboResourcesFromList(Set<Long> userIds) {
		if (CollectionUtilities.isEmpty(userIds)) {
			return Collections.emptySet();
		}

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userIds", userIds);
		List<Long> resourceIds = jdbcTemplate.queryForList(FILTER_MBO_FROM_LIST_SQL, params, Long.class);
		return new HashSet<>(resourceIds);
	}
}
