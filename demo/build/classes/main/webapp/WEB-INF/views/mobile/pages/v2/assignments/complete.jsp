<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.complete" scope="request"/>
<c:set var="pageScriptParams" value="${pricingJson}" scope="request"/>
<c:set var="deliverableInstructions" value="${work.deliverableRequirementGroupDTO.instructions}" scope="request" />
<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div class="wrap completion-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Complete Assignment" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>

		<div class="unit whole">
			<h3 class="work-title">${work.title}</h3>
			<span class="work-number">(ID:<c:out value="${work.workNumber}"/>)</span>

			<span class="small gray db mb">
				<c:out value="${work.company.name}"/>
				<c:if test="${not empty work.clientCompany}">
					(for <c:out value="${work.clientCompany.name}"/>)
				</c:if>
			</span>

			<form name="completeAssignment" action="/mobile/assignments/complete/${workNumber}" method="post">
				<wm-csrf:csrfToken />
				<div id="validation-errors">
					<c:if test="${not empty errors}">
						<div style="background: #f2dede; color: #b94a48">
							<div>
								<c:forEach items="${bundle.errors}" var="message" varStatus="status">
									<li><c:out value="${message}" escapeXml="false"/></li>
								</c:forEach><!-- SHOW ERRORS -->
							</div>
						</div>
					</c:if>
				</div>
				<div id="resolutions">
					<p class="required strong medium">
						Provide a summary of work performed or other relevant information.
					</p>
					<textarea name="resolution" id="resolution"><c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/></textarea>
				</div>
				<div id="additional-instructions">
					<c:if test="${not empty deliverableInstructions}">
						<h4>Additional Instructions</h4>
						<p><c:out value="${deliverableInstructions}"/></p>
					</c:if>
				</div>
				<c:choose>
					<c:when test="${isWorkerCompany}" >
						<c:if test="${!hidePricing}">
								<%-- Pricing --%>
								<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing_details.jsp"/>
						</c:if>
					</c:when>
					<c:otherwise>
						<%-- Pricing --%>
						<c:import url="/WEB-INF/views/mobile/partials/assignments/pricing_details.jsp"/>
					</c:otherwise>
				</c:choose>

				<div style="display: none;" class="error-msg" id="max-spend-limit-error-txt"></div>

				<div id="tax-calculation" <c:if test="${pricingType.equals('INTERNAL')}">style="display: none;"</c:if> >
					<c:import url="/WEB-INF/views/mobile/partials/assignments/tax_calculation.jsp"/>
				</div>
				<br>
				<input type="submit" id="submit-approval" value="Submit for Approval">
			</form>

			<c:if test="!${pricingType.equals('INTERNAL')}">
				<small> Note: You will be able to rate and review this client once you are paid.</small>
			</c:if>
		</div><%--unit--%>
	</div><%--grid--%>
</div><%--wrap--%>
