<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Request Queue">

<div class="sidebar admin">
	<jsp:include page="../../../../partials/admin/quick_links.jsp"/>
	<br/>
</div>

<div class="content">
	<ul class="nav nav-tabs">
		<li><a href="<c:url value="/admin/manage/users/recent"/>">All Users</a></li>
		<li><a href="<c:url value="/admin/manage/users/pending"/>">WM DB Queue</a></li>
		<li class="active"><a href="<c:url value="/admin/manage/profiles/queue"/>">Update Queue</a></li>
		<li><a href="<c:url value="/admin/manage/users/suspended"/>">Suspended Users</a></li>
	</ul>

  <jsp:include page="../../../../partials/message.jsp" />

	<div id="table_profiles">
		<table id="profiles_list" class="table table-striped">
			<thead>
				<tr>
					<th>Date</th>
					<th>Full Name</th>
					<th>User Name</th>
					<th>Company</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="3" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="table_profiles_msg"></div>
</div>


<script type="text/javascript">

	$(document).ready(function() {

		var table = $('#profiles_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'bStateSave': true,
			'bProcessing': true,
			'bServerSide': true,
			'aoColumns': [null, null,null, null],
			'sAjaxSource': 'queue.json',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				aoData.push();
				$.getJSON( sSource, aoData, function (json) {
					if (json.aaData.length == 0)
					{
						$("#profiles_list").hide();
						$('.table_profiles_msg').html('There are no pending profile approvals.');
					}

					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][1] = '<a href="<c:url value="/admin/manage/profiles/index/"/>/'+json.aMeta[i].id+'" href="javascript:void(0)">'+json.aaData[i][1]+'</a>';
						if (json.aaData[i].suspended == true) {
							json.aaData[i][1] += ' <span class="red">(suspended)</span>';
						}
						json.aaData[i][2] = '<a href=\"mailto:'+json.aaData[i][2]+'\">'+json.aaData[i][2]+'</a>';
					}
					fnCallback(json)
				});
			}
		});
	});
</script>

</wm:admin>
