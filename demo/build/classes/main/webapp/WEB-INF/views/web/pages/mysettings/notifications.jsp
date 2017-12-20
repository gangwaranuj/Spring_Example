<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Notification Settings" bodyclass="accountSettings page-settings-notifications" webpackScript="settings">

	<script>
		var config = {
			mode: 'notifications'
		};
	</script>


<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>
<div class="inner-container">
<div class="page-header clear">
	<h3><fmt:message key="notifications.notification_settings" /></h3>
</div>

<div class="row_wide_sidebar_right" id="emailSettings">
<form:form modelAttribute="notificationsForm" action="/mysettings/notifications" method="post" accept-charset="utf-8" class="form">
<wm-csrf:csrfToken />
<input type="hidden" name="post" value="1"/>
<div class="span11">
<c:if test="${currentUser.buyer}">
<fieldset>
<div class="row row-header row-header-first row-header-multiline">
	<div class="span3">
		<strong><fmt:message key="notifications.buyer_assignment" /></strong>
	</div>
	<div class="offset1 span2">
		<i class="icon-envelope-alt"></i><strong> Email</strong>
		<div class="row">
			<div class="span1 muted">
				<a class="tooltip-text tooltipped tooltipped-n" href="#" aria-label="Represents the assignment emails where you are the Assignment Owner"> <fmt:message key="global.owned" /></a>
			</div>
			<div class="span1 muted">
				<a class="tooltip-text tooltipped tooltipped-n" href="#" aria-label="Represents the assignment emails corresponding with the assignments you follow"><fmt:message key="global.followed" /></a>
			</div>
		</div>
	</div>

	<div class="span2">
		<i class="icon-bullhorn"></i><br/>
		<strong><fmt:message key="global.notification" /></strong>
	</div>

	<div class="span1"><i class="icon-mobile-phone"></i><br/>
		<strong><fmt:message key="global.push" /></strong>
	</div>

	<div class="span1"><i class="icon-comments"></i><br/>
		<strong><fmt:message key="global.sms" /></strong>
	</div>
</div>
<hr/>
	<%--This is select all for client assignments --%>
<div class="row">
	<div class="span4"><strong><fmt:message key="notifications.assignment_settings" /></strong></div>
	<div class="span1"><input class="select-all-checkboxes" rel="email" type="checkbox" /></div>
	<div class="span1"><input class="select-all-checkboxes" rel="follow" type="checkbox" /></div>
	<div class="span2"><input class="select-all-checkboxes" rel="bullhorn" type="checkbox" /></div>
	<div class="span1"><input class="select-all-checkboxes" rel="push" ${(not(pushEnabled)) ? "disabled" : ""} type="checkbox" /></div>
	<div class="span1"><input class="select-all-checkboxes" rel="sms" ${(not(profile.smsPhoneVerified)) ? "disabled" : ""} type="checkbox" /></div>
</div>
<br/>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_created" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.created'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.created'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.created'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.created'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.created'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_declined" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.declined'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.declined'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.declined'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.declined'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.declined'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_counter_offers" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.negotiation'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.negotiation'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.negotiation'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.negotiation'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.negotiation'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.question_asked" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.question'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.question'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.question'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.question'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.question'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_accepted" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.accepted'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.accepted'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.accepted'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.accepted'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.accepted'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.accepted'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_time_set"/></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.appointment'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.appointment'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.appointment'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.appointment'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.appointment'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.worker_confirmed" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.resource.confirmed'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.resource.confirmed'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.resource.confirmed'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.resource.confirmed'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.resource.confirmed'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.worker_failed_confirm" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.substatus.notconfirmed'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.substatus.notconfirmed'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.substatus.notconfirmed'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.substatus.notconfirmed'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.substatus.notconfirmed'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.worker_checked_in" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.resource.checkedin'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.resource.checkedin'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.resource.checkedin'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.resource.checkedin'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.resource.checkedin'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.worker_checked_out" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.resource.checkedout'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.resource.checkedout'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.resource.checkedout'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.resource.checkedout'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.resource.checkedout'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.attachment_added" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.attachment'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.attachment'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.attachment'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.attachment'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.attachment'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.note_added" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.note'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.note'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.note'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.note'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.note'].notificationTypeCode"/>
</div>
	<div class="row">
		<div class="span4"><fmt:message key="notifications.note_added.by.employee" /></div>
		<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.note.by.employee'].emailFlag"/></div>
		<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.note.by.employee'].followFlag"/></div>
		<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.note.by.employee'].bullhornFlag"/></div>
		<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.note.by.employee'].pushFlag"/></div>
		<form:hidden path="notifications['manage.work.note.by.employee'].notificationTypeCode"/>
	</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_completed" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.completed'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.completed'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.completed'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.completed'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.completed'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.completed'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.deliverables_completed" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.deliverable.requirements.fulfilled'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.deliverable.requirements.fulfilled'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.deliverable.requirements.fulfilled'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.deliverable.requirements.fulfilled'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.deliverable.requirements.fulfilled'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.deliverable.requirements.fulfilled'].notificationTypeCode"/>
