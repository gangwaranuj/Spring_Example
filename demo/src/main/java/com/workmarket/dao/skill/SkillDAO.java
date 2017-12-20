package com.workmarket.dao.skill;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.dto.SuggestionDTO;

public interface SkillDAO extends DAOInterface<Skill>{

    Skill findSkillById(Long skillId);

    Skill findSkillByNameAndIndustryId(String name, Long industryId);

    SkillPagination findAllSkills(SkillPagination pagination);
    SkillPagination findAllSkills(SkillPagination pagination, boolean findByPrefix);

    SkillPagination findAllSkillsByIndustry(Integer industryId, SkillPagination pagination);

    List<SuggestionDTO> suggest(String prefix, String property);

    List<Skill> findSkillsbyIds(Long[] skillIds);
}
