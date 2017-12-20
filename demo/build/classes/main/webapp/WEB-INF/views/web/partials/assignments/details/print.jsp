<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="showPrintAlert" value="${work.configuration.enableAssignmentPrintout && work.status.code == workStatusTypes['ACTIVE']}" />

<c:if test="${showPrintAlert}">
	<div class="media completion">
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/print-icon.jsp"/>
		</div>
		<div class="media-body">
			<h4>Print
				<small class="meta">
				<c:choose>
					<c:when test="${is_active_resource}">
						<a href="/assignments/generate_pdf/${work.workNumber}" class="alert-message-btn">download and print your assignment</a> before starting work
						<span class="tooltipped tooltipped-n" aria-label="Your printout includes your assignment details, contact information, and IVR contact info. Use the printout to obtain end customer signatures and any other assignment notes.">
							<i class="wm-icon-question-filled"></i>
						</span>
					</c:when>
					<c:otherwise>
						<a href="/assignments/generate_pdf/${work.workNumber}" class="alert-message-btn">Generate the assignment printout</a>
					</c:otherwise>
				</c:choose>
				</small>
			</h4>

		</div>
	</div>
</c:if>
