package com.workmarket.service.infra.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;
import org.springframework.util.StopWatch;

public class TransactionProfiler implements Ordered {

	private Log logger = LogFactory.getLog(TransactionProfiler.class);
	
	private int order;	

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object profile(ProceedingJoinPoint call) throws Throwable {
        Object returnValue;
        StopWatch clock = new StopWatch(getClass().getName());
        try {
        	logger.info("About to execute : " + call.toShortString());        	
            clock.start(call.toShortString());            
            returnValue = call.proceed();
        } finally {
            clock.stop();
            logger.info("Finished :" + call.toShortString() + "  running time (millis) = " 
            		+ clock.getTotalTimeMillis() + "\n-----------------------------------------\n");
            if (clock.getTaskCount() > 1) {
            	logger.info(clock.prettyPrint());
            }
        }
        return returnValue;
    }
}
