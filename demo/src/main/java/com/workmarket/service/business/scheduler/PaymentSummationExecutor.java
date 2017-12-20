package com.workmarket.service.business.scheduler;

import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaymentSummationExecutor implements ScheduledExecutor  {
	@Autowired private AuthenticationService authenticationService;
	@Autowired
	@Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterService;
	private static final Log logger = LogFactory.getLog(PaymentSummationExecutor.class);


	@Override
	public void execute() {
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		//fromDate is solution for now, will re-factor...
		Date fromDate = DateUtilities.getMidnight1YearAgo().getTime();
		DateTimeFormatter dtf =  ISODateTimeFormat.dateTime();
		logger.debug("****** Processing Payment Summations STARTED at: " + dtf.print(System.currentTimeMillis())  +
				" for dates 1 year ago after:" + dtf.print(fromDate.getTime()));

		List<Long> accountRegisterIds = accountRegisterService.findAllAccountRegisterIds();
		logger.debug("****** Found " + accountRegisterIds.size() + "records ");

		for(long registerId :accountRegisterIds) {
			accountRegisterService.processPaymentSummationsForAccountRegister(registerId, fromDate);
			logger.debug("processing account register " + registerId);
		}
		logger.debug("****** Processing Payment Summations ENDED at:" + new Date());
	}
}