</div>
<div class="row">
	<%--need more clarification on this notification--%>
	<div class="span4"><fmt:message key="notifications.worker_rates_employer" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.rated'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['resource.work.rated'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.rated'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.rated'].pushFlag"/></div>
	<form:hidden path="notifications['resource.work.rated'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.worker_cancelled" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.resource.cancelled'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.resource.cancelled'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.resource.cancelled'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.resource.cancelled'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.resource.cancelled'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_updated" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.updated'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.updated'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.updated'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.updated'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.updated'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.updated'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.stop_payment" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.stop_payment'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.stop_payment'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.stop_payment'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.stop_payment'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.stop_payment'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.alert_status_change" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.exception'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.exception'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.exception'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.exception'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.exception'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.exception'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.status_change" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.substatus'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.substatus'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.substatus'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.substatus'].pushFlag"/></div>
	<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.substatus'].smsFlag"/></div>
	<form:hidden path="notifications['manage.work.substatus'].notificationTypeCode"/>
</div>

<div>
	<h5><fmt:message key="notifications.assignment_requests" /></h5>
</div>

<div class="row">
	<div class="span4"><fmt:message key="notifications.schedule_change_requested" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.reschedule.requested'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.reschedule.requested'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.reschedule.requested'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.reschedule.requested'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.reschedule.requested'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.schedule_request_resolution" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.reschedule.decision'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.reschedule.decision'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.reschedule.decision'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.reschedule.decision'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.reschedule.decision'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.budget_change_requested" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.budget.requested'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.budget.requested'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.budget.requested'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.budget.requested'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.budget.requested'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.bonus_requested" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.bonus.requested'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.bonus.requested'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.bonus.requested'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.bonus.requested'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.bonus.requested'].notificationTypeCode"/>
</div>
<div class="row">
	<div class="span4"><fmt:message key="notifications.expense_pay_requested" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.expense.requested'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['manage.work.expense.requested'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.expense.requested'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.expense.requested'].pushFlag"/></div>
	<form:hidden path="notifications['manage.work.expense.requested'].notificationTypeCode"/>
</div>

<div class="row">
	<div class="span4"><fmt:message key="notifications.assignment_survey_completed" /></div>
	<div class="span1"><form:checkbox rel="email" path="notifications['tools.assessment.completed.invitee'].emailFlag"/></div>
	<div class="span1"><form:checkbox rel="follow" path="notifications['tools.assessment.completed.invitee'].followFlag"/></div>
	<div class="span2"><form:checkbox rel="bullhorn" path="notifications['tools.assessment.completed.invitee'].bullhornFlag"/></div>
	<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['tools.assessment.completed.invitee'].pushFlag"/></div>
	<form:hidden path="notifications['tools.assessment.completed.invitee'].notificationTypeCode"/>
</div>

