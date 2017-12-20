package com.workmarket.dao.qualification;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.qualification.WorkToQualification;
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
 * Implementation of WorkToQualificationDAO.
 */
@Repository
public class WorkToQualificationDAOImpl implements WorkToQualificationDAO {

    private static final Log logger = LogFactory.getLog(UserToQualificationDAOImpl.class);

    private static final String FIND_WORK_ASSOCIATIONS_SQL =
        "SELECT work_id, qualification_uuid, qualification_type_id, deleted, creator_id, modifier_id "
            + "FROM work_to_qualification "
            + "WHERE work_id = :workId AND qualification_type_id = :qualificationTypeId";

    private static final String SAVE_OR_UPDATE_SQL =
        "INSERT INTO work_to_qualification (work_id, qualification_uuid, qualification_type_id, creator_id, modifier_id, deleted) "
            + "VALUES (:workId, :qualificationUuid, :qualificationTypeId, :creatorId, :modifierId, :deleted) "
            + "ON DUPLICATE KEY UPDATE modifier_id = VALUES(modifier_id), deleted = :newDeleted";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate readOnlyJdbcTemplate;

    @Autowired
    public WorkToQualificationDAOImpl(@Qualifier("readOnlyJdbcTemplate") final NamedParameterJdbcTemplate readOnlyJdbcTemplate,
                                      @Qualifier("jdbcTemplate") final NamedParameterJdbcTemplate jdbcTemplate) {
        this.readOnlyJdbcTemplate = readOnlyJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<WorkToQualification> findQualifications(final Long workId,
                                                        final QualificationType qualificationType,
                                                        final boolean includeDeleted) {
        final String sql = includeDeleted ? FIND_WORK_ASSOCIATIONS_SQL : FIND_WORK_ASSOCIATIONS_SQL + " AND deleted = 0";
        return readOnlyJdbcTemplate.query(
            sql,
            CollectionUtilities.newObjectMap(
                "workId", workId,
                "qualificationTypeId", qualificationType.getQualificationTypeCode()),
            new WorkToQualificationMapper());
    }

    @Override
    public List<WorkToQualification> setWorkQualificationsByType(final Long workId,
                                                                 final Long userId,
                                                                 final QualificationType qualificationType,
                                                                 final List<Qualification> qualifications) {
        final List<WorkToQualification> workToQualifications = findQualifications(workId, qualificationType, true);
        for (final WorkToQualification wtq : workToQualifications) {
            wtq.setDeleted(Boolean.TRUE);
        }
        for (final Qualification qualification : qualifications) {
            boolean isExisted = false;
            for (final WorkToQualification wtq : workToQualifications) {
                if (wtq.getQualificationUuid().equals(qualification.getUuid())) {
                    wtq.setDeleted(Boolean.FALSE);
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                workToQualifications.add(new WorkToQualification(workId, qualification.getUuid(), qualificationType));
            }
        }

        final Map<String, Object>[] rows = new HashMap[workToQualifications.size()];
        int i = 0;
        for (final WorkToQualification wtq : workToQualifications) {
            Map<String, Object> row = Maps.newHashMap();
            row.put("workId", wtq.getWorkId());
            row.put("qualificationUuid", wtq.getQualificationUuid());
            row.put("qualificationTypeId", wtq.getQualificationType().getQualificationTypeCode());
            row.put("creatorId", userId);
            row.put("modifierId", userId);
            row.put("deleted", wtq.getDeleted());
            row.put("newDeleted", wtq.getDeleted());
            rows[i++] = row;
        }
        jdbcTemplate.batchUpdate(SAVE_OR_UPDATE_SQL, rows);
        return workToQualifications;
    }

    private class WorkToQualificationMapper implements RowMapper<WorkToQualification> {
        @Override
        public WorkToQualification mapRow(ResultSet rs, int rowNum) throws SQLException {
            final WorkToQualification workToQualification = new WorkToQualification(
                rs.getLong("work_id"),
                rs.getString("qualification_uuid"),
                QualificationType.getQualficationTypeByCode(rs.getInt("qualification_type_id")));
            workToQualification.setDeleted(rs.getBoolean("deleted"));
            workToQualification.setCreatorId(rs.getLong("creator_id"));
            workToQualification.setModifierId(rs.getLong("modifier_id"));
            return workToQualification;
        }
    }
}
