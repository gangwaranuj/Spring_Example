package com.workmarket.domains.model.asset;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.AvailabilityType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity(name = "link")
@Table(name = "link")
public class Link extends AbstractEntity implements AssetResource {
	private static final long serialVersionUID = 1L;

	private String name;
	private String remoteUri;
	private AvailabilityType availability = new AvailabilityType(AvailabilityType.ALL);

	public Link(){}

	public Link(String name, String remoteUri) {
		this.name = name;
		this.remoteUri = remoteUri;
	}

	public Link(String name, String remoteUri, AvailabilityType availability){
		this.name = name;
		this.remoteUri = remoteUri;
		this.availability = availability;
	}

	@Column(name = "name", nullable = true, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "remote_uri", nullable = false, length = 255)
	public String getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "availability_type_code", referencedColumnName = "code", nullable = false)
	public AvailabilityType getAvailability() {
		return availability;
	}

	public void setAvailability(AvailabilityType availability) {
		this.availability = availability;
	}

	@Override
	@Transient
	public String getAssetResourceType() {
		return AssetResource.LINK;
	}
}
