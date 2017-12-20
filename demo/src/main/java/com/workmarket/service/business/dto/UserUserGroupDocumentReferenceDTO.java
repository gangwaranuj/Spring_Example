package com.workmarket.service.business.dto;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.utility.DateUtilities;

/**
 * User: micah
 * Date: 12/17/13
 * Time: 11:07 PM
 */
public class UserUserGroupDocumentReferenceDTO {
	private AssetDTO requiredDocumentDTO;
	private AssetDTO referencedDocumentDTO;
	private UserDTO userDTO;
	private UserGroupDTO userGroupDTO;
	private String expirationDate; // ISO8601

	public static UserUserGroupDocumentReferenceDTO newDTO(UserUserGroupDocumentReference reference) {
		UserUserGroupDocumentReferenceDTO userUserGroupDocumentReferenceDTO = new UserUserGroupDocumentReferenceDTO();

		UserDTO userDTO = reference.getUser() == null ? null :  UserDTO.newDTO(reference.getUser());
		UserGroupDTO userGroupDTO = reference.getUserGroup() == null ? null : UserGroup.newUserGroupDTO(reference.getUserGroup());
		AssetDTO requiredDocumentDTO = reference.getRequiredDocument() == null ? null : AssetDTO.newDTO(reference.getRequiredDocument());
		AssetDTO referencedDocumentDTO = reference.getReferencedDocument() == null ? null : AssetDTO.newDTO(reference.getReferencedDocument());
		String expirationDate = DateUtilities.format("MM/dd/yyyy", reference.getExpirationDate(), "UTC", "NONE");

		userUserGroupDocumentReferenceDTO.setUserDTO(userDTO);
		userUserGroupDocumentReferenceDTO.setUserGroupDTO(userGroupDTO);
		userUserGroupDocumentReferenceDTO.setRequiredDocumentDTO(requiredDocumentDTO);
		userUserGroupDocumentReferenceDTO.setReferencedDocumentDTO(referencedDocumentDTO);
		userUserGroupDocumentReferenceDTO.setExpirationDate(expirationDate);

		return userUserGroupDocumentReferenceDTO;
	}

	public AssetDTO getRequiredDocumentDTO() {
		return requiredDocumentDTO;
	}

	public void setRequiredDocumentDTO(AssetDTO requiredDocumentDTO) {
		this.requiredDocumentDTO = requiredDocumentDTO;
	}

	public AssetDTO getReferencedDocumentDTO() {
		return referencedDocumentDTO;
	}

	public void setReferencedDocumentDTO(AssetDTO referencedDocumentDTO) {
		this.referencedDocumentDTO = referencedDocumentDTO;
	}

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	public UserGroupDTO getUserGroupDTO() {
		return userGroupDTO;
	}

	public void setUserGroupDTO(UserGroupDTO userGroupDTO) {
		this.userGroupDTO = userGroupDTO;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
}
