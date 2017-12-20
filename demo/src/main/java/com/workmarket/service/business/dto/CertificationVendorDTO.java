package com.workmarket.service.business.dto;

public class CertificationVendorDTO {
	
	private Long certificationVendorId;
	private String name;
    private Boolean deleted = Boolean.FALSE;

    public Long getCertificationVendorId() {
        return certificationVendorId;
    }

    public void setCertificationVendorId(Long certificationVendorId) {
        this.certificationVendorId = certificationVendorId;
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

}
