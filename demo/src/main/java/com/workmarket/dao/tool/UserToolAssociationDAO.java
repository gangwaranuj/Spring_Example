package com.workmarket.dao.tool;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.tool.Tool;
import com.workmarket.domains.model.tool.ToolPagination;
import com.workmarket.domains.model.tool.UserToolAssociation;
import com.workmarket.domains.model.tool.UserToolAssociationPagination;

import java.util.List;

public interface UserToolAssociationDAO extends DAOInterface<UserToolAssociation> {

	ToolPagination findAllToolsByUser(Long userId, ToolPagination pagination);

	UserToolAssociationPagination findAllAssociationsByUser(Long userId, UserToolAssociationPagination pagination);

	void addToolToUser(Tool tool, User user);

	void removeToolFromUser(Tool tool, User user);

	UserToolAssociation findAssociationsByToolAndUser(Long toolId, Long userId);

	List<UserToolAssociation> findAssociationsByUser(Long userId);

	ToolPagination findAllActiveToolsByUser(Long userId, ToolPagination pagination);

	void mergeTools(Long fromToolId, Long toToolId);
}
