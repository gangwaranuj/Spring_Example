package com.workmarket.cmd;

import org.apache.activemq.xbean.XBeanBrokerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.workmarket.service.search.SearchService;

public class ReindexAllData {

	public static void main(String[] args) throws Exception {
		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring/cmdApplicationcontext.xml");
	
			System.out.println("Re-indexing all data...");
			SearchService searchService = (SearchService) ctx.getBean("searchServiceImpl");
			searchService.reindexAllData();
	
			System.out.println("Re-indexing done!");
			
			for (String beanName : new String[] {"jmsContainer", "eventJMSContainer", "jobJMSContainer", "batchJMSContainer"}) {
				DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) ctx.getBean(beanName);
				container.shutdown();
			}
	
			XBeanBrokerService broker = (XBeanBrokerService) ctx.getBean("broker");
			broker.stop();
			
			System.exit(0);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
