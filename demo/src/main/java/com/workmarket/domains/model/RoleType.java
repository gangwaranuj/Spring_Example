package com.workmarket.domains.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents a role for internal users only, and not
 * application users. (Go see AclRoleType for that).
 *
 * Currently, we treat a RoleType as a permission, as we don't have
 * a database table to associate different permissions to a single RoleType.
 * Instead, users are tied to multiple RoleTypes via the user_role table and
 * we check access against a RoleType that a User might have.
 */
@Entity(name="role_type")
@Table(name="role_type")
public class RoleType extends LookupEntity {
	
	private static final long serialVersionUID = 1L;

	public static final String SUPERUSER = "superuser";
	public static final String EMPLOYEE = "employee";
	public static final String CONTRACTOR = "contractor";
	public static final String BUYER = "buyer";
	public static final String SELLER = "seller";
	
	// internal wm roles
	public static final String MASQUERADE = "masquerade";
	public static final String INTERNAL = "internal";
	public static final String WM_ADMIN = "wm_admin";
	public static final String WM_ATS = "wm_ats";
	public static final String WM_CRM = "wm_crm";
	public static final String WM_EMAIL = "wm_email";
	public static final String WM_EMPLOYEE = "wm_employee_mgmt";
	public static final String WM_QUEUES = "wm_queues";
	public static final String WM_GENERAL = "wm_general";
	public static final String WM_ACCOUNTING = "wm_accounting";
	public static final String WM_SUBSCRIPTION_REPORTING = "wm_subs_reports";
	public static final String WM_SUBSCRIPTION_APPROVAL = "wm_subs_approve";
	public static final String WM_USER_COMPANY_SEARCH = "wm_userco_search";

	public RoleType() {}
	public RoleType(String code) {
		super(code);
	}
}
