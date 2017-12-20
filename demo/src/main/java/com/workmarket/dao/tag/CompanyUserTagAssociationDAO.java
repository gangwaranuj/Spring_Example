package com.workmarket.dao.tag;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tag.CompanyAdminTag;
import com.workmarket.domains.model.tag.CompanyTag;
import com.workmarket.domains.model.tag.CompanyUserTagAssociation;

import java.util.List;

public interface CompanyUserTagAssociationDAO extends DAOInterface<CompanyUserTagAssociation> {

	CompanyUserTagAssociation findByIds(Long companyId, Long userId, Long tagId);

	String[] findAllUniqueCompanyUserTagNames(Long companyId, Long userId);

	void setCompanyTags(Long companyId, Long userId, List<CompanyTag> tags);

	String[] findAllUniqueCompanyAdminUserTagNames(Long companyId, Long userId);

	void setCompanyAdminTags(Long companyId, Long userId, List<CompanyAdminTag> tags);
}
