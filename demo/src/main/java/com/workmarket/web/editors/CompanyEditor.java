package com.workmarket.web.editors;

import com.workmarket.domains.model.Company;
import com.workmarket.service.business.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CompanyEditor extends AbstractEntityEditor<Company> {
	@Autowired private CompanyService service;

	@Override
	public Company getEntity(Long id) {
		return service.findCompanyById(id);
	}
}