</fieldset>
</c:if>
<c:if test="${currentUser.seller or currentUser.dispatcher}">
	<fieldset>
		<div class="row row-header row-header-multiline">
			<div class="span4">
				<strong>
					<c:choose>
						<c:when test="${currentUser.dispatcher}">
							<fmt:message key="global.dispatcher" />
						</c:when>
						<c:otherwise>
							<fmt:message key="global.worker" />
						</c:otherwise>
					</c:choose>
					<fmt:message key="notifications.assignment_notifications" />
				</strong>
			</div>
			<div class="offset1 span1">
				<i class="icon-envelope-alt"></i>
				<strong><fmt:message key="global.email" /></strong>
			</div>
			<div class="span2">
				<i class="icon-bullhorn"></i><br/>
				<strong><fmt:message key="global.notification" /></strong>
			</div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1">
					<i class="icon-mobile-phone"></i><br/>
					<strong><fmt:message key="global.push" /></strong>
				</div>
				<div class="span1">
					<i class="icon-comments"></i><br/>
					<strong><fmt:message key="global.sms" /></strong>
				</div>
			</c:if>
		</div>
		<hr/>
		<%--This is select all for client assignments --%>
		<div class="row">
			<div class="span4"><strong><fmt:message key="notifications.assignment_settings" /></strong></div>
			<div class="offset1 span1"><input class="select-all-checkboxes" rel="email" type="checkbox" /></div>
			<div class="span2"><input class="select-all-checkboxes" rel="bullhorn" type="checkbox" /></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><input class="select-all-checkboxes" rel="push" ${(not(pushEnabled)) ? "disabled" : ""} type="checkbox" /></div>
				<div class="span1"><input class="select-all-checkboxes" rel="sms" ${(not(profile.smsPhoneVerified)) ? "disabled" : ""} type="checkbox" /></div>
			</c:if>
		</div>
		<br/>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.invites_to_assignments" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.invited'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.invited'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.invited'].pushFlag"/></div>
				<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.invited'].smsFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.invited'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_not_available" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.not_available'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.not_available'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.not_available'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.not_available'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.appoinment_set" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.appointment'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.appointment'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.appointment'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.appointment'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_approved_payment" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.closed'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.closed'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.closed'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.closed'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_completed_behalf" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.completed_by_buyer'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.completed_by_buyer'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.completed_by_buyer'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.completed_by_buyer'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_rating_added" /></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['rate.rated'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['rate.rated'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['rate.rated'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['rate.rated'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.question_answered"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.question'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.question'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.question'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.question'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.attachment_added"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.attachment'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.attachment'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.attachment'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.attachment'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.deliverable_rejected"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.deliverable.rejected'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.deliverable.rejected'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.deliverable.rejected'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.deliverable.rejected'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.deliverable_late"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.deliverable.late'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.deliverable.late'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.deliverable.late'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.deliverable.late'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_updated"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.updated'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.updated'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.updated'].pushFlag"/></div>
				<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['resource.work.updated'].smsFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.updated'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.note_added"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.note'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.note'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.note'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.note'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.assignment_cancelled"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.cancelled'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.cancelled'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.cancelled'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.cancelled'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.alert_status_change"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.substatus.alert'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.substatus.alert'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.substatus.alert'].pushFlag"/></div>
				<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['resource.work.substatus.alert'].smsFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.substatus.alert'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.status_change"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.substatus'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.substatus'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.substatus'].pushFlag"/></div>
				<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['resource.work.substatus'].smsFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.substatus'].notificationTypeCode"/>
		</div>
		<div>
			<h5>Reminders</h5>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.confirm_reminder"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.resource.confirm'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.resource.confirm'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['manage.work.resource.confirm'].pushFlag"/></div>
				<div class="span1"><form:checkbox rel="sms" disabled="${not(profile.smsPhoneVerified)}" path="notifications['manage.work.resource.confirm'].smsFlag"/></div>
			</c:if>
			<form:hidden path="notifications['manage.work.resource.confirm'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.check_in_reminder"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['manage.work.checkin'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['manage.work.checkin'].bullhornFlag"/></div>
			<form:hidden path="notifications['manage.work.checkin'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.submit_deliverables_reminder"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.deliverable.reminder'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.deliverable.reminder'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.deliverable.reminder'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.deliverable.reminder'].notificationTypeCode"/>
		</div>
		<div>
			<h5><fmt:message key="global.request_and_approvals"/></h5>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.budget_change_added"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.budget.added'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.budget.added'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.budget.added'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.budget.added'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.budget_request_resolution"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.budget.decision'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.budget.decision'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.budget.decision'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.budget.decision'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.bonus_added"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.bonus.added'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.bonus.added'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.bonus.added'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.bonus.added'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.bonus_request_resolution"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.bonus.decision'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.bonus.decision'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.bonus.decision'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.bonus.decision'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.expense_resolution"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.expense.added'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.expense.added'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.expense.added'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.expense.added'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.expense_request_resolution"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.expense.decision'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.expense.decision'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.expense.decision'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.expense.decision'].notificationTypeCode"/>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.negotiation_resolution"/></div>
			<div class="span1"><form:checkbox rel="email" path="notifications['resource.work.negotiation.decision'].emailFlag"/></div>
			<div class="span2"><form:checkbox rel="bullhorn" path="notifications['resource.work.negotiation.decision'].bullhornFlag"/></div>
			<c:if test="${not currentUser.dispatcher}">
				<div class="span1"><form:checkbox rel="push" disabled="${not(pushEnabled)}" path="notifications['resource.work.negotiation.decision'].pushFlag"/></div>
			</c:if>
			<form:hidden path="notifications['resource.work.negotiation.decision'].notificationTypeCode"/>
		</div>
	</fieldset>
