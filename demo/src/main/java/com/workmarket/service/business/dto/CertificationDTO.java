package com.workmarket.service.business.dto;


import com.workmarket.domains.model.certification.Certification;
import com.workmarket.utility.BeanUtilities;

public class CertificationDTO {

	private Long certificationId;
	private String name;
	private Boolean deleted = Boolean.FALSE;
	private Long certificationVendorId;
	private UserCertificationDTO userCertification;

	public static CertificationDTO newDTO(Certification certification) {
		CertificationDTO DTO = new CertificationDTO();
		BeanUtilities.copyProperties(DTO, certification);
		DTO.setCertificationId(certification.getId());
		return DTO;
	}

	public Long getCertificationId() {
		return certificationId;
	}

	public void setCertificationId(Long certificationId) {
		this.certificationId = certificationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Long getCertificationVendorId() {
		return certificationVendorId;
	}

	public void setCertificationVendorId(Long certificationVendorId) {
		this.certificationVendorId = certificationVendorId;
	}

	public UserCertificationDTO getUserCertification() {
		return userCertification;
	}

	public void setUserCertification(UserCertificationDTO userCertification) {
		this.userCertification = userCertification;
	}
}
