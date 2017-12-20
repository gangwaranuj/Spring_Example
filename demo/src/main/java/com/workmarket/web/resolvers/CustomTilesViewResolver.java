package com.workmarket.web.resolvers;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import java.util.Locale;

/**
 * Created by nick on 2014-01-05 5:33 PM
 */
public class CustomTilesViewResolver extends TilesViewResolver {

	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		View view = super.createView(viewName, locale);
		if (view instanceof RedirectView) {
			((RedirectView) view).setExposeModelAttributes(false); // don't append model to query string on redirects
		}
		return view;
	}

}
