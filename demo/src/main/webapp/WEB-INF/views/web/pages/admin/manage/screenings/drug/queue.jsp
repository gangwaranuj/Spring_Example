<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Queue">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<ul class="nav nav-tabs">
		<li<c:if test="${current_type == 'screening'}"> class="active"</c:if>>
			<a href="<c:url value="/admin/screening"/>">Screened Users</a>
		</li>
		<li<c:if test="${current_type == 'drug_queue'}"> class="active"</c:if>>
			<a href="<c:url value="/admin/manage/screenings/drug/queue"/>">Drug Test Queue</a>
		</li>
		<li<c:if test="${current_type == 'bkgrnd_queue'}"> class="active"</c:if>>
			<a href="<c:url value="/admin/manage/screenings/bkgrnd/queue"/>">Background Check Queue</a>
		</li>
	</ul>

	<form action="#" id="screenings_filter_form">
		<select name="status">
			<option value="cancelled" <c:if test="${param.status == 'cancelled'}">selected="selected"</c:if>>Cancelled</option>
			<option value="error" <c:if test="${param.status == 'error'}">selected="selected"</c:if>>Error</option>
			<option value="failed" <c:if test="${param.status == 'failed'}">selected="selected"</c:if>>Failed</option>
			<option value="passed" <c:if test="${param.status == 'passed'}">selected="selected"</c:if>>Passed</option>
			<option value="requested" <c:if test="${param.status == 'requested' || empty param.status}">selected="selected"</c:if>>Requested</option>
			<option value="review" <c:if test="${param.status == 'review'}">selected="selected"</c:if>>Review</option>
		</select>
	</form>

	<table id="screenings_list_table" class="table table-striped">
		<thead>
			<tr>
				<th>ID</th>
				<th>Vendor ID</th>
				<th>Name</th>
				<th>Company</th>
				<th>Date Requested</th>
				<th width="160">Actions</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>

	<hr/>

	<h3>Instructions on submitting a drug test request</h3>
	<ol>
		<li>Login to the Sterling's site at <a href="https://smwreports.sterlingdirect.com" target="_blank">https://smwreports.sterlingdirect.com</a></li>
		<li>Click on 'Requests' and then the link for either 'Enter a single applicant' or 'Enter a batch of applicants' depending on how many requests you are processing</li>
		<li>Under 'A La Carte Services' click the checkbox for Standard Drug Screening and then click Go</li>
		<li>Enter the requested details on the form</li>
		<li>IMPORTANT: Select the paperless option where it asks for 'Drug Screen Type'</li>
		<li>Click submit</li>
	</ol>

	<p>The user will receive an email from Sterling that will take them through the rest of the process to identify the Drug Screening center and process the drug test.</p>

	<p>
		Paperless collection sites <span class="dark-gray xsmall">(i.e. where to send people living in Hawaii)</span>:<br/>
		<a href="https://smwreports.sterlingdirect.com/CollectionSites/default.aspx?subcode=E8137506">https://smwreports.sterlingdirect.com/CollectionSites/default.aspx?subcode=E8137506</a>
	</p>
</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.screenings.drug.queue('<c:url value="/admin/manage/screenings/drug/queue_list"/>'));
</script>

<script id="name-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="<c:url value="/profile/\${meta.user_number}"/>">\${data}</a>
	</div>
</script>

<script id="action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="<c:url value="/admin/manage/screenings/drug/update_status?id=\${id}&status=passed"/>" class="button">Passed</a>
		<a href="<c:url value="/admin/manage/screenings/drug/update_status?id=\${id}&status=failed"/>" class="button">Failed</a>
	</div>
</script>

</wm:admin>
