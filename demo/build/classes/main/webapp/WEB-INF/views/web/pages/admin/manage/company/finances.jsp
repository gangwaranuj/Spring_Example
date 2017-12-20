<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Finances" bodyclass="manage-company" webpackScript="admin">

	<script>
		var config = {
			mode: 'manageFinances'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>

		<div class="content">
			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}" />
			</c:import>

			<c:if test="${company.locked}">
				<c:import url="/WEB-INF/views/web/partials/admin/manage/company/unlock_header.jsp"/>
			</c:if>
			<c:if test="${company.suspended}">
				<c:import url="/WEB-INF/views/web/partials/admin/manage/company/suspend_header.jsp"/>
			</c:if>

			<h1 class="name"><c:out value="${requestScope.company.name}"/></h1>

			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/tabs.jsp" />
			<form:form modelAttribute="form" id="submit_transactional_service_type_form" action="/admin/manage/company/finances/save_account_service_type_configuration/${requestScope.company.id}" method="post" class="form-horizontal">
				<wm-csrf:csrfToken />
				<p>
					<strong>AP Limit:</strong> <fmt:formatNumber value="${requestScope.account_register.apLimit}" currencySymbol="$" type="currency"/>
					<sec:authorize access="hasAnyRole('ROLE_WM_ACCOUNTING')">
						<span class="small">(<a id="edit_ap_limit">Edit</a>)</span>
					</sec:authorize>
				</p>
				<p>
					<strong>Terms:</strong>
					<c:choose>
						<c:when test="${company.manageMyWorkMarket.paymentTermsEnabled}">Enabled</c:when>
						<c:when test="${company.manageMyWorkMarket.paymentTermsOverride}">Setup overridden</c:when>
						<c:when test="${enable_payment_terms_override}">
							<a href="/admin/manage/company/override_payterms/${company.id}" class="override_payterms_action">
								Override bank setup
							</a>
						</c:when>
						<c:otherwise>Not enabled</c:otherwise>
					</c:choose>
				</p>

				<p>
					<strong>Customer Type</strong>
					<form:select cssClass="span4" path="customerType">
						<form:option value="managed">Managed</form:option>
						<form:option value="buyer">Client</form:option>
						<form:option value="resource">Worker</form:option>
					</form:select>
				</p>

				<p>
					<strong>VIP (never lock this Company):</strong>
					<c:choose>
						<c:when test="${vip_permission}">
							<span><form:radiobutton path="vipFlag" value="true" cssClass="radio_inline" />Yes<form:radiobutton path="vipFlag" value="false" cssClass="radio_inline" />No</span>
						</c:when>
						<c:otherwise>
							<span class="radio_inline">${form.vipFlag ? 'Yes' : 'No'}</span>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${vip_set_by_first_name != null}">
							<small class="muted">(set by <c:out value="${vip_set_by_first_name}" /> <c:out value="${vip_set_by_last_name}" /> on ${wmfmt:formatCalendarWithTimeZone("MM/dd/yyyy 'at' h:mm aa z", vip_set_on, currentUser.timeZoneId)})</small>
						</c:when>
						<c:otherwise>
							<small class="muted">(never set)</small>
						</c:otherwise>
					</c:choose>
				</p>

				<p>
					<strong>Pricing Type:</strong>
					<c:choose>
						<c:when test="${payment_configuration.accountPricingType eq 'transactional'}">Transaction</c:when>
						<c:when test="${payment_configuration.accountPricingType eq 'subscription'}">Subscription</c:when>
					</c:choose>
				</p>

				<p>
					<strong>Service Type:</strong>
				<div id="serviceTypeMessages" class="alert-message alert" style="display:none;"></div>
				<c:choose>
					<c:when test="${payment_configuration.accountPricingType eq 'transactional'}">
						<c:if test="${enable_account_service_type_edit}">
							<button type="button" class="button -small add-btn">+ Add</button>
						</c:if>
						<c:set var="index" value="${0}" />
						<c:forEach var="accountServiceTypeConfiguration" items="${payment_configuration.accountServiceTypeConfigurations}">
							<div class="control-group serviceConfig">
								<label class="control-label">Country:</label>
								<div class="controls">
									<form:select path="accountServiceTypeList[${index}].countryCode">
										<c:forEach var="country" items="${countries}">
											<option value="${country.id}" <c:if test="${country.id eq accountServiceTypeConfiguration.country.id}">selected</c:if> >
												<c:out value="${country.name}" />
											</option>
										</c:forEach>
									</form:select>
								</div>
							</div>
							<div class="control-group">
								<label class="account-service-type-label control-label">Service Type:</label>
								<div class="controls">
									<form:select path="accountServiceTypeList[${index}].accountServiceTypeCode">
										<c:forEach var="serviceType" items="${account_service_types}">
											<option value="${serviceType.code}" <c:if test="${serviceType.code eq accountServiceTypeConfiguration.accountServiceType.code}">selected</c:if> >
												<c:out value="${serviceType.description}" />
											</option>
										</c:forEach>
									</form:select>
								</div>
								<c:if test="${enable_account_service_type_edit}">
									<label class="remove"><a href="#">Remove</a></label>
								</c:if>
							</div>
							<c:set var="index" value="${index+1}" />
						</c:forEach>
					</c:when>
					<c:when test="${payment_configuration.accountPricingType eq 'subscription'}">
						<table class="table table-striped table-hover">
							<thead>
							<tr>
								<th>Country</th>
								<th>Service Type</th>
							</tr>
							</thead>
							<tbody>
							<c:forEach var="accountServiceTypeConfiguration" items="${payment_configuration.accountServiceTypeConfigurations}">
								<tr>
									<td><c:out value="${accountServiceTypeConfiguration.country.name}" /></td>
									<td><c:out value="${accountServiceTypeConfiguration.accountServiceType.description}" /></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</c:when>
				</c:choose>
				</p>
				<c:if test="${vip_permission || payment_configuration.accountPricingType eq 'transactional'}">
					<button type="submit" class="button">Save</button>
				</c:if>
			</form:form>
			<hr/>

			<h5>Banks</h5>

			<c:choose>
				<c:when test="${not empty bankAccounts}">
					<table id="payment_accounts" class="table table-striped">
						<thead>
						<tr>
							<th>Account</th>
							<th>Type</th>
							<th>Status</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="item" items="${bankAccounts}">
							<tr>
								<td>
									<c:choose>
										<c:when test="${item.type eq 'PPA'}"><c:out value="${item.emailAddress}"/></c:when>
										<c:when test="${item.type eq 'GCC'}"><c:out value="${item.bankName} - ${item.accountNumber}"/></c:when>
										<c:otherwise><c:out value="${item.bankName}"/> (${wmfmt:showLastNDigits(item.accountNumber, "*", 4)})</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${item.bankAccountType.code eq 'checking'}">Checking</c:when>
										<c:when test="${item.type eq 'PPA'}">PayPal</c:when>
										<c:otherwise>Savings</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${not empty item.confirmedFlag and item.confirmedFlag}">Verified</c:when>
										<c:otherwise>Not Verified</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					None
				</c:otherwise>
			</c:choose>

			<c:if test="${payment_configuration.accountPricingType eq 'subscription'}">
				<hr>

				<h5>Outstanding Subscription Invoices</h5>
				<table id="payment_subscription_invoices" class="table table-striped">
					<thead>
					<tr>
						<th>Invoice ID</th>
						<th>Invoice Number</th>
						<th>Invoice due date</th>
					</tr>
					</thead>
					<c:if test="${not empty subscription_invoices}">
						<tbody>
						<c:forEach var="item" items="${subscription_invoices}">
							<tr>
								<td><c:out value="${item.invoiceId}" /></td>
								<td><c:out value="${item.invoiceNumber}" /></td>
								<td>${wmfmt:formatCalendar("MM/dd/yyyy", item.invoiceDueDate)}</td>
							</tr>
						</c:forEach>
						</tbody>
					</c:if>
				</table>
			</c:if>
		</div>
	</div>

	<div class="dn">
		<div id="edit_ap_limit_popup">
			<form action="/admin/manage/company/update_ap_limit" method="post" id="edit_ap_limit_form" class="form-horizontal">
				<wm-csrf:csrfToken />

				<input type="hidden" name="id" value="${requestScope.company.id}"/>

				<div class="message error strong pr br dn">
					<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="db close pa">Close</a>
					<b></b>
					<div></div>
				</div>

				<fieldset>
					<div class="clearfix control-group">
						<label class="control-label">Current AP Limit:</label>
						<div class="controls">
							<fmt:formatNumber value="${requestScope.account_register.apLimit}" currencySymbol="$" type="currency"/>
						</div>
					</div>
					<div class="clearfix control-group">
						<label class="control-label">New AP Limit:</label>
						<div class="controls">
							<c:choose>
								<c:when test="${empty requestScope.account_register.apLimit}">
									<input type="text" name="ap_limit" id="ap_limit" class="small" value="1000"/>
								</c:when>
								<c:otherwise>
									<input type="text" name="ap_limit" id="ap_limit" class="small" value="${requestScope.account_register.apLimit}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</fieldset>
				<div class="wm-action-container">
					<button class="button">Update</button>
				</div>
			</form>
		</div>
	</div>


	<script type="text/x-jquery-tmpl" id="add_account_service_type_configuration">
		<div class="control-group clearfix serviceConfig">
			<label>Country:</label>
			<div class="span3">
				<select class="span3 country" name="accountServiceTypeList[\${idx}].countryCode">
					<c:forEach var="country" items="${countries}">
			<option value="${country.id}">
			<c:out value="${country.name}" />
			</option>
			</c:forEach>
					</select>
				</div>
				<label class="account-service-type-label">Service Type:</label>
				<div class="span3">
					<select class="span3" name="accountServiceTypeList[\${idx}].accountServiceTypeCode">
						<c:forEach var="serviceType" items="${account_service_types}">
				<option value="${serviceType.code}">
				<c:out value="${serviceType.description}" />
				</option>
			</c:forEach>
				</select>
			</div>
			<label class="remove"><a href="#">Remove</a></label>
		</div>
	</script>

</wm:admin>
