package com.workmarket.dao.skill;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.skill.CompanySkillAssociation;
import com.workmarket.domains.model.skill.CompanySkillPK;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.utility.sql.SQLBuilder;
import com.workmarket.utility.sql.SQLOperator;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CompanySkillAssociationDAOImpl extends AbstractDAO<CompanySkillAssociation> implements
		CompanySkillAssociationDAO {

	protected Class<CompanySkillAssociation> getEntityClass() {
		return CompanySkillAssociation.class;
	}

	@Override
	public void addCompanySkill(Skill skill, Company company) {
		Assert.notNull(skill);
		Assert.notNull(company);

		CompanySkillAssociation companySkill = findCompanySkill(company, skill);

		if(companySkill == null) {
			companySkill = new CompanySkillAssociation(company, skill);
			saveOrUpdate(companySkill);
		} else {
			companySkill.setDeleted(false);
		}
	}

	@Override
	public void removeCompanySkill(Skill skill, Company company) {
		Assert.notNull(skill);
		Assert.notNull(company);

		CompanySkillAssociation association = findCompanySkill(company, skill);

		Assert.notNull(association);

		association.setDeleted(true);
	}

	private CompanySkillAssociation findCompanySkill(Company company, Skill skill) {

		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("skill", skill))
			.add(Restrictions.eq("company", company));
		return (CompanySkillAssociation)criteria.uniqueResult();
	}

	@Override
	public List<Skill> findCompanySkills(Company company) {
		List<Skill> skills  = Lists.newArrayList();
		for(CompanySkillAssociation skillAssociation : findCompanySkillAssociations(company)) {
			skills.add(skillAssociation.getSkill());
		}
		return skills;
	}

	@Override
	public List<CompanySkillAssociation> findCompanySkillAssociations(Company company) {
		Criteria criteria = getFactory()
			.getCurrentSession().createCriteria(getEntityClass())
			.createAlias("company", "company")
			.createAlias("skill", "skill")

			.add(Restrictions.eq("company", company))
			.add(Restrictions.eq("deleted", Boolean.FALSE))
			.add(Restrictions.eq("skill.deleted", Boolean.FALSE));

		return criteria.list();
	}
}
