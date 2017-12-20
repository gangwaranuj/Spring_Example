<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Weekly Report">

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminAccountingWeeklyRevenueReport" />
	<c:param name="admin" value="true" />
</c:import>

<form action="<c:url value="/admin/accounting/weekly_account_register_report"/>" id="filter-form" method="get">
	<input type="hidden" name="sort_by" id="sort_by" value="<c:out value="${param.sort_by}" />" />
	<input type="hidden" name="sort_order" id="sort_order" value="<c:out value="${param.sort_order}" />" />

	<fieldset class="filters form-stacked">
		<div class="clearfix">
			<label for="filter_sales_user_id">User</label>
			<div class="input">
				<c:choose>
					<c:when test="${whitelisted == true}">
						<select name="filter_sales_user_id" id="filter_sales_user_id">
							<option value="">All</option>
							<c:forEach var="item" items="${usersList}">
								<option value="<c:out value="${item.key}"/>" <c:if test="${param.filter_sales_user_id == item.key}">selected="selected"</c:if>><c:out value="${item.value}"/></option>
							</c:forEach>
						</select>
						<button type="submit" class="button">Filter</button>
					</c:when>
					<c:otherwise>
						<select name="filter_sales_user_id" id="filter_sales_user_id" disabled="disabled">
							<option value="">All</option>
							<c:forEach var="item" items="${usersList}">
								<option value="<c:out value="${item.key}"/>" <c:if test="${param.filter_sales_user_id == item.key}">selected="selected"</c:if>><c:out value="${item.value}"/></option>
							</c:forEach>
						</select>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</fieldset>
</form>

<p>* Revenue is 4 week trailing average</p>

<div style="overflow-x: auto;">
	<table class="table table-striped">
		<thead>
			<tr>
				<th nowrap="nowrap">
					<a href="javascript:void(0);" onclick="javascript:sort_by('company_name', '<c:choose><c:when test="${param.sort_order == 'desc'}">asc</c:when><c:otherwise>desc</c:otherwise></c:choose>'); return false;">Company</a>
					<c:if test="${param.sort_by == 'company_name'}">
						<c:choose>
							<c:when test="${param.sort_order == 'asc'}">
								<img src="${mediaPrefix}/images/data-table/sort_desc.png" alt="Direction" height="19" width="19" style="vertical-align: middle;" />
							</c:when>
							<c:otherwise>
								<img src="${mediaPrefix}/images/data-table/sort_asc.png" alt="Direction" height="19" width="19" style="vertical-align: middle;" />
							</c:otherwise>
						</c:choose>
					</c:if>
				</th>
        <c:set var="count" value="0" scope="page" />
				<c:forEach var="item" items="${weeklyReportRowList[0].weekDetail}">
					<th nowrap="nowrap"><a href="weekly_account_register_report?week_id=${count}">Week of ${wmfmt:formatCalendarWithTimeZone('MM/d/yyyy', item.value.weekStartDate, currentUser.timeZoneId)}</a></th>
          <c:set var="count" value="${count + 1}" scope="page"/>
        </c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${weeklyReportRowList}" varStatus="status">
				<c:choose>
					<c:when test="${status.count % 2 == 0}">
						<c:set var="rowStyle" scope="page" value="odd"/>
					</c:when>
					<c:otherwise>
						<c:set var="rowStyle" scope="page" value="even"/>
					</c:otherwise>
				</c:choose>
				<tr class="${rowStyle}">
					<td nowrap="nowrap">
						<a href="<c:url value="/admin/manage/company/overview/"/><c:out value="${item.companyId}"/>" target="_blank"><c:out value="${item.companyName}"/></a>
					</td>
					<c:forEach var="week" items="${item.weekDetail}">
						<td nowrap="nowrap">
							<fmt:formatNumber value="${week.value.totalAmount}" currencySymbol="$" type="currency"/>
							<c:if test="${not week.value.initialWeek}">
								<c:choose>
									<c:when test="${week.value.trendingUp}">
										<img src="${mediaPrefix}/images/icons/arrow_up.jpg" alt="Up" height="9" width="13" />
									</c:when>
									<c:otherwise>
										<img src="${mediaPrefix}/images/icons/arrow_down.jpg" alt="Down" height="9" width="13" />
									</c:otherwise>
								</c:choose>
							</c:if>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	function sort_by(column, direction) {
		$('#sort_by').val(column);
		$('#sort_order').val(direction);
		$('#filter-form').trigger('submit');
	}
</script>

</wm:admin>
