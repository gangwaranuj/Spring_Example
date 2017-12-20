<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Review">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<ul class="nav nav-tabs">
		<li><a href="<c:url value="/admin/licenses/review"/>">Licenses</a></li>
		<li class="active"><a href="<c:url value="/admin/certifications/review"/>">Certifications</a></li>
		<li><a href="<c:url value="/admin/insurance/review"/>">Insurance</a></li>
	</ul>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<a class="button -small" href="<c:url value="/admin/certifications/add"/>">Add New Certification</a>

	<h3>New Vendors / Providers</h3>
	<a href="<c:url value="/admin/certifications/vendor_instructions"/>" class="small">Vendor Instructions</a>

	<form action="#" method="post" id="vendor-list-filter-form" class="form-horizontal" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
			<c:param name="name" value="status" />
			<c:param name="id" value="vendor-list-filter" />
		</c:import>
	</form>

	<table id="vendor-list" class="table table-striped">
		<thead>
			<tr>
				<th width="8%">Status</th>
				<th width="8%">Added</th>
				<th width="15%">Added By</th>
				<th>Vendor</th>
				<th>Industry</th>
				<th>Last Activity</th>
				<th width="14%">&nbsp;</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>

	<hr/>

	<h3>New Certifications</h3>

	<form action="#" method="post" id="certification-list-filter-form" class="form-horizontal" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
			<c:param name="name" value="status" />
			<c:param name="id" value="certification-list-filter" />
		</c:import>
	</form>

	<table id="certification-list" class="table table-striped">
		<thead>
			<tr>
				<th width="8%">Status</th>
				<th width="8%">Added</th>
				<th width="15%">Added By</th>
				<th>Vendor</th>
				<th>Certification</th>
				<th>Last Activity</th>
				<th width="14%">&nbsp;</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>

	<hr/>

	<h3>User Certifications Requiring Approval</h3>

	<form action="#" method="post" id="userCertification-list-filter-form" class="form-horizontal" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
			<c:param name="name" value="status" />
			<c:param name="id" value="userCertification-list-filter" />
		</c:import>
	</form>

	<table id="userCertification-list" class="table table-striped">
		<thead>
			<tr>
				<th width="8%">Status</th>
				<th width="8%">Added</th>
				<th width="15%">User</th>
				<th>Provider</th>
				<th>Certification</th>
				<th width="8%">Issue Date</th>
				<th width="8%">Exp Date</th>
				<th width="8%">Last Activity</th>
				<th width="14%">&nbsp;</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>

<script type="text/javascript">
	$(wm.pages.admin.certifications.review());
</script>

<script id="vendor-action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="vendors_\${meta.id}_msg">
			<a href="<c:url value="/admin/certifications/editvendor?id="/>\${meta.id}">View Details</a>
		</div>
	</div>
</script>

<script id="certification-action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="certifications_\${meta.id}_msg">
			<a href="<c:url value="/admin/certifications/editcertifications?id="/>\${meta.id}">View Details</a>
		</div>
	</div>
</script>

<script id="userCertification-action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="usercertifications_\${meta.id}_${meta.user_id}_msg">
			<a href="<c:url value="/admin/certifications/edit_usercertification?id="/>\${meta.id}&user_id=\${meta.user_id}">View Details</a>
		</div>
	</div>
</script>

<script id="userCertification-number-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		\${meta.name}
		{{if meta.attachment_relative_uri != ""}}
			<a href="\${meta.attachment_relative_uri}" class="download-attachment-csr pr" title="Download"></a>
		{{/if}}
		<br />
		\${meta.number}
	</div>
</script>

</wm:admin>
