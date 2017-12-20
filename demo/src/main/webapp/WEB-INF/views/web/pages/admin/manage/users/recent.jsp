<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="All">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<div class="row-fluid">
		<div class="span16">
			<form id="recent_user_filter_form">
				<select name="registrationDate">
				<c:forEach var="item" items="${registrationDateFilterOptions}">
					<option value="${item.key}"><c:out value="${item.value}" /></option>
				</c:forEach>
				</select>
			</form>

			<ul class="nav nav-tabs">
				<li class="active"><a href="/admin/manage/users/recent">All Users</a></li>
				<li><a href="/admin/manage/users/pending">WM DB Queue</a></li>
				<li><a href="/admin/manage/profiles/queue">Update Queue</a></li>
				<li><a href="/admin/manage/users/suspended">Suspended Users</a></li>
			</ul>

			<c:import url="/WEB-INF/views/web/partials/message.jsp" />
			<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
				<c:param name="containerId" value="dynamic_messages" />
			</c:import>

			<table id="users_list" class="table table-striped">
				<thead>
					<tr>
						<th><input type="checkbox" name="select-all" id="select-all" title="Select All" /></th>
						<th class="small">Name</th>
						<th class="small">Company</th>
						<th class="large">Email</th>
						<th class="medium">Phone</th>
						<th class="medium">Lanes</th>
						<th class="medium">Signup Date</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>

			<div class="form-actions">
				<a id="send-message-action" class="button">Send Message</a>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.manage.company.recent());
</script>

<script id="user-checkbox-tmpl" type="text/x-jquery-tmpl">
	<div><input type="checkbox" name="user_ids[]" value="\${meta.id}"/></div>
</script>

<script id="user-name-tmpl" type="text/x-jquery-tmpl">
	<div><a href="/admin/manage/profiles/index/\${meta.user_number}">\${data}</a></div>
</script>

<script id="user-company-tmpl" type="text/x-jquery-tmpl">
	<div><a href="/admin/manage/company/overview/\${meta.company_id}">\${data}</a></div>
</script>

<script id="user-email-tmpl" type="text/x-jquery-tmpl">
	<div><a href="mailto:\${data}">\${data}</a></div>
</script>

<script id="user-lane-tmpl" type="text/x-jquery-tmpl">
	<div>
		{{if meta.lane1}}<div class="lane lane-1 fl"><b class="strong xsmall white">1</b></div>{{else}}<div class="lane fl"><b class="strong xsmall white">&nbsp;</b></div>{{/if}}
		{{if meta.lane2}}<div class="lane lane-2 fl"><b class="strong xsmall white">2</b></div>{{else}}<div class="lane fl"><b class="strong xsmall white">&nbsp;</b></div>{{/if}}
		{{if meta.lane3}}<div class="lane lane-3 fl"><b class="strong xsmall white">3</b></div>{{else}}<div class="lane fl"><b class="strong xsmall white">&nbsp;</b></div>{{/if}}
		{{if meta.lane4}}<div class="lane lane-4 fl"><b class="strong xsmall white">WM</b></div>{{else}}<div class="lane fl"><b class="strong xsmall white">&nbsp;</b></div>{{/if}}
	</div>
</script>

</wm:admin>
