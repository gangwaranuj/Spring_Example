package com.workmarket.web.forms.work;

import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.external.TrackingStatus;

import java.io.Serializable;
import java.math.BigDecimal;

public class PartForm implements Serializable {

	private static final long serialVersionUID = -4160172835740083245L;

	private Long id;
	private String uuid;
	private Long partGroupId;
	private String partGroupUuid;
	private String name;
	private String trackingNumber;
	private TrackingStatus trackingStatus;
	private ShippingProvider shippingProvider;
	private BigDecimal partValue;
	private boolean isReturn;

	public String getPartGroupUuid() {
		return partGroupUuid;
	}

	public void setPartGroupUuid(String partGroupUuid) {
		this.partGroupUuid = partGroupUuid;
	}

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}

	public BigDecimal getPartValue() {
		return partValue;
	}

	public void setPartValue(BigDecimal partValue) {
		this.partValue = partValue;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	public void setShippingProvider(ShippingProvider shippingProvider) {
		this.shippingProvider = shippingProvider;
	}

	public TrackingStatus getTrackingStatus() {
		return trackingStatus;
	}

	public void setTrackingStatus(TrackingStatus trackingStatus) {
		this.trackingStatus = trackingStatus;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPartGroupId() {
		return partGroupId;
	}

	public void setPartGroupId(Long partGroupId) {
		this.partGroupId = partGroupId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public PartDTO asDTO() {
		PartDTO dto = new PartDTO();
		dto.setId(id);
		dto.setUuid(uuid);
		dto.setPartGroupId(partGroupId);
		dto.setName(name);
		dto.setTrackingNumber(trackingNumber);
		dto.setTrackingStatus(trackingStatus);
		dto.setShippingProvider(shippingProvider);
		dto.setPartValue(partValue);
		dto.setReturn(isReturn);
		dto.setPartGroupUuid(partGroupUuid);

		return dto;
	}
}
