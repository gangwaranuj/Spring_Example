<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<wm:app
	pagetitle="Invitations"
	bodyclass="invitations"
	webpackScript="invitations"
>
	<sec:authorize var="shouldShowMDL" access="hasFeature('fea--mdl') and hasFeature('fea--mdl-invitations')" />
	<c:set var="now" value="<%=new java.util.Date()%>"/>

	<script>
		context.data["mode"] = "list";
		context.data["nowTime"] = ${wmfmt:escapeJavaScript(now.time)};
		context.data["shouldShowMDL"] = ${shouldShowMDL};
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>
	<div class="inner-container">
		<!--Landing Page and Invitations Tabs Navigations-->
		<c:import url="/WEB-INF/views/web/partials/recruiting/navigation.jsp"/>
		<div class="page-header clear">
			<h3 class="fl">Invitations</h3>
			<a href="/invitations/send" class="button pull-right">New Invitation</a>
		</div>

		<div class="alert alert-info">
			<p>
				Send invitations to contractors who you want to work with on Work Market.
				Manage your list of outstanding and past invitations here.
			</p>
		</div>

		<form action="/invitations/remind" method="POST">
			<wm-csrf:csrfToken />
			<table id="invitations_list">
				<thead>
					<tr>
						<th style="width: 40px;"></th>
						<th style="width: 100px;">Name</th>
						<th style="width: 100px;">Email</th>
						<th style="width: 100px;">Landing Page</th>
						<th style="width: 100px;">Sent By</th>
						<th style="width: 100px;">Last Contacted</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="7" class="dataTables_empty">Loading data from server</td>
					</tr>
				</tbody>
			</table>

			<div class="actions form-stacked clear">
				<wm:button classlist="pull-left" tooltip="Select people from the list above and send them a reminder to join your network. You can send reminders every 72 hours.">Send Reminders</wm:button>
			</div>

		</form>
	</div>
</wm:app>
