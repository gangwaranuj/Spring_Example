<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Reports" bodyclass="reports" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Overview" webpackScript="reports">
	<script>
		var config = {
			mode: 'index'
		};
	</script>

	<div class="inner-container">

		<c:import url="/WEB-INF/views/web/partials/message.jsp" />

		<c:choose>
		<c:when test="${not empty savedReports}">
		<div class="page-header clear">
			<h3 class="pull-left">Reports</h3>
			<strong><a class="button pull-right" href="/reports/custom/manage">New Report</a></strong>
		</div>

		<div class="row">
			<div class="span15">
				<div class="accordion">
					<div class="alert alert-info">
						<div>Build and save your own customized reports, selecting from all fields utilized on assignments. <strong><a class="muted" href="https://workmarket.zendesk.com/hc/en-us/articles/210052687" target="_blank">Learn more <i class="icon-info-sign"></i></a></strong></div>
					</div>

						<table>
							<thead>
							<th>Custom Report Name</th>
							<th>Creator</th>
							<th>Export</th>
							<%--TODO: Barry: to implement service call to enable editing--%>
							<%--<th>Edit</th>--%>
							<th>Copy</th>
							<th>Delete</th>
							</thead>
							<tbody>
							<c:forEach var="item" items="${savedReports}">
								<tr>
									<td>
										<c:if test="${not empty item.recurrence && item.recurrence.recurrenceEnabledFlag}">
											<a href="javascript:void(0);" class="tooltipped tooltipped-n" aria-label="Report is sent ${item.recurrence}">
												<i class="wm-icon-calendar muted"></i>
											</a>
										</c:if>
										<a href="/reports/custom/results?report_id=${item.reportKey}"><c:out value="${item.reportName}" /></a>
									</td>
									<td>
										<div>
											<c:out value="${item.creator}" />
										</div>
									</td>
									<td class="actions">
										<a href="#exportReport" class="cta-export-report tooltipped tooltipped-n" role="button" data-id="${item.reportKey}" aria-label="Email Report"><i class="wm-icon-envelope icon-large muted"></i></a>
									<td class="actions">
										<a class="tooltipped tooltipped-n" href="/reports/custom/manage?reportKey=${item.reportKey}&isCopy=true" aria-label="Copy Report"><i class="wm-icon-copy icon-large muted"></i></a>
									</td>
									<td class="actions">
										<a href="javascript:void(0);" class="cta-delete-report tooltipped tooltipped-n" data-id="${item.reportKey}" aria-label="Delete Report"><i class="wm-icon-trash icon-large muted"></i></a>
									</td>
								</tr>
							</c:forEach>

							</tbody>
						</table>

					</c:when>
					<c:otherwise>
						<div class="hero-unit" id="reports_hero">
							<h2>Reports</h2>
							<p>Our reporting tool gives you deep insight into the most important aspects of your business on Work Market. Use this powerful tool to evaluate different components of your independent workforce, including everything from labor spend, market coverage and worker performance. Flexible reporting options allow you to export a report immediately, schedule automated reports to be sent via email, or view a sub-set of report data before exporting.<a style="text-decoration: none;" href="https://workmarket.zendesk.com/hc/en-us/articles/210052687" target="_blank"> Learn more <i class="icon-info-sign"></i></a></p>
							<p>
								<a class="button" href="/reports/custom/manage">Build a Custom Report</a>
							</p>
						</div>
					</c:otherwise>
					</c:choose>

					<fmt:formatDate value="${last30Start.time}" pattern="MM/dd/yyyy" var="last30StartParam" />
					<fmt:formatDate value="${last30End.time}" pattern="MM/dd/yyyy" var="last30EndParam" />
					<fmt:formatDate value="${last90Start.time}" pattern="MM/dd/yyyy" var="last90StartParam" />
					<fmt:formatDate value="${last90End.time}" pattern="MM/dd/yyyy" var="last90EndParam" />
					<fmt:formatDate value="${ytdStart.time}" pattern="MM/dd/yyyy" var="ytdStartParam" />
					<fmt:formatDate value="${ytdEnd.time}" pattern="MM/dd/yyyy" var="ytdEndParam" />

					<div class="table">
						<div class="alert">
							<div>We've made available some popular report templates for you to utilize and/or review. You may also reference these templates when creating a custom report designed specifically for your business. <strong><a class="muted" href="https://workmarket.zendesk.com/hc/en-us/articles/222478288-Reports" target="_blank">Learn more <i class="icon-info-sign"></i></a></strong></div>
						</div>
						<table>
							<thead>
								<th>Pre-Built Reports</th>
							</thead>
							<tbody>
							<sec:authorize access="principal.personaPreference.buyer">
								<tr>
									<td>
										<a href="/reports/buyer?filters.from_date=${ytdStartParam}&filters.to_date=${ytdEndParam}">Company Report </a>
										<span
											aria-label="Report options include assignments in the last 30 days, last 90 days, year to date, the entire year of 2014 or a customized date range. This report allows you to see a big picture of past assignments with fields including pricing, details, etc."
											class="tooltipped tooltipped-e"
										>
											<i class="wm-icon-question-filled"></i>
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<a href="/reports/transactions?filters.transaction_date_from=${ytdStartParam}&filters.transaction_date_to=${ytdEndParam}">Transaction Report </a>
										<span
											aria-label="This report allows you to view a comprehensive history of all your financial account activity -- withdrawals, credits, paid assignments, and other transactions. Report options include transactions in the last 30 days, last 90 days, year to date, the entire year of 2014, or a customized date range."
											class="tooltipped tooltipped-e"
										>
											<i class="wm-icon-question-filled"></i>
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<a href="/reports/budget?filters.from_date=${ytdStartParam}&filters.to_date=${ytdEndParam}">Budget Report </a>
										<span
											aria-label="This report allows you to gain visibility into your contract labor expenditures, minimize your rogue spend and optimize your cost models. Report options include budget/expenses in the last 30 days, last 90 days, year to date, the entire year of 2014, or a customized date range."
											class="tooltipped tooltipped-e"
										>
											<i class="wm-icon-question-filled"></i>
										</span>
									</td>
								</tr>
								<tr>
									<td>
										<a href="/reports/evidence/backgroundcheck">Background Check Evidence Report </a>
										<span
											aria-label="This report allows you to review and download Evidence Report Certificates for members of a particular talent pool so you can better evaluate talent, mitigate compliance risk and improve work quality."
											class="tooltipped tooltipped-e"
										>
											<i class="wm-icon-question-filled"></i>
										</span>
									</td>
								</tr>
							</sec:authorize>
							<tr>
								<td>
									<a href="/reports/resource?filters.from_date=${ytdStartParam}&filters.to_date=${ytdEndParam}">Earnings Reports </a>
									<span
										aria-label="This report allows you to view earnings that you've accumulated on the Work Market platform. Report options include earnings in the last 30 days, last 90 days, year to date, the entire year of 2014, or a customized date range."
										class="tooltipped tooltipped-e"
									>
										<i class="wm-icon-question-filled"></i>
									</span>
									<div class="promotion">
										<span class="third-party-logo -inline -square -intuit-qb"></span>
										Track your money with QuickBooks Self-Employed.
										<a
											href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=earning&cid=IPD_workmarket_earning_QBSE&utm_email=${email}"
											target="_blank"
										>
											Start For Free
										</a>
									</div>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

</wm:app>
