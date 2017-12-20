package com.workmarket.dao.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.model.tag.CompanyAdminTag;
import com.workmarket.domains.model.tag.CompanyTag;
import com.workmarket.domains.model.tag.CompanyUserTagAssociation;
import com.workmarket.domains.model.tag.Tag;
import com.workmarket.utility.ArrayUtilities;

@Repository
public class CompanyUserTagAssociationDAOImpl extends AbstractDAO<CompanyUserTagAssociation> implements CompanyUserTagAssociationDAO {
	@Autowired
	private TagDAO tagDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private CompanyDAO companyDAO;

	protected Class<CompanyUserTagAssociation> getEntityClass() {
		return CompanyUserTagAssociation.class;
	}

	public CompanyUserTagAssociation findByIds(Long companyId, Long userId, Long tagId) {
		return (CompanyUserTagAssociation) getFactory().getCurrentSession()
				.getNamedQuery("CompanyUserTagAssociation.findByIds")
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.setParameter("tagId", tagId)
				.uniqueResult();
	}

	private void addCompanyTagToUser(Long companyId, Long tagId, Long userId)  {
		Assert.notNull(tagId);
		Assert.notNull(userId);
		Assert.notNull(companyId);

		Tag tag = tagDAO.findTagById(tagId);

		Assert.notNull(tag);
		Assert.state(!tag.getApprovalStatus().isDeclined(), tag.getName() + " is not allowed!");

		CompanyUserTagAssociation association = findByIds(companyId, userId, tagId);

		if (association == null) {

			association = new CompanyUserTagAssociation();
			association.setTag(tag);
			association.setUser(userDAO.get(userId));
			association.setCompany(companyDAO.findCompanyById(companyId));
		} else {
			association.setDeleted(false);
		}
		saveOrUpdate(association);
	}

	@Override
	public String[] findAllUniqueCompanyUserTagNames(Long companyId, Long userId) {
		Assert.notNull(userId);

		List<CompanyUserTagAssociation> tags = findAllActiveCompanyUserTagAssociations(companyId, userId);

		List<String> names = Lists.newLinkedList();

		for (CompanyUserTagAssociation association : tags) {
			names.add(association.getTag().getName());
		}

		return ArrayUtilities.sort(ArrayUtilities.unique(names.toArray(new String[names.size()])));
	}

	@Override
	public void setCompanyTags(Long companyId, Long userId, List<CompanyTag> tags)  {
		Assert.notNull(userId);
		Assert.notNull(tags);

		List<CompanyUserTagAssociation> associations = findAllActiveCompanyUserTagAssociations(companyId, userId);

		for (CompanyUserTagAssociation association : associations)
			association.setDeleted(true);

		for (Tag tag : tags) {
			addCompanyTagToUser(companyId, tag.getId(), userId);
		}
	}

	@Override
	public String[] findAllUniqueCompanyAdminUserTagNames(Long companyId, Long userId) {
		Assert.notNull(userId);

		List<CompanyUserTagAssociation> tags = findAllActiveCompanyAdminUserTagAssociations(companyId, userId);

		List<String> names = Lists.newLinkedList();

		for (CompanyUserTagAssociation association : tags) {
			names.add(association.getTag().getName());
		}

		return ArrayUtilities.sort(ArrayUtilities.unique(names.toArray(new String[names.size()])));
	}

	@Override
	public void setCompanyAdminTags(Long companyId, Long userId, List<CompanyAdminTag> tags)  {
		Assert.notNull(userId);
		Assert.notNull(tags);

		List<CompanyUserTagAssociation> associations = findAllActiveCompanyAdminUserTagAssociations(companyId, userId);

		for (CompanyUserTagAssociation association : associations)
			association.setDeleted(true);

		for (Tag tag : tags) {
			addCompanyTagToUser(companyId, tag.getId(), userId);
		}
	}

	private List<CompanyUserTagAssociation> findAllActiveCompanyAdminUserTagAssociations(Long companyId, Long userId) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().getNamedQuery("CompanyUserTagAssociation.findAllActiveCompanyAdminUserTagAssociations")
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.list();
	}

	public List<CompanyUserTagAssociation> findAllActiveCompanyUserTagAssociations(Long companyId, Long userId) {
		Assert.notNull(userId);

		return getFactory().getCurrentSession().getNamedQuery("CompanyUserTagAssociation.findAllActiveCompanyUserTagAssociations")
				.setParameter("companyId", companyId)
				.setParameter("userId", userId)
				.list();
	}

}
