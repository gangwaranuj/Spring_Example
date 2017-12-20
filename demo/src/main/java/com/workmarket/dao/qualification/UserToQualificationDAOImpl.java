package com.workmarket.dao.qualification;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of UserToQualificationDAO.
 */
@Repository
public class UserToQualificationDAOImpl implements UserToQualificationDAO {

    private static final Log logger = LogFactory.getLog(UserToQualificationDAOImpl.class);

    private static final String FIND_USER_ASSOCIATIONS_SQL =
        "SELECT user_id, qualification_uuid, qualification_type_id, deleted, creator_id, modifier_id "
            + "FROM user_to_qualification "
            + "WHERE user_id = :userId AND qualification_type_id = :qualificationTypeId";

    private static final String SAVE_OR_UPDATE_SQL =
        "INSERT INTO user_to_qualification (user_id, qualification_uuid, qualification_type_id, creator_id, modifier_id, deleted) "
            + " VALUES (:userId, :qualificationUuid, :qualificationTypeId, :creatorId, :modifierId, :deleted) "
            + "ON DUPLICATE KEY UPDATE modifier_id = VALUES(modifier_id), deleted = :newDeleted";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    public UserToQualificationDAOImpl(@Qualifier("readOnlyJdbcTemplate") final NamedParameterJdbcTemplate readOnlyJdbcTemplate,
                                      @Qualifier("jdbcTemplate") final NamedParameterJdbcTemplate jdbcTemplate) {
        this.readOnlyJdbcTemplate = readOnlyJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveOrUpdate(final UserToQualification userToQualification) {
        if (QualificationType.job_title.equals(userToQualification.getQualificationType())) {
            saveOrUpdateJobTitle(userToQualification);
        } else {
            saveOrUpdateNonJobTitle(userToQualification);
        }
    }

    /**
     * Saves or update a user to qualification association.
     *
     * @param userToQualification user qualification association.
     */
    private void saveOrUpdateNonJobTitle(final UserToQualification userToQualification) {
        jdbcTemplate.update(SAVE_OR_UPDATE_SQL, CollectionUtilities.newObjectMap(
            "userId", userToQualification.getUserId(),
            "qualificationUuid", userToQualification.getQualificationUuid(),
            "qualificationTypeId", userToQualification.getQualificationType().getQualificationTypeCode(),
            "creatorId", userToQualification.getUserId(),
            "modifierId", userToQualification.getUserId(),
            "deleted", Boolean.FALSE,
            "newDeleted", Boolean.FALSE
        ));
    }

    /**
     * Saves or updates user job title association. We need a different implementation
     * because we support only one job title for now.
     * In the future, when we support multiple job titles, we can remove this method.
     *
     * @param userToQualification user qualificaiton association.
     */
    private void saveOrUpdateJobTitle(final UserToQualification userToQualification) {
        // we currently support one job title per user
        userToQualification.setDeleted(Boolean.FALSE);
        // get a list of non-deleted UserToQualification in DB
        final List<UserToQualification> userToQualifications =
            findQualifications(userToQualification.getUserId(), QualificationType.job_title, true);
        boolean userToQualificationExists = false;
        for (final UserToQualification utq : userToQualifications) {
            if (utq.getQualificationUuid().equals(userToQualification.getQualificationUuid())) {
                utq.setDeleted(Boolean.FALSE);
                userToQualificationExists = true;
            } else {
                utq.setDeleted(Boolean.TRUE);
            }
        }
        if (!userToQualificationExists) {
            userToQualifications.add(userToQualification);
        }

        final Map<String, Object>[] rows = new HashMap[userToQualifications.size()];
        int i = 0;
        for (final UserToQualification utq : userToQualifications) {
            Map<String, Object> row = Maps.newHashMap();
            row.put("userId", utq.getUserId());
            row.put("qualificationUuid", utq.getQualificationUuid());
            row.put("qualificationTypeId", utq.getQualificationType().getQualificationTypeCode());
            row.put("creatorId", utq.getUserId());
            row.put("modifierId", utq.getUserId());
            row.put("deleted", utq.getDeleted());
            row.put("newDeleted", utq.getDeleted());
            rows[i++] = row;
        }
        jdbcTemplate.batchUpdate(SAVE_OR_UPDATE_SQL, rows);
    }

    @Override
    public List<UserToQualification> findQualifications(final Long userId,
                                                        final QualificationType qualificationType,
                                                        final boolean includeDeleted) {
        final String sql = includeDeleted ? FIND_USER_ASSOCIATIONS_SQL : FIND_USER_ASSOCIATIONS_SQL + " AND deleted = 0";
        return readOnlyJdbcTemplate.query(
            sql,
            CollectionUtilities.newObjectMap(
                "userId", userId,
                "qualificationTypeId", qualificationType.getQualificationTypeCode()),
            new UserToQualificationMapper());
    }

    @Override
    public List<UserToQualification> setUserQualificationsByType(final Long userId,
                                                                 final QualificationType qualificationType,
                                                                 final List<Qualification> qualifications) {
        final List<UserToQualification> userToQualifications = findQualifications(userId, qualificationType, true);
        for (final UserToQualification utq : userToQualifications) {
            utq.setDeleted(Boolean.TRUE);
        }
        for (final Qualification qualification : qualifications) {
            boolean isExisted = false;
            for (final UserToQualification utq : userToQualifications) {
                if (utq.getQualificationUuid().equals(qualification.getUuid())) {
                    utq.setDeleted(Boolean.FALSE);
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                userToQualifications.add(new UserToQualification(userId, qualification.getUuid(), qualificationType));
            }
        }

        final Map<String, Object>[] rows = new HashMap[userToQualifications.size()];
        int i = 0;
        for (final UserToQualification utq : userToQualifications) {
            Map<String, Object> row = Maps.newHashMap();
            row.put("userId", utq.getUserId());
            row.put("qualificationUuid", utq.getQualificationUuid());
            row.put("qualificationTypeId", utq.getQualificationType().getQualificationTypeCode());
            row.put("creatorId", utq.getUserId());
            row.put("modifierId", utq.getUserId());
            row.put("deleted", utq.getDeleted());
            row.put("newDeleted", utq.getDeleted());
            rows[i++] = row;
        }
        jdbcTemplate.batchUpdate(SAVE_OR_UPDATE_SQL, rows);
        return userToQualifications;
    }

    private class UserToQualificationMapper implements RowMapper<UserToQualification> {
        @Override
        public UserToQualification mapRow(ResultSet rs, int rowNum) throws SQLException {
            final UserToQualification userToQualification = new UserToQualification(
                rs.getLong("user_id"),
                rs.getString("qualification_uuid"),
                QualificationType.getQualficationTypeByCode(rs.getInt("qualification_type_id")));
            userToQualification.setDeleted(rs.getBoolean("deleted"));
            userToQualification.setCreatorId(rs.getLong("creator_id"));
            userToQualification.setModifierId(rs.getLong("modifier_id"));
            return userToQualification;
        }
    }
}
