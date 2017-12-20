<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.custom_fields_inactive" var="pagetitle"/>
<wm:app
	pagetitle="${pagetitle}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'indexCustomFields',
			isActiveCustomFields: false
		};
	</script>

	<div class="row_wide_sidebar_left">

		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage/customfields" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="dynamic_messages"/>
				</c:import>
				<div class="page-header clear">
					<a class="button pull-right" href="/settings/manage/custom_fields_edit"><fmt:message key="custom_fields.new_field_set"/></a>
					<h3><fmt:message key="global.custom_fields_inactive"/></h3>
				</div>

				<div class="alert alert-info">
					<div class="row-fluid"><fmt:message key="custom_fields.create_unique_custom_field_groupings"/>
						<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/209336778" target="_blank"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
					</div>
				</div>

				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<div>
					<table id="custom-field-list">
						<thead>
						<tr>
							<th><fmt:message key="custom_fields.custom_field_set"/></th>
							<th><fmt:message key="global.edit"/></th>
							<th><fmt:message key="global.copy"/></th>
							<th><fmt:message key="global.delete"/></th>
							<th><fmt:message key="global.active"/></th>
						</tr>
						</thead>
					</table>
				</div>
				<a class="fr" href="/settings/manage/customfields"><small><fmt:message key="custom_fields.back_to_active_custom_fields"/> &raquo;</small></a>
				<br/>
			</div>
		</div>
	</div>

	<script id="name-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/settings/manage/custom_fields_edit?id=\${meta.id}">\${meta.name}</a><br/>
			<small class="meta"><fmt:message key="global.id"/>: \${meta.id}</small>
		</div>
	</script>

	<script id="edit-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/settings/manage/custom_fields_edit?id=\${meta.id}" class="tooltipped tooltipped-n" aria-label="<fmt:message key="global.edit"/>"><i class="wm-icon-edit icon-large muted"></i></a>
		</div>
	</script>

	<script id="copy-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="#" data-action="copy" data-id="\${meta.id}" aria-label="<fmt:message key="global.copy"/>" class="tooltipped tooltipped-n"><i class="wm-icon-copy icon-large muted"></i></a>
		</div>
	</script>

	<script id="delete-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="delete tooltipped tooltipped-n" aria-label="<fmt:message key="global.delete"/>" data-id="\${meta.id}"><i class="wm-icon-trash icon-large muted"></i></a>
		</div>
	</script>
	<script id="activate-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a class="tooltipped tooltipped-n" aria-label="<fmt:message key="custom_fields.activate"/>" data-action="toggle-activate" data-with-confirm="1" data-id="\${meta.id}"><i class="icon-off icon-large muted"></i></a>
		</div>
	</script>
</wm:app>
