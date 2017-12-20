<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app
	pagetitle="Talent Pool Invitations"
	bodyclass="groups"
	webpackScript="groups"
>

	<script>
		var config = ${contextJson};
	</script>

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

	<div class="page-header groups-page-header">
		<h2>Talent Pool Invitations</h2>
		<c:if test="${currentUser.buyer}">
			<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER')">
				<a href="<c:url value="/groups/create"/>" class="button">New Talent Pool</a>
			</sec:authorize>
		</c:if>
	</div>

	<div class="row_sidebar_right groups-invitations">
		<div class="content">
			<table id="group_list">
				<thead>
				<tr>
					<th width="62%" class="group-name">Name</th>
					<th width="20%">Invited On</th>
					<th>Actions</th>
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

	<script id="cell-text-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${data}
			<br/>
		</div>
	</script>

	<script id="cell-groupactions-tmpl" type="text/x-jquery-tmpl">
		<div>
			<small class="meta">
				<a href="/groups/\${meta.id}">View</a> /
				<a href="/groups/\${meta.id}/decline" data-action="decline" data-value="\${meta.id}">Decline</a>
			</small>
		</div>
	</script>

</wm:app>
