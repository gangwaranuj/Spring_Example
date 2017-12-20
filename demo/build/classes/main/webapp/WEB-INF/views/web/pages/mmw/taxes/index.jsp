<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Tax Documentation" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Tax Documentation">

<div class="row_wide_sidebar_left">
	<div class="sidebar">
		<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
	</div>

	<div class="content">
		<div class="inner-container">

			<div class="page-header clearfix">
				<h3>Tax Documentation</h3>
			</div>
			<p>Your tax documentation, if any, is available for download each year you have been active on the Work Market platform and earned the eligible amount per requirements of your tax authority for that tax year.</p>

			<vr:rope>
				<vr:venue name="COMPANY" bypass="true">
					<div class="promotion -main -intuit">
						<img class="third-party-logo" src="${mediaPrefix}/images/intuit.svg" alt="Intuit QuickBooks Logo">
						<p>
							Are you up to date with your quarterly taxes? Year-round payments could help lower your annual tax bill, and QuickBooks Self-Employed can help ensure those payments are accurate.
							<a
								target="_blank"
								href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=taxdoc&cid=IPD_workmarket_taxdoc_QBSE&utm_email=${email}"
							>
								Learn More with QuickBooks
							</a>
						</p>
					</div>
				</vr:venue>
			</vr:rope>

			<c:choose>
				<c:when test="${not empty forms}">
					<table>
						<thead>
							<tr>
								<th>Document Type</th>
								<th>Creator</th>
								<th>Date Created</th>
								<th>Status</th>
								<th>Active</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="form" items="${forms}">
							<tr>
								<td>
									<c:choose>
										<c:when test="${form.type == '1099-MISC(B)'}">
											1099-MISC
										</c:when>
										<c:otherwise>
											<c:out value="${form.type}" />
										</c:otherwise>
									</c:choose>
									<c:if test="${not empty form.tin}"><c:out value="${form.tin}" /></c:if>
								</td>
								<td><c:out value="${form.createdBy}" /></td>
								<td>${wmfmt:formatCalendar('MMMM d, yyyy', form.createdOn)}</td>
								<td><c:out value="${form.status}" /> <c:if test="${form.status == 'Rejected'}">(<a href="/account/tax#edit" class="meta">Edit</a>)</c:if></td>
								<td>${form.active ? "Yes" : "N/A"}</td>
								<td>
									<c:choose>
										<c:when test="${form.type == 'W9'}">
											<a href="/mmw/taxes/download_tax_form/${form.id}" target="_blank">Download</a>
										</c:when>
										<c:when test="${form.type == '1099-MISC'}">
											<a href="/mmw/taxes/download_1099/${form.id}">Download</a>
										</c:when>
										<c:when test="${form.type == 'Earnings Report'}">
											<a href="/mmw/taxes/download_earning_report/${form.id}">Download</a>
										</c:when>
										<c:when test="${form.type == '1099-MISC Data'}">
											<a href="/mmw/taxes/download_earning_detail_report/${form.id}">Download</a>
										</c:when>
										<c:when test="${form.type == '1099-MISC(B)'}">
											<a href="/mmw/taxes/download_tax_service_detail_report/${form.id}">Download</a>
										</c:when>

										<%-- T4A and OTHER cases are currently disabled, no PDF download option exists --%>
										<c:when test="${form.type == 'T4A' || form.type == 'OTHER'}">
											None
										</c:when>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="alert"><p>You currently do not have any tax documentation on Work Market. <a class="btn fr" href="/account/tax">Submit here</a></p></div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
</wm:app>
