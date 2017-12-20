package com.workmarket.service.business;

import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;
import com.workmarket.service.business.dto.ToolDTO;

import java.util.List;

public interface ToolService {
	Tool findToolById(Long toolId);
	Tool findToolByNameAndIndustryId(String name, Long industryId);
	Tool saveOrUpdateTool(ToolDTO toolDTO);
	Tool saveOrUpdateTool(Tool tool);

	void declineTool(Long toolId);
	void approveTool(Long toolId);

	void mergeTools(Long fromToolId, Long toToolId);

	ToolPagination findAllTools(ToolPagination pagination);
	ToolPagination findAllToolsByIndustry(Integer industryId, ToolPagination pagination);
	ToolPagination findAllToolsByUser(Long userId, ToolPagination pagination);
	ToolPagination findAllActiveToolsByUser(Long userId, ToolPagination pagination);

	void setToolsOfUser(Integer[] toolIds, Long userId) throws Exception;
	void setToolsOfUser(List<Integer> toolIds, Long userId) throws Exception;
	void addToolToUser(Integer toolId, Long userId) throws Exception;
	void removeToolFromUser(Integer toolId, Long userId) throws Exception;
	void removeToolsFromUser(Long userId);

	void setProficiencyLevelsForUser(Integer[] toolIds, Integer[] skillLevels, Long userId);

	UserToolAssociationPagination findAllAssociationsByUser(Long userId, UserToolAssociationPagination userToolAssociationPagination);
	UserToolAssociation findAssociationsByToolAndUser(Integer toolId, Long userId);

	int getToolPopularityThreshold();
}
