<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="workType" value="${isWorkBundle ? 'bundle' : 'assignment'}"/>
<div class="unassign_description">
	You are about to remove ${wmfmt:toPrettyName(workerFullName)} from this ${workType}<c:if test="${!isInternal}"> without pay</c:if>.
	<br>
	<c:choose>
		<c:when test="${isWorkBundle}">
			The bundle and the assignments it contains will return to an "Available" status.
		</c:when>
		<c:when test="${isInWorkBundle}">
			The assignment will be unbundled and will return to an "Available" status.
		</c:when>
		<c:otherwise>
			The assignment will return to an "Available" status.
		</c:otherwise>
	</c:choose>
</div>
<c:if test="${not isWorkBundle and not isInternal}">
	<div class="link_to_cancel">
		<strong>If you would like to pay ${wmfmt:toPrettyName(workerFullName)}</strong>, <a href="/assignments/cancel_work/${work.workNumber}" class="cancel_action">click here to cancel the assignment
		with pay instead</a>
	</div>
</c:if>

<form action="/assignments/unassign/${work.workNumber}" id="unassign_form" class="form-horizontal" method="POST" >
	<wm-csrf:csrfToken />

	<div class="messages"></div>

	<c:if test="${not isInternal}">
	<div class="control-group">
		<label for="reason" class="control-label">Reason for Unassign</label>
		<div class="controls">
			<select id="reason" name="cancellationReasonTypeCode" class="input-block-level">
				<option value="">Not specified</option>
				<c:forEach var="reason" items="${unassignReasons}">
					<option value="${reason.code}">${reason.description}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	</c:if>

<c:choose>
  <c:when test="${hasPriceHistory}">
 		<div class="control-group">
			<label for="price-rollback" class="control-label">Pricing Details</label>
			<div class="controls">
				<wm:radio name="rollbackToOriginalPrice" value="false" isChecked="true">
					<span style='display:inline-block;width:200px;height:20px;'>Keep current price</span>
					<fmt:formatNumber value="${currentPriceTotal}" currencySymbol="$" type="currency"/>
				</wm:radio>
				<wm:radio name="rollbackToOriginalPrice" value="true">
					<span style='display:inline-block;width:200px;'>Revert to original price</span>
					<fmt:formatNumber value="${originalPriceTotal}" currencySymbol="$" type="currency"/>
				</wm:radio>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<input type='hidden' name="price_rollback" value="false" />
	</c:otherwise>
</c:choose>
	<div class="control-group dn">
		<label class="control-label <c:if test="${not isInternal}">required</c:if>" for="unassign_note">Note</label>
		<div class="controls">
			<textarea name="note" id="unassign_note" class="input-block-level" rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Unassign</button>
	</div>

</form>
