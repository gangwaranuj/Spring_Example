package com.workmarket.dao.asset;


import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.dao.ModifiedBeanPropertyRowMapper;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportPagination;
import com.workmarket.data.report.assessment.AttemptResponseAssetReportRow;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.AssetCdnUri;
import com.workmarket.domains.model.asset.AssetRemoteUri;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Repository
public class AssetDAOImpl extends DeletableAbstractDAO<Asset> implements AssetDAO {
	private static final Log logger = LogFactory.getLog(AssetDAOImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Asset> getEntityClass() {
		return Asset.class;
	}

	@Override
	public Asset get(String uuid) {
		return findAssetByUUID(uuid);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> get(String... uuids) {
		return getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.in("UUID", uuids))
			.list();
	}

	@Override
	public Asset findAssetByUUID(String uuid) {
		return (Asset) getFactory().getCurrentSession().getNamedQuery("asset.findByUUID")
			.setString("uuid", uuid)
			.uniqueResult();
	}

	@Override
	public Asset findAssetByIdAndCompany(Long assetId, Long companyId) {
		SQLBuilder sqlBuilder = new SQLBuilder();

		sqlBuilder
			.addColumns("a.*")
			.addTable("asset a")
			.addJoin("INNER JOIN user u ON u.id = a.creator_id")
			.addJoin("INNER JOIN company c ON c.id = u.company_id")
			.addWhereClause("a.id = :assetId AND a.deleted = 0 AND c.id = :companyId")
			.addParam("companyId", companyId)
			.addParam("assetId", assetId);

		List<Asset> results = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new ModifiedBeanPropertyRowMapper(getEntityClass()));

		return CollectionUtilities.isEmpty(results) ? null : results.get(0);
	}

	@Override
	public int countDeliverableAssetsByDeliverableRequirementId(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId);

