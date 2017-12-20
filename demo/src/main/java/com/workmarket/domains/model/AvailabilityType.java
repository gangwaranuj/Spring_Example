package com.workmarket.domains.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="availabilityType")
@Table(name="availability_type")

public class AvailabilityType extends LookupEntity {
	private static final long serialVersionUID = 1L;

	public static final String GUEST = "guest";
	public static final String ALL = "all";
	public static final String INVITE = "invite";
	public static final String WORKER_POOL = "worker_pool";
	public static final String RESOURCE = "resource";
	public static final String GROUP = "group";
	public static final String PRIVATE = "private";


	public AvailabilityType() {}
	public AvailabilityType(String code) {
		super(code);
	}

	@Transient
	public boolean hasPrivateAvailability() {
		return PRIVATE.equals(getCode());
	}

	@Transient
	public boolean hasGroupAvailability() {
		return GROUP.equals(getCode());
	}

	@Transient
	public boolean hasGuestAvailability() {
		return GUEST.equals(getCode());
	}

	@Transient
	public boolean hasPublicAvailability() {
		return ALL.equals(getCode());
	}

	@Transient
	public boolean hasInvitationAvailability() {
		return INVITE.equals(getCode());
	}

	@Transient
	public boolean hasWorkerPoolAvailability() {
		return WORKER_POOL.equals(getCode());
	}

	@Transient
	public boolean hasResourceAvailability() {
		return RESOURCE.equals(getCode());
	}

	@Transient
	public String getUri(String uuid, String... uris) {
		return getUri(false, uuid, uris);
	}

	@Transient
	public String getDownloadableUri(String uuid, String... uris) {
		return getUri(true, uuid, uris);
	}

	private String getUri(boolean isDownloadable, String uuid, String... uris){
		if (hasGuestAvailability()) {
			for (String uri : uris) {
				if (uri != null)
					return uri;
			}
		}
		return getAssetRelativeURI(uuid, isDownloadable);
	}

	private String getAssetRelativeURI(String id, boolean isDownloadable) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return (isDownloadable ? "/asset/download/" : "/asset/") + id;
	}

}
