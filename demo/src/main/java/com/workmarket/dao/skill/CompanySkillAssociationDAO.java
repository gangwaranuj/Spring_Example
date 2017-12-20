package com.workmarket.dao.skill;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.skill.CompanySkillAssociation;
import com.workmarket.domains.model.skill.Skill;

import java.util.List;

public interface CompanySkillAssociationDAO extends DAOInterface<CompanySkillAssociation> {


	void addCompanySkill(Skill skill, Company company);

	void removeCompanySkill(Skill skill, Company company);

	List<Skill> findCompanySkills(Company company);

	List<CompanySkillAssociation> findCompanySkillAssociations(Company company);
}
