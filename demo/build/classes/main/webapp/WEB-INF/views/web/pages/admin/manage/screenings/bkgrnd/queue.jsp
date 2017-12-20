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
		<li <c:if test="${current_type == 'screening'}">class="active"</c:if>>
			<a href="<c:url value="/admin/screening"/>">Screened Users</a>
		</li>
		<li <c:if test="${current_type == 'drug_queue'}">class="active"</c:if>>
			<a href="<c:url value="/admin/manage/screenings/drug/queue"/>">Drug Test Queue</a>
		</li>
		<li <c:if test="${current_type == 'bkgrnd_queue'}">class="active"</c:if>>
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

</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.screenings.bkgrnd.queue('<c:url value="/admin/manage/screenings/bkgrnd/queue_list"/>'));
</script>

<script id="name-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="<c:url value="/profile/\${meta.user_number}"/>">\${data}</a>
	</div>
</script>

<script id="action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="<c:url value="/admin/manage/screenings/bkgrnd/update_status?id=\${id}&status=passed"/>" class="button">Passed</a>
		<a href="<c:url value="/admin/manage/screenings/bkgrnd/update_status?id=\${id}&status=failed"/>" class="button">Failed</a>
	</div>
</script>

</wm:admin>
