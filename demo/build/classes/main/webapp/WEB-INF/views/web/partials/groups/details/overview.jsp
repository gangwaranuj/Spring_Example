<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<p>${wmfmt:tidy(wmfmt:nl2br(group.description))}</p>

<c:set var="hasRequirements" value="${hasNonAgreementRequirements or is_group_admin or not empty missing_contract_versions}" />

<c:if test="${hasRequirements}">
	<input type="hidden" name="groupId" value="${wmfmt:escapeJavaScript(group.id)}"/>
	<input type="hidden" name="userId" value="${userId}"/>
	<div class="row">
		<div class="span10">
			<c:choose>
				<c:when test="${is_group_admin}">
					<h4>Requirements for Membership</h4>
					<div>
						<jsp:include page="/WEB-INF/views/web/partials/groups/requirements.jsp"/>
					</div>
				</c:when>
				<c:otherwise>
					<h4>Requirements for Membership
						<span class="tooltipped tooltipped-n" aria-label="Add or update your profile to meet talent pool requirements for the best chance of becoming a member.">
							<i class="wm-icon-question-filled"></i>
						</span>
					</h4>
					<c:if test="${not is_dispatcher}">
						<div id="fulfilled-requirements">
							<h5>Fulfilled Requirements:</h5>
							<jsp:include page="/WEB-INF/views/web/partials/groups/requirements.jsp?validation_type=yes"/>
						</div>
						<div id="missing-requirements">
							<h5>Missing Requirements:</h5>
							<jsp:include page="/WEB-INF/views/web/partials/groups/requirements.jsp?validation_type=no"/>
						</div>
					</c:if>
					<c:if test="${is_dispatcher}">
						<h5>Requirements for Employees:</h5>
						<jsp:include page="/WEB-INF/views/web/partials/groups/requirements.jsp"/>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>
