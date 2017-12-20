package com.workmarket.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationPagination;
import com.workmarket.domains.model.InvitationStatusType;
import com.workmarket.domains.model.Pagination;

@Repository
public class InvitationDAOImpl extends PaginationAbstractDAO<Invitation> implements
		InvitationDAO {

	protected Class<Invitation> getEntityClass() {
		return Invitation.class;
	}


	@SuppressWarnings("unchecked")
	public List<Invitation> findInvitations(Long inviterPK) {

		Query query = getFactory().getCurrentSession().getNamedQuery("invitation.findbyinviter");
		query.setLong("inviting_user_id", inviterPK);
		return query.list();

	}


	@Override
	@SuppressWarnings("unchecked")
	public List<Invitation> findInvitationsByStatus(String emailAddress, InvitationStatusType statusType) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("invitationStatusType.code", statusType.getCode()))
				.add(Restrictions.eq("email", emailAddress));
		return criteria.list();
	}

	public Integer countInvitationsByCompanyAndStatus(Long companyId, InvitationStatusType statusType) {
		return ((Long) getFactory().getCurrentSession()
				.createQuery("select count(i) from invitation i where i.company.id = :companyId and i.invitationStatusType.code = :statusTypeCode")
				.setParameter("companyId", companyId)
				.setParameter("statusTypeCode", statusType.getCode())
				.uniqueResult()).intValue();
	}

	@Override
	public Invitation findInvitationByRecruitingCampaign(Long recruitingCampaignId, String emailAddress) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("recruitingCampaign.id", recruitingCampaignId))
			.add(Restrictions.eq("email", emailAddress));

		return (Invitation) criteria.uniqueResult();
	}

	@Override
	public Invitation findInvitationByCompany(Long companyId, String emailAddress) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("email", emailAddress));

		return (Invitation) criteria.uniqueResult();
	}

	@Override
	public Invitation findInvitationById(Long invitationId) {
		Assert.notNull(invitationId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("company", FetchMode.JOIN)
				.setFetchMode("companyLogo", FetchMode.JOIN)
				.add(Restrictions.eq("id", invitationId));
		return (Invitation) criteria.uniqueResult();
	}

	public InvitationPagination findInvitations(Long inviterPk, InvitationPagination pagination) {
		Map<String, Object> params = new HashMap<>();
		params.put("inviter", inviterPk);

		return (InvitationPagination) super.paginationQuery(pagination, params);

	}


	public InvitationPagination findInvitationsByCompany(Long companyId, InvitationPagination pagination) {
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", companyId);

		return (InvitationPagination) super.paginationQuery(pagination, params);

	}


	public void applySorts(Pagination<Invitation> pagination, Criteria query, Criteria count) {

		if (pagination.getSortColumn() != null) {

			String sort = "id"; // default if no match found?
			if (pagination.getSortColumn().equals(InvitationPagination.SORTS.EMAIL.toString())) {
				sort = "email";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.INVITATION_DATE.toString())) {
				sort = "invitationDate";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.LAST_REMINDER_DATE.toString())) {
				sort = "lastReminderDate";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.USER_STATUS.toString())) {
				sort = "invitationStatusType.code";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.USER_TYPE.toString())) {
				sort = "invitationType.code";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.FIRST_NAME.toString())) {
				sort = "firstName";
			} else if (pagination.getSortColumn().equals(InvitationPagination.SORTS.LAST_NAME.toString())) {
				sort = "lastName";
			}

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
			}
		}

	}


	public void applyFilters(Pagination<Invitation> pagination, Criteria criteria, Criteria count) {

		DateFormat df = new SimpleDateFormat("MM/dd/yy");

		if (pagination.getFilters() != null) {

			if (pagination.hasFilter(InvitationPagination.FILTER_KEYS.BEFORE)) {
				Date before;
				String dateToParse = pagination.getFilter(InvitationPagination.FILTER_KEYS.BEFORE);
				try {
					before = df.parse(dateToParse);
				} catch (ParseException e) {
					throw new IllegalArgumentException("cannot convert string to date " + dateToParse, e);
				}
				Calendar beforeCal = Calendar.getInstance();
				beforeCal.setTime(before);
				criteria.add(Restrictions.lt("invitationDate", beforeCal));
				count.add(Restrictions.lt("invitationDate", beforeCal));
			}

			if (pagination.hasFilter(InvitationPagination.FILTER_KEYS.SINCE)) {
				String sinceDateString = pagination.getFilter(InvitationPagination.FILTER_KEYS.SINCE);
				Date since;
				try {
					since = df.parse(sinceDateString);
				} catch (ParseException e) {
					throw new IllegalArgumentException("Cannot convert since date string " + sinceDateString, e);
				}
				Calendar sinceCal = Calendar.getInstance();
				sinceCal.setTime(since);
				criteria.add(Restrictions.gt("invitationDate", sinceCal));
				count.add(Restrictions.gt("invitationDate", sinceCal));
			}

			if (pagination.hasFilter(InvitationPagination.FILTER_KEYS.USER_STATUS)) {
				String statusCode = pagination.getFilter(InvitationPagination.FILTER_KEYS.USER_STATUS);
				criteria.add(Restrictions.eq("invitationStatusType.code", statusCode));
				count.add(Restrictions.eq("invitationStatusType.code", statusCode));
			}

			if (pagination.hasFilter(InvitationPagination.FILTER_KEYS.USER_TYPE)) {
				String typeCode = pagination.getFilter(InvitationPagination.FILTER_KEYS.USER_TYPE);
				criteria.add(Restrictions.eq("invitationType.code", typeCode));
				count.add(Restrictions.eq("invitationType.code", typeCode));
			}
		}
	}


	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {

		query.setFetchMode("invitedUser", FetchMode.JOIN);
		query.setFetchMode("invitingUser", FetchMode.JOIN);

		if (params.containsKey("companyId")) {
			query.add(Restrictions.eq("company.id", params.get("companyId")));
			count.add(Restrictions.eq("company.id", params.get("companyId")));
		} else {
			query.add(Restrictions.eq("invitingUser.id", params.get("inviter")));
			count.add(Restrictions.eq("invitingUser.id", params.get("inviter")));
		}

	}


	@Override
	public Integer countInvitationsByCompanyStatusAndDate(Long companyId, String invitationStatusType, Calendar date) {
		return ((Long) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("invitationStatusType.code", invitationStatusType))
				.add(Restrictions.ge("invitationDate", date))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();

	}
}
