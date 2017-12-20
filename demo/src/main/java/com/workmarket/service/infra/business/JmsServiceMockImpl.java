package com.workmarket.service.infra.business;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.workmarket.common.template.NotificationTemplate;
import com.workmarket.service.business.dto.NotificationDTO;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.infra.jms.BatchMessageType;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class JmsServiceMockImpl {//implements JmsService {

    private static final Log logger = LogFactory.getLog(JmsServiceMockImpl.class);
/*
    @Autowired
    @Qualifier("emailMessageTemplate")
    private JmsTemplate emailMessageTemplate;

    @Autowired
    @Qualifier("emailDestination")
    private Queue queue;

    @Autowired
    @Qualifier("eventMessageTemplate")
    private JmsTemplate eventMessageTemplate;

    @Autowired
    @Qualifier("eventDestination")
    private Queue eventDestination;
    
    @Autowired
    @Qualifier("batchTemplate")
    private JmsTemplate batchTemplate;

    @Autowired
    @Qualifier("batchDestination")
    private Queue batchDestination;
*/    

    public void sendMessage(final NotificationDTO notificationDTO, final Calendar scheduleDate) {
/*        this.emailMessageTemplate.send(this.queue, new MessageCreator() {
        	
            public Message createMessage(Session session) throws JMSException {
            	Message message = session.createObjectMessage(notificationDTO);
            	
            	if (scheduleDate != null){            		   		
            		message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, DateUtilities.getDifferenceInMillisFromNow(scheduleDate)); // TODO AP This might be unreliable
            		
            	}
            	
               return message;
            }
        });*/
    }			
       
    
    public void sendUserUpdateMessage(final Long userId) {
        Assert.notNull(userId);
    }

    
    public void sendEventMessage(final Event event) {
        Assert.notNull(event);

/*        if(logger.isDebugEnabled()) {
        	logger.debug("Sending Event message for: " + event);
        	if(event instanceof ScheduledEvent){
        		logger.debug("ScheduledEvent " );
        		logger.debug("ScheduledDate " + ((ScheduledEvent)event).getScheduledDate());
        	}
        }

        this.eventMessageTemplate.send(this.eventDestination, new MessageCreator()
            {
                public Message createMessage(Session session) throws JMSException
                {
                    Message message = session.createObjectMessage(event);
                    
                    if(event instanceof ScheduledEvent)
                    {
                    	Calendar scheduleDate = ((ScheduledEvent)event).getScheduledDate();
                    	message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,
            				DateUtilities.getDifferenceInMillisFromNow(scheduleDate)); 
                    	
                    }
                  return message;
                }
            }
        );*/
    }
    
    
    public void sendBatchMessage(final BatchMessageType type){
    	
    	Assert.notNull(type);

/*        if(logger.isDebugEnabled()) {
        	logger.debug("Sending Batch message: " + type);
        }

        this.batchTemplate.send(this.batchDestination, new MessageCreator()
            {
                public Message createMessage(Session session) throws JMSException
                {
                   Message message = session.createObjectMessage(type);

                  return message;
                }
            }
        );*/
    	
    }



	public void sendMessage(NotificationDTO notificationDTO) {
		// TODO Auto-generated method stub
		
	}


	public void sendMessage(NotificationTemplate notificationTemplate)
			throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void sendMessage(NotificationTemplate notificationTemplate,
			Calendar scheduleDate) throws Exception {
		// TODO Auto-generated method stub
		
	}
}