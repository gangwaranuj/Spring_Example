<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<div class="deliverables-overview tell">
<c:choose>
	<c:when test="${not empty work.deliverableRequirementGroupDTO}">
		<%-- Show instructions, if any --%>
		<c:if test="${not empty work.deliverableRequirementGroupDTO.instructions}">
			<div class="deliverables-instructions">
				<c:out escapeXml="false" value="${work.deliverableRequirementGroupDTO.instructions}"/>
			</div>
		</c:if>
		<%-- Show specific requirements, if any --%>
		<c:if test="${not empty work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
			<div class="deliverable-requirements">
				<c:forEach var="deliverableRequirementDTO" items="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
					<div class="deliverable-requirement">
						<div class="-${deliverableRequirementDTO.type}-instance deliverable-requirement-header">
							<jsp:include page="/WEB-INF/views/web/partials/svg-icons/deliverables/liveicon-deliverables.jsp"/>
							<div class="deliverable-requirement-type"><c:out value="${wmfn:translateDeliverableTypeToName(deliverableRequirementDTO.type)}"/></div>
							<div class="deliverable-requirement-count"><c:out value="${deliverableRequirementDTO.numberOfFiles}"/></div>
						</div>
						<div class="deliverable-requirement-instructions">
							<c:out escapeXml="false" value="${deliverableRequirementDTO.instructions}"/>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:if>
		<%-- Show deadline, if any --%>
		<c:if test="${not empty work.deliverableRequirementGroupDTO.hoursToComplete}">
			<div class="deliverables-deadline">
				Deliverables are due within <span class="hours-to-complete">${work.deliverableRequirementGroupDTO.hoursToComplete} hours</span>  of assignment ${work.schedule.range ? "end" : "start"} time.
			</div>
		</c:if>
	</c:when>
	<c:otherwise>
		<p>No requirements set.</p>
	</c:otherwise>
</c:choose>
</div>