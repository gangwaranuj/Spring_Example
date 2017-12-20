<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div class="well-b2">
	<div class="well-content">
		<h4>Reschedule Requested
			<span class="label label-${isRequestor ? "warning" : "success"}">
				${isRequestor ? "Pending Approval" : "Action Required"}
			</span>
		</h4>

		<dl class="key-value">
			<dt>Original time</dt>
			<dd>
				<c:choose>
					<c:when test="${work.schedule.range}">
						${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', work.schedule.from, work.timeZone)}
						to ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', work.schedule.through, work.timeZone)}<br/>
					</c:when>
					<c:otherwise>
						${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', work.schedule.from, work.timeZone)}
					</c:otherwise>
				</c:choose>
			</dd>
			<dt>New time</dt>
			<dd>
				<c:choose>
					<c:when test="${negotiation.schedule.range}">
						${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', negotiation.schedule.from, work.timeZone)}
						to ${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', negotiation.schedule.through, work.timeZone)}
					</c:when>
					<c:otherwise>
						${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a', negotiation.schedule.from, work.timeZone)}
					</c:otherwise>
				</c:choose>
			</dd>
		</dl>

		<c:if test="${not empty negotiation.note}">
			<p><strong>Note from <c:out value="${negotiation.note.creator.name.firstName}" /> <c:out value="${negotiation.note.creator.name.lastName}" /></strong></p>
			<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
		</c:if>

		<c:choose>
			<c:when test="${isRequestor}">
			<div class="wm-action-container">
				<c:if test="${isDeputy && !negotiation.initiatedByResource}">
					<form action='/assignments/accept_negotiation/${work.workNumber}' class="pull-right ml">
						<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
						<input type="hidden" name="onBehalfOf" value="${work.activeResource.user.userNumber}"/>
						<button type="submit" class="accept-negotiation button -small">Accept on Behalf</button>
					</form>
				</c:if>
				<form action="/assignments/cancel_negotiation/${work.workNumber}">
					<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
					<button type="submit"  class="decline-negotiation button -small">Cancel Offer</button>
				</form>
			</div>
			</c:when>

			<c:when test="${isApprover}">
				<div class="alert-actions clear">
					<form action='/assignments/accept_negotiation/${work.workNumber}' class="wm-action-container">
						<input type="hidden" name='id' value="${negotiation.encryptedId}"/>
						<a rel="prompt_decline_negotiation" data-negotiation-id="${negotiation.encryptedId}" class="decline-negotiation button -small">Decline</a>
						<button type="submit" class="accept-negotiation button -small">Approve</button>
					</form>
				</div>
			</c:when>
		</c:choose>
	</div>
</div>
