<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="negotiation" value="${workResponse.viewingResource.pendingNegotiation}"/>
<c:set var="eligible" value="${eligibility.eligible}"/>
<c:set var="workType" value="${isWorkBundle ? 'bundle' : 'assignment'}"/>
<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<%--
Setup negotiation content.
negotiationLabel, negotiationStatus and negotiationDetail variables are set.
This makes the page rendering logic below much simpler.
--%>
<c:set var="isNegotiation" value="${not empty negotiation and (negotiation.approvalStatus.code eq 'pending' or negotiation.approvalStatus.code == 'declined' or negotiation.isExpired)}"/>
<c:if test="${isNegotiation}">
	<c:set var="negotiationLabel" value="label-important"/>
	<c:choose>
		<%-- DECLINED --%>
		<c:when test="${negotiation.approvalStatus.code eq 'declined'}">
			<c:set var="negotiationStatus" value="Declined"/>
			<c:set var="negotiationDetail">
				<p>
					You can still re-apply for this assignment with an alternative date or price.
					<br/><br/>
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this ${workType}</a>

				</p>

				<div class="toolbar">
					<c:if test="${not eligible}">
						<span class="help-block">
							Please complete the eligibility requirements.
						</span>
					</c:if>
				</div>
			</c:set>
		</c:when>
		<%-- EXPIRED --%>
		<c:when test="${negotiation.isExpired}">
			<c:set var="negotiationStatus" value="Expired"/>
			<c:set var="negotiationDetail">
				<p>
					<strong>Your application for this assignment has expired.</strong>
					You can re-apply for this assignment with an alternative date or price.
					<br/><br/>
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this ${workType}</a>

				</p>

				<div class="toolbar">
					<c:if test="${not eligible}">
						<span class="help-block">
							Please complete the eligibility requirements.
						</span>
					</c:if>
				</div>
			</c:set>
		</c:when>
		<%-- PENDING --%>
		<c:otherwise>
			<c:set var="negotiationStatus" value="Pending"/>
			<c:set var="negotiationLabel" value="label-warning"/>
			<c:set var="negotiationDetail">
				<p>
					You have applied for this  ${workResponse.workBundle ? "bundle" : "assignment"}. The client will review your application and, if you are
					selected, you will be notified via email
				</p>

				<c:if test="${negotiation.isPriceNegotiation}">
					<h6>Proposed Pricing</h6>
					<table>
						<tbody>
						<c:choose>
							<c:when test="${not empty negotiation.pricing.additionalExpenses}">
								<tr>
									<td>New Assignment budget</td>
									<td>
										<fmt:formatNumber value="${negotiation.pricing.maxSpendLimit - negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/>
									</td>
								</tr>
								<tr>
									<td>Additional expenses</td>
									<td>
										<fmt:formatNumber value="${negotiation.pricing.additionalExpenses}" currencySymbol="$" type="currency"/>
									</td>
								</tr>
								<tr>
									<td class="sum">Max Earnings Potential</td>
									<td class="sum">
										<fmt:formatNumber value="${negotiation.pricing.maxSpendLimit}" currencySymbol="$" type="currency"/>
									</td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<td>New Assignment budget</td>
									<td>
										<fmt:formatNumber value="${negotiation.pricing.maxSpendLimit}" currencySymbol="$" type="currency"/>
									</td>
								</tr>
							</c:otherwise>
						</c:choose>
						</tbody>
					</table>
				</c:if>

				<c:if test="${negotiation.isScheduleNegotiation}">
					<h6>Proposed Schedule</h6>
					<p><c:choose>
						<c:when test="${negotiation.schedule.range}">
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
							to <br/>
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.through, work.timeZone)}
						</c:when>
						<c:otherwise>
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
						</c:otherwise>
					</c:choose>
					</p>
				</c:if>

				<c:if test="${not empty negotiation.note}">
					<h6>Your Message</h6>
					<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
				</c:if>

				<c:if test="${negotiation.expiresOn != 0}">
					<h6>Application Expiry</h6>
					<p>
						Your application is valid until
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMM d, YYYY h:mma z', negotiation.expiresOn, work.timeZone)}"/>.
					</p>
				</c:if>
				<c:if test="${not workResponse.inWorkBundle}">
					<form action="/assignments/cancel_negotiation/${work.workNumber}" class="wm-action-container">
						<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
						<button type="submit" class="accept-negotiation button">Cancel Application</button>
					</form>
				</c:if>
				<div class="alert">
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this ${workType}</a>
				</div>
			</c:set>
		</c:otherwise>
	</c:choose>
</c:if>

<%--
This is what will actually show on the page
--%>
<c:choose>
	<c:when test="${isNegotiation}">
		<h4>Your application is <span class="label ${negotiationLabel}">${negotiationStatus}</span></h4>
			<c:if test="${!hidePricing}">
				<%-- Pricing --%>
				${negotiationDetail}
			</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${not workResponse.inWorkBundle}">
			<c:choose>
				<c:when test="${not eligible and not currentUser.dispatcher}">
					<p>Please complete the eligibility requirements below in order to be able to apply for the assignment.</p>
				</c:when>
				<c:otherwise>
					<h4>Apply For or Decline
						<span class="label label-success">Action Required</span>
					</h4>
					<p>
						<c:choose>
							<c:when test="${!hidePricing}">
								Please review the assignment details and apply or decline this request depending on your expertise and availability.
								To suggest an alternative date, please submit a counteroffer.
							</c:when>
							<c:otherwise>
								Your company's Team Agent must apply for or decline this assignment on your behalf.
							</c:otherwise>
						</c:choose>
						<div class="alert">
							<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this ${workType} </a>
						</div>
					</p>
				</c:otherwise>
			</c:choose>
		</c:if>
	</c:otherwise>
</c:choose>
