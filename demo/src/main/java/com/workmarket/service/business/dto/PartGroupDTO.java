package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PartGroupDTO implements Serializable {

	private static final long serialVersionUID = 4574981524597731558L;

	private Long id;
	private Long workId;
	private Boolean suppliedByWorker;
	private ShippingDestinationType shippingDestinationType;
	private LocationDTO shipToLocation;
	private LocationDTO returnToLocation;
	private Boolean returnRequired;
	private List<PartDTO> parts = Lists.newArrayListWithCapacity(0);
	private String uuid;

	public PartGroupDTO() {}

	/**
	 * Perform a *shallow* copy of the DTO.
	 */
	public PartGroupDTO copy() {
		final PartGroupDTO result = new PartGroupDTO();
		result.setId(id);
		result.setWorkId(workId);
		result.setSuppliedByWorker(suppliedByWorker);
		result.setShippingDestinationType(shippingDestinationType);
		result.setShipToLocation(shipToLocation);
		result.setReturnToLocation(returnToLocation);
		result.setReturnRequired(returnRequired);
		result.setParts(parts);
		result.setUuid(uuid);
		return result;
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

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public LocationDTO getShipToLocation() {
		return shipToLocation;
	}

	public void setShipToLocation(LocationDTO shipToLocation) {
		this.shipToLocation = shipToLocation;
	}

	public boolean hasShipToLocation() {
		return this.shipToLocation != null;
	}

	public LocationDTO getReturnToLocation() {
		return returnToLocation;
	}

	public void setReturnToLocation(LocationDTO returnToLocation) {
		this.returnToLocation = returnToLocation;
	}

	public boolean hasReturnToLocation() {
		return this.returnToLocation != null;
	}

	public ShippingDestinationType getShippingDestinationType() {
		return shippingDestinationType;
	}

	public void setShippingDestinationType(ShippingDestinationType shippingDestinationType) { this.shippingDestinationType = shippingDestinationType; }

	public boolean isSetShippingDestinationType() {
		return shippingDestinationType != null;
	}

	public Boolean isSuppliedByWorker() {
		return suppliedByWorker != null && suppliedByWorker;
	}

	public void setSuppliedByWorker(Boolean suppliedByWorker) {
		this.suppliedByWorker = suppliedByWorker;
	}

	public Boolean isReturnRequired() {
		return returnRequired != null && returnRequired;
	}

	public void setReturnRequired(Boolean returnRequired) {
		this.returnRequired = returnRequired;
	}

	public List<PartDTO> getParts() {
		return parts;
	}

	public void setParts(List<PartDTO> parts) {
		this.parts = parts;
	}

	public boolean hasParts() {
		return CollectionUtils.isNotEmpty(parts);
	}

	public void addPart(PartDTO part) {
		if (part == null) {
			return;
		}
		if (parts == null) {
			parts = Lists.newArrayList();
		}
		parts.add(part);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof PartGroupDTO)){
			return false;
		}

		PartGroupDTO that = (PartGroupDTO) o;

		return new EqualsBuilder()
			.append(id, that.getId())
			.append(workId, that.getWorkId())
			.append(shipToLocation, that.getShipToLocation())
			.append(returnToLocation, that.getReturnToLocation())
			.append(shippingDestinationType, that.getShippingDestinationType())
			.append(suppliedByWorker, that.isSuppliedByWorker())
			.append(returnRequired, that.isReturnRequired())
			.append(parts, that.getParts())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(id)
			.append(workId)
			.append(shipToLocation)
			.append(returnToLocation)
			.append(shippingDestinationType)
			.append(suppliedByWorker)
			.append(returnRequired)
			.append(parts)
			.toHashCode();
	}
}
