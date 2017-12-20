<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Active Notifications" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'notifications-list',
			isArchive: true
		};
	</script>

	<div class="inner-container">
		<div class="page-header clear">
			<h3 class="pull-left">Notifications</h3>
			<a class="button pull-right wm-icon-gear" href="/mysettings/notifications">Settings</a>
		</div>
		<ul class="wm-tabs">
			<li class="wm-tab"><a href="/notifications/active">Notifications</a></li>
			<li class="wm-tab -active"><a href="/notifications/archive">Archived Notifications</a></li>
		</ul>

		<table id="data_list">
			<thead>
				<tr>
					<th width="73%">Archived Notification</th>
					<th width="22%">Date</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>

</wm:app>
