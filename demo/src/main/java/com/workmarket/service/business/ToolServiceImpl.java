package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.tool.ToolDAO;
import com.workmarket.dao.tool.UserToolAssociationDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.service.business.dto.ToolDTO;
import com.workmarket.utility.BeanUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Service
public class ToolServiceImpl implements ToolService {

	@Autowired private ToolDAO toolDAO;
	@Autowired private IndustryDAO industryDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserToolAssociationDAO userToolAssociationDAO;
	@Autowired private UserIndexer userSearchIndexerHelper;

	private static final int TOOL_POPULARITY_THRESHOLD = 9;


	@Override
	public Tool saveOrUpdateTool(ToolDTO toolDTO) {
		Assert.notNull(toolDTO);

		Tool tool;
		Industry industry = (toolDTO.getIndustryId() != null ? industryDAO.get(toolDTO.getIndustryId()) : Industry.NONE);

		if (toolDTO.getToolId() == null) {
			tool = toolDAO.findToolByNameAndIndustryId(toolDTO.getName(), industry.getId());

			if (tool == null) {
				tool = BeanUtilities.newBean(Tool.class, toolDTO);
			} else {
				return tool;
			}
		} else {
			tool = findToolById(toolDTO.getToolId());
			BeanUtilities.copyProperties(tool, toolDTO);
		}

		tool.setIndustry(industry);
		toolDAO.saveOrUpdate(tool);

		return tool;
	}

	@Override
	public Tool saveOrUpdateTool(Tool tool) {
		Assert.notNull(tool);
		toolDAO.saveOrUpdate(tool);
		return tool;
	}

	@Override
	public Tool findToolById(Long toolId) {
		return toolDAO.findToolById(toolId);
	}

	@Override
	public Tool findToolByNameAndIndustryId(String name, Long industryId) {
		return toolDAO.findToolByNameAndIndustryId(name, industryId);
	}

	@Override
	public ToolPagination findAllTools(ToolPagination pagination) {
		return toolDAO.findAllTools(pagination);
	}

	@Override
	public ToolPagination findAllToolsByUser(Long userId, ToolPagination pagination) {
		return userToolAssociationDAO.findAllToolsByUser(userId, pagination);
	}

	@Override
	public void setToolsOfUser(Integer[] toolIds, Long userId) throws Exception {
		Assert.noNullElements(toolIds);
		Assert.notNull(userId);

		UserToolAssociationPagination pagination = userToolAssociationDAO.findAllAssociationsByUser(userId, new UserToolAssociationPagination(true));

		List<Integer> newSkillIds = Lists.newArrayList(Arrays.asList(toolIds));

		for (UserToolAssociation association : pagination.getResults()) {
			if (newSkillIds.contains(association.getTool().getId().intValue())) {
				association.setDeleted(false);
				newSkillIds.remove(Integer.valueOf(association.getTool().getId().intValue()));
			} else {
				association.setDeleted(true);
			}
		}

		for (Integer newToolId : newSkillIds)
			addToolToUser(newToolId, userId);
	}

	@Override
	public void setToolsOfUser(List<Integer> toolIds, Long userId) throws Exception {
		setToolsOfUser(toolIds.toArray(new Integer[toolIds.size()]), userId);
	}

	public void addToolToUser(Integer toolId, Long userId) throws Exception {
		userToolAssociationDAO.addToolToUser(toolDAO.findToolById(toolId.longValue()),
				userDAO.get(userId));
		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public void removeToolFromUser(Integer toolId, Long userId) throws Exception {
		userToolAssociationDAO.removeToolFromUser(toolDAO.findToolById(toolId.longValue()),
				userDAO.get(userId));
		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public void removeToolsFromUser(Long userId) {
		List<UserToolAssociation> userSkillAssociations = userToolAssociationDAO.findAssociationsByUser(userId);

		for (UserToolAssociation userToolAssociation : userSkillAssociations) {
			userToolAssociation.setDeleted(true);
		}

		userSearchIndexerHelper.reindexById(userId);
	}

	@Override
	public ToolPagination findAllToolsByIndustry(Integer industryId, ToolPagination pagination) {
		return toolDAO.findAllToolsByIndustry(industryId, pagination);
	}

	@Override
	public UserToolAssociationPagination findAllAssociationsByUser(Long userId, UserToolAssociationPagination pagination) {
		return userToolAssociationDAO.findAllAssociationsByUser(userId, pagination);
	}

	@Override
	public void setProficiencyLevelsForUser(Integer[] toolIds, Integer[] skillLevels, Long userId) {
		Assert.noNullElements(toolIds);
		Assert.noNullElements(skillLevels);
		Assert.notNull(userId);

		for (int i = 0; i < toolIds.length; i++) {
			UserToolAssociation association = userToolAssociationDAO.findAssociationsByToolAndUser(toolIds[i].longValue(), userId);

			Assert.notNull(association);

			association.setProficiencyLevel(skillLevels[i]);
		}
	}

	@Override
	public UserToolAssociation findAssociationsByToolAndUser(Integer toolId, Long userId) {
		return userToolAssociationDAO.findAssociationsByToolAndUser(toolId.longValue(), userId);
	}

	@Override
	public void declineTool(Long toolId) {
		Assert.notNull(toolId);

		Tool tool = findToolById(toolId);

		Assert.notNull(tool);

		tool.setDeleted(Boolean.TRUE);
	}

	@Override
	public void approveTool(Long toolId) {
		Assert.notNull(toolId);

		Tool tool = findToolById(toolId);

		Assert.notNull(tool);

		tool.setApprovalStatus(ApprovalStatus.APPROVED);
	}

	@Override
	public void mergeTools(Long fromToolId, Long toToolId) {
		Assert.notNull(fromToolId);
		Assert.notNull(toToolId);
		Assert.notNull(toolDAO.get(toToolId));

		Tool fromTool = toolDAO.get(fromToolId);
		Assert.notNull(fromTool);

		userToolAssociationDAO.mergeTools(fromToolId, toToolId);

		fromTool.setDeleted(true);
	}

	@Override
	public ToolPagination findAllActiveToolsByUser(Long userId, ToolPagination pagination) {
		return userToolAssociationDAO.findAllActiveToolsByUser(userId, pagination);
	}

	@Override
	public int getToolPopularityThreshold() {
		return TOOL_POPULARITY_THRESHOLD;
	}
}