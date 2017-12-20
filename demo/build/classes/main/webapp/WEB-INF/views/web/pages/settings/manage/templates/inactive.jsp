<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="templates.deactivated_templates" var="templates_deactivated_templates"/>
<wm:app
	pagetitle="${templates_deactivated_templates}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'templates',
			isActiveTemplates: false
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header clear">
					<a class="pull-right" href="/assignments/template_create"><fmt:message key="templates.new_template"/></a>
					<h3>${templates_deactivated_templates}</h3>
				</div>

				<div class="alert alert-info">
					<div class="row-fluid"><fmt:message key="templates.build_and_manage_templates"/>
						<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/210052717" target="_blank"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
					</div>
				</div>

				<div id="table_templates">
					<table id="templates_list_inactive">
						<thead>
						<tr>
							<th><fmt:message key="global.template"/></th>
							<th><fmt:message key="global.last_used"/></th>
							<th class="text-center"><fmt:message key="global.edit"/></th>
							<th class="text-center"><fmt:message key="global.copy"/></th>
							<th class="text-center"><fmt:message key="global.active"/></th>
							<th class="text-center"><fmt:message key="global.delete"/></th>
						</tr>
						</thead>
						<tbody>
						<tr>
							<td colspan="4" class="dataTables_empty"><fmt:message key="templates.loading_data_from_server"/></td>
						</tr>
						</tbody>
					</table>
					<a class="fr" href="/settings/manage/templates"><small><fmt:message key="templates.back_to_active_templates"/> &raquo;</small></a>
					<br/>
				</div>


				<div class="table_templates_msg"></div>
			</div>
		</div>
	</div>

	<script id="name-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<strong>\${meta.name}</strong><br/>
			<small class="meta"><fmt:message key="global.id"/>: \${meta.number}</small>
		</div>
	</script>

	<script id="edit-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="tooltipped tooltipped-n" href="/assignments/template_edit/\${meta.number}" aria-label="<fmt:message key="global.edit"/>"><i class="wm-icon-edit icon-large muted"></i></a>
		</div>
	</script>

	<script id="copy-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="tooltipped tooltipped-n" href="/assignments/template_copy/\${meta.number}" aria-label="<fmt:message key="global.copy"/>"><i class="wm-icon-copy icon-large muted"></i></a>
		</div>
	</script>

	<script id="active-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="inactive tooltipped tooltipped-n" aria-label="<fmt:message key="global.activate"/>" data-id="\${meta.number}">
				<i class="icon-off icon-large icon-red"></i>
			</a>
		</div>
	</script>

	<script id="delete-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="delete tooltipped tooltipped-n" data-id="\${meta.number}" aria-label="<fmt:message key="global.delete"/>"><i class="wm-icon-trash icon-large muted"></i></a>
		</div>
	</script>

</wm:app>
