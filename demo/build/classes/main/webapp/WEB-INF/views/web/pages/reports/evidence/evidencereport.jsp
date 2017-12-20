<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Background Check Evidence Report" bodyclass="reports" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="Evidence Reports" webpackScript="reports">

	<script>
		var config = {
			mode: 'background',
			groupId: 0,
			recipientEmail: '${wmfmt:escapeJavaScript(recipientEmail)}',
			screeningType: '${wmfmt:escapeJavaScript(screeningType)}'
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="evidence-report-alert" />
	</c:import>

	<div class="inner-container">

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

		<div class="page-header clear">
			<h3><c:out value="${reportTitle}" /> Evidence Report</h3>
			<div class="pull-left">
				<label class="inline">Filter By Talent Pool:
					<select id="group-filter">
					</select>
					<img id="results_spinner" src="${mediaPrefix}/images/loading.gif" alt="Loading" height="20"
						 width="20" class="vab dn"/>
				</label>
			</div>
			<div class="pull-right">
				<a class="button disabled" disabled id="download-certs" href="#">Download Certificates</a>
				<a class="button disabled" disabled id="export-csv" href="#">Export CSV</a>
			</div>
		</div>
	</div>

	<table id="evidence_report"></table>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

	<table id="evidence_report"></table>
</wm:app>
