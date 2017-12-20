<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Calendar Sync" bodyclass="accountSettings">

	<div id="error-message" class="alert alert-error" style="text-align: center;" hidden></div>
	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
		</div>
		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Sync to Google Calendar</h3>
				</div>
				<div id='google-calendar'>
					<c:import url="/WEB-INF/views/web/partials/profile/calendar_sync_modal.jsp"/>
				</div>
			</div>
		</div>
	</div>

</wm:app>
