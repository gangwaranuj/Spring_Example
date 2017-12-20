package com.workmarket.web.editors;

import com.workmarket.domains.model.CallingCode;
import com.workmarket.service.infra.business.InvariantDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: jasonpendrey
 * Date: 6/12/13
 * Time: 11:19 AM
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallingCodeEditor extends AbstractEntityEditor<CallingCode> {
	@Autowired private InvariantDataService invariantDataService;


	@Override
	public CallingCode getEntity(Long id) {
		return invariantDataService.findCallingCodeFromID(id);
	}
}
