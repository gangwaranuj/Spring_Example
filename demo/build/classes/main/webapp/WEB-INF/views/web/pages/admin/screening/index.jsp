<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Screening">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">

	<ul class="tabs">
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

	<table id="screening_list" class="zebra-striped">
		<thead>
			<tr>
				<th>First Name</th>
				<th>Last Name</th>
				<th>Company</th>
				<th>Background</th>
				<th>Drug Test</th>
				<th>Credit Check</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#screening_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'iDisplayLength': 100,
			'aaSorting': [[1,'asc']],
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': '<c:url value="/admin/screening/listusers"/>',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.getJSON(sSource, aoData, function (json) {
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][0] = '<a href="<c:url value="profile"/>?id=' + json.aMeta[i].id + '">' + json.aaData[i][0] + '</a>';
						json.aaData[i][1] = '<a href="<c:url value="profile"/>?id=' + json.aMeta[i].id + '">' + json.aaData[i][1] + '</a>';
					}
					fnCallback(json)
				});
			}
		});
	});
</script>

</wm:admin>
