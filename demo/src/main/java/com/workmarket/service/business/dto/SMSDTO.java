package com.workmarket.service.business.dto;

public class SMSDTO extends NotificationDTO {
   
	private static final long serialVersionUID = 1L;

	private Long providerId;
    private String toNumber;
    
    public SMSDTO() {}
    
    public SMSDTO(Long fromId, Long providerId, String toNumber) {
    	setFromUserId(fromId);
    	this.providerId = providerId;
    	this.toNumber = toNumber;
    }
    
    public SMSDTO(Long fromId) {
    	setFromUserId(fromId);
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public boolean hasProviderId() {
        return providerId != null;
    }
}
