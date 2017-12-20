<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Pending">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<ul class="nav nav-tabs">
		<li><a href="/admin/manage/users/recent">All Users</a></li>
		<li class="active"><a href="/admin/manage/users/pending">WM DB Queue</a></li>
		<li><a href="/admin/manage/profiles/queue">Update Queue</a></li>
		<li><a href="/admin/manage/users/suspended">Suspended Users</a></li>
	</ul>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

	<form:form action="/admin/manage/users/approve" id="user_approvals" method="post">
		<wm-csrf:csrfToken />

		<div id="table_users">
			<table id="users_list" class="table table-striped">
				<thead>
					<tr>
						<th width="10"><input type="checkbox" name="select-all" id="select-all" title="Select All" /></th>
						<th>Date</th>
						<th>Name</th>
						<th>Company</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="5" class="dataTables_empty">Loading data from server</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="table_users_msg"></div>

		<div class="form-actions">
			<button type="submit" id="submit-complete" class="button">Bulk Approve</button>
		</div>
	</form:form>

</div>

<script type="text/javascript">

	$(document).ready(function() {

		$('#select-all').click(function() {
			$('#users_list input[type=checkbox]').prop('checked', $(this).is(':checked'))
		});

		var drawRowCheckbox = function(row) {
			return '<input type="checkbox" name="user_ids[]" value="'+row.aData[0]+'"/>';
		};

		var drawRowAction = function(row) {
			return '<a href="/profile/'+row.aData[4]+'">View profile</a>';
		};

		var drawRowProfileLink = function(row) {
			return '<a href="/admin/manage/profiles/index/'+row.aData[4]+'">'+row.aData[2]+'</a>';
		};

		var table = $('#users_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'iDisplayLength': 50,
			'bFilter': true,
			'bStateSave': true,
			'bProcessing': true,
			'bServerSide': true,
			'aoColumns': [{"bSortable":false, "fnRender": drawRowCheckbox}, null, {"fnRender": drawRowProfileLink}, null, {"bSortable":false, "fnRender": drawRowAction}],
			'sAjaxSource': '/admin/manage/users/pending',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				aoData.push();
				$.getJSON( sSource, aoData, function (json) {
					if (json.aaData.length == 0) {
						$("#table_users").hide();
						$('.table_users_msg').html('No users pending approval.');
					}
					fnCallback(json)
				});
			}
		});
	});
</script>

</wm:admin>
