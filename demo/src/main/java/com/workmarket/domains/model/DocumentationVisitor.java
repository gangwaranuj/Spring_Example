package com.workmarket.domains.model;

import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociation;

/**
 * User: micah
 * Date: 1/5/14
 * Time: 7:05 PM
 */
public interface DocumentationVisitor {
	public void visit(DocumentationManager documentationManager, UserUserGroupDocumentReference document);

	void visit(DocumentationManager documentationManager, UserLicenseAssociation userLicenseAssociation);

	void visit(DocumentationManager documentationManager, UserInsuranceAssociation userInsuranceAssociation);

	void visit(DocumentationManager documentationManager, UserCertificationAssociation userCertificationAssociation);
}
