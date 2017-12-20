<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Earnings" webpackScript="admin">
	<script>
		var config = {
			mode: 'taxIssuanceReport',
			canPublish: ${canPublish}
		}
	</script>

	<c:import url="/breadcrumb">
		<c:param name="pageId" value="adminAccountingTaxFormIssuanceService" />
	</c:import>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<div id="dynamic_messages"></div>

		<jsp:include page="/WEB-INF/views/web/partials/admin/accounting/tax_tabs.jsp" />

		<h4>Earnings Report</h4>

		<div id="earnings">
			<div class="toolbar">
				<a id="generate_csv" class="button">Generate ${taxYear} CSV</a>
			</div>

			<table class="table table-striped">
				<thead>
				<tr>
					<td></td>
					<td>Tax Year</td>
					<td>Date Created</td>
					<td>Actions</td>
				</tr>
				</thead>
				<tbody class="report_table">
					<%-- Filled dynamically --%>
				</tbody>
			</table>

			<div class="wm-action-container">
				<a id="publish" class="button">Publish</a>
			</div>
		</div>

		<hr>
		<h5>Earnings Report Data</h5>

		<div id="earnings_published">
			<table class="table-bordered table table-striped">
				<thead>
				<tr>
					<td>Tax Year</td>
					<td>Date Created</td>
					<td>Actions</td>
				</tr>
				</thead>
				<tbody class="report_table">
					<%-- Filled dynamically --%>
				</tbody>
			</table>
		</div>
	</div>

	<script type="text/x-jquery-tmpl" id="earnings_report_row_template">
		<tr>
			<td>
				{{if status.issued }}
					<input type="radio" name="csv_choose" value="\${id}">
				{{/if}}
			</td>
			<td>\${taxYear}</td>
			<td>\${createdOn}</td>
			<td>
				{{if status.processing || status.new }}
					<span><em>processing</em></span>
				{{else}}
					<a class="download" href="/admin/accounting/earnings/download/\${id}">Download</a> / <a class="delete" data-id="\${id}" href="#">Delete</a>
				{{/if}}
			</td>
		</tr>
	</script>

	<script type="text/x-jquery-tmpl" id="earnings_report_published_row_template">
		<tr>
			<td>\${taxYear}</td>
			<td>\${createdOn}</td>
			<td><a class="download" href="/admin/accounting/earnings/download/\${id}">Download</a>
		</tr>
	</script>

</wm:admin>
