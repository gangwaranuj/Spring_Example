package com.workmarket.domains.model.acl;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.LookupEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "permission")
@Table(name = "permission")
@NamedQueries({
	@NamedQuery(name="permission.findAll", query="from permission p join fetch p.permissionGroup"),
	@NamedQuery(name="permission.find", query="from permission p where p.code = :code")
})
@AttributeOverrides({
	@AttributeOverride(name="code", column=@Column(length=15))
})
@AuditChanges
public class Permission extends LookupEntity {

	private static final long serialVersionUID = 1L;

	private PermissionGroup permissionGroup;
	private Set<RolePermissionAssociation> roleAssociations = Sets.newLinkedHashSet();

	public static final String MANAGE_BANK_ACCOUNTS = "manageBank";
	public static final String ADD_FUNDS = "addFunds";
	public static final String VIEW_ACCOUNT_BALANCE = "viewBalance";
	public static final String WITHDRAW_FUNDS = "withdraw";

	public static final String CREATE_ASSIGNMENTS = "createWork";
	public static final String APPROVE_ASSIGNMENTS = "approveWork";
	public static final String VIEW_AND_MANAGE_MY_ASSIGNMENTS = "manageMyWork";
	public static final String CHANGE_ASSIGNMENTS_SPEND_LIMIT = "spendLimit";
	public static final String VIEW_AND_MANAGE_MYCOMPANY_ASSIGNMENTS = "manageCoWork";
	public static final String ACCEPT_WORK_FROM_MYCOMPANY = "acceptWork1";
	public static final String ACCEPT_WORK_FROM_OTHER_COMPANY = "acceptWork3";
	public static final String ACCEPT_WORK_AS_DISPATCHER = "acceptWorkD";
	public static final String MY_ASSIGNMENTS_REPORTS = "reportMyWork";
	public static final String MYCOMPANY_REPORTS = "reportCoWork";

	public static final String MANAGE_CLIENT_PROFILES = "profiles";

	public static final String MANAGE_CONTRACTS = "contracts";
	public static final String MANAGE_TRAINING_MATERIALS = "training";
	public static final String UPLOAD_FILES_TO_LIBRARY = "library";

	public static final String ACCESS_MYWORKMARKET = "accessmmw";
	public static final String MANAGE_WORKFLOWS = "workflows";
	public static final String MANAGE_CUSTOM_FIELDS = "customFields";
	public static final String MANAGE_CLIENT_ACCESS_PORTAL = "managecap";

	public static final String MANAGE_ASSESSMENTS = "assessments";

	public static final String PAYABLES = "payables";
	public static final String INVOICES = "invoices";
	public static final String PAY_ASSIGNMENT = "payAssignment";
	public static final String PAY_INVOICE = "payInvoice";
	public static final String REALTIME_DASHBOARD_ACTIONS = "realtimeActions";

	public static final String IDEAS_ADMIN = "ideasAdmin";

	//can approve or disapprove counteroffers
	public static final String COUNTEROFFER_AUTH = "counterOffer";

	//can approve or disapprove budget changes, expense reimbursement, bonus requests and edit pricing
	public static final String EDIT_PRICING_AUTH = "editPricing";

	public static final String APPROVE_WORK_AUTH = "approveWorkAuth";

	public static final String MANAGE_PROJECTS = "project";

	public Permission(){
		super();
	}

	public Permission(String code){
		super(code);
	}

	@ManyToOne
	@JoinColumn(name = "permission_group_id")
	public PermissionGroup getPermissionGroup() {
		return permissionGroup;
	}

	public void setPermissionGroup(PermissionGroup permissionGroup) {
		this.permissionGroup = permissionGroup;
	}

	@OneToMany(mappedBy = "permission", cascade = {}, fetch = FetchType.LAZY)
	public Set<RolePermissionAssociation> getRoleAssociations() {
		return roleAssociations;
	}

	public void setRoleAssociations(Set<RolePermissionAssociation> roleAssociations) {
		this.roleAssociations = roleAssociations;
	}

	@Transient
	public String getConstantName() {
		return String.format("PERMISSION_%s", StringUtils.upperCase(getCode()));
	}
}
