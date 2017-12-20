package com.workmarket.dao.search.user;

import com.workmarket.data.solr.model.SolrUserData;
import com.workmarket.data.solr.model.SolrVendorData;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * DAO implementation for SolrVendor data.
 */
@Repository
public class SolrVendorDAOImpl implements SolrVendorDAO {

    private static final Logger logger = LoggerFactory.getLogger(SolrVendorDAOImpl.class);

    private static final String VENDOR_SQL =
        "SELECT c.id, c.uuid, c.company_number, c.name, c.effective_name, c.overview, " +
            "c.operating_as_individual_flag, c.created_on, c.company_status_type_code, " +
            "a.latitude, a.longitude, a.city, s.short_name as state, a.postal_code, a.country " +
        "FROM company c " +
        "LEFT JOIN address a on c.address_id = a.id " +
        "LEFT JOIN state s on a.state = s.id  " +
        "WHERE c.in_vendor_search = 1 AND ";

    // vendor or company doesn't have video asset
    private static final String VENDOR_AVATARS_SQL =
        "SELECT caa.company_id, origAsset.uuid origUuid, origAssetUri.cdn_uri_prefix origUriPrefix, " +
            "largeAsset.uuid largeUuid, largeAssetUri.cdn_uri_prefix largeUriPrefix, " +
            "smallAsset.uuid smallUuid, smallAssetUri.cdn_uri_prefix smallUriPrefix " +
        "FROM " +
            "(SELECT * " +
            " FROM company_asset_association ca " +
            " WHERE ca.company_id = :vendorId " +
            "     AND ca.asset_type_code = 'avatar' " +
            "     AND ca.active = 1 " +
            "     AND ca.deleted = 0 " +
            "     AND ca.approval_status = 1 " +
            " ORDER BY ca.created_on DESC " +
            " LIMIT 1) caa " +
        "LEFT JOIN asset as origAsset ON caa.asset_id = origAsset.id " +
        "LEFT JOIN asset as largeAsset ON caa.transformed_large_asset_id = largeAsset.id " +
        "LEFT JOIN asset as smallAsset ON caa.transformed_small_asset_id = smallAsset.id " +
        "LEFT JOIN asset_cdn_uri origAssetUri ON origAsset.asset_cdn_uri_id = origAssetUri.id " +
        "LEFT JOIN asset_cdn_uri largeAssetUri ON largeAsset.asset_cdn_uri_id = largeAssetUri.id " +
        "LEFT JOIN asset_cdn_uri smallAssetUri ON smallAsset.asset_cdn_uri_id = smallAssetUri.id ";

    private static final String VENDOR_WORKERS_SQL =
        "SELECT distinct u.id " +
        "FROM user u " +
        "LEFT JOIN user_acl_role uar ON u.id = uar.user_id " +
        "LEFT JOIN acl_role ar ON ar.id = uar.acl_role_id " +
        "WHERE u.company_id = :vendorId " +
        "    AND u.user_status_type_code = 'approved' " +
        "    AND ((u.lane3_approval_status = 1 and ar.name = 'External' and uar.deleted = 0) or (u.lane3_approval_status = 4 and ar.name = 'Internal' and uar.deleted = 0)) ";

