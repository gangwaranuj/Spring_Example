<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="stylesheet" type="text/css" href="http://localhost:8080/media/common.css" />
	<style type="text/css">
		@page {
			size: A4 landscape;
			margin: 0;
			padding: 0;
		}

		html, body {
			margin: 0;
			padding: 0;
			background-color: #fff;
		}
		table {
			margin: 0;
		}
		table.layout {
			border: none;
			border-collapse: collapse;
		}
		table.layout > thead {
			background-color: #000;
			color: #fff;
		}
		table.layout > thead small {
			color: #fff;;
		}
		table.layout th, table.layout td {
			padding: 4px;
			vertical-align: top;
		}
		table.layout h3 {
			font-size: 14px;
			line-height: 20px;
		}
		table.simple {
			border: none;
		}
		table.simple th, table.simple td {
			padding: 2px 1px;
		}
	</style>
</head>
<body>

<table class="layout">
	<thead>
		<tr>
			<th>
				&mdash;
			</th>
			<th>
				<c:out value="${company.name}"/>
				<small class="meta">(Activated <fmt:formatDate value="${company.createdOn.time}" pattern="MM/dd/yyyy"/>)</small><br/>
				<small>Credit Line: <fmt:formatNumber value="${accountRegister.apLimit}" currencySymbol="$" type="currency"/></small>
			</th>
			<th>
				Report Range <fmt:formatDate value="${filterForm.fromDate.time}" pattern="MM/yyyy"/> to <fmt:formatDate value="${filterForm.toDate.time}" pattern="MM/yyyy"/>
			</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td colspan="2">
				<h3>Top Users</h3>

				<table class="simple">
					<thead>
					<tr>
						<th>Name</th>
						<th>Email</th>
						<th>Rating</th>
						<th class="tar">Sent</th>
						<th class="tar">In Progress</th>
						<th class="tar">Approved</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach var="t" items="${topUsers}">
						<tr>
							<td nowrap="nowrap"><c:out value="${t.firstName}" /> <c:out value="${t.lastName}" /></td>
							<td><c:out value="${t.email}" /></td>
							<td>${wmfn:ratingStars(t.rating)}</td>
							<td class="tar"><c:out value="${t.sentAssignments}" /></td>
							<td class="tar"><c:out value="${t.activeAssignments}" /></td>
							<td class="tar"><c:out value="${t.closedAssignments}" /></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</td>

			<c:forEach var="report" items="${reports}" varStatus="i">
				<c:set var="values" value="" />
				<c:set var="labels" value="" />
				<c:set var="max" value="0" />
				<c:forEach var="point" items="${report.value.chartData}" varStatus="j">
					<fmt:formatNumber value="${point.y}" pattern="#" var="value" />
					<c:set var="values" value="${values}${value}${j.last ? '' : ','}" />
					<c:set var="labels" value="${labels}|${fn:substring(wmfmt:formatMillis('MMM', point.x), 0, 1)}" />
					<c:if test="${point.y > max}">
						<c:set var="max" value="${point.y}" />
					</c:if>
				</c:forEach>

				<%-- Use Google Chart API --%>
				<%-- @see https://google-developers.appspot.com/chart/image --%>
				<c:url value="http://chart.apis.google.com/chart" var="chartUri">
					<c:param name="chbh" value="a,5,5" />
					<c:param name="chs" value="320x90" />
					<c:param name="cht" value="bvg" />
					<c:param name="chco" value="DDDDDD" />
					<c:param name="chm" value="N,000000,0,-1,9" />
					<c:param name="chma" value="0,0,0,2" />
					<%-- Axes --%>
					<c:param name="chxl" value="0:${labels}" />
					<c:param name="chxt" value="x,y" />
					<c:param name="chxs" value="0,000000,10,0,l,67676700|1,676767,0,0,_,676767" />
					<c:param name="chxr" value="1,0,0" />
					<%-- Values --%>
					<c:param name="chd" value="t:${values}" />
					<c:param name="chds" value="0,${max}" />
					<c:param name="chdlp" value="t" />
				</c:url>

				<c:if test="${i.index % 3 == 1 and not i.first}"><tr></c:if>
				<td>
					<h3><c:out value="${report.key}" /></h3>
					<img src="<c:out value="${wmfmt:stripXSS(chartUri)}" />" />
				</td>
				<c:if test="${i.index % 3 == 0 and not i.last}"></tr></c:if>
			</c:forEach>
		</tr>
	</tbody>
</table>

</body>
</html>