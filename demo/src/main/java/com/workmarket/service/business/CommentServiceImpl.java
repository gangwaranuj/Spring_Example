package com.workmarket.service.business;

import com.workmarket.dao.comment.CommentDAO;
import com.workmarket.domains.model.comment.ClientServiceCompanyComment;
import com.workmarket.domains.model.comment.ClientServiceUserComment;
import com.workmarket.domains.model.comment.CommentPagination;
import com.workmarket.domains.model.comment.CompanyUserComment;
import com.workmarket.service.business.dto.CompanyCommentDTO;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.utility.BeanUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired private UserService userService;
	@Autowired private CommentDAO commentDAO;
	@Autowired private CompanyService companyService;

	@Override
	public ClientServiceUserComment saveOrUpdateClientServiceUserComment(UserCommentDTO userCommentDTO) {
		Assert.notNull(userCommentDTO, "Invalid comment data");
		Assert.notNull(userCommentDTO.getUserId(), "Invalid user");
		Assert.hasText(userCommentDTO.getComment(), "Comment text missing");

		ClientServiceUserComment comment = null;
		if (userCommentDTO.getCommentId() == null) {
			// new
			comment = new ClientServiceUserComment();
			comment.setUser(userService.getUser(userCommentDTO.getUserId()));
		} else {
			// update
			comment = commentDAO.findClientServiceUserCommentById(userCommentDTO.getCommentId());
			Assert.notNull(comment, "Comment was not found");
		}
		BeanUtilities.copyProperties(comment, userCommentDTO);
		commentDAO.saveOrUpdate(comment);
		return comment;
	}

	@Override
	public CommentPagination findAllActiveClientServiceUserComments(Long userId, CommentPagination pagination) {
		return commentDAO.findAllActiveClientServiceUserComments(userId, pagination);
	}

	@Override
	public ClientServiceCompanyComment saveOrUpdateClientServiceCompanyComment(CompanyCommentDTO companyCommentDTO) {
		Assert.notNull(companyCommentDTO, "Invalid comment data");
		Assert.notNull(companyCommentDTO.getCompanyId(), "Invalid company");
		Assert.hasText(companyCommentDTO.getComment(), "Comment text missing");

		ClientServiceCompanyComment comment = null;
		if (companyCommentDTO.getCommentId() == null) {
			// new
			comment = new ClientServiceCompanyComment();
			comment.setCompany(companyService.findCompanyById(companyCommentDTO.getCompanyId()));
		} else {
			// update
			comment = commentDAO.findClientServiceCompanyCommentById(companyCommentDTO.getCommentId());
			Assert.notNull(comment, "Comment was not found");
		}
		BeanUtilities.copyProperties(comment, companyCommentDTO);
		commentDAO.saveOrUpdate(comment);
		return comment;
	}

	@Override
	public CommentPagination findAllActiveClientServiceCompanyComments(Long companyId, CommentPagination pagination) {
		return commentDAO.findAllActiveClientServiceCompanyComments(companyId, pagination);
	}

	@Override
	public void deleteComment(Long commentId) {
		commentDAO.deleteComment(commentId);
	}

	@Override
	public CompanyUserComment saveOrUpdateCompanyUserComment(Long companyId, UserCommentDTO userCommentDTO) {
		Assert.notNull(companyId, "Invalid company id");
		Assert.notNull(userCommentDTO, "Invalid comment data");
		Assert.notNull(userCommentDTO.getUserId(), "Invalid company");
		Assert.hasText(userCommentDTO.getComment(), "Comment text missing");

		CompanyUserComment comment = null;
		if (userCommentDTO.getCommentId() == null) {
			// new
			comment = new CompanyUserComment();
			comment.setUser(userService.getUser(userCommentDTO.getUserId()));
			comment.setCommentatorCompany(companyService.findCompanyById(companyId));
		} else {
			// update
			comment = commentDAO.findCompanyUserCommentById(userCommentDTO.getCommentId());
			Assert.notNull(comment, "Comment was not found");
		}
		BeanUtilities.copyProperties(comment, userCommentDTO);
		commentDAO.saveOrUpdate(comment);
		return comment;
	}

	@Override
	public CommentPagination findAllActiveCompanyUserComments(Long commentatorCompanyId, Long userId, CommentPagination pagination) {
		return commentDAO.findAllActiveCompanyUserComments(commentatorCompanyId, userId, pagination);
	}
}
