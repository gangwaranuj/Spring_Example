<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app
	pagetitle="Talent Pool Memberships"
	bodyclass="groups"
	webpackScript="groups"
>

	<script>
		var config = ${contextJson};
	</script>

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

	<div class="page-header groups-page-header">
		<h2>Talent Pool Memberships</h2>
	</div>

	<div class="row_sidebar_right groups-invitations">
		<div class="content">
			<table id="group_list">
				<thead>
				<tr>
					<th width="62%" class="group-name">Name</th>
					<th width="20%">Managed By</th>
					<th>Status</th>
					<th>Member Since</th>
				</tr>
				</thead>
				<tbody class="groups_tbody"></tbody>
			</table>
		</div>
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/groups/view/sidebar.jsp"/>
		</div>
	</div>

	<script id="cell-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/groups/\${meta.id}"><strong>\${data}</strong></a>
			<br/>
			<small class="meta">\${meta.owner_company}</small>
		</div>
	</script>

	<script id="cell-status-tmpl" type="text/x-jquery-tmpl">
		<div>
		{{if meta.is_member}}
			Member
		{{else}}
			Applied
		{{/if}}
		</div>
	</script>

	<script id="cell-approved-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${meta.date_approved}
		</div>
	</script>

	<script id="cell-owner-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${meta.owner_full_name}
		</div>
	</script>

</wm:app>
