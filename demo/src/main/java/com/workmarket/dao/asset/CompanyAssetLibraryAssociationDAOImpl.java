package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.asset.CompanyAsset;
import com.workmarket.domains.model.asset.CompanyAssetLibraryAssociation;
import com.workmarket.domains.model.asset.CompanyAssetPagination;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CompanyAssetLibraryAssociationDAOImpl extends DeletableAbstractDAO<CompanyAssetLibraryAssociation> implements CompanyAssetLibraryAssociationDAO {

	@Qualifier("jdbcTemplate") @Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<CompanyAssetLibraryAssociation> getEntityClass() {
        return CompanyAssetLibraryAssociation.class;
    }
	
	@Override
	public CompanyAssetLibraryAssociation findByCompanyAndAssetId(Long companyId, Long assetId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("entity.id", companyId))
			.add(Restrictions.eq("asset.id", assetId));
		
		return (CompanyAssetLibraryAssociation) criteria.uniqueResult();
	}

	private static final class CompanyAssetMapper implements RowMapper<CompanyAsset> {

		@Override
		public CompanyAsset mapRow(ResultSet rs, int rowNum) throws SQLException {
			CompanyAsset row = new CompanyAsset();
			row.setAssetId(rs.getLong("assetId"));
			row.setActive(rs.getBoolean("active"));
			row.setCdnUri(rs.getString("cdn_uri"));
			row.setContent(rs.getString("content"));
			row.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getDate("created_on")));
			row.setCreatorFullName(StringUtilities.fullName(rs.getString("first_name"), rs.getString("last_name")));
			row.setDescription(rs.getString("description"));
			row.setDisplayable(rs.getBoolean("displayable"));
			row.setFileByteSize(rs.getInt("file_byte_size"));
			row.setLocalUri(rs.getString("local_uri"));
			row.setMimeType(rs.getString("mime_type"));
			row.setModifiedOn(DateUtilities.getCalendarFromDate(rs.getDate("modified_on")));
			row.setName(rs.getString("name"));
			row.setRemoteUri(rs.getString("remote_uri"));
			row.setUuid(rs.getString("uuid"));
			row.setAvailability(new AvailabilityType(rs.getString("availability_type_code")));
			return row;
		}
	}

	@Override
	public CompanyAssetPagination findAllAssetsByCompany(Long companyId, CompanyAssetPagination pagination) {
		SQLBuilder builder = new SQLBuilder();
		builder.addColumns("asset.id as assetId", "asset.name", "asset.description", "asset.uuid", "asset.mime_type",
				"asset.created_on", "asset.modified_on", "asset.displayable", "asset.active", "asset.content",
				"asset.availability_type_code", "asset.cdn_uri", "asset.remote_uri", "asset.local_uri",
				"creator.first_name", "creator.last_name", "asset.file_byte_size")

				.addTable("asset")
				.addJoin("INNER JOIN company_asset_library_association ca on ca.asset_id = asset.id")
				.addJoin("INNER JOIN user creator on creator.id = asset.creator_id")
				.addWhereClause("asset.deleted = false")
				.addWhereClause("ca.deleted = false")
				.addWhereClause("ca.company_id = :companyId")
				.addParam("companyId", companyId);

		if (pagination.getSortColumn() != null) {
			builder.addOrderBy(CompanyAssetPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addOrderBy(CompanyAssetPagination.SORTS.NAME.getColumn(), CompanyAssetPagination.SORT_DIRECTION.ASC.toString());
		}

		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.getResultsLimit());

		applyFilters(builder, pagination);

		Integer rowCount = jdbcTemplate.queryForObject(builder.buildCount("asset.id"), builder.getParams(), Integer.class);
		pagination.setRowCount(rowCount);
		if (rowCount > 0) {
			pagination.setResults(jdbcTemplate.query(builder.build(), builder.getParams(), new CompanyAssetMapper()));
		}
		return pagination;
	}

	private static SQLBuilder applyFilters(SQLBuilder builder, CompanyAssetPagination pagination) {
		int i = 0;
		for (String filter : pagination.getFilters().keySet()) {

			CompanyAssetPagination.FILTER_KEYS filterKey = CompanyAssetPagination.FILTER_KEYS.valueOf(filter);
			String filterValue = pagination.getFilters().get(filter);

			// All the date filters
			if (filterKey.equals(CompanyAssetPagination.FILTER_KEYS.CREATION_DATE_FROM) ||
					filterKey.equals(CompanyAssetPagination.FILTER_KEYS.MODIFICATION_DATE_FROM)) {

				builder.addWhereClause(filterKey.getColumn() + " >= :param" + i)
						.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));

			} else if (filterKey.equals(CompanyAssetPagination.FILTER_KEYS.CREATION_DATE_TO) ||
					filterKey.equals(CompanyAssetPagination.FILTER_KEYS.MODIFICATION_DATE_TO)) {

				builder.addWhereClause(filterKey.getColumn() + " <= :param" + i)
						.addParam("param" + i, DateUtilities.getCalendarFromISO8601(pagination.getFilters().get(filter)));

			} else if (filterKey.equals(CompanyAssetPagination.FILTER_KEYS.ACTIVE) ||
					filterKey.equals(CompanyAssetPagination.FILTER_KEYS.DISPLAYABLE)) {

				builder.addWhereClause(filterKey.getColumn() + " = :param" + i)
						.addParam("param" + i, Boolean.valueOf(filterValue));
			} else if (filterKey.equals(CompanyAssetPagination.FILTER_KEYS.TYPE)) {
				filterValue = filterValue.replace('*', '%');

				builder.addWhereClause(filterKey.getColumn() + " like :param" + i)
						.addParam("param" + i, StringUtilities.processForLike(filterValue));

			} else {
				builder.addWhereClause(filterKey.getColumn() + " = :param" + i)
						.addParam("param" + i, filterValue);
			}
			i++;
		}

		return builder;
	}


}
