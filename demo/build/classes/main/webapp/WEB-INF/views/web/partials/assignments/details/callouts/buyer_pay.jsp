<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div class="media completion">
	<div class="completion-icon">
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/cash-icon.jsp"/>
	</div>
	<div class="media-body">
		<h4>
			Approve Work <span class="label label-success">Action Required</span>
		</h4>

	<form action="/assignments/pay/${work.workNumber}" method="post" enctype="multipart/form-data" id="approve_assignment_form">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="${work.workNumber}" />

		<p>This assignment is marked complete and ready for approval and payment processing (if applicable).</p>

		<div>
			<c:choose>
				<c:when test="${isWorkerCompany}" >
					<c:if test="${!hidePricing}">
						<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_close.jsp"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_close.jsp"/>
				</c:otherwise>
			</c:choose>
		</div>


		<h6>Resolution</h6>
		<blockquote class="wordwrap"><em><c:out value="${work.resolution}"/></em></blockquote>

		<c:set var="hasMultipleApprovals" value="false" />
		<sec:authorize access="hasFeature('MultipleApprovals')">
			<c:set var="hasMultipleApprovals" value="true" />
		</sec:authorize>

		<div class="feedback-well form-stacked">
			<c:choose>
				<c:when test="${hasMultipleApprovals}">
					<c:if test="${numRemainingDecisions == 1}">
						<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_feedback.jsp" />
					</c:if>
				</c:when>
				<c:otherwise>
					<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_feedback.jsp" />
				</c:otherwise>

			</c:choose>
		</div>

		<hr/>

		<p>
			<small>
				By approving this assignment, you are reaffirming the <a href="/tos">Terms of Use Agreement</a> and
				you agree that you are 100% satisfied with the work performed. Funds will be released to the worker
				according to the payment terms of the assignment and are non-refundable. If you are not satisfied, click
				"I'm Not Satisfied" to send the assignment back to the worker.
			</small>
		</p>

		<sec:authorize access="(!(principal.approveWorkCustomAuth || hasAnyRole('PERMISSION_APPROVEWORK'))) || principal.isMasquerading()">
			<c:set var="disable" value="disabled" />
		</sec:authorize>


		<div class="wm-action-container">
			<c:choose>
				<c:when test="${not empty work.activeResource && not isPendingNegotiation}">
					<a class="button sendback_action" href="<c:url value="/assignments/sendback/${work.workNumber}"/>">I'm Not Satisfied</a>
					<c:choose>
						<c:when test="${can_pay}">
							<c:choose>
								<c:when test="${hasMultipleApprovals}">
									<c:choose>
										<c:when test="${decisionResult eq 'DECISION_OPEN'}">
											<button
													type="submit"
													class="buyer-approve button <c:if test="${(disable == 'disabled')}">tooltipped tooltipped-n disabled</c:if>"
													<c:if test="${disable == 'disabled'}">${disable} aria-label="You are not authorized to approve assignments."</c:if>>
												Approve Work
											</button>
											<input type="hidden" name="flowUuid" value="${flowUuid}" />
											<input type="hidden" name="decisionUuid" value="${decisionUuid}" />
											<input type="hidden" name="deciderUuid" value="${deciderUuid}" />
										</c:when>
										<c:otherwise>
											<button
													class="button tooltipped tooltipped-n disabled"
													disabled aria-label="This assignment is pending someone else's approval.">
												Approve
											</button>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${work.configuration.paymentTermsDays > 0 || work.pricing.id == pricingStrategyTypes['INTERNAL'] || mboEnabled }">
									<button
                                            type="submit"
                                            class="buyer-approve button <c:if test="${(disable == 'disabled')}">tooltipped tooltipped-n disabled</c:if>"
                                            <c:if test="${disable == 'disabled'}">${disable} aria-label="You are not authorized to approve assignments."</c:if>>
                                        Approve Work
                                    </button>
								</c:when>
								<c:otherwise>
									<button
                                            type="submit"
                                            class="buyer-approve button <c:if test="${(disable == 'disabled')}">tooltipped tooltipped-n disabled</c:if>"
                                            <c:if test="${disable == 'disabled'}">${disable} aria-label="You are not authorized to approve and pay for assignments."</c:if>>
                                        Approve and Pay Now
                                    </button>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<button
                                    class="buyer-approve button <c:if test="${(disable == 'disabled')}">tooltipped tooltipped-n disabled</c:if>"
                                    <c:if test="${(disable == 'disabled')}">${disable} aria-label="You are not authorized to approve assignments."</c:if>>
                                Approve and Pay
                            </button>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<a class="button sendback_action button" href="<c:url value="/assignments/sendback/${work.workNumber}"/>">I'm Not Satisfied</a>
					<c:choose>
						<c:when test="${work.configuration.paymentTermsDays > 0}">
							<button class="button tooltipped tooltipped-n disabled" disabled aria-label="To approve this assignment, you must first approve or decline the outstanding Spend Limit Increase request.">Approve</button>
						</c:when>
						<c:otherwise>
							<button class="button tooltipped tooltipped-n disabled" disabled aria-label="To approve this assignment, you must first approve or decline the outstanding Spend Limit Increase request">Approve and Pay <i class="icon-double-angle-right"></i></button>
						</c:otherwise>
					</c:choose>
					<div class="help-inline" style="color:red;">To approve this assignment, you must first approve or decline the pending negotiations</div>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${disable == 'disabled'}">
			<div class="alert alert-danger">You are not authorized to approve this request. Please contact your manager or account administrator to approve.</div>
		</c:if>
	</form>
	</div>
</div>
