<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="row kpi-header">
	<div class="span6">
		&mdash;
	</div>

	<div class="span8">
		<h2 class="name">
			<c:out value="${company.name}"/>
			<small class="meta">(Activated <fmt:formatDate value="${company.createdOn.time}" pattern="MM/dd/yyyy"/>)</small><br/>
			<small>Credit Line: <fmt:formatNumber value="${accountRegister.apLimit}" currencySymbol="$" type="currency"/></small>
		</h2>
	</div>

	<div class="span4 tar">
		<p>Report Range <fmt:formatDate value="${filterForm.fromDate.time}" pattern="MM/yyyy"/> to <fmt:formatDate value="${filterForm.toDate.time}" pattern="MM/yyyy"/></p>
	</div>
</div>

<div class="row">
	<div class="span12">
		<div class="kpi-data">
			<h3>Top users</h3>

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
				<c:forEach var="topUser" items="${topUsers}">
					<tr>
						<td nowrap="nowrap"><a href="/admin/manage/profiles/index/<c:out value='${topUser.userNumber}' />"><c:out value="${topUser.firstName}" /> <c:out value="${topUser.lastName}" /></a></td>
						<td><c:out value="${topUser.email}" /></td>
						<td>${wmfn:ratingStars(topUser.rating)}</td>
						<td class="tar"><c:out value="${topUser.sentAssignments}" /></td>
						<td class="tar"><c:out value="${topUser.activeAssignments}" /></td>
						<td class="tar"><c:out value="${topUser.closedAssignments}" /></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<c:forEach var="report" items="${reports}" varStatus="i">
		<c:if test="${i.index % 3 == 1}"><div class="row"></c:if>
		<div class="span6">
			<div class="kpi-data">
				<h3><c:out value="${report.key}" /></h3>

				<div class="graph">
					<div class="x-axis">
						<c:forEach var="point" items="${report.value.chartData}" varStatus="status">
							<div class="tick"
								 data-timestamp="${point.x}"
								 style="left:${(barWidth + barPadding) * status.index - (labelWidth / 2) + ((barWidth + 2) / 2)}px;">${fn:substring(wmfmt:formatMillis("MMM", point.x), 0, 1)}</div>
						</c:forEach>
					</div>

					<div class="data">
						<c:set var="max" value="0" />
						<c:forEach var="point" items="${report.value.chartData}">
							<c:if test="${point.y > max}">
								<c:set var="max" value="${point.y}" />
							</c:if>
						</c:forEach>

						<c:forEach var="point" items="${report.value.chartData}" varStatus="status">
							<div class="bar"
								 data-x="${point.x}"
								 data-y="${point.y}"
								 style="height:${point.y / max * 100}%; left:${(barWidth + barPadding) * status.index}px;">
								<span class="value"><fmt:formatNumber value="${point.y}" pattern="#" /></span>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<c:if test="${i.index % 3 == 0 and not i.last}"></div></c:if>
	</c:forEach>
</div>