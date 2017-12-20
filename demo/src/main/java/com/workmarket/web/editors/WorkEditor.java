package com.workmarket.web.editors;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkEditor extends AbstractEntityEditor<Work> {
	@Autowired private WorkService service;

	@Override
	public Work getEntity(Long id) {
		return service.findWork(id);
	}
}
