 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="callout">

	<c:if test="${is_active_resource and work.status.code eq workStatusTypes['EXCEPTION']}">
		<c:forEach items="${work.subStatuses}" var="s">
			<c:if test="${s.code eq workSubStatusTypes['INCOMPLETE_WORK']}">
				<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/resource_resolve_status.jsp">
					<c:param name="status" value="${s}"/>
				</c:import>
			</c:if>
		</c:forEach>
	</c:if>

	<c:if test="${not empty work.activeResource and work.activeResource.budgetNegotiationPending}">
		<c:set scope="request" var="negotiation" value="${work.activeResource.budgetNegotiation}"/>
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation_price.jsp"/>
		<c:remove var="negotiation" scope="request"/>
	</c:if>

	<c:if test="${not empty work.activeResource and work.activeResource.expenseNegotiationPending}">
	<c:set scope="request" var="negotiation" value="${work.activeResource.expenseNegotiation}"/>
	<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation_expense.jsp"/>
	<c:remove var="negotiation" scope="request"/>
	</c:if>

	<c:if test="${not empty work.activeResource and work.activeResource.bonusNegotiationPending}">
		<c:set scope="request" var="negotiation" value="${work.activeResource.bonusNegotiation}"/>
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation_bonus.jsp"/>
		<c:remove var="negotiation" scope="request"/>
	</c:if>

	<c:if test="${not empty work.activeResource and work.activeResource.rescheduleNegotiationPending}">
		<c:set scope="request" var="negotiation" value="${work.activeResource.rescheduleNegotiation}"/>
		<c:set scope="request" var="isRequestor" value="${is_active_resource}"/>
		<c:set scope="request" var="isApprover" value="${is_admin}"/>
		<c:set scope="request" var="isDeputy" value="${is_deputy}"/>
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation_schedule.jsp"/>
		<c:remove scope="request" var="negotiation"/>
		<c:remove scope="request" var="isRequestor"/>
		<c:remove scope="request" var="isApprover"/>
		<c:remove scope="request" var="isDeputy"/>
	</c:if>

	<c:if test="${not empty workResponse.buyerRescheduleNegotiation}">
		<c:set scope="request" var="negotiation" value="${workResponse.buyerRescheduleNegotiation}"/>
		<c:set scope="request" var="isRequestor" value="${is_admin && !is_active_resource}"/>
		<c:set scope="request" var="isApprover" value="${is_active_resource}"/>
		<c:set scope="request" var="isDeputy" value="${is_deputy}"/>
		<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation_schedule.jsp"/>
		<c:remove scope="request" var="negotiation"/>
		<c:remove scope="request" var="isRequestor"/>
		<c:remove scope="request" var="isApprover"/>
		<c:remove scope="request" var="isDeputy"/>
	</c:if>

	<c:if test="${(is_admin and not is_resource) and work.status.code eq workStatusTypes['SENT'] and work.configuration.assignToFirstResource}">
		<c:forEach items="${work.pendingNegotiations}" var="n">
			<c:if test="${not (n.approvalStatus.code ne 'pending' or n.isExpired)}">

				<c:set var="negotiation" value="${n}" scope="request"/>
				<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/negotiation.jsp"/>
				<c:remove var="negotiation" scope="request"/>

			</c:if>
		</c:forEach>
	</c:if>

	<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/decline_negotiation.jsp"/>


</div>
