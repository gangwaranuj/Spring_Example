package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.specialty.SpecialtyDAO;
import com.workmarket.dao.specialty.UserSpecialtyAssociationDAO;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociation;
import com.workmarket.domains.model.specialty.UserSpecialtyAssociationPagination;
import com.workmarket.service.business.dto.SpecialtyDTO;
import com.workmarket.utility.BeanUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SpecialtyServiceImpl implements SpecialtyService {
	
	@Autowired IndustryDAO industryDAO;
	@Autowired SpecialtyDAO specialtyDAO;
	@Autowired UserDAO userDAO;
	@Autowired UserSpecialtyAssociationDAO userSpecialtyAssociationDAO;

	private static final int SPECIALITY_POPULARITY_THRESHOLD = 9;

	@Override
	public Specialty saveOrUpdateSpecialty(SpecialtyDTO specialtyDTO) {
		Assert.notNull(specialtyDTO);

		Specialty specialty = null;
		Industry industry = (specialtyDTO.getIndustryId() != null ? industryDAO.get(specialtyDTO.getIndustryId()) : Industry.NONE);
		
		if(specialtyDTO.getSpecialtyId() == null) {
			specialty = specialtyDAO.findSpecialtyByNameAndIndustryId(specialtyDTO.getName(), industry.getId());
			
			if (specialty == null) {
				specialty = BeanUtilities.newBean(Specialty.class, specialtyDTO);
			}
			else {
				if (specialty.getDeleted()) {
					specialty.setDeleted(false);
					specialtyDAO.saveOrUpdate(specialty);
				}
				return specialty;
			}
		} else {
			specialty = findSpecialtyById(specialtyDTO.getSpecialtyId());
			BeanUtilities.copyProperties(specialty, specialtyDTO);
		}

		specialty.setIndustry(industry);
		specialty.setDeleted(false);
		specialtyDAO.saveOrUpdate(specialty);

		return specialty;
	}

	@Override
	public Specialty saveOrUpdateSpecialty(Specialty specialty) {
		Assert.notNull(specialty);
		specialtyDAO.saveOrUpdate(specialty);
		return specialty;
	}

	@Override
	public Specialty findSpecialtyById(Long specialtyId) {
		return specialtyDAO.findSpecialtyById(specialtyId);
	}

	@Override
	public Specialty findSpecialtyByNameAndIndustryId(String name, Long industryId) {
		Assert.hasText(name);
		Assert.notNull(industryId);
		return specialtyDAO.findBy(
				"name", name,
				"industry.id", industryId
		);
	}

	@Override
	public SpecialtyPagination findAllSpecialties(SpecialtyPagination pagination) {
		return specialtyDAO.findAllSpecialties(pagination);
	}

	@Override
	public SpecialtyPagination findAllSpecialtiesByUser(Long userId, SpecialtyPagination pagination) {
		return userSpecialtyAssociationDAO.findAllSpecialtiesByUser(userId, pagination);
	}

	@Override
	public void setSpecialtiesOfUser(Integer[] specialtyIds, Long userId) throws Exception {
		Assert.noNullElements(specialtyIds);
		Assert.notNull(userId);

		UserSpecialtyAssociationPagination pagination = userSpecialtyAssociationDAO.findAllAssociationsByUser(userId, new UserSpecialtyAssociationPagination(true));

		List<Integer> newSkillIds = Lists.newArrayList(Arrays.asList(specialtyIds));

		for(UserSpecialtyAssociation association : pagination.getResults()) {
			if(newSkillIds.contains(association.getSpecialty().getId().intValue())) {
				association.setDeleted(false);
				newSkillIds.remove(Integer.valueOf(association.getSpecialty().getId().intValue()));
			} else {
				association.setDeleted(true);
			}
		}

		for(Integer newSpecialtyId : newSkillIds)
			addSpecialtyToUser(newSpecialtyId, userId);
	}

	@Override
	public void setSpecialtiesOfUser(List<Integer> specialtyIds, Long userId) throws Exception {
		setSpecialtiesOfUser(specialtyIds.toArray(new Integer[specialtyIds.size()]), userId);
	}

	public void addSpecialtyToUser(Integer specialtyId, Long userId) throws Exception {
		userSpecialtyAssociationDAO.addSpecialtyToUser(specialtyDAO.findSpecialtyById(specialtyId.longValue()),
				userDAO.get(userId));
	}

	@Override
	public void removeSpecialtyFromUser(Integer specialtyId, Long userId) throws Exception {
		userSpecialtyAssociationDAO.removeSpecialtyFromUser(specialtyDAO.findSpecialtyById(specialtyId.longValue()),
				userDAO.get(userId));
	}

	@Override
	public void removeSpecialtiesFromUser(Long userId) {
		List<UserSpecialtyAssociation> userSkillAssociations = userSpecialtyAssociationDAO.findAssociationsByUser(userId);

		for (UserSpecialtyAssociation userSpecialtyAssociation : userSkillAssociations) {
			userSpecialtyAssociation.setDeleted(true);
		}
	}

	@Override
	public SpecialtyPagination findAllSpecialtiesByIndustry(Integer industryId, SpecialtyPagination pagination) {
		return specialtyDAO.findAllSpecialtiesByIndustry(industryId, pagination);
	}

	@Override
	public UserSpecialtyAssociationPagination findAllAssociationsByUser(Long userId, UserSpecialtyAssociationPagination pagination) {
		return userSpecialtyAssociationDAO.findAllAssociationsByUser(userId, pagination);
	}

	@Override
	public void setSpecialtyLevelsForUser(Integer[] specialtyIds, Integer[] skillLevels, Long userId) {
		Assert.noNullElements(specialtyIds);
		Assert.noNullElements(skillLevels);
		Assert.notNull(userId);


		for(int i = 0; i < specialtyIds.length; i++) {
			UserSpecialtyAssociation association = userSpecialtyAssociationDAO.findAssociationsBySpecialtyAndUser(specialtyIds[i].longValue(), userId);

			Assert.notNull(association);

			association.setSkillLevel(skillLevels[i]);
		}
	}

	@Override
	public UserSpecialtyAssociation findAssociationsBySpecialtyAndUser(Integer specialtyId, Long userId) {
		return userSpecialtyAssociationDAO.findAssociationsBySpecialtyAndUser(specialtyId.longValue(), userId);
	}

	@Override
	public SpecialtyPagination findAllActiveSpecialtiesByUser(Long userId, SpecialtyPagination pagination)
	{
		return userSpecialtyAssociationDAO.findAllActiveSpecialtiesByUser(userId, pagination);
	}

	@Override
	public void declineSpecialty(Long specialtyId)
	{
		Assert.notNull(specialtyId);

		Specialty tool = findSpecialtyById(specialtyId);

		Assert.notNull(tool);

		tool.setDeleted(Boolean.TRUE);
	}

	@Override
	public void approveSpecialty(Long specialtyId)
	{
		Assert.notNull(specialtyId);

		Specialty tool = findSpecialtyById(specialtyId);

		Assert.notNull(tool);

		tool.setApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Override
	public void mergeSpecialties(Long fromSpecialtyId, Long toSpecialtyId) {
		Assert.notNull(fromSpecialtyId);
		Assert.notNull(toSpecialtyId);
		Assert.notNull(specialtyDAO.get(toSpecialtyId));

		Specialty fromSpecialty = specialtyDAO.get(fromSpecialtyId);
		Assert.notNull(fromSpecialty);

		userSpecialtyAssociationDAO.mergeSpecialties(fromSpecialtyId, toSpecialtyId);

		fromSpecialty.setDeleted(true);
	}

	@Override
	public int getSpecialityPopularityThreshold() {
		return SPECIALITY_POPULARITY_THRESHOLD;
	}

	@Override
	public List<Specialty> findSpecialtiesByIds(Long[] specialtyIds) {
		Assert.notNull(specialtyIds);
		return specialtyDAO.findSpecialtiesByIds(specialtyIds);
	}
}