</c:if>


<fieldset>
	<div class="row row-header">
		<div class="span5"><strong><fmt:message key="global.invitations"/></strong></div>
		<div class="span1">
			<i class="icon-envelope-alt"></i>
			<strong><fmt:message key="global.email"/></strong>
		</div>
		<div class="span2">
			<i class="icon-bullhorn"></i><br/>
			<strong><fmt:message key="global.notification"/></strong>
		</div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1">
				<i class="icon-mobile-phone"></i><br/>
				<strong><fmt:message key="global.push"/></strong>
			</div>
		</c:if>
	</div>
	<hr/>
	<div class="row">
		<div class="span5"><fmt:message key="notifications.when_users_add_me"/></div>
		<div class="span1"><form:checkbox path="notifications['find.lane23.created'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['find.lane23.created'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['find.lane23.created'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['find.lane23.created'].notificationTypeCode"/>
	</div>
</fieldset>
<fieldset>
	<div class="row row-header">
		<div class="span5"><strong><fmt:message key="global.talent_pools"/></strong></div>
		<div class="span1">
			<i class="icon-envelope-alt"></i>
			<strong><fmt:message key="global.email"/></strong>
		</div>
		<div class="span2">
			<i class="icon-bullhorn"></i><br/>
			<strong><fmt:message key="global.notification"/></strong>
		</div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1">
				<i class="icon-mobile-phone"></i><br/>
				<strong><fmt:message key="global.push"/></strong>
			</div>
		</c:if>
	</div>
	<hr/>
	<c:if test="${currentUser.buyer}">
	<div class="row">
		<div class="span5"><fmt:message key="notifications.when_users_apply_talent_pool"/></div>
		<div class="span1"><form:checkbox path="notifications['find.group.apply'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['find.group.apply'].bullhornFlag"/></div>
		<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['find.group.apply'].pushFlag"/></div>
		<form:hidden path="notifications['find.group.apply'].notificationTypeCode"/>
	</div>
	</c:if>
	<div class="row">
		<div class="span5"><fmt:message key="notifications.my_talent_pool_resolution"/></div>
		<div class="span1"><form:checkbox path="notifications['find.group.approved'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['find.group.approved'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['find.group.approved'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['find.group.approved'].notificationTypeCode"/>
	</div>

	<div class="row">
		<div class="span5"><fmt:message key="notifications.invitations_join_talent_pools"/></div>
		<div class="span1"><form:checkbox path="notifications['find.group.invited'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['find.group.invited'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['find.group.invited'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['find.group.invited'].notificationTypeCode"/>
	</div>

	<div class="row">
		<div class="span5"><fmt:message key="notifications.messages_talent_pool_owners"/></div>
		<div class="span1"><form:checkbox path="notifications['find.group.message'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['find.group.message'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['find.group.message'].pushFlag"/></div>
		</c:if>
	</div>
