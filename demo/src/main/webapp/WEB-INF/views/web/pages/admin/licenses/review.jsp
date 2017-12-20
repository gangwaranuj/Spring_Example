<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Review" webpackScript="admin">
	<script>
		var config = {
			mode: 'licensesReview'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<a class="pull-right button -small" href="/admin/licenses/add">Add New License</a>

		<form action="/licenses_list_filters" class="form-horizontal" method="post" id="licenses_list_filter_form" accept-charset="utf-8">
			<wm-csrf:csrfToken />
			<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
				<c:param name="name" value="status" />
				<c:param name="id" value="licenses_list_filter" />
			</c:import>
		</form>

		<div id="table_licenses">
			<table id="licenses_list" class="table table-striped">
				<thead>
					<tr>
						<th width="8%">Status</th>
						<th width="8%">Added</th>
						<th width="15%">Added By</th>
						<th width="7%">State</th>
						<th width="14%">License</th>
						<th width="10%">Last Activity</th>
						<th width="17%">&nbsp;</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
		<div class="table_licenses_msg"></div>

		<hr/>
		<h3>User Licenses</h3>
		<form action="/userlicenses_list_filters" method="post" id="userlicenses_list_filter_form" class="form-horizontal" accept-charset="utf-8">
			<wm-csrf:csrfToken />
			<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
				<c:param name="name" value="status" />
				<c:param name="id" value="userlicenses_list_filter" />
			</c:import>
		</form>

		<div id="table_userlicenses">
			<table id="userlicenses_list" class="table table-striped">
				<thead>
					<tr>
						<th width="8%">Status</th>
						<th width="8%">Added</th>
						<th width="15%">User</th>
						<th width="10%">State</th>
						<th width="25%">License</th>
						<th width="10%">#</th>
						<th width="14%">Last Activity</th>
						<th width="10%">&nbsp;</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
		<div class="table_userlicenses_msg"></div>
	</div>

	<script id="action-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<div id="licenses_\${meta.id}_msg">
				<a name="licenses_approve" id="licenses_\${meta.id}">Approve</a><span class="separator"> / </span>
				<a name="licenses_decline" id="licenses_\${meta.id}">Decline</a><span class="separator"> / </span>
				<a name="licenses_unverified" id="licenses_\${meta.id}">Unverified</a>
			</div>
		</div>
	</script>

	<script id="action2-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<div id="userlicense_\${meta.id}_\${meta.user_id}_msg">
				<a href="<c:url value="/admin/licenses/edit_userlicense?id="/>\${meta.id}&user_id=\${meta.user_id}">View</a>
			</div>
		</div>
	</script>

	<script id="userlicensenumber-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.attachment_relative_uri}}
				<a href="\${meta.attachment_relative_uri}" class="download-attachment-csr pr" title="Download Attachment">
			{{/if}}
				\${data}
			{{if meta.attachment_relative_uri}}
				</a>
			{{/if}}
		</div>
	</script>
</wm:admin>
