package com.workmarket.dao.note;

import com.workmarket.dao.DeletableAbstractDAO;
import com.workmarket.dao.ModifiedBeanPropertyRowMapper;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.PrivacyType;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkMessagePagination;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;
import com.workmarket.utility.sql.SQLBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class NoteDAOImpl extends DeletableAbstractDAO<Note> implements NoteDAO {
	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	protected Class<Note> getEntityClass() {
		return Note.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public NotePagination findAllNotesByWorkIdForCompany(Long workId, Long companyId, NotePagination pagination) {
		SQLBuilder sqlBuilder = newWorkNoteSQLBuilder(workId)
				.addParam("companyId", companyId);

		if (pagination.isIncludePrivileged()) {
			sqlBuilder
					.addWhereClause("(u.company_id = :companyId OR (n.privacy_type = 'PUBLIC' OR n.privacy_type = 'PRIVILEGED'))");
		} else {
			sqlBuilder
					.addWhereClause("(u.company_id = :companyId OR n.privacy_type = 'PUBLIC')");
		}

		applyWorkNotePagination(sqlBuilder, pagination);
		List<Note> results = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new ModifiedBeanPropertyRowMapper(Note.class));
		pagination.setResults(results);
		pagination.setRowCount(results.size());

		return pagination;
	}

	@Override
	public WorkMessagePagination findAllTopMessagesVisibleToUser(Long workId, Long userId, WorkMessagePagination pagination, boolean shouldDenyMessages, Boolean shouldShowPublicAndPrivilegedMessages) {
		SQLBuilder sqlBuilder = newWorkNoteSQLBuilder(workId)
				.addParam("userId", userId)
				.addColumn("EXISTS(SELECT id FROM note_metadata WHERE parent_id = n.id AND deleted = false) AS parent")
				.addJoin("INNER JOIN work_top_note_association top ON w.id = top.work_id AND top.note_id = n.id");

		return findMessages(sqlBuilder, userId, pagination, shouldDenyMessages, shouldShowPublicAndPrivilegedMessages);
	}

	@Override
	public WorkMessagePagination findAllMessagesVisibleToUser(Long workId, Long userId, Long parentNoteId, WorkMessagePagination pagination, boolean shouldDenyMessages, Boolean shouldShowPublicAndPrivilegedMessages) {
		SQLBuilder sqlBuilder = newWorkNoteSQLBuilder(workId)
				.addColumn("meta.parent_id as parentId");

		if (parentNoteId != null) {
			sqlBuilder.addWhereClause(" parent_id = :parentNoteId")
					.addParam("parentNoteId", parentNoteId);
		}

		return findMessages(sqlBuilder, userId, pagination, shouldDenyMessages, shouldShowPublicAndPrivilegedMessages);
	}

	private WorkMessagePagination findMessages(SQLBuilder sqlBuilder, Long userId, WorkMessagePagination pagination, boolean shouldDenyMessages, boolean shouldShowPublicAndPrivilegedMessages) {
		if (shouldDenyMessages) {
			//If not in any of those contexts, we shouldn't return anything
			return pagination;
		}
		if (shouldShowPublicAndPrivilegedMessages) {
			sqlBuilder.addWhereClause("(n.privacy_type = 'PUBLIC' OR (n.privacy_type = 'PRIVILEGED' AND (u.id = :userId OR reply_to_id = :userId)))")
					.addParam("userId", userId);
		}
		applyWorkNotePagination(sqlBuilder, pagination);
		List<Note> results = jdbcTemplate.query(sqlBuilder.build(), sqlBuilder.getParams(), new ModifiedBeanPropertyRowMapper(Note.class));
		pagination.setResults(results);
		pagination.setRowCount(results.size());

		return pagination;
	}

	private SQLBuilder newWorkNoteSQLBuilder(Long workId) {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder
				.addColumns("n.*, n.note_content AS content, n.privacy_type AS privacy, n.work_id AS work")
				.addColumns("u.user_number AS creatorNumber, modifier.user_number AS modifierNumber")
				.addColumns("onBehalf.user_number AS onBehalfUserNumber", "onBehalf.first_name AS onBehalfFirstName", "onBehalf.last_name AS onBehalfLastName")
				.addTable("note n")
				.addJoin("INNER JOIN work w ON w.id = n.work_id")
				.addJoin("INNER JOIN user u ON u.id = n.creator_id")
				.addJoin("INNER JOIN user modifier ON modifier.id = n.modifier_id")
				.addJoin("INNER JOIN company c on c.id = u.company_id") // creator company id
				.addJoin("LEFT JOIN note_metadata meta ON meta.note_id = n.id")
				.addJoin("LEFT JOIN user onBehalf ON meta.on_behalf_of_user_id = onBehalf.id")
				.addWhereClause("n.work_id = :workId")
				.addWhereClause("n.deleted = false")
				.addParam("workId", workId);
		return sqlBuilder;
	}

	private void applyWorkNotePagination(SQLBuilder sqlBuilder, NotePagination pagination) {
		if (pagination.hasFilter(NotePagination.FILTER_KEYS.PRIVATE)) {
			Boolean isPrivate = Boolean.parseBoolean(pagination.getFilter(NotePagination.FILTER_KEYS.PRIVATE));
			PrivacyType privacy = (isPrivate) ? PrivacyType.PRIVATE : PrivacyType.PUBLIC;

			sqlBuilder.addWhereClause("n.privacy_type = :privacy").addParam("privacy", privacy.toString());
		}

		if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
			sqlBuilder.addDescOrderBy("n.created_on");
		} else {
			sqlBuilder.addAscOrderBy("n.created_on");
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ConcernPagination findAllConcerns(Class<? extends Concern> clazz, ConcernPagination pagination) {
		Assert.isAssignable(Concern.class, clazz);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(clazz)
				.createAlias("resolvedBy", "resolvedBy", Criteria.LEFT_JOIN)
				.add(Restrictions.eq("deleted", false));

		if (pagination.getStartRow() != null) {
			criteria.setFirstResult(pagination.getStartRow());
		}

		if (pagination.getResultsLimit() != null) {
			criteria.setMaxResults(pagination.getResultsLimit());
		}

		Criteria count = getFactory().getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("deleted", false))
				.setProjection(Projections.rowCount());

		String sort = "id";

		if (pagination.getSortColumn() != null) {
			if (pagination.getSortColumn().equals(ConcernPagination.SORTS.TYPE.toString())) {
				sort = "class";
			} else if (pagination.getSortColumn().equals(ConcernPagination.SORTS.CONTENT.toString())) {
				sort = "content";
			} else if (pagination.getSortColumn().equals(ConcernPagination.SORTS.CREATED_ON.toString())) {
				sort = "id";
			}
		}

		if (pagination.getSortDirection() != null)
			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				criteria.addOrder(Order.desc(sort));
			} else {
				criteria.addOrder(Order.asc(sort));
			}
		else
			criteria.addOrder(Order.desc(sort));

		if (pagination.getFilters() != null) {
			if (pagination.hasFilter(ConcernPagination.FILTER_KEYS.RESOLVED)) {

				Boolean resolved = Boolean.parseBoolean(pagination.getFilter(ConcernPagination.FILTER_KEYS.RESOLVED));

				criteria.add(Restrictions.eq("resolved", resolved));
				count.add(Restrictions.eq("resolved", resolved));
			}
		}

		pagination.setResults(criteria.list());
		if (count.list().size() > 0)
			pagination.setRowCount(((Long) count.list().get(0)).intValue());
		else
			pagination.setRowCount(0);

		return pagination;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Note> T findById(Class<? extends Note> clazz, Long id) {
		Assert.isAssignable(Note.class, clazz);
		return (T) getFactory().getCurrentSession().get(clazz, id);
	}
}
