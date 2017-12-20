package com.workmarket.domains.model.acl;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "aclRole")
@Table(name = "acl_role")
@NamedQueries({
	@NamedQuery(name="aclrole.findAll", query="from aclRole r "),
	@NamedQuery(name="aclrole.findSystemRoleByName", query="from aclRole r where role_type_code = 'system' and name = :roleName")
})

@AuditChanges
public class AclRole extends DeletableEntity {

	public static final long ACL_ADMIN         = 1L;
	public static final long ACL_MANAGER       = 2L;
	public static final long ACL_USER          = 3L;
	public static final long ACL_VIEW_ONLY     = 4L;
	public static final long ACL_CONTROLLER    = 5L;
	public static final long ACL_WORKER        = 6L;
	public static final long ACL_SHARED_WORKER = 7L;

	public static final long ACL_STAFF         = 15L; //This role doesn't have any permissions.
	public static final long ACL_DEPUTY        = 16L; //This role has permissions
	public static final long ACL_DISPATCHER    = 34L; //This role has permissions
	//This role is assigned to any employee who can ONLY perform work. The employee who has this role can ONLY have this role.
	public static final long ACL_EMPLOYEE_WORKER		 = 35L;

	// Network roles; IDs are taken from the acl_role table -- DO NOT CHANGE THESE
	public static final long ACL_NETWORK_OWNER     = 21L;
	public static final long ACL_NETWORK_ADMIN     = 25L;
	public static final long ACL_NETWORK_SHARE     = 29L; // Network is shareable
	public static final long ACL_NETWORK_READ      = 33L; // Readonly

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private Set<RolePermissionAssociation> permissionAssociations = Sets.newLinkedHashSet();
	private Set<UserAclRoleAssociation> userAssociations = Sets.newLinkedHashSet();
	private Boolean virtual = Boolean.FALSE;
	private Company company;
	private AclRoleType aclRoleType;

	public AclRole() {
	}

	public AclRole(String name, String description, AclRoleType aclRoleType) {
		this.name = name;
		this.description = description;
		this.aclRoleType = aclRoleType;
	}

	@Column(name = "name", nullable = false, length=45)
	public String getName() {
		return name;
	}

	@Column(name = "description", length=100)
	public String getDescription() {
		return description;
	}

	@OneToMany(mappedBy = "role", cascade = {}, fetch = FetchType.LAZY)
	public Set<RolePermissionAssociation> getPermissionAssociations() {
		return permissionAssociations;
	}

	@OneToMany(mappedBy = "role", cascade = {}, fetch = FetchType.LAZY)
	public Set<UserAclRoleAssociation> getUserAssociations() {
		return userAssociations;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPermissionAssociations(Set<RolePermissionAssociation> permissionAssociations) {
		this.permissionAssociations = permissionAssociations;
	}

	public void setUserAssociations(Set<UserAclRoleAssociation> userAssociations) {
		this.userAssociations = userAssociations;
	}

	@Column(name="virtual")
	public Boolean isVirtual() {
		return virtual;
	}

	public void setVirtual(Boolean virtual) {
		this.virtual = virtual;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = true)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_type_code", referencedColumnName="code")
	public AclRoleType getAclRoleType(){
		return aclRoleType;
	}

	public void setAclRoleType(AclRoleType aclRoleType){
		this.aclRoleType = aclRoleType;
	}

	@Transient
	public boolean isCustom() {
		return aclRoleType.getCode().equals(AclRoleType.CUSTOM);
	}

	@Transient
	public boolean isInternal() {
		return aclRoleType.getCode().equals(AclRoleType.INTERNAL);
	}

	/**
	 * Expose the constants as strings for use in Spring Security expressions.
	 * @return
	 */
	@Transient
	public String getConstantName() {
		switch (getId().intValue()) {
			case (int)ACL_ADMIN: return "ACL_ADMIN";
			case (int)ACL_MANAGER: return "ACL_MANAGER";
			case (int)ACL_USER: return "ACL_USER";
			case (int)ACL_VIEW_ONLY: return "ACL_VIEW_ONLY";
			case (int)ACL_CONTROLLER: return "ACL_CONTROLLER";
			case (int)ACL_WORKER: return "ACL_WORKER";
			case (int)ACL_SHARED_WORKER: return "ACL_SHARED_WORKER";
			case (int)ACL_STAFF: return "ACL_STAFF";
			case (int)ACL_DEPUTY: return "ACL_DEPUTY";
			case (int)ACL_DISPATCHER: return "ACL_DISPATCHER";
			case (int)ACL_EMPLOYEE_WORKER: return "ACL_EMPLOYEE_WORKER";
		}
		return String.format("ACL_%s", getName().toUpperCase());
	}
}
