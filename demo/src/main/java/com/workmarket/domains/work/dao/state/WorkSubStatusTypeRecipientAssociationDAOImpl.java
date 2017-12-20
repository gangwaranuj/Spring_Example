package com.workmarket.domains.work.dao.state;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeRecipientAssociation;
import com.workmarket.utility.HibernateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class WorkSubStatusTypeRecipientAssociationDAOImpl extends AbstractDAO<WorkSubStatusTypeRecipientAssociation> implements WorkSubStatusTypeRecipientAssociationDAO {

	@Autowired
	@Resource(name = "readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<WorkSubStatusTypeRecipientAssociation> getEntityClass() { return WorkSubStatusTypeRecipientAssociation.class; }

	@Override
	public List<Long> findRecipientsByWorkSubStatusId(Long workSubStatusId) {
		SQLBuilder sql = new SQLBuilder()
			.addColumn("this.user_id")
			.addTable("work_sub_status_type_to_user_association this")
			.addWhereClause("this.work_sub_status_type_id", "=", "workSubStatusId", workSubStatusId);

		return jdbcTemplate.queryForList(sql.build(), sql.getParams(), Long.class);
	}

	@Override
	public List<Long> findRecipientsByWorkSubStatusCodeAndCompanyId(String workSubStatusCode, Long companyId) {
		SQLBuilder sql = new SQLBuilder()
			.addColumn("a.user_id")
			.addTable("work_sub_status_type t")
			.addJoin("INNER JOIN work_sub_status_type_to_user_association a ON t.id = a.work_sub_status_type_id")
			.addWhereClause("t.code", "=", "workSubStatusCode", workSubStatusCode)
			.addWhereClause("t.company_id", "=", "companyId", companyId);

		return jdbcTemplate.queryForList(sql.build(), sql.getParams(), Long.class);
	}

	@Override
	public List<WorkSubStatusTypeRecipientAssociation> findAssociationsByWorkSubStatusId(Long workSubStatusId) {
		return HibernateUtilities.listAndCast(getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("workSubStatusType.id", workSubStatusId))
			.add(Restrictions.eq("deleted", false)));
	}

	@Override
	public WorkSubStatusTypeRecipientAssociation findUniqueAssociationByUserIdAndWorkSubStatusId(Long userId, Long workSubStatusId) {
		return (WorkSubStatusTypeRecipientAssociation) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("recipient.id", userId))
			.add(Restrictions.eq("workSubStatusType.id", workSubStatusId))
			.setMaxResults(1).uniqueResult();
	}
}
