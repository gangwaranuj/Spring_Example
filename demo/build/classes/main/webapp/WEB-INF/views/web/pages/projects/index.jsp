<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Projects" bodyclass="projects" webpackScript="projects" breadcrumbSection="Work" breadcrumbSectionURI="/dashboard" breadcrumbPage="Projects">
	<script>
		var config = ${contextJson};
	</script>

	<div class="inner-container table">
		<div class="table--header">
			<h1 class="table--title">Projects</h1>
			<c:if test="${!hasFeatureProjectPermission || hasProjectAccess}">
				<a href="/projects/add" class="button">New Project</a>
			</c:if>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

		<table id="project_list">
			<thead>
				<tr>
					<th width="300">Project Name</th>
					<th width="200">Client</th>
					<th width="200">Owner</th>
					<th width="100">Due</th>
					<th width="100">Remaining Balance</th>
					<th width="80">Edit</th>
					<th width="80">Delete</th>
					<th width="80">Active</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>

	<script id="cell-title-tmpl" type="text/x-jquery-tmpl">
		<div>
			<strong><a href="/projects/view/\${meta.id}">\${data}</a></strong>
			{{if !meta.active}}
				<small class="meta">(inactive)</small>
			{{/if}}
		</div>
	</script>

	<script id="cell-edit-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/projects/edit/\${meta.id}" class="tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="cell-active-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.active}}
				<a data-id="\${meta.id}" class="deactivate-project">Deactivate</a>
			{{else}}
				<a href="/projects/activate/\${meta.id}">Activate</a>
			{{/if}}
		</div>
	</script>

	<script id="cell-delete-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a data-id="\${meta.id}" class="delete-project tooltipped tooltipped-n" aria-label="Delete">
				<i class="wm-icon-trash icon-large muted" data-id="\${meta.id}"></i>
			</a>
		</div>
	</script>

</wm:app>
