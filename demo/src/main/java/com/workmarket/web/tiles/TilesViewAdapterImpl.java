package com.workmarket.web.tiles;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nick on 8/22/13 6:00 PM
 */
@Component
public class TilesViewAdapterImpl implements TilesViewAdapter{

	@Override
	public void mapTilesView(ModelAndView mav, HttpServletRequest request) {

		String originalView = mav.getViewName();

		if (request.getHeader("x-requested-with") != null) { return; }
		if (WebUtils.isIncludeRequest(request)) { return; }
		if (originalView == null) { return; }
		if (originalView.startsWith("redirect:")) { return; }
		if (originalView.startsWith("forward:")) { return; }

		mav.setViewName(String.format("tiles:%s", mav.getViewName()));
	}
}