</fieldset>
<fieldset>
	<div class="row row-header">
		<div class="span5"><strong><fmt:message key="notifications.tests_and_training"/></strong></div>
		<div class="span1">
			<i class="icon-envelope-alt"></i>
			<strong><fmt:message key="global.email"/></strong>
		</div>
		<div class="span2">
			<i class="icon-bullhorn"></i><br/>
			<strong><fmt:message key="global.notification"/></strong>
		</div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1">
				<i class="icon-mobile-phone"></i><br/>
				<strong><fmt:message key="global.push"/></strong>
			</div>
		</c:if>
	</div>
	<hr/>
	<div class="row">
		<div class="span5"><fmt:message key="notifications.invitations_take_tests"/></div>
		<div class="span1"><form:checkbox path="notifications['tools.assessment.invite'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['tools.assessment.invite'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['tools.assessment.invite'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['tools.assessment.invite'].notificationTypeCode"/>
	</div>

	<div class="row">
		<div class="span5"><fmt:message key="notifications.invitations_take_survey"/></div>
		<div class="span1"><form:checkbox path="notifications['tools.survey.invite'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['tools.survey.invite'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['tools.survey.invite'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['tools.survey.invite'].notificationTypeCode"/>
	</div>

	<div class="row">
		<div class="span5"><fmt:message key="notifications.people_complete_survey"/></div>
		<div class="span1"><form:checkbox rel="email" path="notifications['tools.survey.completed'].emailFlag"/></div>
		<div class="span2"><form:checkbox rel="bullhorn" path="notifications['tools.survey.completed'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" rel="push" path="notifications['tools.survey.completed'].pushFlag"/></div>
		</c:if>
		<form:hidden path="notifications['tools.survey.completed'].notificationTypeCode"/>
	</div>
</fieldset>

<c:set var="aclAdminManagerController" value="false"/>
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_CONTROLLER')">
	<c:set var="aclAdminManagerController" value="true"/>
</sec:authorize>
<c:set var="aclAdminController" value="false"/>
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_CONTROLLER')">
	<c:set var="aclAdminController" value="true"/>
