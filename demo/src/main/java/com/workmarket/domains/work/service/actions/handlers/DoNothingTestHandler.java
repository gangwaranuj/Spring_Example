package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.stereotype.Service;

@Service
public class DoNothingTestHandler implements WorkEventHandler {

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event){

		return event.getResponse().setSuccessful(true);

	}

}
