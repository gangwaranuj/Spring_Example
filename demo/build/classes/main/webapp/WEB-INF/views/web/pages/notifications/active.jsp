<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Active Notifications" bodyclass="accountSettings" webpackScript="settings">
	<%@ page isELIgnored="true"%>

	<script>
		var config = {
			mode: 'notifications-list',
			isArchive: false
		};
	</script>

	<div class="inner-container">
		<div class="page-header clear">
			<h3 class="pull-left">Notifications</h3>
			<a class="button pull-right wm-icon-gear" href="/mysettings/notifications">Settings</a>
		</div>


		<c:import url="/WEB-INF/views/web/partials/message.jsp" />

		<table id="notifications_list">
			<thead>
			<tr>
				<th width="73%">Message</th>
				<th width="22%">Date</th>
				<th></th>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
		<a class="fr" href="/notifications/archive"><small>Archived Notifications &raquo;</small></a>
		<br/>
	</div>

	<script id="link-archive-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/notifications/${meta.uuid}/archive" class="archive-action">Archive</a>
		</div>
	</script>

</wm:app>
