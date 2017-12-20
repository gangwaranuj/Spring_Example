package com.workmarket.service.business.dto;

import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.service.external.TrackingStatus;
import com.workmarket.web.forms.work.PartForm;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class PartDTO implements Serializable {

	public static final BigDecimal PART_VALUE_MAX = new BigDecimal(999999999.99);
	public static final BigDecimal PART_VALUE_MIN = new BigDecimal(0);
	public static final int NAME_MAX = 80;
	public static final int TRACKING_NUMBER_MAX = 80;
	public static final Map<String, Object> PARTS_CONSTANTS = ImmutableMap.of(
		"PART_VALUE_MAX", PART_VALUE_MAX,
		"PART_VALUE_MIN", PART_VALUE_MIN,
		"NAME_MAX", NAME_MAX,
		"TRACKING_NUMBER_MAX", TRACKING_NUMBER_MAX,
		"SHIPPING_PROVIDERS", ShippingProvider.getValidShippingProviders()
	);
	private static final long serialVersionUID = 9008332210432635700L;

	private Long id;
	private String uuid;
	private Long partGroupId;
	private String partGroupUuid;
	private String name;
	private String trackingNumber;
	private TrackingStatus trackingStatus;
	private ShippingProvider shippingProvider = ShippingProvider.OTHER;
	private BigDecimal partValue;
	private boolean isReturn = false;

	public PartDTO() {}

	public PartDTO(String trackingNumber, TrackingStatus trackingStatus, ShippingProvider shippingProvider) {
		this.trackingNumber = trackingNumber;
		this.trackingStatus = trackingStatus;
		this.shippingProvider = shippingProvider;
	}

	public PartDTO(ShippingProvider shippingProvider, String trackingNumber, BigDecimal value, boolean isReturn, String name) {
		this.shippingProvider = shippingProvider;
		this.trackingNumber = trackingNumber;
		this.partValue = value;
		this.isReturn = isReturn;
		this.name = name;
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

	public Long getPartGroupId() {
		return partGroupId;
	}

	public void setPartGroupId(Long partGroupId) { this.partGroupId = partGroupId; }


	public String getPartGroupUuid() {
		return partGroupUuid;
	}

	public void setPartGroupUuid(String partGroupUuid) {
		this.partGroupUuid = partGroupUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public boolean isSetTrackingNumber() {
		return trackingNumber != null;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public TrackingStatus getTrackingStatus() { return trackingStatus; }

	public void setTrackingStatus(TrackingStatus trackingStatus) {
		this.trackingStatus = trackingStatus;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider == null ? ShippingProvider.OTHER : shippingProvider;
	}

	public void setShippingProvider(ShippingProvider shippingProvider) {
		this.shippingProvider = shippingProvider;
	}

	public boolean isSetShippingProvider() {
		return this.shippingProvider != null;
	}

	public BigDecimal getPartValue() {
		return partValue;
	}

	public void setPartValue(BigDecimal partValue) {
		this.partValue = partValue;
	}

	public boolean isSetPartValue() {
		return this.partValue != null;
	}

	public boolean isReturn() { return isReturn; }

	public void setReturn(boolean isReturn) { this.isReturn = isReturn; }

	public PartForm asForm() {
		PartForm form = new PartForm();
		form.setId(id);
		form.setUuid(uuid);
		form.setPartGroupId(partGroupId);
		form.setPartGroupUuid(partGroupUuid);
		form.setName(name);
		form.setTrackingNumber(trackingNumber);
		form.setTrackingStatus(trackingStatus);
		form.setShippingProvider(shippingProvider);
		form.setPartValue(partValue);
		form.setReturn(isReturn);
		return form;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PartDTO)) {
			return false;
		}

		PartDTO that = (PartDTO) obj;
		return new EqualsBuilder()
			.append(id, that.getId())
			.append(partGroupId, that.getPartGroupId())
			.append(name, that.getName())
			.append(trackingNumber, that.getTrackingNumber())
			.append(trackingStatus, that.getTrackingStatus())
			.append(shippingProvider, that.getShippingProvider())
			.append(partValue, that.getPartValue())
			.append(isReturn, that.isReturn())
			.append(uuid, that.getUuid())
			.append(partGroupUuid, that.getPartGroupUuid())
			.isEquals();
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(partGroupId)
			.append(name)
			.append(trackingNumber)
			.append(trackingStatus)
			.append(shippingProvider)
			.append(partValue)
			.append(isReturn)
			.append(uuid)
			.append(partGroupUuid)
			.toHashCode();
	}
}
