package com.workmarket.dao.recruiting;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.utility.DateUtilities;

@Repository
public class RecruitingCampaignDAOImpl extends AbstractDAO<RecruitingCampaign> implements RecruitingCampaignDAO {

	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<RecruitingCampaign> getEntityClass() {
		return RecruitingCampaign.class;
	}

	@Override
	public RecruitingCampaignPagination findAllCampaignsByCompanyId(Long companyId, RecruitingCampaignPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);

		Criteria count = getFactory().getCurrentSession().createCriteria(getEntityClass());
		count.add(Restrictions.eq("deleted", false));
		count.add(Restrictions.eq("company.id", companyId));

		Map<String, Object> params = Maps.newHashMap();
		params.put("companyId", companyId);

		StringBuilder sql = new StringBuilder()
				.append(" SELECT  id, title, description, created_on , short_url, active,  ")
				.append(" (SELECT count(im.id) FROM impression im LEFT JOIN recruiting_campaign rc ON rc.id = im.campaign_id WHERE rc.id = recruiting_campaign.id) AS clicks, ")
				.append(" (SELECT count(u.id) FROM user u LEFT JOIN recruiting_campaign r ON r.id = u.recruiting_campaign_id WHERE r.id = recruiting_campaign.id) AS users ")
				.append(" FROM    recruiting_campaign ")
				.append(" WHERE   deleted = false AND company_id = :companyId ");

		if (pagination.getFilters() != null) {
			if (pagination.getFilter(RecruitingCampaignPagination.FILTER_KEYS.ACTIVE) != null) {

				boolean activeStatus = Boolean.parseBoolean(pagination.getFilter(RecruitingCampaignPagination.FILTER_KEYS.ACTIVE));

				sql.append(" AND active = :activeStatus ");
				params.put("activeStatus", activeStatus);

				count.add(Restrictions.eq("active", activeStatus));
			}
		}

		pagination.setRowCount(HibernateUtilities.getRowCount(count));

		RowMapper<RecruitingCampaign> mapper = new RowMapper<RecruitingCampaign>() {
			public RecruitingCampaign mapRow(ResultSet rs, int rowNum) throws SQLException {
				RecruitingCampaign campaign = new RecruitingCampaign();

				campaign.setId(rs.getLong("id"));
				campaign.setTitle(rs.getString("title"));
				campaign.setDescription(rs.getString("description"));
				campaign.setCreatedOn(DateUtilities.getCalendarFromDate(rs.getTimestamp("created_on")));
				campaign.setShortUrl(rs.getString("short_url"));
				campaign.setClicks(rs.getInt("clicks"));
				campaign.setUsers(rs.getInt("users"));
				campaign.setActive(rs.getBoolean("active"));

				return campaign;
			}
		};

		String sort = "id";
		if (pagination.getSortColumn() != null) {
			if (RecruitingCampaignPagination.SORTS.CAMPAIGN_TITLE.toString().equals(pagination.getSortColumn())) {
				sort = "title";
			} else if (RecruitingCampaignPagination.SORTS.CAMPAIGN_DATE.toString().equals(pagination.getSortColumn())) {
				sort = "created_on";
			} else if (RecruitingCampaignPagination.SORTS.CLICKS.toString().equals(pagination.getSortColumn())) {
				sort = "clicks";
			} else if (RecruitingCampaignPagination.SORTS.SIGNUPS.toString().equals(pagination.getSortColumn())) {
				sort = "users";
			}
		}

		sql.append(" ORDER BY ")
				.append(sort)
				.append(Pagination.SORT_DIRECTION.DESC.equals(pagination.getSortDirection()) ? " DESC" : " ASC")
				.append(" LIMIT ")
				.append(pagination.getStartRow())
				.append(", ")
				.append(pagination.getResultsLimit());

		pagination.setResults(jdbcTemplate.query(sql.toString(), params, mapper));

		return pagination;
	}

	@Override
	public int countCampaignsForCompany(Long companyId) {
		return ((Long) getFactory().getCurrentSession()
				.createQuery("select count(rc) from recruitingCampaign rc where rc.company.id = :companyId and active = 1 and deleted = false")
				.setParameter("companyId", companyId)
				.uniqueResult())
				.intValue();
	}

	@Override
	public boolean existCampaignsForCompanyAndTitle(Long companyId, String campaignTitle) {
		return (((Long) getFactory().getCurrentSession()
				.createQuery("select count(rc) from recruitingCampaign rc where rc.title = :campaignTitle and rc.company.id = :companyId and active = 1 and deleted = false")
				.setParameter("companyId", companyId)
				.setParameter("campaignTitle", campaignTitle)
				.uniqueResult())
				.intValue() > 0);
	}

	@Override
	public int countClicksByRecruitingCampaign(Long recruitingCampaignId) {
		return ((Long) getFactory().getCurrentSession()
				.createQuery("select count(*) from impression im where im.campaignId = :recruitingCampaignId")
				.setParameter("recruitingCampaignId", recruitingCampaignId)
				.uniqueResult())
				.intValue();
	}

	@Override
	public int countUsersByRecruitingCampaign(Long recruitingCampaignId) {
		return ((Long) getFactory().getCurrentSession()
				.createQuery("select count(*) from user u where u.recruitingCampaign.id = :recruitingCampaignId")
				.setParameter("recruitingCampaignId", recruitingCampaignId)
				.uniqueResult())
				.intValue();
	}
}
