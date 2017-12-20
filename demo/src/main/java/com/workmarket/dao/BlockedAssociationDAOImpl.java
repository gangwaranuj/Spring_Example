package com.workmarket.dao;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.model.BlockedCompanyUserAssociationPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.block.AbstractBlockedAssociation;
import com.workmarket.domains.model.block.BlockedCompanyCompanyAssociation;
import com.workmarket.domains.model.block.BlockedCompanyUserAssociation;
import com.workmarket.domains.model.block.BlockedUserCompanyAssociation;
import com.workmarket.domains.model.block.BlockedUserUserAssociation;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class BlockedAssociationDAOImpl extends AbstractDAO<AbstractBlockedAssociation> implements BlockedAssociationDAO {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Qualifier("readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplateReadOnly;

	protected Class<AbstractBlockedAssociation> getEntityClass() {
		return AbstractBlockedAssociation.class;
	}

	private static final String BLOCKED_USERS_SQL =
		" SELECT blocked_user_id " +
		" FROM blocked_user_association " +
		" WHERE user_id = :userId " +
		" AND deleted = 0 " +
		" AND type = 2 " +

		" UNION " +

		" SELECT b.blocked_user_id " +
		" FROM blocked_user_association b " +
		" INNER join user u on b.blocking_company_id = u.company_id " +
		" WHERE u.id = :userId " +
		" AND b.deleted = 0 " +
		" AND b.type = 2 ";

	private static final String BLOCKED_USER_NUMBERS_SQL =
		" SELECT blocked_user.user_number " +
			" FROM blocked_user_association bua, user blocked_user " +
			" WHERE user_id = :userId " +
			" AND deleted = 0 " +
			" AND type = 2 " +
			" AND blocked_user.id = bua.blocked_user_id " +

			" UNION " +

			" SELECT blocked_user.user_number " +
			" FROM blocked_user_association b " +
			" INNER join user u on b.blocking_company_id = u.company_id " +
			" INNER join user blocked_user on blocked_user.id = b.blocked_user_id " +
			" WHERE u.id = :userId " +
			" AND b.deleted = 0 " +
			" AND b.type = 2 ";

	private static final String FIND_BLOCKED_OR_BLOCKED_BY_COMPANY_IDS_SQL =
		" SELECT blocking_company_id " +
		" FROM blocked_user_association " +
		" WHERE blocked_user_id = :userId " +
		" AND deleted = 0 " +

		" UNION " +

		" SELECT blocked_company_id " +
		" FROM blocked_user_association " +
		" WHERE user_id = :userId " +
		" AND blocked_company_id IS NOT NULL " +
		" AND deleted = 0 ";

	private static final String IS_COMPANY_BLOCKED_BY_USER_SQL =
		" SELECT id " +
		" FROM blocked_user_association " +
		" WHERE deleted = 0 " +
		" AND user_id = :userId AND blocked_company_id = :blockedCompanyId " +
		" LIMIT 1" +

		" UNION " +

		" SELECT id " +
		" FROM blocked_user_association " +
		" WHERE deleted = 0 " +
		" AND blocking_company_id = :userCompanyId AND blocked_company_id = :blockedCompanyId " +
		" LIMIT 1 ";

	private static final String IS_USER_BLOCKED_BY_COMPANY =
		" SELECT id " +
		" FROM blocked_user_association " +
		" WHERE deleted = 0 " +
		" AND blocked_user_id = :userId AND blocking_company_id = :blockedCompanyId " +
		" LIMIT 1" +

		" UNION " +

		" SELECT id " +
		" FROM blocked_user_association " +
		" WHERE deleted = 0 " +
		" AND blocked_company_id = :userCompanyId AND blocking_company_id = :blockedCompanyId " +
		" LIMIT 1 ";

	private static final String BLOCKED_USERS =
		" SELECT blocked_user_id " +
			" FROM blocked_user_association " +
			" WHERE deleted = 0 " +
			" AND blocking_company_id = :blockingCompanyId ";

	private static final String BLOCKED_COMPANIES =
		" SELECT blocked_company_id " +
			" FROM blocked_user_association " +
			" WHERE deleted = 0 " +
			" AND blocking_company_id = :blockingCompanyId ";

	private static final String IS_COMPANY_BLOCKED_BY_COMPANY =
			" SELECT id " +
			" FROM blocked_user_association " +
			" WHERE deleted = 0 " +
			" AND blocking_company_id = :blockingCompanyId AND blocked_company_id = :blockedCompanyId " +
			" LIMIT 1 ";

	private static final String IS_USER_BLOCKED_FOR_COMPANY_SQL = IS_COMPANY_BLOCKED_BY_USER_SQL + " UNION " + IS_USER_BLOCKED_BY_COMPANY;

	@Override
	public BlockedUserUserAssociation findActiveByUserAndBlockedUser(Long userId, Long blockedUserId) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedUserUserAssociation.class);
		criteria.add(Restrictions.eq("user.id", userId))
			.add(Restrictions.eq("blockedUser.user.id", blockedUserId))
			.add(Restrictions.eq("deleted", false)).setMaxResults(1);

		return (BlockedUserUserAssociation) criteria.uniqueResult();
	}

	@Override
	public BlockedUserCompanyAssociation findByCompanyIdAndBlockedUser(Long companyId, Long blockedUserId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedUserCompanyAssociation.class);
		criteria.add(Restrictions.eq("blockingCompany.company.id", companyId))
			.add(Restrictions.eq("blockedUser.user.id", blockedUserId))
			.setMaxResults(1);

		return (BlockedUserCompanyAssociation) criteria.uniqueResult();
	}

	@Override
	public BlockedCompanyUserAssociation findByUserIdAndBlockedCompanyId(Long userId, Long blockedCompanyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedCompanyUserAssociation.class);
		criteria.add(Restrictions.eq("user.id", userId))
				.add(Restrictions.eq("blockedCompany.company.id", blockedCompanyId))
				.setMaxResults(1);

		return (BlockedCompanyUserAssociation) criteria.uniqueResult();
	}

	@Override
	public BlockedCompanyCompanyAssociation findByCompanyIdAndBlockedCompanyId(Long blockingCompanyId, Long blockedCompanyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedCompanyCompanyAssociation.class);
		criteria.add(Restrictions.eq("blockingCompany.company.id", blockingCompanyId))
			.add(Restrictions.eq("blockedCompany.company.id", blockedCompanyId))
			.setMaxResults(1);

		return (BlockedCompanyCompanyAssociation) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BlockedUserUserAssociation> findAllBlockedUsersByUser(Long userId, Long companyId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedUserUserAssociation.class);
		criteria.add(Restrictions.eq("deleted", false))
				.add(Restrictions.or(Restrictions.eq("user.id", userId), Restrictions.eq("blockingCompany.company.id", companyId)));

		return criteria.list();
	}

	@Override
	public List<Long> findAllBlockedUserIdsByBlockingUserId(Long userId) {
		return jdbcTemplateReadOnly.queryForList(
			BLOCKED_USERS_SQL, new MapSqlParameterSource("userId", userId), Long.class
		);
	}

	@Override
	public List<String> findAllBlockedUserNumbersByBlockingUserId(Long userId) {
		return jdbcTemplateReadOnly.queryForList(
			BLOCKED_USER_NUMBERS_SQL, new MapSqlParameterSource("userId", userId), String.class
		);
	}

	@Override
	public List<Long> findBlockedOrBlockedByCompanyIdsByUserId(Long userId) {
		return jdbcTemplateReadOnly.queryForList(
			FIND_BLOCKED_OR_BLOCKED_BY_COMPANY_IDS_SQL, new MapSqlParameterSource("userId", userId), Long.class
		);
	}

	@Override
	public int deleteAllBlockedUserUserAssociationByBlockedUserAndCompanyId(Long userId, Long companyId) {
		String sql =
			" UPDATE    blocked_user_association  \n" +
				" INNER     JOIN user u ON u.id = blocked_user_association.user_id \n" +
				" SET       blocked_user_association.deleted = true \n" +
				" WHERE     blocked_user_association.blocked_user_id = :userId \n" +
				" AND       u.company_id = :companyId \n" +
				" AND       blocked_user_association.deleted = false \n";

		MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
		sqlParameterSource.addValue("userId", userId);
		sqlParameterSource.addValue("companyId", companyId);

		return jdbcTemplate.update(sql, sqlParameterSource);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BlockedCompanyUserAssociation> findAllBlockedCompanyUserAssociationByBlockedCompanyAndBlockingCompany(Long blockingCompanyId, Long blockedCompanyId) {
		return getFactory().getCurrentSession().createCriteria(BlockedCompanyUserAssociation.class)
				.setFetchMode("user", FetchMode.JOIN)
				.createAlias("user", "u")
				.add(Restrictions.eq("u.company.id", blockingCompanyId))
				.add(Restrictions.eq("blockedCompany.company.id", blockedCompanyId))
				.add(Restrictions.eq("deleted", false)).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public BlockedCompanyUserAssociationPagination findAllBlockedCompaniesByUser(Long userId, Long companyId, BlockedCompanyUserAssociationPagination pagination) {
		Assert.notNull(userId, "User id is required");
		Assert.notNull(companyId, "Company id is required");
		Criteria criteria = getFactory().getCurrentSession().createCriteria(BlockedCompanyUserAssociation.class)
				.setFetchMode("blockedCompany.company", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.or(
					Restrictions.eq("user.id", userId),
					Restrictions.eq("blockingCompany.company.id", companyId)
				));
		Criteria count = getFactory().getCurrentSession().createCriteria(BlockedCompanyUserAssociation.class)
				.setFetchMode("blockedCompany.company", FetchMode.JOIN)
				.setFetchMode("user", FetchMode.JOIN)
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.or(
					Restrictions.eq("user.id", userId),
					Restrictions.eq("blockingCompany.company.id", companyId)
				));

		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		Long rowCount = HibernateUtilities.getRowCount(count);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public boolean isUserBlockedForCompany(Long userId, Long userCompanyId, Long blockCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			IS_USER_BLOCKED_FOR_COMPANY_SQL, createQueryParams(userId, userCompanyId, blockCompanyId), Integer.class
		).size() > 0;
	}

	@Override
	public boolean isCompanyBlockedByUser(Long userId, Long userCompanyId, Long blockedCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			IS_COMPANY_BLOCKED_BY_USER_SQL, createQueryParams(userId, userCompanyId, blockedCompanyId), Integer.class
		).size() > 0;
	}

	@Override
	public boolean isUserBlockedByCompany(Long userId, Long userCompanyId, Long blockingCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			IS_USER_BLOCKED_BY_COMPANY, createQueryParams(userId, userCompanyId, blockingCompanyId), Integer.class
		).size() > 0;
	}

	@Override
	public List<Long> listBlockedCompanies(Long blockingCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			BLOCKED_COMPANIES, createQueryParams(blockingCompanyId), Long.class
		);
	}

	@Override
	public List<Long> listBlockedUsers(Long blockingCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			BLOCKED_USERS, createQueryParams(blockingCompanyId), Long.class
		);
	}

	@Override
	public boolean isVendorBlockedByCompany(Long blockingCompanyId, Long blockedCompanyId) {
		return jdbcTemplateReadOnly.queryForList(
			IS_COMPANY_BLOCKED_BY_COMPANY, createQueryParams(blockingCompanyId, blockedCompanyId), Integer.class
		).size() > 0;
	}

	private MapSqlParameterSource createQueryParams(Long userId, Long userCompanyId, Long blockedCompanyId) {
		return new MapSqlParameterSource(ImmutableMap.of(
			"userId", userId,
			"userCompanyId", userCompanyId,
			"blockedCompanyId", blockedCompanyId
		));
	}

	private MapSqlParameterSource createQueryParams(Long blockingCompanyId, Long blockedCompanyId) {
		return new MapSqlParameterSource(ImmutableMap.of(
			"blockingCompanyId", blockingCompanyId,
			"blockedCompanyId", blockedCompanyId
		));
	}

	private MapSqlParameterSource createQueryParams(Long blockingCompanyId) {
		return new MapSqlParameterSource(ImmutableMap.of(
			"blockingCompanyId", blockingCompanyId
		));
	}

}
