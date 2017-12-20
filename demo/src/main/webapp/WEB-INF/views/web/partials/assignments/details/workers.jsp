<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%--TODO: this whole file can be deleted and moved to handlebar templates--%>
<div class="assignment-workers--sort-container">
	<div class="limit-container">
		Show
		<select name="limit" class="assignment-workers--filter-number wm-select">
			<option value="10">10</option>
			<option value="25">25</option>
			<option value="50">50</option>
		</select>
	</div>
	Sort by
	<select name="sortColumn" class="wm-select assignment-workers--sort-type">
		<option value="NEGOTIATION_TOTAL_COST">Assignment Value</option>
		<option value="NEGOTIATION_SCHEDULE_FROM">Start Time</option>
		<option value="NEGOTIATION_CREATED_ON" selected="selected">Applied Time</option>
		<option value="AVG_RATING">Rating</option>
		<option value="DISTANCE" data-default-direction="ASC">Distance</option>
	</select>

	<select name="sortDirection" class="wm-select assignment-workers--sort-dir">
		<option value="ASC">&#9650;</option>
		<option value="DESC" selected="selected">&#9660;</option>
	</select>
</div>

<div class="assignment-workers--feed"></div>
<wm:pagination min="1" max="10" />
<c:choose>
	<c:when test="${(is_admin || isInternal) and not isInWorkBundle and (work.status.code eq workStatusTypes['DRAFT'] or work.status.code eq workStatusTypes['SENT'])}">
		<div class="wm-action-container">
			<c:if test="${work.pricing.type == 'INTERNAL' and is_deputy}">
				<button href="/assignments/assign/${work.workNumber}" class="button assign_action">Assign to Employee</button>
			</c:if>
			<c:if test="${work.status.code eq workStatusTypes['SENT']}">
				<a href="/assignments/resend_resource_invitation/${work.workNumber}" class="resend_invite button">Resend</a>
			</c:if>
			<c:if test="${work.status.code eq workStatusTypes['SENT']}">
				<c:choose>
					<c:when test="${!work.workNotifyAvailable}">
						<wm:button tooltip="None of the workers have opted to receive notifications." disabled="true">Work Notify</wm:button>
					</c:when>
					<c:when test="${!work.workNotifyAllowed}">
						<wm:button tooltip="Notifying workers is limited to once per hour." disabled="true">Work Notify</wm:button>
					</c:when>
					<c:otherwise>
						<form action="/assignments/workNotify/${work.workNumber}" method="POST" class="dib">
							<wm-csrf:csrfToken />
							<button class="button" type="submit">Work Notify<sup>&trade;</sup></button>
						</form>
					</c:otherwise>
				</c:choose>
			</c:if>
			<a href="/assignments/contact/${work.workNumber}" class="button js-workers-invite-more-workers">Invite More Workers</a>
		</div>
	</c:when>

	<c:when test="${is_dispatcher and not isInWorkBundle and (work.status.code eq workStatusTypes['SENT'] or work.status.code eq workStatusTypes['DECLINED'])}">
		<div class="wm-action-container">
			<form action="/assignments/vendor/reject/${work.workNumber}" method="POST" class="dib">
				<wm-csrf:csrfToken />
				<button class="button" type="submit">Decline</button>
			</form>
			<a href="/assignments/contact/${work.workNumber}" class="button -add">Add Candidates</a>
		</div>
	</c:when>
</c:choose>

<c:if test="${isInWorkBundle and (work.status.code eq workStatusTypes['DRAFT'] or work.status.code eq workStatusTypes['SENT'] or work.status.code eq workStatusTypes['DECLINED'])}">
	<div class="form-actions">
		This assignment is part of a bundle called: "${workResponse.workBundleParent.title}".
		<c:choose>
			<c:when test="${is_dispatcher}">
				You can click <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">here</a> to see this bundle.
			</c:when>
			<c:otherwise>
				You can click <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">here</a> to invite more workers to this bundle.
			</c:otherwise>
		</c:choose>
	</div>
</c:if>
