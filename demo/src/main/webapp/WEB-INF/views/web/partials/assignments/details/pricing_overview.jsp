<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:if test="${work.pricing.id != pricingStrategyTypes['INTERNAL'] && !(is_in_work_company && is_invited_resource)}">
<dl class="iconed-dl">
	<dt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/cash_v2.jsp"/></dt>
	<dd>
		<c:choose>
			<c:when test="${work.status.code == workStatusTypes['INVOICED'] ||
							work.status.code == workStatusTypes['CANCELLED_WITH_PAY'] ||
							work.status.code == workStatusTypes['CANCELLED_PAYMENT_PENDING'] ||
							work.status.code == workStatusTypes['PAYMENT_PENDING'] ||
							work.status.code == workStatusTypes['PAID']}">
				<c:if test="${(isWorkerCompany && (!hidePricing || isAdminOrManagerOrDispatcher)) || (!isWorkerCompany && !isEmployeeWorker)}">
					<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_close.jsp"/>
				</c:if>
				<div class="promotion -main -intuit">
					<p><span class="third-party-logo -inline -square -intuit-qb"></span> Track your money and calculate how much you're really making with QuickBooks Self-Employed
						<a href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=assignment&cid=IPD_workmarket_assignment_QBSE&utm_email=${email}" target="_blank">Start for free</a>
					</p>
				</div>
			</c:when>
			<c:otherwise>

				<c:if test="${(isWorkerCompany && (!hidePricing || isAdminOrManagerOrDispatcher)) || (!isWorkerCompany && !isEmployeeWorker)}">
					<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing.jsp"/>
				</c:if>

				<c:choose>
					<c:when test="${work.configuration.paymentTermsDays > 0}">
						<span class="payment-terms">Paid <c:out value="${work.configuration.paymentTermsDays}"/> days</span> after approval
						<c:if test="${is_admin && isAutoPayEnabled}">
							<small class="meta"><span class="label label-success">Auto Pay</span></small>
						</c:if>
					</c:when>
					<c:otherwise>
						<span class="payment-terms">Paid Immediately</span> after approval
					</c:otherwise>
				</c:choose>
				<c:if test="${not is_admin}">
					<br/><small class="meta">No fees will be deducted from the above price.</small>
				</c:if>
			</c:otherwise>
		</c:choose>
		<c:if test="${work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'] ||
						work.status.code == workStatusTypes['ACCEPTED'] || work.status.code == workStatusTypes['ACTIVE'] && (is_admin || isInternal)}">
		    <c:if test="${work.configuration.disablePriceNegotiation}">
		         <small class="meta">Price Negotiation: Disabled</small>
		   </c:if>
		</c:if>

		<c:if test="${is_admin and work.status.code eq workStatusTypes['PAYMENT_PENDING'] and not work.pendingPaymentFulfillment}">
			<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/paynow.jsp"/>
		</c:if>
	</dd>

</dl>
</c:if>
