package com.workmarket.domains.model;

import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class LookupEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;
	protected String description;

	public LookupEntity() {}
	public LookupEntity(String code) {
		this.code = code;
	}
	public LookupEntity(String code, String description) {
		this.code = code;
		this.description = description;
	}

	@Id
	@Column(name = "code", nullable = false, length=10)
	public String getCode(){
		return code;
	}

	@Column(name = "description", nullable = false, length=50)
	public String getDescription() {
		return description;
	}

	public void setCode(String code){
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int hashCode() {
		return this.code.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof LookupEntity)) {
			return false;
		}
		LookupEntity e = (LookupEntity) obj;

		// take this Hibernate! Your lazy Jedi tricks do not work on me!
		String otherId;
		if (e instanceof HibernateProxy) {
			otherId = (String) ((HibernateProxy) e).getHibernateLazyInitializer().getIdentifier();
		} else {
			otherId = e.code;
		}
		return otherId != null && this.code.equals(otherId);
	}

	@Override
	public String toString() {
		return getCode();
	}
}
