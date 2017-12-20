<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<dl class="iconed-dl">
	<dt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/location_v2.jsp"/></dt>
	<dd>
		<c:if test="${not empty work.location.name}">
			<c:if test="${not is_resource or workResponse.work.status.code ne workStatusTypes['SENT']}">
				<Strong><c:out value="${work.location.name}" escapeXml="false"/></strong>
				<c:if test="${not empty work.location.number}">
					<small>(ID: <c:out value="${work.location.number}" />)</small>
				</c:if>
				<c:if test="${not empty work.offsiteLocation && not empty work.activeResource}">
					<small>(<fmt:formatNumber value="${work.activeResource.distanceToAssignment}" maxFractionDigits="1"/> mi)
						<a href="javascript:void(0);" class="tooltipped tooltipped-n" aria-label="Note: Calculated distance and driving distance may not match"><i class="wm-icon-question-filled"></i></a></small>
				</c:if>
				<br/>
			</c:if>
		</c:if>

		<c:if test="${not empty work.location.address}">
			<c:choose>
				<c:when test="${is_resource}">
					<c:choose>
						<c:when test="${work.status.code == workStatusTypes['SENT']}">
							<strong><c:out value="${wmfmt:upcaseFirstLetter(wmfmt:formatAddressShort(work.location.address))}" /></strong>
							<c:if test="${not empty workResponse.viewingResource.user.profile.address}">
								(<fmt:formatNumber value="${workResponse.viewingResource.distanceToAssignment}" maxFractionDigits="1"/> mi)
							</c:if>
							<br/>
						</c:when>
						<c:otherwise>
							<c:out escapeXml="false" value="${wmfmt:upcaseFirstLetter(wmfmt:formatAddressLong(work.location.address))}" /><br/>
							<small>
								<br/>
								<strong>Location Type:</strong>
								<c:out value="${wmfmt:upcaseFirstLetter(wmfmt:formatLocationType(work.location.address.locationType))}" />
							</small>
							<c:if test="${not empty work.location.instructions}">
								<small>
									<br/>
									<strong>Travel Instructions:</strong>
									<c:out value="${work.location.instructions}" />
								</small>
							</c:if>
							<c:if test="${not empty work.activeResource}">
								<c:url var="drivingUri" value="http://maps.google.com/maps">
									<c:param name="saddr" value="${wmfmt:formatAddressShort(work.activeResource.user.profile.address)}" />
									<c:param name="daddr" value="${wmfmt:formatAddress(work.location.address, ', ', true)}" />
								</c:url>
								<p><small><a href="<c:out value="${drivingUri}" />" target="_blank">View driving directions</a></small></p>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:out escapeXml="false" value="${wmfmt:formatAddressLong(work.location.address)}" />
					<small>
						<br/>
						<br/>
						<strong>Location Type:</strong>
						<c:out value="${wmfmt:upcaseFirstLetter(wmfmt:formatLocationType(work.location.address.locationType))}" />
					</small>
					<c:if test="${not empty work.location.instructions}">
						<small>
							<br/>
							<strong>Travel Instructions:</strong>
							<c:out value="${work.location.instructions}" />
						</small>
					</c:if>
					<c:if test="${not empty work.activeResource}">
						<c:url var="drivingUri" value="http://maps.google.com/maps">
							<c:param name="saddr" value="${wmfmt:formatAddressShort(work.activeResource.user.profile.address)}" />
							<c:param name="daddr" value="${wmfmt:formatAddress(work.location.address, ', ', true)}" />
						</c:url>
						<small><a href="<c:out value="${drivingUri}" />" target="_blank" class="clear">View worker travel info</a></small>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
	</dd>
</dl>
