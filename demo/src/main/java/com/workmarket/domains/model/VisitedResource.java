package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by ant on 9/23/14.
 */
@Entity
@Table(
	name = "visited_resource",
	uniqueConstraints= @UniqueConstraint(columnNames = {"user_id", "resource_name"})
)
@NamedQueries({
	@NamedQuery(name="VisitedResource.getByUserId", query="SELECT vr.resourceName from VisitedResource vr where vr.userId = :userId")
})
public class VisitedResource {

	public VisitedResource() {}

	public VisitedResource(Long userId, String resourceName) {
		this.resourceName = resourceName;
		this.userId = userId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, length = 11)
	private Long id;

	@Column(name =  "user_id")
	private Long userId;

	@Column(name =  "resource_name")
	private String resourceName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}