</sec:authorize>
<c:if test="${hasPaymentCenterAndEmailsAccess}">
	<fieldset>
		<div class="row row-header">
			<div class="span5"><strong><fmt:message key="global.account_payment"/></strong></div>
			<div class="span1">
				<i class="icon-envelope-alt"></i>
				<strong><fmt:message key="global.email"/></strong>
			</div>
			<div class="span2">
				<i class="icon-bullhorn"></i><br/>
				<strong><fmt:message key="global.notification"/></strong>
			</div>
			<div class="span1">
				<i class="icon-mobile-phone"></i><br/>
				<strong><fmt:message key="global.push"/></strong>
			</div>
		</div>
		<hr/>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.account_past_due_invoices"/></div>
			<div class="span1"><form:checkbox path="notifications['pay.invoice.due.myaccount'].emailFlag"/></div>
			<div class="span2"><form:checkbox path="notifications['pay.invoice.due.myaccount'].bullhornFlag"/></div>
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['pay.invoice.due.myaccount'].pushFlag"/></div>
			<form:hidden path="notifications['pay.invoice.due.myaccount'].notificationTypeCode"/>
		</div>

		<div class="row">
			<div class="span5"><fmt:message key="notifications.account_locked_past_due"/></div>
			<div class="span1"><form:checkbox path="notifications['locked.invoice.due.myaccount'].emailFlag"/></div>
			<div class="span2"><form:checkbox path="notifications['locked.invoice.due.myaccount'].bullhornFlag"/></div>
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['locked.invoice.due.myaccount'].pushFlag"/></div>
			<form:hidden path="notifications['locked.invoice.due.myaccount'].notificationTypeCode"/>
		</div>

		<c:if test="${hasStatementEnabled}">
			<div class="row">
				<div class="span5"><fmt:message key="notifications.statement_invoice_available"/></div>
				<div class="span1"><form:checkbox path="notifications['pay.statement'].emailFlag" disabled="${empty(statements_configuration)}"/></div>
				<div class="span2"><form:checkbox path="notifications['pay.statement'].bullhornFlag" disabled="${empty(statements_configuration)}"/></div>
				<div class="span1"><form:checkbox path="notifications['pay.statement'].pushFlag" disabled="${empty(statements_configuration) or not(pushEnabled)}"/></div>
				<form:hidden path="notifications['pay.statement'].notificationTypeCode"/>
			</div>
		</c:if>

		<c:if test="${hasSubscriptionEnabled}">
			<div class="row">
				<div class="span5"><fmt:message key="notifications.subscription_invoice_available"/></div>
				<div class="span1"><form:checkbox path="notifications['pay.subscription'].emailFlag"/></div>
				<div class="span2"><form:checkbox path="notifications['pay.subscription'].bullhornFlag"/></div>
				<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['pay.subscription'].pushFlag"/></div>
				<form:hidden path="notifications['pay.subscription'].notificationTypeCode"/>
			</div>
		</c:if>
		<c:if test="${hasManageFundsAccess}">
			<div class="row">
				<div class="span5"><fmt:message key="notifications.funds_deposited"/></div>
				<div class="span1"><form:checkbox path="notifications['money.deposited'].emailFlag"/></div>
				<div class="span2"><form:checkbox path="notifications['money.deposited'].bullhornFlag"/></div>
				<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['money.deposited'].bullhornFlag"/></div>
				<form:hidden path="notifications['money.deposited'].notificationTypeCode"/>
			</div>
			<div class="row">
				<div class="span5"><fmt:message key="notifications.funds_withdrawn"/></div>
				<div class="span1"><form:checkbox path="notifications['money.withdrawn'].emailFlag"/></div>
				<div class="span2"><form:checkbox path="notifications['money.withdrawn'].bullhornFlag"/></div>
				<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['money.withdrawn'].bullhornFlag"/></div>
				<form:hidden path="notifications['money.withdrawn'].notificationTypeCode"/>
			</div>
			<div class="row">
				<div class="span5"><fmt:message key="notifications.deposited_by_credit_card"/></div>
				<div class="span1"><form:checkbox path="notifications['money.cc.receipt'].emailFlag"/></div>
				<div class="span2"><form:checkbox path="notifications['money.cc.receipt'].bullhornFlag"/></div>
				<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['money.cc.receipt'].pushFlag"/></div>
				<form:hidden path="notifications['money.cc.receipt'].notificationTypeCode"/>
			</div>
		</c:if>
	</fieldset>

	<fieldset>
		<div class="row row-header">
			<div class="span5"><strong><fmt:message key="global.assignment_invoices"/></strong></div>
			<div class="span1">
				<i class="icon-envelope-alt"></i>
				<strong><fmt:message key="global.email"/></strong>
			</div>
		</div>
		<hr/>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.all_invoices_for_assignments"/></div>
			<div class="span1"><form:checkbox path="notifications['invoice.created.radio.only'].emailFlag"/></div>
		</div>

		<div class="row">
			<div class="span5"><fmt:message key="notifications.all_invoices_due_within_3_days"/></div>
			<div class="span1"><form:checkbox path="notifications['invoice.due.3.days.radio.only'].emailFlag"/></div>
		</div>

		<div class="row">
			<div class="span5"><fmt:message key="notifications.all_invoices_due_within_24_hours"/></div>
			<div class="span1"><form:checkbox path="notifications['invoice.due.24.hours.radio.only'].emailFlag"/></div>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.my_invoices_due_within_3_days"/></div>
			<div class="span1"><form:checkbox path="notifications['invoice.due.3.days.mine'].emailFlag"/></div>
		</div>
		<div class="row">
			<div class="span5"><fmt:message key="notifications.my_invoices_due_within_24_hours"/></div>
			<div class="span1"><form:checkbox path="notifications['invoice.due.24.hours.mine'].emailFlag"/></div>
		</div>

	</fieldset>
</c:if>

