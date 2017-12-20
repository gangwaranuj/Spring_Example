package com.workmarket.service.business;

import com.workmarket.domains.model.comment.ClientServiceCompanyComment;
import com.workmarket.domains.model.comment.ClientServiceUserComment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.comment.CompanyUserComment;
import com.workmarket.service.business.dto.CompanyCommentDTO;
import com.workmarket.service.business.dto.UserCommentDTO;

/**
 * Comment service provides single access point to consumer service and company tags.
 */
public interface CommentService {

	/**
	 * Saves or updates a client service user comment. Please, provide userId as well as comment text. Comment text cannot be empty
	 *
	 * @param userCommentDTO
	 */
	ClientServiceUserComment saveOrUpdateClientServiceUserComment(UserCommentDTO userCommentDTO);

	/**
	 * Returns all client service user comments for a user that are active (not deleted)
	 *
	 * @param userId
	 * @param pagination
	 * @return pagination
	 */
	CommentPagination findAllActiveClientServiceUserComments(Long userId, CommentPagination pagination);

	/**
	 * Saves client service company comment
	 *
	 * @param companyCommentDTO
	 */
	ClientServiceCompanyComment saveOrUpdateClientServiceCompanyComment(CompanyCommentDTO companyCommentDTO);

	/**
	 * Returns all active client service company comments.
	 *
	 * @param companyId
	 * @param pagination
	 * @return list of comments | empty list
	 */
	CommentPagination findAllActiveClientServiceCompanyComments(Long companyId, CommentPagination pagination);

	/**
	 * Deletes a comment by setting the deleted property to true
	 *
	 * @param commentId
	 */
	void deleteComment(Long commentId);

	/**
	 * Saves company user tag. These tags are visible for all company users.
	 *
	 * @param companyId      - id of the company that the comment belongs to
	 * @param userCommentDTO
	 * @return comment object
	 */
	CompanyUserComment saveOrUpdateCompanyUserComment(Long companyId, UserCommentDTO userCommentDTO);

	/**
	 * Finds all active (not deleted) company user tags.
	 *
	 * @param commentatorCompanyId - company that owns the comment
	 * @param userId               - user being commented on
	 * @param pagination           @return list of comments | empty list
	 */
	CommentPagination findAllActiveCompanyUserComments(Long commentatorCompanyId, Long userId, CommentPagination pagination);
}
