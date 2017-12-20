package com.workmarket.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public abstract class SpringInitializedService implements ApplicationListener<ContextRefreshedEvent> {

 
    @Autowired
    private TransactionTemplate tt;

    private static final Log logger = LogFactory.getLog(SpringInitializedService.class);

    public SpringInitializedService() {
    }
  
    public SpringInitializedService(PlatformTransactionManager tm){
        tt.setTransactionManager(tm);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                initialize();
                }
                catch (Exception ex){
                	logger.error("Error during initialization",ex);
                    status.setRollbackOnly();
                }
            }
        });

    }
    public abstract void initialize();
} 
