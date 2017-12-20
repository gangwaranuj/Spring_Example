<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="alert alert-info" id="only-google-sync" <c:if test="${isAuthorized and hasSettings}">style="display:none;"</c:if>>
	Note: We currently support syncing with Google Calendar. Please make sure your pop up blocker is disabled for Work Market.
</div>

<div class="row">
	<div <c:if test="${isAuthorized and hasSettings}">style="display:none;"</c:if> id="calendar-sync-unauth">
		<div class="span9">
			<p><strong>Step 1:</strong> Authorize Google Calendar access to Work Market.</p>
			<button id="calendar-access" class="button">Allow Access to Work Market</button>
		</div>
	</div>
	<div class="alert alert-info span8" <c:if test="${!isAuthorized or !hasSettings}">style="display:none;"</c:if> id="calendar-sync-authed">
		Your assignments are currently synced with your Google Calendar and other calendar options are coming soon
	</div>
</div>
<div class="row">
	<div <c:if test="${isAuthorized and hasSettings}">style="display:none;"</c:if> id='calendar-sync-access-calendar'>
		<div class="span9">
			<p> <strong>Step 2: </strong> Choose a calendar to push your assignment events to, or just create a new calendar for Work Market</p>
			<div class="controls controls-row">
				<select name="select-calendar" class="select-calendar span3" id="select-calendar" <c:if test="${!isAuthorized}">disabled</c:if> >
					<option value="new-calendar">Create New Calendar</option>
					<c:forEach items="${calendars}" var="option">
						<option value="${option.key}"><c:out value="${option.value}"/></option>
					</c:forEach>
				</select>
				<input class="ml fl" id="new-calendar-name" placeholder="Please input new calendar name" type="text"/>
			</div>
		</div>
	</div>
	<div <c:if test="${!isAuthorized or !hasSettings}">style="display:none;"</c:if> id="calendar-sync-cancel">
		<div class="span9">
			<h5> Cancel Calendar Sync</h5>
			<button id="cancel-sync" class="button">Cancel Sync</button>
		</div>
	</div>
</div>

<c:if test="${!isAuthorized or !hasSettings}">
	<div class="form-actions fixed-bottom" id="save-action">
		<button id="save-calendar-settings" type="submit" class="button pull-right" <c:if test="${(!isAuthorized) or (isAuthorized and hasSettings)}">disabled</c:if> >Submit</button>
	</div>
</c:if>
