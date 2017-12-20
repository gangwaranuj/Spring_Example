<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Assignments" bodyclass="manage-company">

<div class="sidebar admin">
	<div class="well">
		<h5>Company Actions</h5>
		<ul>
			<li><a href="/admin/manage/company/workcredits/${requestScope.company.id}">Add work credits</a></li>
			<li><a href="/admin/accounting/managefunds/${requestScope.company.id}">Refund a transaction</a></li>

			<c:choose>
				<c:when test="${requestScope.company.suspended}">
					<li><a href="/admin/manage/company/unsuspend/${requestScope.company.id}" onclick="return confirm('Are you sure you want to unsuspend this company?');">Unsuspend account</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="/admin/manage/company/suspend/${requestScope.company.id}" onclick="return confirm('Are you sure you want to suspend this company?');">Suspend account</a></li>
				</c:otherwise>
			</c:choose>
			<li><a id="reindex" href="/admin/manage/company/reindexwork/${requestScope.company.id}">Reindex all work</a></li>
		</ul>
	</div>

	<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>

</div>

<div class="content">

	<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp"/>

	<h3>Active Assignments</h3>

	<form action="/admin/manage/company/work" id="project_filters" method="get">
		<input type="hidden" name="id" value="${requestScope.company.id}"/>
	</form>

	<table id="inprogress_list" class="table table-striped">
		<thead>
		<tr>
			<th>ID</th>
			<th>Status</th>
			<th>Title</th>
			<th>Client</th>
			<th>Location</th>
			<th>Work Date</th>
			<th>Time to Appt</th>
			<th>Spend</th>
			<th>Worker</th>
		</tr>
		</thead>
		<tbody>
		<tr>
			<td colspan="9" class="dataTables_empty">Loading data from server</td>
		</tr>
		</tbody>
	</table>
</div>


<script type="text/javascript">
	$(document).ready(function() {

		var cellRenderer = function(template) {
			return function(row) {
				return $(template).tmpl({
					data: row.aData[row.iDataColumn],
					meta: meta[row.iDataRow]
				}).html();
			};
		};

		function apply_filters() {
			list_filters = $('#project_filters').serializeArray();
			datatable_obj.fnDraw();
		}

		// Run the filters on load.
		var list_filters = $('#project_filters').serializeArray();

		var meta;
		var datatable_obj = $('#inprogress_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 25,
			'aaSorting': [[5,'desc']],
			'aoColumnDefs': [
				{'fnRender': cellRenderer('#cell-assignment-detail-tmpl'), 'aTargets': [0,2]},
				{'bSortable': false, 'aTargets': []}
			],
			'sAjaxSource': '/admin/manage/company/assignments/${wmfmt:escapeJavaScript(requestScope.company.id)}',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				for (var i = 0, size = list_filters.length; i < size; i++) {
					aoData.push(list_filters[i]);
				}
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});

		$('#submit_search_filters').click(function(){
			apply_filters();
		});
	});
</script>

<script id="cell-assignment-detail-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="/assignments/details/\${meta.id}">\${data}</a>
	</div>
</script>

</wm:admin>
