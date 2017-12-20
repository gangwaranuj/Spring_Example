package com.workmarket.web.interceptors;

import com.workmarket.web.tiles.TilesViewAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor that only sets up a view for Apache Tiles for
 * primary, non-Ajax requests. Uses <code>tiles:</code> as a view prefix.
 */
public class TilesInterceptor extends ExcludableInterceptor {

	@Autowired TilesViewAdapter tilesViewAdapter;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception {
		super.postHandle(request, response, handler, model);

		if (isExcluded(request)) { return; }
		if (model == null) { return; }

		tilesViewAdapter.mapTilesView(model, request);
	}
}
