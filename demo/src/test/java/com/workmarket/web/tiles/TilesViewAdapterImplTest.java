package com.workmarket.web.tiles;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import static org.apache.commons.lang3.StringUtils.startsWith;

/**
 * Created by nick on 8/22/13 6:12 PM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TilesViewAdapterImplTest {

	private static final String VIEW_NAME = "test";
	private TilesViewAdapterImpl adapter = new TilesViewAdapterImpl();
	private ModelAndView mav;
	private MockHttpServletRequest request;

	@Before
	public void init() {
		mav = new ModelAndView();
		mav.setViewName(VIEW_NAME);

		request = new MockHttpServletRequest();
	}

	@Test
	public void mapTilesView_NormalRequest_convertToTiles() {
		adapter.mapTilesView(mav, request);
		Assert.assertTrue(startsWith(mav.getViewName(), "tiles:"));
	}

	@Test
	public void mapTilesView_WithXRequestedWithHeader_doNothing() {
		request.addHeader("X-Requested-With", "/test/page");
		adapter.mapTilesView(mav, request);
		Assert.assertEquals(VIEW_NAME, mav.getViewName());
	}

	@Test
	public void mapTilesView_WithNoNamedMav_doNothing() {
		mav = new ModelAndView();
		adapter.mapTilesView(mav, request);
		Assert.assertEquals(null, mav.getViewName());
	}

	@Test
	public void mapTilesView_WithForwardView_doNothing() {
		mav.setViewName("forward:/zombo");
		adapter.mapTilesView(mav, request);
		Assert.assertEquals("forward:/zombo", mav.getViewName());
	}

	@Test
	public void mapTilesView_WithRedirectView_doNothing() {
		mav.setViewName("redirect:/zombo");
		adapter.mapTilesView(mav, request);
		Assert.assertEquals("redirect:/zombo", mav.getViewName());
	}
}
