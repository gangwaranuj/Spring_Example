<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Concerns">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h1>Reported Concerns</h1>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<div class="concern-messages alert-message dn" data-alert="alert">
		<a class="close">×</a>
		<div class="content"></div>
	</div>

	<form action="#" method="post" id="data_filter_form">
		<wm-csrf:csrfToken />
		<select name="filters_resolved">
			<option value="false" <c:if test="${param.filters_resolved == 'false'}">selected="selected"</c:if>>Un-Resolved</option>
			<option value="true" <c:if test="${param.filters_resolved == 'true'}">selected="selected"</c:if>>Resolved</option>
		</select>
	</form>

	<table id="data_list" class="table table-striped">
		<thead>
			<tr>
				<th width="120">Date Added</th>
				<th width="150">Added By</th>
				<th>Comment</th>
				<th width="200">Action</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>

<script type="text/javascript">
	$(wm.pages.admin.concerns('<c:url value="/admin/concerns/list"/>'));
</script>

<script id="concern-messages-tmpl" type="text/template">
	<ul>
		{{ _.each(data, function (element) { }}
			<li>{{= element }}</li>
		{{ }); }}
	</ul>
</script>

<script id="cell-reporter-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a target="_blank" href="/profile/\${meta.creator_user_number}">\${data}</a><br/>
		<span>\${meta.creator_company_name}</span>
	</div>
</script>

<script id="cell-action-tmpl" type="text/x-jquery-tmpl">
	<div>
		<span class="nowrap">
		{{if meta.type == 'profile'}}
			<a target="_blank" href="/profile/\${meta.entity_number}">View</a>
		{{else meta.type == 'work'}}
			<a target="_blank" href="/assignments/details/\${meta.entity_id}">View</a>
		{{else meta.type == 'group'}}
			<a target="_blank" href="/groups/\${meta.entity_id}">View</a>
		{{else meta.type == 'invitation'}}

		{{else meta.type == 'campaign'}}
			<a target="_blank" href="/campaigns/details/\${meta.entity_id}">View</a>
		{{else meta.type == 'assessment'}}
			<a target="_blank" href="/lms/manage/step1/\${meta.entity_id}">View</a>
		{{/if}}
		/ <a class="send-message-action" href="/admin/manage/users/message?user_ids[]=\${meta.creator_id}&user_names[]=\${meta.creator_fullname}&subject=Regarding%20your%20recent%20concern" title="Send \${meta.creator_fullname} a note">Send Note</a> /
		{{if meta.resolved}}
			<a class="reopen-action" href="/admin/concerns/reopen/\${meta.id}">Reopen</a>
		{{else}}
			<a class="resolve-action" href="/admin/concerns/resolve/\${meta.id}?creator_user_number=\${meta.creator_user_number}">Resolve</a>
		{{/if}}
		</span>
	</div>
</script>

</wm:admin>
