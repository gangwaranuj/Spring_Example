<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Search">

<div class="sidebar admin">
	<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div class="content">
	<div class="row-fluid">
		<div class="span16">
			<h1>Company Search</h1>
			<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

			<div id="companies_list_container" style="width:100%">
				<table id="companies_list" class="table table-striped">
					<thead>
						<tr>
							<th width="30%">Company</th>
							<th>Type</th>
							<th>Users</th>
							<th>Employees</th>
							<th>Vendors</th>
							<th>3rd Parties</th>
							<th>Date Created</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="7" class="dataTables_empty">Loading data from server</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="companies_list_msg message notice dn"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.company.search());
</script>

</wm:admin>
