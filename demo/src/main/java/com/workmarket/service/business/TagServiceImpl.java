package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.tag.CompanyUserTagAssociationDAO;
import com.workmarket.dao.tag.TagDAO;
import com.workmarket.dao.tag.UserTagAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tag.CompanyAdminTag;
import com.workmarket.domains.model.tag.CompanyTag;
import com.workmarket.domains.model.tag.Tag;
import com.workmarket.domains.model.tag.TagPagination;
import com.workmarket.service.business.dto.CompanyTagDTO;
import com.workmarket.service.business.dto.TagDTO;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.ArrayUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

	@Autowired private UserService userService;
	@Autowired private TagDAO tagDAO;
	@Autowired private UserTagAssociationDAO userTagAssociationDAO;
	@Autowired private CompanyUserTagAssociationDAO companyUserTagAssociationDAO;
	@Autowired private UserIndexer userIndexer;

	@Override
	public Tag findTagById(Long tagId) {
		return tagDAO.findTagById(tagId);
	}

	@Override
	public TagPagination findAllTags(TagPagination pagination) {
		return tagDAO.findAllTags(pagination);
	}

	@Override
	public Tag findTagByName(String name) {
		return tagDAO.findTagByName(name);
	}

	@Override
	public Tag createTag(TagDTO tagDTO) {
		Assert.notNull(tagDTO);
		Assert.hasText(tagDTO.getName());

		Tag tag = tagDAO.findTagByName(tagDTO.getName());

		if (tag == null) {
			tag = new Tag(tagDTO.getName().toLowerCase());
			tagDAO.saveOrUpdate(tag);
		}

		return tag;
	}

	@Override
	public void deleteTagById(Long tagId) {
		Assert.notNull(tagId);
		tagDAO.findTagById(tagId).setDeleted(true);
	}

	@Override
	public void approveTag(Long tagId) {
		Assert.notNull(tagId);

		Tag tag = tagDAO.findTagById(tagId);

		Assert.notNull(tag);

		Assert.state(tag.getApprovalStatus() != ApprovalStatus.APPROVED,
				"Tag is already approved!");

		tag.setApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Override
	public String[] findAllUniqueCompanyUserTagNames(Long companyId,
													 Long userId, RequestContext requestContext) {
		return companyUserTagAssociationDAO.findAllUniqueCompanyUserTagNames(
				companyId, userId);
	}

	@Override
	public void setCompanyTags(Long companyId, Long userId, String[] tagNames) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(tagNames);

		User user = userService.getUser(userId);

		Assert.notNull(user);

		String[] uniqueNames = ArrayUtilities.unique(tagNames);

		List<CompanyTag> tags = Lists.newLinkedList();
		for (String tagName : uniqueNames) {
			CompanyTagDTO dto = new CompanyTagDTO();
			dto.setName(tagName);
			tags.add(createCompanyTag(dto));
		}

		companyUserTagAssociationDAO.setCompanyTags(companyId, userId, tags);
		userIndexer.reindexById(userId);
	}

	@Override
	public TagPagination findAllUserTags(Long userId, TagPagination pagination) {
		return userTagAssociationDAO.findAllUserTags(userId, pagination);
	}

	private CompanyTag createCompanyTag(CompanyTagDTO tagDTO) {
		Assert.notNull(tagDTO);
		Assert.hasText(tagDTO.getName());

		CompanyTag tag = tagDAO.findCompanyTagByName(tagDTO.getName());

		if (tag == null) {
			tag = new CompanyTag(tagDTO.getName());
			tagDAO.saveOrUpdate(tag);
		}

		return tag;
	}

	@Override
	public String[] findAllUniqueCompanyAdminUserTagNames(Long companyId,
														  Long userId, RequestContext requestContext) {
		return companyUserTagAssociationDAO
				.findAllUniqueCompanyAdminUserTagNames(companyId, userId
				);
	}

	@Override
	public void setCompanyAdminTags(Long companyId, Long userId,
									String[] tagNames) {
		Assert.notNull(companyId);
		Assert.notNull(userId);
		Assert.notNull(tagNames);

		User user = userService.getUser(userId);

		Assert.notNull(user);

		String[] uniqueNames = ArrayUtilities.unique(tagNames);

		List<CompanyAdminTag> tags = Lists.newLinkedList();
		for (String tagName : uniqueNames) {
			CompanyTagDTO dto = new CompanyTagDTO();
			dto.setName(tagName);
			tags.add(createCompanyAdminTag(dto));
		}

		companyUserTagAssociationDAO.setCompanyAdminTags(companyId, userId,
				tags);
		userIndexer.reindexById(userId);
	}

	private CompanyAdminTag createCompanyAdminTag(CompanyTagDTO tagDTO) {
		Assert.notNull(tagDTO);
		Assert.hasText(tagDTO.getName());

		CompanyAdminTag tag = tagDAO
				.findCompanyAdminTagByName(tagDTO.getName());

		if (tag == null) {
			tag = new CompanyAdminTag(tagDTO.getName());
			tagDAO.saveOrUpdate(tag);
		}

		return tag;
	}
}
