package com.workmarket.web.resolvers;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tiles.request.render.StringRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.tiles3.TilesView;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomTilesViewResolverTest {

	CustomTilesViewResolver resolver;
	static final String REGULAR_VIEW_NAME = "testView";
	static final String REDIRECT_VIEW_NAME = "redirect:testView";

	@Before
	public void setup() {
		resolver = new CustomTilesViewResolver();

		// assumes it's running in an ApplicationContext, so need to mock those classes
		ApplicationContext context = mock(ApplicationContext.class);
		AutowireCapableBeanFactory beanFactory = mock(AutowireCapableBeanFactory.class);

		TilesView tilesView = new TilesView();
		tilesView.setBeanName(REGULAR_VIEW_NAME);
		tilesView.setApplicationContext(context);
		tilesView.setRenderer(new StringRenderer());
		tilesView.setUrl("test");

		when(beanFactory.initializeBean(anyObject(), eq(REDIRECT_VIEW_NAME))).thenReturn(new RedirectView());
		when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);

		resolver.setApplicationContext(context);
	}

	@Test(expected = Exception.class)
	public void createView_NullViewName_Exception() throws Exception {
		resolver.createView(null, Locale.US);
		fail("No exception thrown");
	}

	@Test(expected = Exception.class)
	public void createView_NullLocaleName_Exception() throws Exception {
		resolver.createView(REGULAR_VIEW_NAME, null);
		fail("No exception thrown");
	}

	public void createView_RedirectViewName_CreateNonNullView() {
		View view = null;
		try {
			view = resolver.createView(REDIRECT_VIEW_NAME, Locale.US);
		} catch (Exception e) {
			fail(ExceptionUtils.getStackTrace(e));
		}
		assertNotNull(view);
	}

	@Test
	public void createView_RedirectViewName_CreateRedirectView() {
		View view = null;
		try {
			view = resolver.createView(REDIRECT_VIEW_NAME, Locale.US);
		} catch (Exception e) {
			fail(ExceptionUtils.getStackTrace(e));
		}
		assertTrue(view instanceof RedirectView);
	}

	@Test
	public void createView_RedirectViewName_DoNotExposeModelAttributes() {
		View view = null;
		try {
			view = resolver.createView(REDIRECT_VIEW_NAME, Locale.US);
		} catch (Exception e) {
			fail(ExceptionUtils.getStackTrace(e));
		}
		// need to use reflection to access this protected field
		assertFalse((boolean) ReflectionTestUtils.getField(view, "exposeModelAttributes"));
	}

}