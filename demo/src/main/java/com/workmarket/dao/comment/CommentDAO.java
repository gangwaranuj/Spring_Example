package com.workmarket.dao.comment;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.comment.ClientServiceCompanyComment;
import com.workmarket.domains.model.comment.ClientServiceUserComment;
import com.workmarket.domains.model.comment.Comment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.comment.CompanyUserComment;

public interface CommentDAO extends PaginatableDAOInterface<Comment> {

	ClientServiceUserComment findClientServiceUserCommentById(Long clientServiceUserCommentId);

	CommentPagination findAllActiveClientServiceUserComments(Long userId, CommentPagination pagination);

	ClientServiceCompanyComment findClientServiceCompanyCommentById(Long clientServiceCompanyId);

	CommentPagination findAllActiveClientServiceCompanyComments(Long companyId, CommentPagination pagination);

	void deleteComment(Long commentId);

	CompanyUserComment findCompanyUserCommentById(Long commentId);

	CommentPagination findAllActiveCompanyUserComments(Long commentatorCompanyId, Long userId, CommentPagination pagination);
}
