<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp"/>

<table id="mappings-table">
	<thead>
		<tr>
			<th>Name</th>
			<th width="25%">Actions</th>
		</tr>
	</thead>
	<tbody></tbody>
</table>

<script id="cell-actions-tmpl" type="text/x-jquery-tmpl">
	<div>
		<small class="meta">
			<a href="/assignments/upload/rename_mapping/\${meta.id}?name=\${meta.name}" class="rename">Rename</a> /
			<a href="/assignments/upload/delete_mapping/\${meta.id}" class="delete">Delete</a>
		</small>
	</div>
</script>
