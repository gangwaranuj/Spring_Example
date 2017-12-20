<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp" />

<div class="activity-tab">
	<c:forEach var="entry" items="${work.changelog}">
		<div class="activity-tab__row">
			<div>
				<c:if test="${entry.type eq 'WORK_STATUS_CHANGE'}">
					<c:set var="isAlert" value="${entry.status eq 'Cancelled' or entry.status eq 'Void'or entry.status eq 'Deleted'
					or entry.status eq 'Cancelled - Payment Pending' or entry.status eq 'Cancelled and Paid' }"></c:set>
					<span class="activity-tab__work-status-change <c:if test='${isAlert}'>activity-tab__alert</c:if>"><c:out value="${entry.status}"></c:out></span>
				</c:if>
				<c:if test="${entry.type eq 'WORK_SUB_STATUS_CHANGE'}">
					<span class="activity-tab__work-sub-status-change" style="background-color:#${entry.subStatus.colorRgb}"><c:out value="${entry.subStatus.description}"></c:out></span>
				</c:if>
				<c:if test="${entry.type eq 'WORK_PROPERTY'}">
					<i class="wm-icon-information-filled activity-tab__icon"></i>
				</c:if>
				<c:if test="${entry.type eq 'WORK_RESOURCE_STATUS_CHANGE'}">
					<c:set var="isAlert" value="${entry.status eq 'cancelled'}"></c:set>
					<span class="activity-tab__work-status-change <c:if test='${isAlert}'>activity-tab__alert</c:if>"><c:out value="${entry.status}"></c:out></span>
				</c:if>
				<c:if test="${entry.type eq 'WORK_CREATED'}">
					<i class="wm-icon-assignments activity-tab__icon"></i>
				</c:if>
				<c:if test="${entry.type eq 'WORK_NEGOTIATION_REQUESTED'}">
					<c:choose>
						<c:when test="${entry.scheduleNegotiationOnly}">
							<i class="wm-icon-calendar activity-tab__icon"></i>
						</c:when>
						<c:otherwise>
							<i class="wm-icon-payments-filled activity-tab__icon <c:if test='${entry.rejectAction}'>action-reject</c:if>"></i>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test="${entry.type eq 'WORK_RESCHEDULE_REQUESTED'}">
					<i class="wm-icon-calendar activity-tab__icon"></i>
				</c:if>
				<c:if test="${entry.type eq 'WORK_NEGOTIATION_EXPIRED'}">
					<i class="wm-icon-calendar activity-tab__icon"></i>
				</c:if>
				<c:if test="${entry.type eq 'WORK_NOTE_CREATED'}">
					<i class="wm-icon-note activity-tab__icon"></i>
				</c:if>
				<c:if test="${entry.type eq 'WORK_QUESTION_ASKED'}">
					<i class="wm-icon-question-filled activity-tab__icon"></i>
				</c:if>
			</div>
			<div class="activity-tab__time">
				<small class="meta">
					<em>
						<c:choose>
							<c:when test="${not empty entry.onBehalfOfUser}">
								(Action Taken by <c:out value="${entry.onBehalfOfUser}"/> at <c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy h:mma', entry.timestamp, work.timeZone)}"/>)
							</c:when>
							<c:otherwise>
								(<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy h:mma', entry.timestamp, work.timeZone)}"/>)
							</c:otherwise>
						</c:choose>
					</em>
				</small>
			</div>
			<p class="activity-tab__description">
				<c:out escapeXml="false" value="${entry.text}" />
			</p>
		</div>
	</c:forEach>
</div>
