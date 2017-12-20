package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name = "industry")
@Table(name = "industry")
@AuditChanges
public class Industry extends DeletableEntity {

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer order;
	private String otherName;

	public static final Long NO_INDUSTRY_CODE = 1L;
	public static final Industry GENERAL = new Industry(1060L);
	public static final Industry NONE = new Industry(NO_INDUSTRY_CODE);
	public static final Industry TECHNOLOGY_AND_COMMUNICATIONS = new Industry(1000L);
	public static final Industry RETAIL = new Industry(1032L);

	public Industry() {
	}

	public Industry(Long id) {
		setId(id);
	}

	public Industry(Long id, String name) {
		setId(id);
		this.name = name;
	}

	@Column(name = "name", nullable = true, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "order_number")
	public Integer getOrder() {
		return order;
	}

	@Transient
	public String getOtherName() { return otherName; }
	
	public void setOtherName(String otherName){ this.otherName = otherName; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Industry industry = (Industry) o;

		if (name != null ? !name.equals(industry.name) : industry.name != null) { return false; }
		if (order != null ? !order.equals(industry.order) : industry.order != null) { return false; }
		if (getId() != null ? !getId().equals(industry.getId()) : industry.getId() != null) { return false; }

		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (order != null ? order.hashCode() : 0);
		result = 31 * result + (getId() != null ? getId().hashCode() : 0);
		return result;
	}
}
