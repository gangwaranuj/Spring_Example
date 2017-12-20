<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form action="/admin/manage/company/update_fee_ranges" method="post" modelAttribute="fees" cssClass="form-horizontal well tier-form" id="transactional_form">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${requestScope.company.id}"/>
  <c:if test="${enable_fee_band_edit}">
		<button type="button" class="button add-tier-btn">Add Tier</button>
  </c:if>
	<div class="control-group transaction-fee-ranges-title">
		<div class="item">Lower Bound Throughput</div>
		<div class="item">Upper Bound Throughput</div>
		<div class="item">Transaction Fee Rate</div>
	</div>
	<c:set var="tierNum" value="${0}" />
	<c:forEach items="${fees.workFeeBands}">
		<div class="control-group transactional-tier <c:if test="${tierNum >0 and workFeeBands[tierNum].minimum eq 0}">dn</c:if>">
			<label>Tier ${tierNum +1}</label>
			<div class="input-prepend">
				<span class="add-on">$</span>
				<form:input type="text" cssClass="input-small" readOnly="readOnly" path="workFeeBands[${tierNum}].minimum" />
			</div>
			<div class="input-prepend">
				<span class="add-on">$</span>
				<form:input type="text" cssClass="input-small transactional-tier-upper" placeholder="infinity" path="workFeeBands[${tierNum}].maximum" />
			</div>
			<div class="input-append">
				<form:input type="text" class="input-small" path="workFeeBands[${tierNum}].percentage" />
				<span class="add-on">%</span>
			</div>
		</div>
		<c:set var="tierNum" value="${tierNum +1}" />
	</c:forEach>
</form:form>


<script type="text/x-jquery-tmpl" id="transactional_tier_template">
		<div class="control-group transactional-tier clearfix">
			<label>Tier \${tierNum}</label>
			<div class="input-prepend">
				<span class="add-on">$</span>
				<input type="text" class="input-small" readonly="readonly" name="workFeeBands[\${idx}].minimum" />
			</div>
			<div class="input-prepend">
				<span class="add-on">$</span>
				<input type="text" class="input-small transactional-tier-upper" placeholder="infinity" name="workFeeBands[\${idx}].maximum" {{if lastTier}} readonly="readonly" {{/if}}/>
			</div>
			<div class="input-append">
				<input type="text" class="input-small" name="workFeeBands[\${idx}].percentage" />
				<span class="add-on">%</span>
			</div>
		</div>
</script>
