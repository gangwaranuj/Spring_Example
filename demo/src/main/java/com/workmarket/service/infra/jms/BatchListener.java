package com.workmarket.service.infra.jms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.logging.NRTrace;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.BankingFileGenerationService;
import com.workmarket.service.business.UserService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Service
public class BatchListener implements MessageListener {

	private static final Log logger = LogFactory.getLog(BatchListener.class);

	@Autowired private BankingFileGenerationService bankingFileGenerationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private MetricRegistry metricRegistry;
	private Meter consumeMeter;

	@PostConstruct
	private void init() {
		final WMMetricRegistryFacade wmMetricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.batch");
		consumeMeter = wmMetricRegistryFacade.meter("consume");
	}
	
	@NRTrace(dispatcher = true)
	public void onMessage(Message message) {
		consumeMeter.mark();
		
		User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);

		authenticationService.setCurrentUser(currentUser);

		if (!(message instanceof ObjectMessage))
			throw new IllegalArgumentException("Message must be of type ObjectMessage");

		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			BatchMessageType messageType = (BatchMessageType) objectMessage.getObject();

			switch (messageType) {
				case BANK_FILE_ACH:
					bankingFileGenerationService.processPendingAch();
					break;
				case BANK_FILE_OUTBOUND:
					bankingFileGenerationService.processPendingOutbound(Country.USA_COUNTRY);
					break;
				case BANK_FILE_OUTBOUND_NON_US:
					bankingFileGenerationService.processPendingOutbound(Country.CANADA_COUNTRY);
					break;
				case BANK_FILE_INBOUND:
					bankingFileGenerationService.processPendingInbound();
					break;
				case BANK_FILE_PAYPAL:
					bankingFileGenerationService.processPendingPayPal();
					break;
				case BANK_FILE_GCC:
					bankingFileGenerationService.processPendingGCC();
					break;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
