<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Suspend">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<div class="row-fluid">
		<div class="span16">
			<ul class="nav nav-tabs">
				<li><a href="/admin/manage/users/recent">All Users</a></li>
				<li><a href="/admin/manage/users/pending">WM DB Queue</a></li>
				<li><a href="/admin/manage/profiles/queue">Update Queue</a></li>
				<li class="active"><a href="/admin/manage/users/suspended">Suspended Users</a></li>
			</ul>

			<c:import url="/WEB-INF/views/web/partials/message.jsp" />

			<table id="users_list" class="table table-striped">
				<thead>
					<tr>
						<th class="large">Name</th>
						<th class="large">Company</th>
						<th class="small">Date Suspended</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="3" class="dataTables_empty">Loading data from server</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.users.suspended());
</script>

</wm:admin>
