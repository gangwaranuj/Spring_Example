<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<div id="resource-complete-wrapper">

	<c:choose>
	<c:when test="${hidePricing and work.pricing.id ne pricingStrategyTypes.INTERNAL}">
		<div id="complete-info-box">
			<c:if test="${work.pricing.id == pricingStrategyTypes['PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.maxNumberOfHours)}"/>
				<p>
				This assignment is for up to ${maxHoursMinutes.hours} hours<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes} minutes</c:if>.
				</p>
			</c:if>
			<c:if test="${work.pricing.id == pricingStrategyTypes['BLENDED_PER_HOUR']}">
				<c:set var="maxHoursMinutes" value="${wmfmt:getHoursAndMinutes(work.pricing.initialNumberOfHours + work.pricing.maxBlendedNumberOfHours)}" scope="page"/>
				<p>
				This assignment is for up to ${maxHoursMinutes.hours} hours<c:if test="${maxHoursMinutes.minutes > 0}"> ${maxHoursMinutes.minutes} minutes</c:if>.
				</p>
			</c:if>
			<c:if test="${work.pricing.id == pricingStrategyTypes['PER_UNIT']}">
				This assigment is for up to ${work.pricing.maxNumberOfUnits} units.
			</c:if>
			<p>
			Please contact your Team Agent to complete this assignment for you.
			</p>
			<c:import url="/WEB-INF/views/web/partials/assignments/details/dispatcher_info.jsp"/>
		</div>
	</c:when>
	<c:otherwise>
		<div id="resource-complete" class="wm-action-container">
			<div id="complete-todo">
				<c:choose>
					<c:when test="${not isPendingNegotiation}">
						<button class="button resource-complete-toggle"<c:if test="${mboEnabled}">disabled</c:if>>Complete this Assignment <i class="icon-double-angle-right"></i></button>
					</c:when>
					<c:otherwise>
						<button class="button tooltipped tooltipped-n disabled" disabled aria-label="Why is this disabled? You cannot complete this assignment until the pending negotiations are approved or declined.">
							Complete this Assignment <i class="icon-double-angle-right"></i>
						</button>
						<div class="help-inline" style="color:red">You cannot complete this assignment until the pending negotiations are approved or declined.</div>
					</c:otherwise>
				</c:choose>
			</div>

			<div id="complete" class="dn">
				<div class="media completion">
					<div class="completion-icon">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/cash-icon.jsp"/>
					</div>
					<div class="media-body">
						<h4>Assignment Completion</h4>

						<form:form id="complete_form" cssClass="completion-well" commandName="work">
							<div class="messages"></div>

							<input type="hidden" name="id" value="${work.workNumber}"/>
							<p class="ml">
								<span class="label label-important">Required</span>
								Provide a summary of work performed or other relevant information.
							</p>

							<p>
								<textarea id="resolution" name="resolution" class="input-block-level" rows="5"><c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/></textarea>
							</p>

							<c:import url="/WEB-INF/views/web/partials/assignments/details/pricing_complete.jsp"/>

							<div class="alert-actions">
								<a class="button resource-complete-save pull-right">Submit for Approval</a>
							</div>
							<p><span class="help-block">You will be able to rate and review this client once you are paid.</span></p>

						</form:form>
					</div>
				</div>
			</div>
		</div>
	</c:otherwise>
	</c:choose>
</div>
