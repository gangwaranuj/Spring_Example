package com.workmarket.domains.groups.service;

import com.workmarket.domains.groups.model.UserGroup;

import java.util.Map;

public interface UserGroupValidationService {

	void revalidateAllAssociations(Long groupId);

	void revalidateAllAssociationsByUser(Long userId, Map<String, Object> modificationType);

	void revalidateAllAssociationsByUserAsync(Long userId, Map<String, Object> modificationType);
}
