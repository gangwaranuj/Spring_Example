package com.workmarket.dao.profile;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileActionType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.utility.NumberUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Repository
public class ProfileDAOImpl extends AbstractDAO<Profile> implements ProfileDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Profile findById(Long profileId) {
		return (Profile) getFactory().getCurrentSession().get(Profile.class, profileId);
	}

	protected Class<Profile> getEntityClass() {
		return Profile.class;
	}

	@Override
	public Long findProfileId(Long userId) {
		return (Long) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("user.id", userId))
			.setProjection(Projections.property("id"))
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public Profile findByUser(Long userId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("user", FetchMode.JOIN)
				.add(Restrictions.eq("user.id", userId));

		Profile profile = (Profile) criteria.uniqueResult();
		if (profile != null) {
			Hibernate.initialize(profile.getBlacklistedPostalCodes());
		}
		return profile;
	}

	@Override
	public TimeZone findUserProfileTimeZone(Long userId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("user.id", userId))
				.setProjection(Projections.property("timeZone"));

		return (TimeZone) criteria.uniqueResult();
	}

	@Override
	public Map<String, Boolean> getProfileCompleteness(Long userId) {
		String sql = "SELECT 	user.id userId, company.overview companyOverview, company.address_id companyAddressId, company.website, " +
				" profile.max_travel_distance, profile.address_id profileAddressId,\n" +
				" profile.time_zone_id, COALESCE(profile.min_offsite_hourly_rate, 0) minOffsite, COALESCE(profile.min_onsite_hourly_rate, 0) minOnsite, \n" +
				"EXISTS(SELECT user_specialty_association.id from user_specialty_association WHERE user_specialty_association.user_id = user.id AND user_specialty_association.deleted = false) AS specialties,\n" +
				"EXISTS(SELECT user_certification_association.id from user_certification_association WHERE user_certification_association.user_id = user.id AND user_certification_association.deleted = false) AS certifications,\n" +
				"EXISTS(SELECT user_license_association.id from user_license_association WHERE user_license_association.user_id = user.id AND user_license_association.deleted = false) AS licenses,\n" +
				"EXISTS(SELECT assessment_user_association.id from assessment_user_association WHERE assessment_user_association.user_id = user.id  ) AS assessments,\n" +
				"EXISTS(SELECT user_skill_association.id from user_skill_association WHERE user_skill_association.user_id = user.id AND user_skill_association.deleted = false) AS skills,\n" +
				"EXISTS(SELECT user_insurance_association.id from user_insurance_association WHERE user_insurance_association.user_id = user.id AND user_insurance_association.deleted = false ) AS insurance,\n" +
				"EXISTS(SELECT user_availability.id from user_availability WHERE user_availability.user_id = user.id AND user_availability.type  = 'work' AND user_availability.deleted = false) AS workingHours, \n" +
				"EXISTS(SELECT blacklisted_zipcode.id from blacklisted_zipcode WHERE blacklisted_zipcode.profile_id = profile.id) AS blacklistedPostalCodes, " +
				"EXISTS(SELECT profile_language.id from profile_language WHERE profile_language.profile_id = profile.id) AS languages, " +
				"EXISTS(SELECT screening.id from screening WHERE screening.user_id = user.id AND screening.type = 'background' AND screening_status_type_code IN ('requested', 'passed')) AS backgroundCheck, " +
				"EXISTS(SELECT screening.id from screening WHERE screening.user_id = user.id AND screening.type = 'drug' AND screening_status_type_code IN ('requested', 'passed')) AS drugTest " +
				"FROM user \n" +
				"INNER JOIN company ON user.company_id = company.id \n" +
				"INNER JOIN profile ON profile.user_id = user.id \n" +
				"WHERE user.id = :userId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		List<Map<String, Boolean>> results = jdbcTemplate.query(sql, params, new UserProfileCompletenessMapper());
		if (isNotEmpty(results)) {
			return results.get(0);
		}
		return Maps.newHashMap();
	}

	private static class UserProfileCompletenessMapper implements RowMapper<Map<String, Boolean>> {

		@Override
		public Map<String, Boolean> mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Map<String, Boolean> profileCompleteness = Maps.newHashMap();

			profileCompleteness.put(ProfileActionType.TAGS, rs.getBoolean("specialties"));
			profileCompleteness.put(ProfileActionType.CERTIFICATIONS, rs.getBoolean("certifications"));
			profileCompleteness.put(ProfileActionType.LICENSES, rs.getBoolean("licenses"));
			profileCompleteness.put(ProfileActionType.TESTS, rs.getBoolean("assessments"));
			profileCompleteness.put(ProfileActionType.SKILLS, rs.getBoolean("skills"));
			profileCompleteness.put(ProfileActionType.INSURANCE, rs.getBoolean("insurance"));
			profileCompleteness.put(ProfileActionType.WORKING_HOURS, rs.getBoolean("workingHours"));

			profileCompleteness.put(ProfileActionType.COMPANY_NAME, true);
			profileCompleteness.put(ProfileActionType.COMPANY_OVERVIEW, rs.getString("companyOverview") != null);
			profileCompleteness.put(ProfileActionType.COMPANY_ADDRESS, rs.getLong("companyAddressId") > 0);
			profileCompleteness.put(ProfileActionType.COMPANY_WEBSITE, rs.getString("website") != null);

			profileCompleteness.put(ProfileActionType.TRAVEL_DISTANCE, rs.getBigDecimal("max_travel_distance") != null);
			profileCompleteness.put(ProfileActionType.PERSONAL_ADDRESS, rs.getLong("profileAddressId") > 0);
			profileCompleteness.put(ProfileActionType.TIMEZONE, rs.getLong("time_zone_id") > 0);
			profileCompleteness.put(ProfileActionType.EXCLUDED_ZIP_CODES, rs.getBoolean("blacklistedPostalCodes"));
			profileCompleteness.put(ProfileActionType.LANGUAGES, rs.getBoolean("languages"));
			profileCompleteness.put(ProfileActionType.HOURLY_RATE, NumberUtilities.isPositive(rs.getBigDecimal("minOffsite")) || NumberUtilities.isPositive(rs.getBigDecimal("minOnsite")));

			profileCompleteness.put(ProfileActionType.DRUG_TEST, rs.getBoolean("drugTest"));
			profileCompleteness.put(ProfileActionType.BACKGROUND_CHECK, rs.getBoolean("backgroundCheck"));
			return profileCompleteness;
		}
	}

	@Override
	public Map<String, Object> getProjectionMapByUserNumber(String userNumber, String... fields) {
		Map<String, Object> result = Maps.newLinkedHashMap();

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.createAlias("user", "u")
				.add(Restrictions.eq("u.userNumber", userNumber));

		List<String> props = new ArrayList<String>(Arrays.asList(fields));
		setProjections(criteria, props);

		List properties = criteria.list();

		if (props.size() == 1) {
			result.put(props.get(0), properties.get(0));
		} else {
			Object[] propValues = (Object[])properties.get(0);
			for (int i=0; i < props.size(); i++) {
				result.put(props.get(i), propValues[i]);
			}
		}

		return result;
	}
}
