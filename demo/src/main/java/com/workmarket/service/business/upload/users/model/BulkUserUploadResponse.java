package com.workmarket.service.business.upload.users.model;

import com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.dto.UserImportDTO;
import com.workmarket.utility.CollectionUtilities;

import java.io.Serializable;
import java.util.List;

public class BulkUserUploadResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private ImmutableList<UserImportDTO> userUploads;
	private List<String> errors = Lists.newArrayList();
	private int numOfRowsWithValidationError;
	private int uploadCount;
	private String fileUUID;
	private User user;
	private BulkUserUploadCompletionStatus status;

	public ImmutableList<UserImportDTO> getUserUploads() {
		return userUploads;
	}

	public void setUserUploads(ImmutableList<UserImportDTO> userUploads) {
		this.userUploads = userUploads;
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !CollectionUtilities.isEmpty(errors);
	}

	public int getNumOfRowsWithValidationError() {
		return numOfRowsWithValidationError;
	}

	public void setNumOfRowsWithValidationError(final int numOfRowsWithValidationError) {
		this.numOfRowsWithValidationError = numOfRowsWithValidationError;
	}

	public int getUploadCount() {
		return uploadCount;
	}

	public void setUploadCount(int uploadCount) {
		this.uploadCount = uploadCount;
	}

	public void addAllErrors(List<String> errors) {
		this.errors.addAll(errors);
	}

	public void addError(String error) {
		errors.add(error);
	}

	public String getFileUUID() {
		return fileUUID;
	}

	public void setFileUUID(String fileUUID) {
		this.fileUUID = fileUUID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BulkUserUploadCompletionStatus getStatus() {
		return status;
	}

	public void setStatus(final BulkUserUploadCompletionStatus status) {
		this.status = status;
	}
}
