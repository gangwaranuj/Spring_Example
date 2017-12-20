package com.workmarket.web.forms.feed

import org.junit.Test
import static org.junit.Assert.assertEquals

class FeedRequestParamsTest {
	@Test
	void setLSetsLimit() {
		def l = 7
		def params = new FeedRequestParams(l: l)
		assertEquals(l, params.limit)
	}

	@Test
	void setCSetsCompanyId() {
		def c = "2001"
		def params = new FeedRequestParams(c: c)
		assertEquals(c, params.companyId)
	}

	@Test
	void setPSetsPostalCode() {
		def p = "11787"
		def params = new FeedRequestParams(p: p)
		assertEquals(p, params.postalCode)
	}

	@Test
	void setSSetsState() {
		def s = "NY"
		def params = new FeedRequestParams(st: s)
		assertEquals(s, params.state)
	}

	@Test
	void setDSetsDistanceInMiles() {
		def d = "9999999"
		def params = new FeedRequestParams(d: d)
		assertEquals(d, params.distanceInMiles)
	}

	@Test
	void setISetsIndustryId() {
		def i = "1337"
		def params = new FeedRequestParams(i: i)
		assertEquals(i, params.industryId)
	}

	@Test
	void setKSetsKeyword() {
		def k = "1337"
		def params = new FeedRequestParams(k: k)
		assertEquals(k, params.keyword)
	}

	@Test
	void setKSetsWhen() {
		def w = "today"
		def params = new FeedRequestParams(w: w)
		assertEquals(w, params.when)
	}
}
