<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty work.activeResource}">
	<div class="row-resource">
		<div class="resource-info">
			<h4>
				<a href="<c:url value="/profile/${work.activeResource.user.userNumber}"/>" class="profile_link tooltipped tooltipped-n" aria-label="Assigned Worker" id="assigned-worker">
						<c:out value="${wmfmt:toPrettyName(resourceFullName)}" />
				</a>
					<c:if test="${not work.offsiteLocation}">
						<small>(<fmt:formatNumber value="${work.activeResource.distanceToAssignment}" maxFractionDigits="1"/> mi)</small>
					</c:if>
			</h4>
			<c:if test="${work.activeResource.user.company.name != resourceFullName}" >
				<c:out value="${work.activeResource.user.company.name}" /> <br/>
			</c:if>
			W: <c:out value="${resource_work_phone}"/><br/>
			<c:if test="${not empty resource_mobile_phone}">
				M: <c:out value="${resource_mobile_phone}"/><br/>
			</c:if>
			<a href="mailto:<c:out value="${work.activeResource.user.email}"/>"><c:out value="${work.activeResource.user.email}"/></a>
			<c:if test="${not empty work.partGroup}">
				<br /><a href="javascript:void(0);" id="toggle_active_resource_address">View Address</a>
			</c:if>
			<c:if test="${not empty work.partGroup}">
				<p id="active_resource_address" class="dn">
					<c:choose>
						<c:when test="${empty work.activeResource.user.profile.address}">
							Address not available. Please contact Worker.
						</c:when>
						<c:otherwise>
							<c:out escapeXml="false" value="${wmfmt:formatAddressLong(work.activeResource.user.profile.address)}" />
						</c:otherwise>
					</c:choose>
				</p>
			</c:if>
		</div>
		<div class="worker-avatar">
			<c:choose>
				<c:when test="${not empty work.activeResource.user.avatarSmall}">
					<wm:avatar src="${wmfn:stripUriProtocol(wmfmt:stripXSS(work.activeResource.user.avatarSmall.uri))}" />
				</c:when>
				<c:otherwise>
					<wm:avatar hash="${work.activeResource.user.userNumber}" />
				</c:otherwise>
			</c:choose>
			<span>#<c:out value="${work.activeResource.user.userNumber}"/></span>
			<c:if test="${empty rating and is_admin and work.status.code == workStatusTypes['ACTIVE']}">
				<small><a class="rating-sidebar" href="/assignments/create_update_rating/${work.workNumber}">Leave Rating</a></small>
			</c:if>
			<c:if test="${not empty rating and is_admin and work.status.code == workStatusTypes['ACTIVE']}">
				<small><a class="rating-sidebar" href="/assignments/create_update_rating/${work.workNumber}">View Rating</a></small>
			</c:if>
		</div>
	</div>
	<div class="clearfix"></div>
</c:if>
