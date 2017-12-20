<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="accept-terms">
	<c:if test="${work.configuration.standardTermsFlag or work.configuration.standardInstructionsFlag}">
		<c:if test="${work.configuration.standardTermsFlag}">
			<a href="javascript:void(0)" class="show after-details-link">
				Terms of Agreement
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
			</a>
			<div class="tell"><c:out value="${wmfmt:tidy(wmfmt:nl2br(work.configuration.standardTerms))}" escapeXml="false" /></div>
		</c:if>

		<c:if test="${work.configuration.standardInstructionsFlag}">
			<a href="javascript:void(0)" class="show after-details-link">
				Code of Conduct
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
			</a>
			<div class="tell"><c:out value="${wmfmt:tidy(wmfmt:nl2br(work.configuration.standardInstructions))}" escapeXml="false" /></div>
		</c:if>
	</c:if>

	<div>
		<c:choose>
			<c:when test="${not eligibility.eligible}">
				<h4>Important Eligibility Notice:</h4>
				<p class="notice">
					This assignment has eligibility requirements that you do not currently
					meet. Please view it on the full website for details. Once these
					requirements are met, you will be able to
						${not work.configuration.assignToFirstResource ? 'accept' : 'apply for'}
					this assignment.
				</p>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${work.configuration.paymentTermsDays > 0 and work.configuration.standardTermsFlag}">
						<p class="notice">
							<c:if test="${work.configuration.paymentTermsDays > 0}">
								I understand that <strong class="strong">I will be paid <c:out value="${work.configuration.paymentTermsDays}"/> days after successful completion and approval of the work</strong>
							</c:if>

							<c:if test="${work.configuration.paymentTermsDays > 0 and work.configuration.standardTermsFlag}">
								AND
							</c:if>

							<c:if test="${work.configuration.standardTermsFlag}">
								I agree with the client terms described above.
							</c:if>
						</p>
					</c:when>
					<c:otherwise>
						<p class="notice">Only accept this assignment if you are available and qualified. Counteroffer to suggest an alternate
							<c:choose>
								<c:when test="${is_employee}">
									day or time.
								</c:when>
								<c:otherwise>
									day, time, or price.
								</c:otherwise>
							</c:choose>
						</p>
					</c:otherwise>
				</c:choose>

				<c:if test="${workResponse.viewingResource.user.laneType.value == 1}">
					<p class="notice">
						You received this assignment because you are on W2 payroll from the company that sent it to you. If you are not a W2 employee, then accepting this work is in violation of the Terms of Use Agreement and your account will be suspended immediately.
					</p>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div>
</div><%--grid--%>
