<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="isApply" value="${not work.configuration.assignToFirstResource}"/>
<c:set var="pageScript" value="wm.pages.mobile.assignments.reschedule" scope="request"/>

<div class="wrap reschedule">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Reschedule" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>
		<div class="unit whole">
			<form id="reschedule-form" action="/mobile/assignments/reschedule/${work.workNumber}" method="post">
				<wm-csrf:csrfToken />
				<input type="hidden" name='schedule_negotiation' value='1' id='schedule_negotiation'/>
				<c:if test="${not is_employee}">
					<p>Offer an alternate date for the assignment.</p>
				</c:if>
				<div class="grid">
					<input class="ratio" type="radio" name="scheduling" value="0" id="scheduling1" <c:if test="${not work.schedule.range}">checked="checked"</c:if> />
					<label for="scheduling1" class="header-label">At a specific time</label>
					<input class="ratio" type="radio" name="scheduling" value="1" id="scheduling2" <c:if test="${work.schedule.range}">checked="checked"</c:if> />
					<label for="scheduling2" class="header-label">During a time window</label>
					<div id="fixed_schedule_container" class="inner-tag nine-tenths" <c:if test="${work.schedule.range}">hidden</c:if>>
						<label for="from" class="header-label">From:</label>
						<div class="half">
							<input name="from" id="from" class="pickadate" placeholder="date" readonly/>
						</div>
						<div class="half">
							<input name="fromtime" id="fromtime" class="timepicker" placeholder="time" readonly/>
						</div>
					</div>
					<div id="variable_schedule_container" class="nine-tenths"<c:if test="${not work.schedule.range}">hidden</c:if>>
						<div class="inner-tag">
							<label for="from2" class="header-label">From:</label>
							<div class="half">
								<input name="variable_from" id="from2" class="pickadate" placeholder="date" readonly/>
							</div>
							<div class="half">
								<input name="variable_fromtime" id="fromtime2" class="timepicker" placeholder="time" readonly/>
							</div>
						</div>
						<div class="inner-tag">
							<label for="to" class="header-label">To:</label>
							<div class="half">
								<input name="to" id="to" class="pickadate" placeholder="date" readonly/>
							</div>
							<div class="half">
								<input name="totime" id="totime" class="timepicker" placeholder="time" readonly/>
							</div>
						</div>
					</div>
				</div>
				<div class="grid">
					<label for="note">
						<c:choose>
							<c:when test="${isApply}">Include a message with your application for this assignment
								<small>(recommended)</small>
							</c:when>
							<c:otherwise>Note:</c:otherwise>
						</c:choose>
					</label>
					<textarea name="note" id="negotiation_note"></textarea>
				</div>
				<input id="submit-reschedule"  type="submit" name="submit" value="Submit"/>
			</form>
		</div>
	</div>
</div>
