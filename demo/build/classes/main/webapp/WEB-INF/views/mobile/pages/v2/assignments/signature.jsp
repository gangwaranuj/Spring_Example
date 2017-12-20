<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.signature" scope="request"/>

<div class="wrap signature-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Signature" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole">
			<div class="unit whole" id="public-message">
				<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
			</div><%--unit whole--%>

			<div class="signature-line">
				<c:choose>
					<c:when test="${not empty work.company.customSignatureLine}">
						<div><c:out value="${work.company.customSignatureLine}"/></div>
					</c:when>
					<c:otherwise>
						<div>By signing below, you acknowledge the satisfactory completion of this assignment.
							<c:if test="${not empty work.activeResource.timeTrackingLog}">Additionally, you verify the accuracy of the check in & out times below.</c:if>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<%-- Show time tracking log --%>
			<c:if test="${not empty work.activeResource.timeTrackingLog}">
				<div class="check-in-log">
					<a href="javascript:void(0);" class="show active">Check In / Out Times
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
					</a>
					<div class="tell">
						<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-log.jsp"/>
					</div>
				</div>
			</c:if>
			<div class="signature-pad">
				<form class="add-signature-form" action="" method="post">
					<wm-csrf:csrfToken />
					<span for="work-resolution" class="summary-of-work">Summary of Work <span class="summary-of-work-subtext">(will be added to sign-off document)</span></span>
					<textarea id="work-resolution" class="work-resolution" name="workResolution"><c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/></textarea>
					<label for="signer-name">Signer's Name:</label>
					<input type="text" id="signer-name" name="signerName" class="signer-name" placeholder="Print Name Here" value="" />
					<input type="hidden" name="dataUrl" class="data-url" value="" />
				</form>
				<canvas id="signature-canvas"></canvas>
				<div class="clear">Clear</div>
				<div class="save spin">Save</div>
			</div>
		</div><%--unit--%>
	</div><%--grid--%>
</div><%--wrap--%>