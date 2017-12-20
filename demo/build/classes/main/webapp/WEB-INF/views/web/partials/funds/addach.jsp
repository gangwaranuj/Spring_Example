<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="form-horizontal inner-container">
	<div class="messages"></div>
	<sf:form action="/funds/addach" method="POST" modelAttribute="addACHForm" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<div class="alert alert-info">
			<strong>Step 2: Enter your payment details</strong>
			<p>It can take 2-3 business days to complete the transfer depending on your bank's holiday schedule and payment policies. If your information is received by 4pm ET, we will process your details that business day. Learn more about <a href="https://workmarket.zendesk.com/hc/en-us/articles/210052757-How-do-I-fund-my-Work-Market-account" target="_blank">adding funds.</a></p>
		</div>
		<c:if test="${empty accounts}">
			<p><strong>Sorry, you are unable to add funds via ACH until you link a financial account. <a href="<c:url value="/funds/accounts/new?return_to_page=payments"/>">Add a Financial Account</a></strong>
			</p>
		</c:if>

			<div class="control-group" id="add_ach_funds_0">
				<label class="control-label">Amount to Add:</label>

				<div class="controls">
					<div class="input-prepend">
						<span class="add-on">$</span>
						<sf:input path="amount" value="${amount}" id='amount_ach' maxlength='10' class='span2'/> USD
						<c:if test="${doesCompanyHaveReservedFundsEnabledProject}">
							<span class="help-block"><a class="add_more" onclick="javascript:add_ach_allocate_dropdown();">Reserve for projects</a></span>
						</c:if>
					</div>
				</div>
			</div>

			<div class="control-group">
				<sf:label cssClass="control-label" path="account">Transfer From:</sf:label>
				<div class="controls">
					<sf:select path="account" id="account" name="account">
						<sf:option value="">- Select -</sf:option>
						<c:forEach items="${accounts}" var="account" varStatus="loop">
							<sf:option value='${account.id}'><c:out value="${account.bankName}" /> (<c:out value="${fn:substring(unobfuscatedAccountNumbers.get(loop.index), fn:length(unobfuscatedAccountNumbers.get(loop.index)) - 4, -1)}" />)</sf:option>
						</c:forEach>
					</sf:select>
					<span class="help-block"><a href="<c:url value="/funds/accounts/new?return_to_page=payments"/>">Add a Financial Account</a></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label">Total:</label>
				<div class="controls currency-control">
					<span class="strong" id="calc_ach_total">$0</span>
				</div>
			</div>

			<div class="wm-action-container">
				<button id="btn-addach-back" type="button" class="button" >Back</button>
				<button type="submit" class="button" disabled="disabled" id="add_funds_ach">Add Funds</button>
			</div>
		</sf:form>
</div>

<script id="tmpl-ach-allocate-dropdown" type="text/x-jquery-tmpl">
	<div class="clearfix" id="add_ach_funds_\${ach_dropdown_id}">
		<label class="required">Amount to Add:</label>
		<div class="input">
			<div class="input-prepend">
				<span class="add-on">$</span>
				<input id="add_ach_funds_amount_\${ach_dropdown_id}" onkeyup="javascript:calc_ach_amount();" name="project_amount[\${ach_dropdown_id}]" maxlength="10" class="span2"/> USD
				<select id="add_ach_funds_project_\${ach_dropdown_id}" name="project_id[\${ach_dropdown_id}]" onchange="javascript:calc_ach_amount();">
					<option name="" value="">- Select -</option>
					<option value="general_cash" label="Unreserved Cash"></option>
					<c:forEach items="${project_list}" var="project">
						<option value="${project.key}" label="${project.value}"></option>
					</c:forEach>
				</select>
				<a id="add_ach_funds_add_more_\${ach_dropdown_id}" class="add_more"  onclick="javascript:add_ach_allocate_dropdown();">Add another</a>
				<a id="add_ach_funds_remove_\${ach_dropdown_id}" class="dn" onclick="javascript:remove_ach_allocate_dropdown(\${ach_dropdown_id});"><i class="wm-icon-trash icon-inline"></i></a>
			</div>
			<input id="add_ach_funds_percentage_\${ach_dropdown_id}" type="hidden" value="0.00">
		</div>
	</div>
</script>
