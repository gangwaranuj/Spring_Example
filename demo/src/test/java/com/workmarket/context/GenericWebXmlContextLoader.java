package com.workmarket.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.servlet.RequestDispatcher;

/**
 * Created by nick on 6/11/12 6:28 PM
 */
public class GenericWebXmlContextLoader extends AbstractContextLoader {
	private static final Log log = LogFactory.getLog(GenericWebXmlContextLoader.class);
	private final MockServletContext servletContext;

	public GenericWebXmlContextLoader() {
		this("", true);
	}
	
	public GenericWebXmlContextLoader(String warRootDir, boolean isClasspathRelative) {
		Class<GenericWebXmlContextLoader> c = GenericWebXmlContextLoader.class;
		ResourceLoader resourceLoader = isClasspathRelative ? new DefaultResourceLoader(c.getClassLoader()) : new FileSystemResourceLoader();
		this.servletContext = initServletContext(warRootDir, resourceLoader);
	}

	private MockServletContext initServletContext(String warRootDir, ResourceLoader resourceLoader) {
		
		return new MockServletContext(warRootDir, resourceLoader) {
			// Required for DefaultServletHttpRequestHandler...
			public RequestDispatcher getNamedDispatcher(String path) {
				return (path.equals("default")) ? new MockRequestDispatcher(path) : super.getNamedDispatcher(path);
			}
		};
	}

	public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
		log.debug("loading merged context configuration");
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		context.getEnvironment().setActiveProfiles(mergedConfig.getActiveProfiles());
		prepareContext(context, mergedConfig.getLocations());
		new XmlBeanDefinitionReader(context).loadBeanDefinitions(mergedConfig.getLocations());
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		context.refresh();
		context.registerShutdownHook();
		log.debug("loaded merged conext configuration");
		return context;
	}

	public ApplicationContext loadContext(String... locations) throws Exception {
		log.debug("loading config locations...");
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		prepareContext(context, locations);
		new XmlBeanDefinitionReader(context).loadBeanDefinitions(locations);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		context.refresh();
		context.registerShutdownHook();
		return context;
	}

	protected void prepareContext(GenericWebApplicationContext context, final String [] locations) {
		log.debug("preparing GenericWebApplicationContext ");
		this.servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		
		StringBuilder configLocations = new StringBuilder();
		for(int i = 0 ; i < locations.length; i ++) {
			log.debug(" ----->" + locations[i]);
			configLocations.append(locations[i]);
			if((i + 1) < locations.length) 
				configLocations.append(",");
		}
		this.servletContext.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, configLocations.toString());
		context.setServletContext(this.servletContext);
		log.debug("prepared GenericWebApplicationContext ");
	}

	@Override
	protected String getResourceSuffix() {
		return "-context.xml";
	}
}
