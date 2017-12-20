package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.ApproveForPaymentWorkEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static ch.lambdaj.Lambda.*;

@Service
public class ApproveForPaymentEventHandler implements WorkEventHandler {

	@Autowired WorkService workService;


	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event){
		Assert.notNull(event);
		Assert.isTrue(event instanceof ApproveForPaymentWorkEvent);
		@SuppressWarnings("ConstantConditions") ApproveForPaymentWorkEvent approveForPaymentWorkEvent = (ApproveForPaymentWorkEvent) event;
		Assert.isTrue(approveForPaymentWorkEvent.isValid());
		List<Long> workIds = extract(event.getWorks(),on(Work.class).getId());
		for(Long workId : workIds){
			workService.closeWork(workId);
		}
		return event.setSuccessful(true);
	}

}
