package com.workmarket.web.tiles;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nick on 8/22/13 5:58 PM
 * This is used to convert views to the correct format for rendering by Tiles
 */
public interface TilesViewAdapter {
	public void mapTilesView(ModelAndView mav, HttpServletRequest request);
}
