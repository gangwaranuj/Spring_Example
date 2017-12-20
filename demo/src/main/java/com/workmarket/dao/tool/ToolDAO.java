package com.workmarket.dao.tool;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.dto.SuggestionDTO;

import java.util.List;

public interface ToolDAO extends DAOInterface<Tool>
{

	Tool findToolById(Long toolId);

	Tool findToolByNameAndIndustryId(String name, Long industryId);

	ToolPagination findAllTools(ToolPagination pagination);

	ToolPagination findAllToolsByIndustry(Integer industryId, ToolPagination pagination);

	List<SuggestionDTO> suggest(String prefix, String property);
}
