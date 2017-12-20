package com.workmarket.service.business;

import com.workmarket.domains.model.tag.Tag;
import com.workmarket.domains.model.tag.TagPagination;
import com.workmarket.service.business.dto.TagDTO;
import com.workmarket.service.infra.security.RequestContext;

public interface TagService {
	/**
	 * Finds a tag by id, it might be a generic, product or brand tag
	 *
	 * @param tagId
	 * @return Tag entity
	 */
	Tag findTagById(Long tagId);

	/**
	 * Finds all tags using pagination
	 *
	 * @param pagination
	 * @return pagination
	 */
	TagPagination findAllTags(TagPagination pagination);

	/**
	 * Finds a tag by name
	 *
	 * @param name
	 * @return tag entity
	 */
	Tag findTagByName(String name);

	/**
	 * Create a generic tag
	 *
	 * @param tagDTO
	 * @return tag entity
	 */
	Tag createTag(TagDTO tagDTO);

	/**
	 * Marks a tag as deleted
	 *
	 * @param tagId
	 */
	void deleteTagById(Long tagId);

	/**
	 * Sets approval status of a tag to be used by consumer service
	 *
	 * @param tagId
	 */
	void approveTag(Long tagId);

	/**
	 * Finds all user tags
	 *
	 * @param userId
	 * @param pagination
	 * @return pagination
	 */
	TagPagination findAllUserTags(Long userId, TagPagination pagination);

	/**
	 * Returns unique company tag names. These tags are on company level and are available to all employees of the
	 * company.
	 *
	 * @param companyId
	 * @param userId
	 * @param requestContext
	 * @return array of tag names
	 */
	String[] findAllUniqueCompanyUserTagNames(Long companyId, Long userId, RequestContext requestContext);

	/**
	 * Sets company user tags. There tags are only visible to the employees of a company, and not to the tagged user
	 *
	 * @param companyId
	 * @param userId
	 * @param tagNames
	 */
	void setCompanyTags(Long companyId, Long userId, String[] tagNames);

	/**
	 * Returns unique company admin tag names. These tags are on company level and are available to all employees of the
	 * company.
	 *
	 * @param companyId
	 * @param userId
	 * @param requestContext
	 * @return array of tag names
	 */
	String[] findAllUniqueCompanyAdminUserTagNames(Long companyId, Long userId, RequestContext requestContext);

	/**
	 * Sets company admin user tags. There tags are only visible to the employees of a company, and not to the tagged user
	 *
	 * @param companyId
	 * @param userId
	 * @param tagNames
	 */
	void setCompanyAdminTags(Long companyId, Long userId, String[] tagNames);

}
