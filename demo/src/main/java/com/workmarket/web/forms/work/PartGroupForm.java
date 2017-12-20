package com.workmarket.web.forms.work;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.web.forms.addressbook.LocationForm;

import java.io.Serializable;
import java.util.List;

public class PartGroupForm implements Serializable {

	private static final long serialVersionUID = 8513932091647310119L;

	private Long id;
	private String uuid;
	private boolean suppliedByWorker;
	private ShippingDestinationType shippingDestinationType;
	private LocationForm shipToLocation;
	private LocationForm returnToLocation;
	private boolean returnRequired;
	private List<PartForm> parts = Lists.newArrayListWithCapacity(0);

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

	public LocationForm getShipToLocation() {
		return shipToLocation;
	}

	public void setShipToLocation(LocationForm shipToLocation) {
		this.shipToLocation = shipToLocation;
	}

	public LocationForm getReturnToLocation() {
		return returnToLocation;
	}

	public void setReturnToLocation(LocationForm returnToLocation) {
		this.returnToLocation = returnToLocation;
	}

	public ShippingDestinationType getShippingDestinationType() {
		return shippingDestinationType;
	}

	public void setShippingDestinationType(ShippingDestinationType shippingDestinationType) {
		this.shippingDestinationType = shippingDestinationType;
	}

	public boolean isSuppliedByWorker() {
		return suppliedByWorker;
	}

	public void setSuppliedByWorker(boolean suppliedByWorker) {
		this.suppliedByWorker = suppliedByWorker;
	}

	public boolean isReturnRequired() {
		return returnRequired;
	}

	public void setReturnRequired(boolean returnRequired) {
		this.returnRequired = returnRequired;
	}

	public List<PartForm> getParts() {
		return parts;
	}

	public void setParts(List<PartForm> parts) {
		this.parts = parts;
	}

}
