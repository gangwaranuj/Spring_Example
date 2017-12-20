package com.workmarket.domains.model;

import com.workmarket.utility.EncryptionUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, length = 11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// ----- Transient

	@Transient
	public String getIdHash() {
		return EncryptionUtilities.getMD5Digest(getId());
	}

	@Transient
	public String getEncryptedId() {
		return EncryptionUtilities.encryptLong(getId());
	}

	@Override
	public int hashCode() {
		// this is trivial implementation for now
		Long eid;
		if (this instanceof HibernateProxy) {
			eid = (Long) ((HibernateProxy) this).getHibernateLazyInitializer().getIdentifier();
		} else {
			eid = this.id;
		}
		return new HashCodeBuilder(17, 317).append(eid).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO: fix??
		if (!(obj instanceof AbstractEntity)) {
			return false;
		}
		AbstractEntity e = (AbstractEntity) obj;

		// take this Hibernate! Your lazy Jedi tricks do not work on me!
		Long otherId;
		if (e instanceof HibernateProxy) {
			otherId = (Long) ((HibernateProxy) e).getHibernateLazyInitializer().getIdentifier();
		} else {
			otherId = e.id;
		}
		if (otherId == null) {
			return false;
		}
		if (this.getId() != null) {
			return this.id.equals(otherId);
		}
		return false;
	}
}
