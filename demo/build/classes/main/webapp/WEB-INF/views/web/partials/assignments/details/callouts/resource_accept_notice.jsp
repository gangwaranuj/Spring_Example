<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="negotiation" value="${workResponse.viewingResource.pendingNegotiation}"/>
<c:set var="eligible" value="${eligibility.eligible}"/>

<c:choose>
	<c:when test="${not empty negotiation and (negotiation.approvalStatus.code eq 'pending' or negotiation.approvalStatus.code == 'declined' or negotiation.isExpired)}">
		<h4>
			Your counteroffer is
			<c:choose>
				<c:when test="${negotiation.approvalStatus.code eq 'declined'}">
					<span class="label label-important">Declined</span>
				</c:when>
				<c:when test="${negotiation.isExpired}">
					<span class="label label-important">Expired</span>
				</c:when>
				<c:otherwise>
					<span class="label label-warning ">Pending</span>
				</c:otherwise>
			</c:choose>
		</h4>
		<c:choose>
			<c:when test="${negotiation.approvalStatus.code eq 'declined'}">
				<p>
					You still have the opportunity to accept the assignment as is, or make another counteroffer.
					<br/><br/>
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this assignment</a>
				</p>
				<div class="toolbar">
					<c:if test="${not eligible}">
						<span class="help-block">
							Please complete the eligibility requirements.
						</span>
					</c:if>
				</div>
			</c:when>
			<c:when test="${negotiation.isExpired}">
				<p>
					You still have the opportunity to accept the assignment, extend your counteroffer, or make another counteroffer.
					<br/><br/>
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this assignment</a>
				</p>

				<div class="toobar">
					<c:if test="${not eligible}">
						<span class="help-block">
							Please complete the eligibility requirements.
						</span>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<p>If the client accepts your counteroffer, the assignment will be assigned to you automatically.</p>

				<c:if test="${negotiation.isPriceNegotiation}">
					<h6>Pricing Offer</h6>
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
					<h6>Schedule Offer</h6>
					<c:choose>
						<c:when test="${negotiation.schedule.range}">
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
							to <br/>
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.through, work.timeZone)}
						</c:when>
						<c:otherwise>
							${wmfmt:formatMillisWithTimeZone('E, MM/d/yyyy h:mm a z', negotiation.schedule.from, work.timeZone)}
						</c:otherwise>
					</c:choose>
				</c:if>

				<c:if test="${not empty negotiation.note}">
					<h6>Your Message</h6>
					<blockquote><em><c:out value="${negotiation.note.text}"/></em></blockquote>
				</c:if>

				<c:if test="${negotiation.expiresOn != 0}">
					<h6>Offer Expiry</h6>
					<p>
						Your offer
						<c:out value="${negotiation.isExpired ? 'expired on' : 'is available until'}"/>
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMM d, YYYY h:mma z', negotiation.expiresOn, work.timeZone)}"/>
					</p>
				</c:if>

				<form action="/assignments/cancel_negotiation/${work.workNumber}" class="wm-action-container">
					<input type="hidden" name="id" value="${negotiation.encryptedId}"/>
					<button type="submit" class="accept-negotiation button">Cancel Offer</button>
				</form>
				<br/>
				<div class="alert">
					<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this assignment</a>
				</div>
			</c:otherwise>
		</c:choose>
	</c:when>

	<c:otherwise>
		<c:if test="${not workResponse.inWorkBundle}">
			<c:choose>
				<c:when test="${not eligible and not currentUser.dispatcher}">
					<p>Please complete the eligibility requirements below in order to be able to apply for the assignment.</p>
				</c:when>
				<c:otherwise>
					<h4>
						Accept or Decline
						<span class="label label-success">Action Required</span>
					</h4>

					<p>
						Please review the assignment details and accept or decline this request depending on your expertise and
						availability.
						<div class="alert">
							<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action ml"> <i class="icon-question-sign"></i> Ask a question about this assignment</a>
						</div>
					</p>
				</c:otherwise>
			</c:choose>

		</c:if>
		<c:if test="${workResponse.inWorkBundle}">
			<div class="form-actions">
				This assignment is part of a bundle called: "${workResponse.workBundleParent.title}".
				You can click <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">here</a> to see this bundle.
			</div>
		</c:if>
	</c:otherwise>
</c:choose>