		return ((Long) getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.countDeliverableAssetsByDeliverableRequirementId")
			.setParameter("deliverableRequirementId", deliverableRequirementId)
			.uniqueResult()).intValue();
	}

	@Override
	public List<Integer> findDeliverableAssetPositionsByDeliverableRequirementId(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId);

		return getFactory().getCurrentSession().getNamedQuery("workAssetAssociation.findDeliverableAssetPositionsByDeliverableRequirementId")
			.setParameter("deliverableRequirementId", deliverableRequirementId).list();
	}

	@Override
	public void setPositionToWorkAssetAssociationId(Long deliverableRequirementId) {
		Assert.notNull(deliverableRequirementId, "deliverableRequirementId must not be null");
		getFactory().getCurrentSession().createQuery("update workAssetAssociation set position = id " +
			"where deliverable_requirement_id = :deliverableRequirementId and deleted = false and is_deliverable = 1 and rejectedBy IS NULL")
			.setParameter("deliverableRequirementId", deliverableRequirementId).executeUpdate();
	}

	@Override
	public boolean authorizeByUserId(Long userId, Long companyId, Long assetId, boolean isAdminOrManager) {
		Assert.notNull(userId);
		Assert.notNull(companyId);
		Assert.notNull(assetId);

		SQLBuilder workAssetSQLBuilder = new SQLBuilder();
		workAssetSQLBuilder.addColumn("asset.id")
			.addTable(" work_asset_association association")
			.addJoin(" INNER JOIN work ON work.id = association.entity_id")
			.addJoin(" INNER JOIN asset ON asset.id = association.asset_id")
			.addWhereClause(" asset.id = :assetId AND asset.active = true ")
			/**
			 * Rules:
			 * User is the owner of the assignment
			 * User is admin/manager and belongs to the assignment's company
			 * User is an open resource of an assignment in SENT status
			 * User is admin/manager of an open resource's company and assignment is in SENT status
			 * User is the active resource
			 * User is admin/manager of the active resource's company
			 **/
			.addWhereClause("( \n" +
				" (work.buyer_user_id = :userId) \n" +
				" OR (work.company_id = :companyId AND TRUE = :isAdminOrManager) \n" +
				" OR (work.work_status_type_code = 'sent' AND EXISTS( \n" +
				" SELECT 	wr.id FROM work_resource wr INNER JOIN user on user.id = wr.user_id  \n" +
				" WHERE 	(wr.user_id = :userId OR (user.company_id = :companyId AND TRUE = :isAdminOrManager))  \n" +
				" AND 		work_resource_status_type_code = 'open' AND wr.work_id = work.id)) \n" +
				" OR (EXISTS(  \n" +
				" SELECT 	wr.id FROM work_resource wr INNER JOIN user on user.id = wr.user_id  \n" +
				" WHERE 	(wr.user_id = :userId OR (user.company_id = :companyId AND TRUE = :isAdminOrManager))  \n" +
				" AND 		assigned_to_work = true AND wr.work_id = work.id))) \n");

		SQLBuilder assessmentAssetSQLBuilder = new SQLBuilder();
		assessmentAssetSQLBuilder.addColumn("asset.id")
			.addTable(" assessment_item_asset_association item_association")
			.addJoin(" INNER JOIN assessment_item item ON item.id = item_association.assessment_item_id")
			.addJoin(" INNER JOIN asset ON asset.id = item_association.asset_id")
			.addJoin(" INNER JOIN assessment ON assessment.id = item.assessment_id")
			.addJoin(" INNER JOIN company ON	company.id = assessment.company_id")
			.addJoin(" LEFT  JOIN assessment_user_association association ON	(assessment.id = association.assessment_id AND association.user_id = :userId)")
			.addJoin(" LEFT  JOIN lane_association lane ON (lane.user_id = :userId \n" +
				" AND lane.deleted = false AND lane.approval_status IN (1,5) \n" +
				" AND lane.verification_status = 1 AND lane.company_id = company.id) \n")
			/**
			 * Rules:
			 * User is the owner of the assessment
			 * User is admin/manager and belongs to the assessment's company
			 * User is able to take the assessment base on the availability type of the assessment
			 **/
			.addWhereClause(" asset.id = :assetId AND asset.active = true ")
			.addWhereClause(" ( assessment.user_id = :userId \n" +
				" OR ( assessment.company_id = :companyId AND true = :isAdminOrManager) \n" +
				" OR ( assessment.assessment_status_type_code = 'active' )) \n")
			.addParam("assetId", assetId)
			.addParam("companyId", companyId)
			.addParam("userId", userId)
			.addParam("isAdminOrManager", isAdminOrManager);

		String sql = "SELECT IF( EXISTS (" + workAssetSQLBuilder.build() + " )" + " OR EXISTS (" + assessmentAssetSQLBuilder.build() + " ), 1, 0) ";

		try {
			return jdbcTemplate.queryForObject(sql, assessmentAssetSQLBuilder.getParams(), Boolean.class);
		} catch (EmptyResultDataAccessException ex) {
			logger.error("Empty result", ex);
		}

		return false;
	}

	@Override
	public AttemptResponseAssetReportPagination findAssessmentAttemptResponseAssets(AttemptResponseAssetReportPagination pagination) {
		SQLBuilder sql = new SQLBuilder()
			.addColumns("asset.id", "asset.uuid AS asset_uuid", "asset.mime_type", "asset.file_byte_size", "asset.name AS asset_name", "asset.description", "asset.availability_type_code AS asset_availability_type_code", "asset.remote_uri AS asset_remote_uri", "asset.cdn_uri AS asset_cdn_uri", "asset.created_on")
			.addColumns("small_thumbnail.uuid AS small_thumbnail_uuid", "small_thumbnail.availability_type_code AS small_thumbnail_availability_type_code", "small_thumbnail.remote_uri AS small_thumbnail_remote_uri", "small_thumbnail.cdn_uri AS small_thumbnail_cdn_uri")
			.addColumns("large_thumbnail.uuid AS large_thumbnail_uuid", "large_thumbnail.availability_type_code AS large_thumbnail_availability_type_code", "large_thumbnail.remote_uri AS large_thumbnail_remote_uri", "large_thumbnail.cdn_uri AS large_thumbnail_cdn_uri")
			.addColumns("item.position", "item.prompt")
			.addColumns("user.user_number", "user.first_name", "user.last_name")
			.addColumns("company.effective_name")
			.addColumns("work.work_number", "work.title", "work.schedule_from")
			.addColumns("location.name AS location_name", "location.location_number")
			.addColumns("project.name AS project_name")
			.addColumns("client.name AS client_name")
			.addTable("asset")
			.addJoin("INNER JOIN assessment_attempt_response_asset_association aaa ON asset.id = aaa.asset_id")
			.addJoin("INNER JOIN assessment_attempt_response r ON r.id = aaa.entity_id")
			.addJoin("INNER JOIN assessment_item item ON item.id = r.assessment_item_id")
			.addJoin("INNER JOIN assessment_attempt attempt ON attempt.id = r.assessment_attempt_id")
			.addJoin("INNER JOIN assessment_user_association aua ON aua.id = attempt.assessment_user_association_id")
			.addJoin("INNER JOIN user ON user.id = aua.user_id")
			.addJoin("INNER JOIN company ON company.id = user.company_id")
			.addJoin("LEFT JOIN asset small_thumbnail ON small_thumbnail.id = aaa.transformed_small_asset_id")
			.addJoin("LEFT JOIN asset large_thumbnail ON large_thumbnail.id = aaa.transformed_large_asset_id")
			.addJoin("LEFT JOIN work ON work.id = attempt.work_id")
			.addJoin("LEFT JOIN location ON location.id = work.client_location_id")
			.addJoin("LEFT JOIN project_work_association pwa ON work.id = pwa.work_id")
			.addJoin("LEFT JOIN project ON project.id = pwa.project_id")
			.addJoin("LEFT JOIN client_company client ON client.id = work.client_company_id")
			.addWhereClause("r.deleted = false");

		int i = 0;
		for (String filterKey : pagination.getFilters().keySet()) {
			AttemptResponseAssetReportPagination.FILTER_KEYS f = AttemptResponseAssetReportPagination.FILTER_KEYS.valueOf(filterKey);

			if (f.equals(AttemptResponseAssetReportPagination.FILTER_KEYS.CREATED_ON_FROM) ||
				f.equals(AttemptResponseAssetReportPagination.FILTER_KEYS.CREATED_ON_THROUGH)) {

				Calendar value = DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filterKey));
				sql.addWhereClause(f.getColumn(), f.getOperator(), "param" + (++i), value);
			} else {
				sql.addWhereClause(f.getColumn(), f.getOperator(), "param" + (++i), pagination.getFilters().get(filterKey));
			}
		}

		if (pagination.getSortColumn() != null) {
			sql.addOrderBy(AttemptResponseAssetReportPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			sql.addOrderBy(AttemptResponseAssetReportPagination.SORTS.CREATED_ON.getColumn(), Pagination.SORT_DIRECTION.ASC.toString());
		}

		sql.setStartRow(pagination.getStartRow());
		sql.setPageSize(pagination.getResultsLimit());

		List<AttemptResponseAssetReportRow> rows = jdbcTemplate.query(sql.build(), sql.getParams(), new RowMapper<AttemptResponseAssetReportRow>() {
			@Override
			public AttemptResponseAssetReportRow mapRow(ResultSet rs, int rowNum) throws SQLException {
				AttemptResponseAssetReportRow row = new AttemptResponseAssetReportRow();
				row.setId(rs.getLong("asset.id"));
				row.setUuid(rs.getString("asset_uuid"));
				row.setName(rs.getString("asset_name"));
				row.setDescription(rs.getString("asset.description"));
				row.setMimeType(rs.getString("asset.mime_type"));
				row.setFileByteSize(rs.getInt("asset.file_byte_size"));
				row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("asset.created_on")));
				AvailabilityType assetAvailability = new AvailabilityType(rs.getString("asset_availability_type_code"));
				row.setUri(assetAvailability.getUri(rs.getString("asset_uuid"), rs.getString("asset_remote_uri"), rs.getString("asset_cdn_uri")));
				if (rs.getString("small_thumbnail_uuid") != null) {
					row.setSmallThumbnailUuid(rs.getString("small_thumbnail_uuid"));
					AvailabilityType smallAvailability = new AvailabilityType(rs.getString("small_thumbnail_availability_type_code"));
					row.setSmallThumbnailUri(smallAvailability.getUri(rs.getString("small_thumbnail_uuid"), rs.getString("small_thumbnail_remote_uri"), rs.getString("small_thumbnail_cdn_uri")));
				}
				if (rs.getString("large_thumbnail_uuid") != null) {
					row.setLargeThumbnailUuid(rs.getString("large_thumbnail_uuid"));
					AvailabilityType largeAvailability = new AvailabilityType(rs.getString("large_thumbnail_availability_type_code"));
					row.setLargeThumbnailUri(largeAvailability.getUri(rs.getString("large_thumbnail_uuid"), rs.getString("large_thumbnail_remote_uri"), rs.getString("large_thumbnail_cdn_uri")));
				}
				row.setCreatorUserNumber(rs.getString("user.user_number"));
				row.setCreatorFirstName(rs.getString("user.first_name"));
				row.setCreatorLastName(rs.getString("user.last_name"));
				row.setCreatorCompanyName(rs.getString("company.effective_name"));
				row.setItemPosition(rs.getInt("item.position") + 1);
				row.setItemPrompt(rs.getString("item.prompt"));
				row.setWorkNumber(rs.getString("work.work_number"));
				row.setWorkTitle(rs.getString("work.title"));
				row.setWorkScheduledFrom(DateUtilities.getCalendarFromDate(rs.getTimestamp("work.schedule_from")));
				row.setWorkLocationName(rs.getString("location_name"));
				row.setWorkLocationNumber(rs.getString("location.location_number"));
				row.setWorkProjectName(rs.getString("project_name"));
				row.setWorkClientName(rs.getString("client_name"));
				return row;
			}

		});

		Map<String, Object> response = jdbcTemplate.queryForMap(sql.buildCountFromSubSelectWithColumns("IFNULL(SUM(file_byte_size), 0) AS total_file_byte_size"), sql.getParams());
		Long count = Long.valueOf(response.get("count").toString());
		Long fileByteSize = Long.valueOf(response.get("total_file_byte_size").toString());

		pagination.setResults(rows);
		pagination.setRowCount(count);
		pagination.setTotalFileByteSize(fileByteSize);

		return pagination;
	}

	@Override
	public List<String> findAssessmentAttemptResponseAssetUuidsByAssessment(Long assessmentId) {
		SQLBuilder sql = new SQLBuilder()
			.addColumns("asset.uuid")
			.addTable("asset")
			.addJoin("INNER JOIN assessment_attempt_response_asset_association aaa ON asset.id = aaa.asset_id")
			.addJoin("INNER JOIN assessment_attempt_response r ON r.id = aaa.entity_id")
			.addJoin("INNER JOIN assessment_item item ON item.id = r.assessment_item_id")
			.addJoin("INNER JOIN assessment_attempt att ON att.id = r.assessment_attempt_id")
			.addJoin("INNER JOIN assessment_user_association aua ON aua.id = att.assessment_user_association_id")
			.addWhereClause("aua.assessment_id", "=", "assessmentId", assessmentId);

		return jdbcTemplate.query(sql.build(), sql.getParams(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("asset.uuid");
			}
		});
	}

	@Override
	public List<String> findAssessmentAttemptResponseAssetUuidsByAttempt(Long attemptId) {
		SQLBuilder sql = new SQLBuilder()
			.addColumns("asset.uuid")
			.addTable("asset")
			.addJoin("INNER JOIN assessment_attempt_response_asset_association aaa ON asset.id = aaa.asset_id")
			.addJoin("INNER JOIN assessment_attempt_response r ON r.id = aaa.entity_id")
			.addJoin("INNER JOIN assessment_item item ON item.id = r.assessment_item_id")
			.addJoin("INNER JOIN assessment_attempt att ON att.id = r.assessment_attempt_id")
			.addWhereClause("att.id", "=", "attemptId", attemptId);

		return jdbcTemplate.query(sql.build(), sql.getParams(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("asset.uuid");
			}
		});
	}

	@Override
	public AssetCdnUri findDefaultAssetCdnUri() {
		final long defaultAssetCdnUriId = 1L;
		return (AssetCdnUri) getFactory().getCurrentSession().createQuery("from assetCdnUri where id = :id ")
			.setParameter("id", defaultAssetCdnUriId)
			.setMaxResults(1)
			.uniqueResult();
	}

	@Override
	public AssetRemoteUri findDefaultAssetRemoteUri() {
		final long defaultAssetRemoteUriId = 1L;
		return (AssetRemoteUri) getFactory().getCurrentSession().createQuery("from assetRemoteUri where id = :id ")
			.setParameter("id", defaultAssetRemoteUriId)
			.setMaxResults(1)
			.uniqueResult();
	}
}
