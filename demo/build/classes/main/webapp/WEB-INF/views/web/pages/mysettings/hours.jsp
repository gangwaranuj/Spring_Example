<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.working_hours" var="global_working_hours"/>
<wm:app
	pagetitle="${global_working_hours}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'hours'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="myprofile.hours" scope="request"/>
			<jsp:include page="../../partials/profile/profile_edit_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3><fmt:message key="hours.working_hours"/></h3>
				</div>

				<jsp:include page="../../partials/message.jsp" />

				<form:form modelAttribute="hoursForm" action="/mysettings/hours" method="post" acceptCharset="utf-8" cssClass="form-stacked">
					<wm-csrf:csrfToken />
					<input type="hidden" name="post" value="1" />

					<div class="clearfix">
						<label><fmt:message key="hours.working_hours"/></label>
						<div class="input">
							<ul class="inputs-list">
								<c:forEach var="i" begin="0" end="6" step="1">
									<li>
										<form:checkbox path="workingHours[${i}].active" id="day_${i}"/>
										<label style="width:70px; display:inline-block;" for="day_${i}"><spring:message code="userAvailability.weekdays.${i}"/></label>

										<form:input path="workingHours[${i}].fromTime" id="time_from_${i}" cssClass="span2"/>
										<span class="ml mr">to</span>

										<form:input path="workingHours[${i}].toTime" id="time_to_${i}" cssClass="span2"/>
									</li>
								</c:forEach>
							</ul>
						</div>
					</div>

					<div class="wm-action-container">
						<a class="button" href="/profile"><fmt:message key="global.cancel"/></a>
						<button type="submit" class="button"><fmt:message key="global.save_changes"/></button>
					</div>
				</form:form>

				<h4><fmt:message key="global.vacation_on_hold"/></h4>
				<p><fmt:message key="hours.place_account_on_hold_message"/></p>

				<p>
					<c:choose>
						<c:when test="${user.userStatusType.code eq 'hold'}">
							<a class="button reactivate-account"><fmt:message key="hours.reactivate_my_account"/></a>
						</c:when>
						<c:otherwise>
							<a class="button suspend-account"><fmt:message key="hours.put_my_account_on_hold"/></a>
						</c:otherwise>
					</c:choose>
				</p>
				<c:choose>
					<c:when test="${user.lane3ApprovalStatus eq 'APPROVED'}">
						<div class="alert alert-success">
							<div class="tac"><fmt:message key="hours.profile_listed_in_search_results"/> <a href="/profile-edit/lanes"><fmt:message key="hours.edit"/></a></div>
						</div>
					</c:when>
					<c:when test="${user.lane3ApprovalStatus eq 'PENDING'}">
						<div class="alert alert-success">
							<p><fmt:message key="hours.profile_pending_for_search_listing"/></p>
						</div>
					</c:when>
					<c:otherwise>
						<form action='/profile-edit/lanes' method="post">
							<wm-csrf:csrfToken />
							<div class="alert alert-success">
								<strong><fmt:message key="hours.promote_profile_in_search"/></strong>
								<input type="hidden" name="shared_worker_role" value="1" />
								<p><fmt:message key="hours.not_listed_in_search_results"/></p>
								<button type="submit" class="button"><fmt:message key="hours.list_me_in_search"/></button>
							</div>
						</form>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</wm:app>
