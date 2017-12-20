<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<wm:app pagetitle="Invoices" bodyclass="payment page-payments" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="Invoices" webpackScript="payments">

<c:import url="/payments/dashboard"/>
	<script>
		var config = {
			payments: ${contextJson}
		};
	</script>

<div class="inner-container">
	<c:import url="/WEB-INF/views/web/partials/payments/navigation.jsp"/>

	<form:form commandName="filterForm">
		<input type="hidden" name="sortDirection" value="asc"/>
		<div class="row_wide_sidebar_left">
			<div class="sidebar">
				<c:if test="${currentView eq 'payables' and mmw.statementsEnabled}">
					<c:choose>
						<c:when test="${not empty statements}">
							<p><strong>Select a statement:</strong></p>
							<p><form:select path="statementId" ref="filter" items="${statements}" cssClass=""/></p>
						</c:when>
						<c:otherwise>
							<p>No unpaid statements</p>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test="${mmw.statementsEnabled}">
					<div id="statementDetail"></div>
					<c:if test="${not empty statements_configuration and not empty statements_configuration.nextStatementDate}">
						<p>Your next statement is scheduled to be emailed on <fmt:formatDate value="${statements_configuration.nextStatementDate.time}" pattern="MM/dd/yyyy"/>.</p>
					</c:if>
					<p><strong><a id="show-adv-filters">Advanced Filters &#9660;</a></strong></p>
				</c:if>
					<%--Hide Advance Filters if account is on statement--%>
				<div ${(mmw.statementsEnabled) ? 'class="dn"' : '' } id="advFilters">
					<div class="well clear" id="advanced-filters">
						<h5>Filter Invoices:</h5>
						<sec:authorize access="hasAnyRole('PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK')">
							<form:select path="buyerId" ref="filter" name="internal_owners" id="internal-owner-dropdown" data-placeholder="Any Internal Owner" class="wm-select" multiple="multiple" style="margin-bottom:5px; width:188px;">
								<c:forEach var="user" items="${users}">
									<form:option value="${user.key}"><c:out value="${user.value}" /></form:option>
								</c:forEach>
							</form:select>

						</sec:authorize>

						<form:select path="clientCompanyId" ref="filter" name="client_companies" id="client_company" data-placeholder="All Clients" class="wm-select" multiple="multiple" style="margin-bottom:5px; width:188px;">
							<optgroup id="clients" label="All Clients"></optgroup>
							<c:forEach var="item" items="${clients}">
								<form:option value="${item.key}"><c:out value="${item.value}"/></form:option>
							</c:forEach>
						</form:select>

						<form:select path="projectId" ref="filter" name="projects" id="project-dropdown" data-placeholder="All Projects" class="wm-select" multiple="multiple" style="margin-bottom:5px; width:188px;">
							<optgroup id="projects" label="All Projects"></optgroup>
							<c:forEach var="item" items="${projects}">
								<form:option value="${item.key}"><c:out value="${item.value}"/></form:option>
							</c:forEach>
						</form:select>

						<div id="assigned-resource-invoice">
							<form:input path="assignedResourceId" ref="filter" name="assigned_resources" id="resources-dropdown" placeholder="Any Assigned Worker" type="text" />
						</div>

						<form:select path="paidStatus" ref="filter" cssClass="wm-select">
							<form:option value="">By Payment Status</form:option>
							<form:options items="${payment_statuses}"/>
						</form:select>

						<c:if test="${currentView eq 'payables'}">
							<form:select path="invoiceType" ref="filter" cssClass="wm-select">
								<form:option value="">By Type</form:option>
								<form:options items="${invoice_type}"/>
							</form:select>
						</c:if>

						<form:hidden path="payables" ref="filter"/>
						<form:checkbox path="ignoreStatements" ref="filter" style="display:none;"/>
						<form:checkbox path="bundledInvoices" ref="filter" style="display:none;"/>

						<hr/>
						<c:if test="${not mmw.statementsEnabled}">
							<p>
								<form:select path="dateFilterType" ref="filter" cssClass="wm-select">
									<form:option value="due">By due date from</form:option>
									<form:option value="created">By approved date from</form:option>
									<form:option value="paid">By paid date from</form:option>
									<form:option value="work">By date of work</form:option>
								</form:select>
								<form:input path="fromDate" cssClass="span2" placeholder="MM/DD/YYYY" maxlength="10" ref="filter"/> to
								<form:input path="toDate" cssClass="span2" placeholder="MM/DD/YYYY" maxlength="10" ref="filter"/>
							</p>
							<hr/>
						</c:if>
						<p style="margin-bottom:10px">
							<button type="button" class="button">Apply</button>
							<button type="reset" class="button">Clear</button>
						</P>
						<p>
							<small>Export to
								<a href="/payments/invoices/print" id="print-filtered-outlet">PDF</a> or
								<a href="/payments/invoices/export" id="export-filtered-outlet">CSV</a>
							</small>
						</p>
					</div>
				</div>
			</div>
			<div class="content">
				<c:if test="${currentView eq 'receivables'}">
					<p>
						For payment inquiries, please contact the assignment owner.<br/>
						Their contact information is available on your assignment details view.
					</p>
				</c:if>

				<div class="mini-pagination">
					<wm:pagination min="1" max="10" />
					<div class="dataTables_info">
						<div id="invoice_list_size">
							Showing <span class="start_index">0</span> to <span class="end_index">0</span> of <span class="count">0</span> entries.
						</div>
						<div class="invoice_list_sorting">
							Show
							<select name="limit" class="rows_per_page invoice_limit_sort" size="1">
								<option value="10">10</option>
								<option value="25">25</option>
								<option value="50" selected="selected">50</option>
								<option value="100">100</option>
								<option value="250">250</option>
								<option value="500">500</option>
							</select>
							Sort By
							<select name="limit" class="sort_by invoice_limit_sort" size="1">
								<option value="payment_status" selected="selected">Payment Status</option>
								<option value="amount">Amount</option>
								<option value="invoice_number">Invoice Number</option>
							</select>
							<div class="btn-group sort" id="invoice_sort_direction">
								<a id="invoice_sorting_dsc" class="btn"><i class="icon-arrow-down"></i></a>
								<a id="invoice_sorting_asc" class="btn toggle_selected"><i class="icon-arrow-up"></i></a>
							</div>
						</div>
					</div>
				</div>
				<table id="invoices-table" class="pagination-selection">
					<thead>
						<tr>
							<th><input type="checkbox" name="select-all" value="1" class="select-all-visible-outlet"/></th>
							<th>Details</th>
							<th id="cta-sort-status" class="sorting_desc">Status</th>
							<th class="amount">Amount</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="4" class="tac">No data available in table</td>
						</tr>
					</tbody>
				</table>

				<div class="mini-pagination clear">
					<div class="dataTables_info">
						Showing <span class="start_index">0</span> to <span class="end_index">0</span> of <span class="count">0</span> entries. Show
						<select name="limit" class="rows_per_page invoice_limit_sort" size="1">
							<option value="10">10</option>
							<option value="25">25</option>
							<option value="50" selected="selected">50</option>
							<option value="100">100</option>
							<option value="250">250</option>
							<option value="500">500</option>
						</select>
						entries
					</div>
					<wm:pagination min="1" max="10" />
				</div>

				<c:if test="${currentView eq 'payables'}">
					<div class="wm-action-container">
						<span class="tabs-content payables-content">
							<a href="/payments/invoices/add_to_bundle" id="add-to-bundle-selected-outlet" class="button -new tooltipped tooltipped-n" aria-label="There should be at least one bundle to add invoices. You must select at least one unpaid invoice and selected invoices should not be locked to add to bundle.">
								<div class="button--content">Add to Bundle</div>
							</a>
							<c:if test="${not mmw.statementsEnabled}">
								<a href="/payments/invoices/bundle" id="bundle-selected-outlet" class="button -new tooltipped tooltipped-n" aria-label="You must select at least two unpaid invoices to create a bundle.">
									<div class="button--content">Bundle</div>
								</a>
							</c:if>
							<sec:authorize access="!principal.isMasquerading()">
								<a href="/payments/invoices/pay" id="pay-selected-outlet" class="button -new tooltipped tooltipped-n" aria-label="You must select at least one unpaid invoice to pay.">
									<div class="button--content">Pay</div>
								</a>
							</sec:authorize>
						</span>
					</div>
				</c:if>
			</div>
		</div>
	</form:form>
</div>

<script type="application/json" id="json_statements_details"><c:out value="${statements_details}" /></script>

</wm:app>
