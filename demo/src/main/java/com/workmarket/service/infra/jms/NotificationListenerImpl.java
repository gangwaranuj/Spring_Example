package com.workmarket.service.infra.jms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.logging.NRTrace;
import com.workmarket.service.business.UserNotificationService;
import com.workmarket.service.business.dto.EMailDTO;
import com.workmarket.service.business.dto.NotificationDTO;
import com.workmarket.service.business.dto.PushDTO;
import com.workmarket.service.business.dto.SMSDTO;
import com.workmarket.service.infra.PushService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.dto.UserNotificationDTO;
import com.workmarket.service.infra.email.EmailService;
import com.workmarket.service.infra.notification.NotificationDispatcher;
import com.workmarket.service.infra.sms.SMSService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Service
public class NotificationListenerImpl implements MessageListener, NotificationListener {

	private static final Log logger = LogFactory.getLog(NotificationListenerImpl.class);

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private SMSService smsService;
	@Autowired private EmailService emailService;
	@Autowired private NotificationDispatcher notificationDispatcher;
	@Autowired private PushService pushService;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	private WMMetricRegistryFacade metricRegistryFacade;
	private Meter consumeMeter;
	
	@PostConstruct
	private void init() {
		metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, "jms.listener.notification");
		consumeMeter = metricRegistry.meter("consume");
	}

	@Override
	@NRTrace(dispatcher = true)
	public void onMessage(Message message) {
		consumeMeter.mark();
		if (message instanceof ObjectMessage) {
			try {
				ObjectMessage objectMessage = (ObjectMessage) message;
				Object object = objectMessage.getObject();
				webRequestContextProvider.extract(object);

				// The listener is running in a separate context from wherever the message originated from.
				// Assume that the context's current user should be set to whomever the notification is FROM.
				if (object instanceof NotificationDTO) {
					NotificationDTO dto = (NotificationDTO) object;
					if (dto.getFromUserId() != null) {
						authenticationService.setCurrentUser(dto.getFromUserId());
						// Recreate the web context in case it is to be used downstream
					}
				}

				if (object instanceof PushDTO) {
					logger.trace("***** PushDTO Sent - about to send push *****");
					metricRegistryFacade.meter("push").mark();
					pushService.sendPush((PushDTO)object);
				} else if (object instanceof EMailDTO) {
					logger.trace("***** EMailDTO Sent - about to send email *****");
					metricRegistryFacade.meter("email").mark();
					emailService.sendEmail((EMailDTO) object);
				} else if (object instanceof SMSDTO) {
					logger.trace("***** SMSDTO Sent - about to send sms *****");
					metricRegistryFacade.meter("sms").mark();
					smsService.sendSMS((SMSDTO) object);
				} else if (object instanceof UserNotificationDTO) {
					logger.trace("***** UserNotificationDTO Sent - about to send user notification *****");
					metricRegistryFacade.meter("bullhorn").mark();
					userNotificationService.sendUserNotification((UserNotificationDTO) object);
				} else if (object instanceof NotificationTemplate) {
					logger.debug("***** NotificationTemplate *****");
					metricRegistryFacade.meter("template").mark();
					NotificationTemplate template = (NotificationTemplate) object;
					if (template.getFromId() != null) {
						authenticationService.setCurrentUser(template.getFromId());
						// Recreate the web context in case it is to be used downstream
						webRequestContextProvider.extract(template);
					}
					notificationDispatcher.dispatchNotification(template);
				}
			} catch (Exception ex) {
				logger.error("Error receiving object message", ex);
				throw new RuntimeException(ex);
			}
		} else {
			throw new IllegalArgumentException("Message must be of type ObjectMessage");
		}
	}
}