package com.workmarket.dao.comment;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.comment.ClientServiceCompanyComment;
import com.workmarket.domains.model.comment.ClientServiceUserComment;
import com.workmarket.domains.model.comment.Comment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.comment.CompanyUserComment;
import com.workmarket.utility.HibernateUtilities;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Map;

@SuppressWarnings("unchecked")
@Repository
public class CommentDAOImpl extends PaginationAbstractDAO<Comment> implements CommentDAO {
	@Override
	protected Class<Comment> getEntityClass() {
		return Comment.class;
	}

	@Override
	public ClientServiceUserComment findClientServiceUserCommentById(Long clientServiceUserCommentId) {
		Assert.notNull(clientServiceUserCommentId, "Invalid comment id");
		return (ClientServiceUserComment) getFactory().getCurrentSession().get(ClientServiceUserComment.class, clientServiceUserCommentId);
	}

	@Override
	public CommentPagination findAllActiveClientServiceUserComments(Long userId, CommentPagination pagination) {
		Assert.notNull(userId, "Invalid user id");
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientServiceUserComment.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(ClientServiceUserComment.class);

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "user.id", userId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "user.id", userId, "deleted", Boolean.FALSE);

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public ClientServiceCompanyComment findClientServiceCompanyCommentById(Long clientServiceCompanyId) {
		Assert.notNull(clientServiceCompanyId, "Invalid comment id");
		return (ClientServiceCompanyComment) getFactory().getCurrentSession().get(ClientServiceCompanyComment.class, clientServiceCompanyId);
	}

	@Override
	public CommentPagination findAllActiveClientServiceCompanyComments(Long companyId, CommentPagination pagination) {
		Assert.notNull(companyId, "Invalid user id");
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(ClientServiceCompanyComment.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(ClientServiceCompanyComment.class);

		// base restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "company.id", companyId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "company.id", companyId, "deleted", Boolean.FALSE);

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public void deleteComment(Long companyCommentId) {
		Assert.notNull(companyCommentId, "Invalid comment id");

		Comment comment = findCommentById(companyCommentId);

		Assert.notNull(comment, "Unable to find client service comment before deletion");
		Assert.isTrue(!comment.getDeleted(), "Comment already deleted");

		comment.setDeleted(true);
	}

	private Comment findCommentById(Long commentId) {
		Assert.notNull(commentId, "Invalid comment id");
		return (Comment) getFactory().getCurrentSession().get(Comment.class, commentId);
	}

	@Override
	public CompanyUserComment findCompanyUserCommentById(Long commentId) {
		Assert.notNull(commentId, "Invalid comment id");
		return (CompanyUserComment) getFactory().getCurrentSession().get(CompanyUserComment.class, commentId);
	}

	@Override
	public CommentPagination findAllActiveCompanyUserComments(Long commentatorCompanyId, Long userId, CommentPagination pagination) {
		Assert.notNull(commentatorCompanyId, "Invalid company id");
		Assert.notNull(userId, "Invalid user id");
		Assert.notNull(pagination, "Invalid pagination");

		Criteria criteria = getFactory().getCurrentSession().createCriteria(CompanyUserComment.class);
		Criteria count = getFactory().getCurrentSession().createCriteria(CompanyUserComment.class);

		// restrictions
		HibernateUtilities.addRestrictionsEq(criteria, "commentatorCompany.id", commentatorCompanyId, "user.id", userId, "deleted", Boolean.FALSE);
		HibernateUtilities.addRestrictionsEq(count, "commentatorCompany.id", commentatorCompanyId, "user.id", userId, "deleted", Boolean.FALSE);

		// count
		Long rowCount = HibernateUtilities.getRowCount(count);

		// sorts
		applySorts(pagination, criteria, count);

		// pagination
		HibernateUtilities.setupPagination(criteria, pagination.getStartRow(), pagination.getResultsLimit(), Pagination.MAX_ROWS);

		pagination.setRowCount(rowCount);
		pagination.setResults(criteria.list());

		return pagination;
	}

	@Override
	public void applySorts(Pagination<Comment> pagination, Criteria query, Criteria count) {
		if (pagination.getSortColumn() != null) {
			String sort = "id";

			if (CommentPagination.SORTS.valueOf(pagination.getSortColumn()) != null)
				sort = CommentPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn();

			if (pagination.getSortDirection().equals(Pagination.SORT_DIRECTION.DESC)) {
				query.addOrder(Order.desc(sort));
				count.addOrder(Order.desc(sort));
			} else {
				query.addOrder(Order.asc(sort));
				count.addOrder(Order.asc(sort));
			}
		}
	}

	@Override
	public void applyFilters(Pagination<Comment> pagination, Criteria query, Criteria count) {
	}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {
	}
}