<fieldset>
	<div class="row row-header">
		<div class="span5"><strong>Forums</strong></div>
		<div class="span1">
			<i class="icon-envelope-alt"></i>
			<strong><fmt:message key="global.email"/></strong>
		</div>
		<div class="span2">
			<i class="icon-bullhorn"></i><br/>
			<strong><fmt:message key="global.notifications"/></strong>
		</div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1">
				<i class="icon-mobile-phone"></i><br/>
				<strong><fmt:message key="global.push"/></strong>
			</div>
		</c:if>
	</div>
	<hr/>
	<div class="row">
		<div class="span5"><fmt:message key="notifications.comment_in_discussion_following"/></div>
		<div class="span1"><form:checkbox path="notifications['forums.comment.added'].emailFlag"/></div>
		<div class="span2"><form:checkbox path="notifications['forums.comment.added'].bullhornFlag"/></div>
		<c:if test="${not currentUser.dispatcher}">
			<div class="span1"><form:checkbox disabled="${not(pushEnabled)}" path="notifications['forums.comment.added'].pushFlag"/></div>
		</c:if>
	</div>
</fieldset>


<fieldset>
	<div class="row row-header">
		<div class="span5"><strong><fmt:message key="global.general_updates"/></strong></div>
	</div>
	<hr/>
	<div class="row">
		<div class="span10">
			<label>
				<form:checkbox path="notifications['workmarket.marketing'].emailFlag"/>
				<form:hidden path="notifications['workmarket.marketing'].notificationTypeCode"/>
				<fmt:message key="global.send_me_marketing_messages"/>
			</label>
		</div>
	</div>
	<div class="row">
		<div class="span10">
			<label>
				<form:checkbox path="notifications['workmarket.newsletter'].emailFlag"/>
				<form:hidden path="notifications['workmarket.newsletter'].notificationTypeCode"/>
				<fmt:message key="global.wm_newsletter_signup"/>
			</label>
		</div>
	</div>
</fieldset>
<hr/>
<div class="wm-action-container">
	<button type="submit" class="button"><fmt:message key="global.save_changes"/></button>
</div>
</div>
<div class="span4">
	<div class="well-b2">
		<h3><fmt:message key="global.default_contact_info"/></h3>
		<div class="well-content">
			<h5><i class="icon-envelope-alt"></i> <fmt:message key="global.email"/>:</h5>
			<dd class="clearfix contact-info__email"><c:out value="${email}" /> (<a href="/profile-edit"><fmt:message key="global.edit"/></a>)</dd>

			<h5><i class="icon-comments"></i> <fmt:message key="global.sms"/> /<fmt:message key="global.text"/>:</h5>
			<dd class="clearfix">
				<c:if test="${profile.smsPhoneVerified}">
					<c:out value="${profile.smsPhone}"/> (activated)<br/>
					<a id="sms_change"><fmt:message key="global.change"/></a> or <a id="sms_remove"><fmt:message key="global.remove"/></a>
				</c:if>
				<c:if test="${not(profile.smsPhoneVerified)}">
					<button class="button activate-sms"><fmt:message key="global.activate"/></button>
				</c:if>
					<span class="tooltipped tooltipped-n" aria-label="<fmt:message key="notifications.info_receive_sms_text"/>">
						<i class="wm-icon-question-filled"></i>
					</span>
			</dd>

			<h5><i class="icon-phone"></i> <fmt:message key="global.phone"/>:</h5>
			<p class="clearfix"><form:select path="ivrPreference" class="span3" items="${phoneNumbers}"/></p>
		</div>
	</div>
	<c:if test="${pushEnabled}" >
		<div class="well-b2">
			<h3><a href="/mysettings/devices"><fmt:message key="notifications.manage_mobile_devices"/></a></h3>
		</div>
	</c:if>
	<a href="https://workmarket.zendesk.com/hc/en-us/articles/215505177-How-do-I-edit-my-notification-settings" class="alert-message-btn" target="_blank">
		<div class="alert alert-info">
			<div class="media">
				<i class="media-object icon-lightbulb icon-2x pull-left"></i>
				<div class="media-body">
					<h5 class="media-heading"><fmt:message key="global.notifications"/></h5>
					<p><fmt:message key="notifications.account_notifications_settings_explanation"/></p>
				</div>
			</div>
		</div>
	</a>
</div>
</form:form>
</div>
</div>
</wm:app>
