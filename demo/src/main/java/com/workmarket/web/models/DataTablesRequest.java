package com.workmarket.web.models;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;

public class DataTablesRequest extends AbstractPaginatableHttpRequest {
	String echo;

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public static DataTablesRequest newInstance(HttpServletRequest request) {
		return newInstance(request, null);
	}

	public static DataTablesRequest newInstance(HttpServletRequest request, Object backingBean) {
		DataTablesRequest r = new DataTablesRequest();
		r.setEcho(request.getParameter("sEcho"));
		r.setStart(NumberUtils.createInteger(request.getParameter("iDisplayStart")));
		r.setLimit(NumberUtils.createInteger(request.getParameter("iDisplayLength")));
		r.setSortColumnIndex(NumberUtils.createInteger(request.getParameter("iSortCol_0")));
		r.setSortColumnDirection(request.getParameter("sSortDir_0"));
		r.setRequestParams(request.getParameterMap());
		r.setBackingBean(backingBean);
		return r;
	}
}
