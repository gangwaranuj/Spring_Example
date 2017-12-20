<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Time & Place --%>
<c:if test="${(isAdmin or isResource or isWmEmployee)}">
	<div id="time-place">
		<%-- Scheduling --%>
		<c:set var="timeZone" value="${work.timeZone}" />
		<c:choose>
			<c:when test="${not empty work.schedule and not empty work.schedule.from}">
				<c:choose>
					<c:when test="${work.schedule.range}">
						<p class="range-time">
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clock.jsp"/>
						<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma', work.schedule.from, timeZone)}"/> to   <br/>
						<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma z', work.schedule.through, timeZone)}"/></p>
					</c:when>
					<c:otherwise>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clock.jsp"/>
						<p><c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma', work.schedule.from, timeZone)}" /></p>
					</c:otherwise>
				</c:choose>
			</c:when>

			<c:otherwise>
				<p><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clock.jsp"/> No scheduling information</p>
			</c:otherwise>
		</c:choose>

		<c:if test="${not empty work.location.address}">
			<c:choose>
				<c:when test="${work.offsiteLocation}">
					<p>This job is <strong>virtual / off-site</strong>.</p>
				</c:when>
				<c:otherwise>
					<c:set var="address" value="${(isAdmin or isActiveResource) ?
					fn:replace(wmfmt:formatAddressLong(work.location.address), '<br/>', ',') :
					fn:replace(wmfmt:formatAddressShort(work.location.address), '<br/>', ',')}" />

					<c:if test="${not empty work.location.name}">
						<c:if test="${not isResource or work.status.code ne WorkStatusType.SENT}">
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-buildings.jsp"/>
							<p class="location-name"><c:out escapeXml="false" value="${work.location.name}"/></p><br/>
						</c:if>
					</c:if>

					<c:if test="${not empty work.location.number}">
						<c:if test="${not isResource}">
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-asterisk.jsp"/>
							<p class="location-name">Location ID: <c:out value="${work.location.number}"/></p><br/>
						</c:if>
					</c:if>

					<c:choose>
						<c:when test="${not (isActiveResource or isAdmin)}">
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-buildings.jsp"/><p class="location-name"><c:out escapeXml="false" value="${wmfmt:formatAddressShort(work.location.address)}"/></p>
						</c:when>
						<c:otherwise>
							<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-buildings.jsp"/><p class="location-name"><c:out escapeXml="false" value="${wmfmt:formatAddressLong(work.location.address)}"/></p>
						</c:otherwise>
					</c:choose>

					<div id="location-map-container">
						<c:set var="showMarker" value="${work.status.code eq WorkStatusType.SENT ? 'false' : 'true'}" />
						<a href="<c:out value="${wmfn:getMapUrlFromAddress(address)}" escapeXml="false" />">
							<div id="location-map" style="background: url('<c:out value="${wmfn:getStaticMapImageUrlFromAddress(640, 400, address, showMarker, 9, 1)}" escapeXml="false" />') no-repeat center;">
								<c:if test="${not showMarker}"><div id="map-circle"></div></c:if>
									<span id="go-to-map"><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-go-to-map.jsp"/></span>
							</div><%--location map--%>
						</a>
					</div><%--location-map-container--%>
				</c:otherwise>
			</c:choose>
		</c:if>
	</div><%--time and place--%>
</c:if>