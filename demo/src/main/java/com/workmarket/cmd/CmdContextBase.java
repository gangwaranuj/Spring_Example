package com.workmarket.cmd;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.configuration.Constants;
import com.workmarket.service.infra.business.AuthenticationService;

//@ContextConfiguration("/spring/application-context.xml")
public abstract class CmdContextBase{// implements ApplicationContextAware {

	/*
	 * Instance variables and constants
	 */
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	public final static String A_STRING = "aString";
	public String contextFile = "/spring/cmdApplicationcontext.xml";

	private ApplicationContext applicationContext = null;

    /**
     * @param name
     * @return
     */
    protected Object getBeanWithAuthentication(String name){
		//userService = (UserService)getBean("userService");
    	User currentUser = userService.findUserById(Constants.WORKMARKET_SYSTEM_USER_ID);
		Authentication authentication = new PreAuthenticatedAuthenticationToken(currentUser.getEmail(), "");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//authenticationService = (AuthenticationService)getBean("authenticationService");
		authenticationService.setCurrentUser(currentUser);
    	return getApplicationContext().getBean(name);
    }

    protected Object getBean(String name){
    	return getApplicationContext().getBean(name);
    }

    public abstract void execute() throws Exception;
	public abstract void init();


	protected ApplicationContext getApplicationContext() {
		if(applicationContext == null)
			applicationContext = new ClassPathXmlApplicationContext(contextFile);

		return applicationContext;
	}


	//@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the contextFile
	 */
	public String getContextFile() {
		return contextFile;
	}

	/**
	 * @param contextFile the contextFile to set
	 */
	public void setContextFile(String contextFile) {
		this.contextFile = contextFile;
	}


}