    // aggregate on employees rather than this
    private static final String VENDOR_RATINGS_SQL =
        "SELECT c.id AS company_id, \n" +
            "-- Rating count\n" +
            "(SELECT COALESCE(COUNT(DISTINCT rating.work_id),0) FROM rating \n" +
            " INNER JOIN work_resource ON work_resource.user_id = rated_user_id AND work_resource.work_id = rating.work_id \n" +
            " WHERE rating.rating_shared_flag = 'Y' AND rating.deleted = 0 AND rating.is_pending = 0 \n" +
            "     AND rating.rated_user_id = u.id AND rating.created_on >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS ratingCount,\n" +
            "-- Good Rating count\n" +
            "(SELECT COALESCE(COUNT(DISTINCT rating.work_id),0) FROM rating \n" +
            " INNER JOIN work_resource ON work_resource.user_id = rated_user_id AND work_resource.work_id = rating.work_id \n" +
            " WHERE rating.rating_shared_flag = 'Y' AND rating.deleted = 0 AND rating.is_pending = 0 \n" +
            "     AND rating.rated_user_id = u.id and rating.value in (2, 3) AND rating.created_on >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS goodRatingCount,\n" +
            "(SELECT COALESCE(COUNT(DISTINCT work_id),0) FROM work_history_summary \n" +
            " INNER JOIN time_dimension ON time_dimension.id = work_history_summary.date_id \n" +
            " WHERE work_status_type_code = 'paid' \n" +
            "     AND work_history_summary.active_resource_user_id = u.id \n" +
            "     AND time_dimension.date >=  DATE_SUB(now(), INTERVAL 6 MONTH)) AS paidWorkLastSixMonths,\n" +
            "(SELECT COALESCE(COUNT(work_resource_label.id), 0) FROM work_resource_label\n" +
            " WHERE work_resource_label.work_resource_user_id = u.id \n" +
            "     AND work_resource_label.work_resource_label_type_code = 'cancelled'\n" +
            "     AND work_resource_label.ignored = false AND work_resource_label.confirmed = true) AS workCancelled\n" +
        "FROM company c, user u\n" +
        "WHERE u.company_id = c.id ";

    private static final String UUIDS = "SELECT uuid from company where id in (:vendorIds)";

    @Autowired
    @Qualifier("readOnlyJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("solrUserDAOImpl")
    private SolrUserDAO solrUserDAO;

    @Override
    public SolrVendorData getSolrDataById(final Long vendorId) {
        String whereClause = "c.id = :vendorId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("vendorId", vendorId);
        List<SolrVendorData> data = getResults(whereClause, params);

        return CollectionUtils.isNotEmpty(data) ? data.get(0) : null;
    }

    @Override
    public List<SolrVendorData> getSolrDataById(final List<Long> vendorIds) {
        if (CollectionUtils.isEmpty(CollectionUtilities.filterNull(vendorIds))) {
            return Collections.emptyList();
        }
        String whereClause = "c.id IN (" + StringUtils.join(vendorIds, ",") + ")";

        return getResults(whereClause, new MapSqlParameterSource());
    }

    @Override
    public List<SolrVendorData> getSolrDataBetweenIds(Long fromId, Long toId) {
        String whereClause = "c.id BETWEEN :fromId AND :toId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fromId", fromId);
        params.addValue("toId", toId);

        return getResults(whereClause, params);
    }

    @Override
    public List<SolrVendorData> getSolrDataChanged(Calendar from) {
        String whereClause = " c.modified_on >= :from ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("from", from);

        return getResults(whereClause, params);
    }

    @Override
    public List<String> getSolrDataUuidsByIds(final List<Long> vendorIds) {
        final String sql = UUIDS.replace(":vendorIds", StringUtils.join(vendorIds, ","));
        final SolrVendorUuidMapper uuidMapper = new SolrVendorUuidMapper();
        jdbcTemplate.query(sql, uuidMapper);
        return uuidMapper.getUuids();
    }

    private List<SolrVendorData> getResults(final String whereClause, final MapSqlParameterSource params) {
        final String sql = VENDOR_SQL + whereClause;
        final List<SolrVendorData> solrVendorDataList = jdbcTemplate.query(sql, params, new SolrVendorDataRowMapper());
        loadAvatars(solrVendorDataList);
        loadEmployees(solrVendorDataList);
        return solrVendorDataList;
    }

    private void loadAvatars(List<SolrVendorData> solrVendorDataList) {
        for (final SolrVendorData svd : solrVendorDataList) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("vendorId", svd.getId());
            jdbcTemplate.query(VENDOR_AVATARS_SQL, params, new SolrVendorAvatarDataMapper(svd));
        }
    }

    private void loadEmployees(List<SolrVendorData> solrVendorDataList) {
        for (final SolrVendorData solrVendorData : solrVendorDataList) {
            final MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("vendorId", solrVendorData.getId());
            final List<Long> employeeIds = jdbcTemplate.query(VENDOR_WORKERS_SQL, params, new RowMapper<Long>() {
                @Override
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long val = rs.getLong("id");
                    return !rs.wasNull() ? val : null;
                }
            });

            List<SolrUserData> employees = solrUserDAO.getSolrDataById(employeeIds);
            solrVendorData.setEmployees(employees);
        }
    }
}
