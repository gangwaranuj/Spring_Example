<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="License Report" bodyclass="accountSettings" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="License Reports" webpackScript="reports">

	<script>
		var config = {
			mode: 'license',
			groupId: 0,
			recipientEmail: '${wmfmt:escapeJavaScript(recipientEmail)}',
			screeningType: '${wmfmt:escapeJavaScript(screeningType)}'
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="evidence-report-alert" />
	</c:import>

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
			<a class="button disabled" id="download-certs" href="#">Download Certificates</a>
			<a class="button  disabled" id="export-csv" href="#">Export CSV</a>
		</div>

	</div>


	<c:import url="/WEB-INF/views/web/partials/message.jsp" />
	<p>Evidence reports help you manage compliance and
		and ensure workers meet your workplace requirements.
		Select a talent pool from above that has license requirements.
		Below we will show you a list of the talent pool members and the current information
		on their licenses.
		Download certificate will send an email to you with images of your
		talent pool members' licenses as evidence.</p>

	<table id="evidence_report"></table>

</wm:app>
