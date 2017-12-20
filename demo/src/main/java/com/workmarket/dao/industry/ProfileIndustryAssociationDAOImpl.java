package com.workmarket.dao.industry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.dao.DeletableAbstractDAO;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ProfileIndustryAssociation;
import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Repository
public class ProfileIndustryAssociationDAOImpl extends DeletableAbstractDAO<ProfileIndustryAssociation> implements ProfileIndustryAssociationDAO {

	@Resource(name = "readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	private static final String DEFAULT_INDUSTRY_IDS_FOR_USERS_SQL = String.format(
		" SELECT  u.id as user_id, IFNULL( MAX(pia.industry_id), %d) as industry_id\n"+
		" FROM user u \n"+
		" INNER JOIN profile p on u.id = p.user_id AND u.id IN (:userIds)\n"+
		" LEFT JOIN profile_industry_association pia on p.id = pia.profile_id\n"+
		" AND pia.deleted = false\n"+
		" GROUP BY u.id;", Industry.NO_INDUSTRY_CODE
	);

	protected Class<ProfileIndustryAssociation> getEntityClass() {
		return ProfileIndustryAssociation.class;
	}

	@Override
	public Set<Industry> findIndustriesForProfile(Long id, boolean includeDeleted) {
		Query query;

		if (includeDeleted) {
			query = getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.findAllIndustriesForProfile");
		} else {
			query = getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.findIndustriesForProfile");
		}

		query.setParameter("profileId", id);

		return Sets.newHashSet(query.list());
	}

	// We've defined the default industry to be the latest industry added to a user's profile
	// Default industry was never a defined feature in our app
	// We used to return the first result from our DB as the default industry, with no ordering property on the DB query
	// This interim solution will at least provide some consistency in our results
	@Override
	public Industry findDefaultIndustryForProfile(Long id) {
		return (Industry)getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.findDefaultIndustryForProfile")
			.setParameter("profileId", id)
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public Map<Long, Long> findDefaultIndustriesForUsers(Collection<Long> userIds) {
		return jdbcTemplate.query(DEFAULT_INDUSTRY_IDS_FOR_USERS_SQL, new MapSqlParameterSource("userIds", userIds), new ResultSetExtractor<Map<Long, Long>>() {
			@Override
			public Map<Long, Long> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				Map<Long, Long> result = Maps.newHashMap();
				while (resultSet.next()) {
					result.put(resultSet.getLong("user_id"), resultSet.getLong("industry_id"));
				}
				return result;
			}
		});
	}

	@Override
	public boolean doesProfileHaveIndustry(Long profileId, Long industryId) {
		return (Long)getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.doesProfileHaveIndustry")
			.setParameter("profileId", profileId)
			.setParameter("industryId", industryId).uniqueResult() > 0;
	}

	@Override
	public Set<ProfileIndustryAssociation> findAllIndustryProfileAssociationsByProfile(Long id) {
		Query query = getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.findAllIndustriesAssociationsForProfile")
			.setParameter("profileId", id);

		return Sets.newHashSet(query.list());
	}

	@Override
	public ProfileIndustryAssociation findByProfileIdAndIndustryId(Long profileId, Long industryId) {
		Query query = getFactory().getCurrentSession().getNamedQuery("profileIndustryAssociation.findByIndustryAndProfile")
			.setParameter("industryId", industryId)
			.setParameter("profileId", profileId);

		return (ProfileIndustryAssociation)query.uniqueResult();
	}

	@Override
	public String findOtherNameByProfileId(Long profileId){
		return (String) getFactory().getCurrentSession()
				.createSQLQuery("select name from industry_others io where io.profile_id = :profileId")
				.setParameter("profileId", profileId)
				.uniqueResult();
	}

	@Override
	public void saveOtherNameForProfileId(Long profileId, String otherName){
		getFactory().getCurrentSession()
				.createSQLQuery("INSERT INTO industry_others (profile_id, name) VALUES (:profileId, :otherName) ON DUPLICATE KEY UPDATE name = VALUES(name);")
				.setParameter("profileId", profileId)
				.setParameter("otherName", otherName)
				.executeUpdate();
	}
}
