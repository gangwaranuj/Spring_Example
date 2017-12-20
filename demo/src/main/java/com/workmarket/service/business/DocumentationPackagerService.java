package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.groups.model.UserGroup;

import java.util.List;

public interface DocumentationPackagerService {
	public Optional<Asset> getDocumentationPackage(Long downloaderId, UserGroup userGroup, List<User> users);
	public Optional<Asset> getDocumentationPackageForUser(Long downloaderId, UserGroup userGroup, User user);

	Optional<Asset> getDocumentationPackage(Long downloaderId, Long groupId);
	void buildDocumentationPackage(Long downloaderId, Long groupId, List<Long> userIds);
	Optional<Asset> getDocumentationPackageForUser(Long downloaderId, Long groupId, Long userId);

	Optional<Asset> getDocumentationPackageForUsers(Long downloaderId, Long groupId, List<Long> userIds);
}
