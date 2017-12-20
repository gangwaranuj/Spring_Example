package com.workmarket.domains.model;

import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.DocumentationVisitor;

/**
 * User: micah
 * Date: 1/5/14
 * Time: 6:50 PM
 */
public interface VisitableDocumentation {
	public void accept(DocumentationManager documentationManager, DocumentationVisitor documentationVisitor);
}
