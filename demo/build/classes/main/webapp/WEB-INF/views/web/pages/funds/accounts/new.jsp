<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="New Account" bodyclass="accountSettings" breadcrumbSection="Payments" breadcrumbSectionURI="/funds" breadcrumbPage="New Account" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/addgcc.jsp"/>

	<div class="inner-container intro-add-account">
		<div class="page-header clear">
			<h3 class="fl">Add a Financial Account</h3>
			<a href="<c:url value="/funds/accounts"/>" class="pull-right button">Back to List</a>
		</div>
		<div class="form-horizontal row_wide_sidebar_right">
			<div class="content">
				<form:form modelAttribute="accountForm" action="/funds/accounts" method="POST">
					<wm-csrf:csrfToken />

					<input id="return_to_page" type="hidden" name="return" value="<c:out value="${returnToPage}"/>"/>

					<c:import url='/WEB-INF/views/web/partials/message.jsp'/>
					<c:import url='/WEB-INF/views/web/partials/general/notices_js.jsp'>
						<c:param name="containerId" value="validation_messages"/>
					</c:import>

					<c:if test="${!hasVerifiedTaxEntity}">
						<div class="alert" style="text-align: center;">
							<a href="/account/tax" class="alert-message-btn">Add your Tax Information</a>
							<p>You will not be able to withdraw your earnings until your tax information is verified.</p>
						</div>
					</c:if>

					<div class="control-group">
						<form:label cssClass="control-label" path="type">Method</form:label>
						<div class="controls">
							<form:select path="type">
								<c:if test="${currentUser.seller || currentUser.dispatcher}"><form:option value="Select"/></c:if>
								<c:if test="${!isInternational}">
									<form:option value="ach" label="Bank Account"/>
								</c:if>
								<c:if test="${currentUser.seller || currentUser.dispatcher}">
									<form:option value="ppa" label="PayPal"/>
									<form:option value="gcc" label="Work Market Visa Card"/>
								</c:if>
							</form:select>
						</div>
					</div>

					<div class="control-group" id="countryControl">
						<form:label cssClass="control-label" path="type">Country</form:label>
						<div class="controls">
							<form:select path="country">
								<c:forEach items="${taxInfoCountries}" var="taxCountry" >
									<form:option value="${taxCountry.id}" label="${taxCountry.name}"/>
								</c:forEach>
							</form:select>
							<span class="tooltipped tooltipped-n" aria-label="The account country selected is determined by your verified tax information.">
								<i class="wm-icon-question-filled"></i>
							</span>
						</div>
					</div>

					<fieldset class="config dn" rel="ach">
						<div class="page-header">
							<h4>Add your Bank Account Information</h4>
						</div>

						<div class="control-group">
							<form:label path="nameOnAccount" class="control-label required">Name on Account:</form:label>
							<div class="controls">
								<form:select path="nameOnAccount">
									<form:option value="${currentUser.fullName}" label="Select"/>
									<c:choose>
										<c:when test="${currentUser.companyIsIndividual}">
											<form:option value="${currentUser.fullName}"/>
										</c:when>
										<c:otherwise>
											<form:options items="${accountUserNames}"/>
										</c:otherwise>
									</c:choose>
								</form:select>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required">Account Type</label>
							<div class="controls">
								<ul class="inputs-list">
									<li>
										<label>
											<form:radiobutton path="bankAccountTypeCode" value="checking" id="bank_account_type_checking"/>
											<span>Checking</span>
										</label>
									</li>
									<li>
										<label>
											<form:radiobutton path="bankAccountTypeCode" value="savings" id="bank_account_type_savings"/>
											<span>Savings</span>
										</label>
									</li>
								</ul>
							</div>
						</div>

						<div class="control-group" data-country="USA" style="display: ${accountForm.country == 'USA' ? 'block' : 'none'}">
							<form:label path="routingNumber" class="control-label required">Routing Number (9 digits)</form:label>
							<div class="controls">
								<form:input path="routingNumber" id="routing_number" maxlength="9"/>
								<span class="help-block"><strong>Note:</strong> Please specify a US routing number</span>
							</div>
						</div>
						<div class="control-group" data-country="CAN" style="display: ${accountForm.country == 'CAN' ? 'block' : 'none'}">
							<form:label path="branchNumber" class="control-label required">Transit Branch Number (5 digits)</form:label>
							<div class="controls">
								<form:input path="branchNumber" id="branch_number" maxlength="5" />
								<span class="help-block"><strong>Note:</strong> Please specify a Canadian Branch Number</span>
							</div>
						</div>
						<div class="control-group" data-country="CAN" style="display: ${accountForm.country== 'CAN' ? 'block' : 'none'}">
							<form:label path="institutionNumber" class="control-label required">Financial Institution Number (3 digits)</form:label>
							<div class="controls">
								<form:input path="institutionNumber" id="institution_number" class="inspectletIgnore" maxlength="3" />
							</div>
						</div>

						<div class="control-group">
							<form:label path="accountNumber" class="control-label required">Account Number</form:label>
							<div class="controls">
								<form:input path="accountNumber" id="account_number" class="inspectletIgnore" maxlength="45" autocomplete="off"/>
							</div>
						</div>
						<div class="control-group">
							<form:label path="accountNumberConfirm" class="control-label required">Confirm Account Number</form:label>
							<div class="controls">
								<form:input path="accountNumberConfirm" id="account_number_confirm" class="inspectletIgnore" maxlength="45" autocomplete="off"/>
							</div>
						</div>
						<div class="control-group">
							<form:label path="bankName" class="control-label required">Bank Name</form:label>
							<div class="controls">
								<form:input path="bankName" id="bank_name" maxlength="45" size="30" class="br inspectletIgnore"/>
							</div>
						</div>
						<div class="control-group">
							<div class="controls">
								<p style="display: ${accountForm.country == 'CAN' ? 'inline-block' : 'none'}" data-country="CAN"><img src='/media/images/canadian_check_image.png' alt="Check"/></p>
								<p style="display: ${accountForm.country == 'USA' ? 'inline-block' : 'none'}" data-country="USA"><img src='/media/images/samplecheck.gif' alt="Check"/></p>
							</div>
						</div>

						<div class="alert alert-info" style="display: ${accountForm.country == 'USA' ? 'block' : 'none'}">
							<p>After submitting your account information, we will attempt to make two small deposits to your account
								in the next 2-3 business days. Please return to Work Market after you receive the two small deposits to verify your bank account.</p>
						</div>

						<div class="wm-action-container" style="display: ${accountForm.country == 'USA' ? 'block' : 'none'}">
							<button type="submit" class="button">Initiate Verification Deposit</button>
						</div>
						<div class="wm-action-container" style="display: ${accountForm.country == 'CAN' ? 'block' : 'none'}">
							<button type="submit" class="button">Create Bank Account</button>
						</div>
					</fieldset>

					<fieldset class="config dn" rel="ppa">
						<div class="clearfix">
							<h3>PayPal Information </h3>
						</div>

						<div class="control-group">
							<form:label path="emailAddress" class="control-label required">PayPal Email</form:label>
							<div class="controls">
								<form:input path="emailAddress" readonly="true"/>
							</div>
						</div>

						<div class="control-group">
							<form:label path="countryCode" class="control-label required">Country</form:label>
							<div class="controls">
								<form:select path="countryCode" items="${countries}"/>
							</div>
						</div>

						<div class="wm-action-container">
							<c:choose>
								<c:when test="${hasPayPal}">
									<a class="wm-action-container disabled tooltipped tooltipped-n" aria-label="Unable to add a PayPal account because you already have one added.">Add PayPal Account</a>
								</c:when>
								<c:otherwise>
									<button type="submit" class="button">Add PayPal Account</button>
								</c:otherwise>
							</c:choose>
						</div>
					</fieldset>

				</form:form>
			</div>
			<div class="sidebar">
				<div class="well-b2 config dn" rel="ach">
					<h3><img class="mr" src="${mediaPrefix}/images/icons/alert_icon_info.png">About Adding a Bank Account</h3>
					<div class="well-content">
						<div style="display: ${accountForm.country == 'CAN' ? 'none' : 'block'}">
							<h5>How long does it take?</h5>
							<p>Please wait 2 to 3 business days for the two small deposits that will show up in your bank account. Once you receive the two small deposits, click on the verify link and enter the two small amounts. </p>
						</div>
						<h5>Need more help?</h5>
						<p>Please visit our help center on 	<a href="https://workmarket.zendesk.com/hc/en-us/articles/214799148-How-do-I-add-a-payment-account" target="_blank">Learn more about adding a bank account</a>.</p>
					</div>
				</div>
				<div class="well-b2 config dn" rel="ppa">
					<h3><img class="mr" src="${mediaPrefix}/images/icons/alert_icon_info.png">About Adding a PayPal Account</h3>
					<div class="well-content">
						<h5>Who Can Setup PayPal Accounts?</h5>
						<p>All users who earn money on the platform can setup a PayPal account to withdraw your earnings. </p>
						<h5>What if my PayPal Email is different?</h5>
						<p>You can either add a secondary email on your PayPal account or change your primary email on Work Market. </p>
						<h5>Need more help?</h5>
						<p>Please visit our help center on <a href="https://workmarket.zendesk.com/hc/en-us/articles/214799148-How-do-I-add-a-payment-account" target="_blank">Learn more about adding a PayPal account</a>.</p>
					</div>
				</div>
			</div>
		</div>
	</div>

</wm:app>
