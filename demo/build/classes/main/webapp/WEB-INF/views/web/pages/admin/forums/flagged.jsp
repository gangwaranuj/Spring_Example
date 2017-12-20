<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Flagged">

<c:set var="pageScript" value="wm.pages.admin.forums.flaggedPosts" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

	<div class="content">
		<h1>Flagged Posts</h1>
		<table id="flaggedPostsTable" class="table table-striped forums-admin-paginated-table">
			<thead>
				<tr>
					<c:forEach var="label" items="${labels}">
						<th>${label}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="7" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>

<script id="tmpl-actions" type="text/template">
	<a class="admin-forums-review" href="javascript:void(0);" data-post-id="{{= postId }}">Unflag</a>
	<a class="admin-forums-ban" href="javascript:void(0);" data-user-id="{{= creatorId }}">Ban</a>
	<a class="admin-forums-delete" href="javascript:void(0);" data-post-id="{{= postId }}">Delete</a>
</script>

</wm:admin>
