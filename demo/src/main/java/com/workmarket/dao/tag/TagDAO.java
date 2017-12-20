package com.workmarket.dao.tag;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tag.CompanyAdminTag;
import com.workmarket.domains.model.tag.CompanyTag;
import com.workmarket.domains.model.tag.Tag;
import com.workmarket.domains.model.tag.TagPagination;


public interface TagDAO extends DAOInterface<Tag> {

	Tag findTagById(Long tagId);

	TagPagination findAllTags(TagPagination pagination);

	Tag findTagByName(String name);

	CompanyTag findCompanyTagByName(String tagName);

	CompanyAdminTag findCompanyAdminTagByName(String name);
}
