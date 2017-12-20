<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="internalPricing" value="${work.pricing.id == pricingStrategyTypes['INTERNAL']}" />

<div class="wm-action-container" id="buyer_complete_toggle">
	<c:choose>
		<c:when test="${not empty work.activeResource && not isPendingNegotiation}">
			<a class="button buyer-complete-toggle">Complete for Worker</a>
		</c:when>
		<c:otherwise>
			<a class="button tooltipped tooltipped-n disabled" disabled aria-label="Why is this disabled? To complete this assignment, you must first approve or decline the outstanding Spend Limit Increase request.">
				Complete For Worker <i class="icon-double-angle-right"></i>
			</a>
			<div class="help-inline" style="color:red">To complete this assignment, you must first approve or decline the pending negotiations.</div>
		</c:otherwise>
	</c:choose>
</div>

<div id="buyer_complete" class="dn">

	<div class="media completion">
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/cash-icon.jsp"/>
		</div>
		<div class="media-body">
			<h4>Complete Assignment and Approve<c:if test="${not internalPricing}"> for Payment</c:if></h4>

			<form:form id="complete_and_approve_assignment_form" cssClass="completion-well">
				<input type="hidden" name="workNumber" value="${work.workNumber}" />
				<p class="ml">Worker will be notified that the assignment has been closed out on their behalf</p>

				<div>
					<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_complete.jsp" />
				</div>

				<h4>Closeout Notes <span class="label label-important">Required</span></h4>
				<p class="mr">Provide a summary of work performed or other relevant information.</p>
				<p>
					<textarea name="resolution" id="resolution" rows="5" class='input-block-level'><c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/></textarea>
				</p>

				<div class="feedback-well form-stacked">
					<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_feedback.jsp" />
				</div>

				<hr/>

				<p>
					<small>By approving this assignment, you are reaffirming the <a href="/tos">Terms of Use Agreement</a> and
						you agree that you are 100% satisfied with the work performed. Funds will be released to the worker
						according to the payment terms of the assignment and are non-refundable. If you are not satisfied, click
						"I'm Not Satisfied" to send the assignment back to the worker.
					</small>
				</p>

				<div id="onbehalf_messages" class="messages"></div>

				<div class="wm-action-container">
					<c:choose>
						<c:when test="${not empty work.activeResource && not isPendingNegotiation}">
							<c:choose>
								<c:when test="${can_pay}">
									<a type="submit" class="button on-behalf-complete-save">Complete and Approve</a>
								</c:when>
								<c:otherwise>
									<a class="button tooltipped tooltipped-n disabled" disabled aria-title="Why is this disabled? You are not authorized to approve and pay for assignments."> Complete and Approve <i class="icon-double-angle-right"></i></a>
									<div class="alert alert-danger">You are not authorized to approve or decline this request. Please contact your manager or account administrator to request this custom permission.</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<a class="button tooltipped tooltipped-n disabled" disabled aria-label="Why is this disabled? To approve this assignment, you must first approve or decline the outstanding Spend Limit Increase request">Complete and Approve <i class="icon-double-angle-right"></i></a>
							<div class="help-inline" style="color:red">To complete and approve this assignment, you must first approve or decline the outstanding Spend Limit Increase request</div>
						</c:otherwise>
					</c:choose>
				</div>
			</form:form>
		</div>

	</div>

</div>
