package com.workmarket.domains.model.certification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.AbstractEntity;

@Entity(name="certificationVendorInstruction")
@Table(name="certification_vendor_instruction")
public class CertificationVendorInstruction extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String description;

	public static final CertificationVendorInstruction DEFAULT = new CertificationVendorInstruction(1L);
	 
	public CertificationVendorInstruction() { }
	        
	public CertificationVendorInstruction(Long id) {
        setId(id);
    }
	
    @Column(name = "description", nullable = false, length=500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
