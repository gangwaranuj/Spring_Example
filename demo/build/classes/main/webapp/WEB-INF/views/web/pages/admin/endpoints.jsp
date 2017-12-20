<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<wm:admin pagetitle="Endpoints">

<div class="row_sidebar_left">
	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content endpoints">
		<h1>Work Market Endpoints</h1>
		<table class="table">
			<thead>
				<tr>
					<th>URL Patterns</th>
					<th>Java method</th>
					<th>Request Methods</th>
					<th>Headers</th>
					<th>Consumes</th>
					<th>Produces</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${requestMappings}" var="entry">
				<tr>
					<td><code>${entry.patterns}</code></td>
					<td><code>${entry.javaMethod}</code></td>
					<td>${entry.methods}</td>
					<td>${entry.headers}</td>
					<td>${entry.consumes}</td>
					<td>${entry.produces}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>

</div

</wm:admin>>