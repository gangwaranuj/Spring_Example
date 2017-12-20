package com.workmarket.domains.groups.service;

import com.workmarket.domains.model.DocumentationManager;
import org.springframework.stereotype.Component;

/**
 * User: micah
 * Date: 1/6/14
 * Time: 6:02 PM
 */
@Component
public class DocumentationManagerFactory {
	public DocumentationManager build() {
		return new DocumentationManager();
	}
}
