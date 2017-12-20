<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="frm" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.payment_settings" var="global_payment_settings"/>
<wm:app
	pagetitle="${global_payment_settings}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'paycycle',
			pendingPaymentWorkCount: parseInt('${wmfmt:escapeJavaScript(num_assignments_payment_pending)}'),
			statementsEnabled: ${wmfn:boolean(mmw.statementsEnabled, '1', '0')},
			payTermsEnabled: ${wmfn:boolean(mmw.paymentTermsEnabled, '1', '0')}
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage/paymenterms" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<form:form id="form_paymenterms" modelAttribute="mmw" action="/settings/manage/paymenterms" method="post" acceptCharset="utf-8">
					<wm-csrf:csrfToken />

					<div class="page-header">
						<h3><fmt:message key="global.payment_settings"/></h3>
					</div>

					<div class="alert alert-info">
						<div class="row-fluid">Configure payment terms which fit your specific business needs. Terms can be set at the company(global), template, and individual assignment level.
							<strong><a href="https://workmarket.zendesk.com/hc/en-us/search/click?data=BAh7CjoHaWRpBIsmhQw6CXR5cGVJIgxhcnRpY2xlBjoGRVQ6CHVybEkiIS9oYy9lbi11cy9hcnRpY2xlcy8yMTAwNTI3NDcGOwdGOg5zZWFyY2hfaWRJIiljNjg4NjA3ZS00NDEwLTRjZDgtYTEyYi01MjE3ZmQ5OTZkNjMGOwdGOglyYW5raQY%3D--0fb1046f010f44bec2c34be9097cde1b27fc56a1" target="_blank">Learn more <i class="icon-info-sign"></i></a></strong>
						</div>
					</div>

					<c:if test="${mmw.paymentTermsOverride == false && has_accounts == false}">
						<div class="alert alert-warning">
							<c:url var="addAccountUrl" value="/funds/accounts/new" scope="page"/>
							<p><fmt:message key="paymenterms.must_have"/> <a href="${addAccountUrl}"><fmt:message key="paymenterms.linked_bank_account"/></a> <fmt:message key="paymenterms.to_enable_payment_terms"/></p>
						</div>
					</c:if>

					<c:choose>
						<c:when test="${mmw.paymentTermsOverride || has_accounts}">
							<div class="page-header">
								<h4><fmt:message key="global.payment_terms"/> <span class="label label-success"><fmt:message key="paymenterms.available"/></span></h4>
							</div>
							<p><fmt:message key="paymenterms.assignment_durations_available"/>:</p>

							<ul class="icons-ul">
								<c:forEach items="${paymentTermsDurations}" var="duration" >
									<div>
										<i class="icon-li icon-ok text-success"></i>
										<c:choose>
											<c:when test="${duration.numDays eq 0}">
												<fmt:message key="global.immediate"/>
											</c:when>
											<c:when test="${duration.numDays eq 1}">
												<fmt:message key="global.one_day"/>
											</c:when>
											<c:otherwise>
												<fmt:message key="global.multiple_days" var="global_multiple_days">
													<fmt:param value="${duration.numDays}"/>
												</fmt:message>
												${global_multiple_days}
											</c:otherwise>
										</c:choose>
										<c:if test="${duration.numDays eq company.paymentTermsDays}">
											<span class="label muted"><fmt:message key="paymenterms.default"/></span>
										</c:if>
									</div>
								</c:forEach>
							</ul>
						</c:when>
						<c:otherwise>
							<div class="page-header">
								<h4><fmt:message key="paymenterms.are"/> <span class="label label-error"><fmt:message key="paymenterms.unavailable"/></span></h4>
							</div>
							<p><fmt:message key="paymenterms.default_all_invoices_paid"/> <strong><fmt:message key="paymenterms.approval"/></strong>.</p>
						</c:otherwise>
					</c:choose>
					<c:if test="${mmw.paymentTermsOverride or has_accounts}">
						<p class="dn" id="message_enable_pay_terms"><fmt:message key="paymenterms.define_payment_process"/></p>
					</c:if>

					<div class="clearfix">
						<c:choose>
							<c:when test="${mmw.statementsEnabled and not empty(statements_configuration)}">
								<table class="zebra-striped">
									<tbody>
									<tr>
										<td><fmt:message key="paymenterms.when_will_receive_statements"/></td>
										<td>
											<c:choose>
												<c:when test="${statements_configuration.paymentCycle eq 'DAILY'}">
													Daily
												</c:when>
												<c:when test="${statements_configuration.paymentCycle eq 'WEEKLY'}">
													Weekly
												</c:when>
												<c:when test="${statements_configuration.paymentCycle eq 'BIWEEKLY'}">
													Bi-Weekly
												</c:when>
												<c:when test="${statements_configuration.paymentCycle eq 'MONTHLY'}">
													Monthly
												</c:when>
											</c:choose>
											<small class="meta">(<c:out value="${statements_configuration.paymentCycleDays}"/> <fmt:message key="paymenterms.days"/>)</small>
										</td>
									</tr>

									<c:if test="${not empty(statements_configuration.preferredDayOfMonth) or not empty(statements_configuration.preferredDayOfWeek)}">
										<tr>
											<td><fmt:message key="paymenterms.what_days_will_statements_be_sent"/></td>
											<td>
												<c:choose>
													<c:when test="${statements_configuration.paymentCycleDays eq 30}">
														<c:out value="${statements_configuration.preferredDayOfMonth}"/>
													</c:when>
													<c:when test="${statements_configuration.paymentCycleDays eq 14}">

														<c:if test="${statements_configuration.biweeklyPaymentOnSpecificDayOfMonth}">
															Day <c:out
																value="${statements_configuration.preferredDayOfMonthBiweeklyFirstPayment}"/> and
															Day <c:out
																value="${statements_configuration.preferredDayOfMonthBiweeklySecondPayment}"/>
														</c:if>

														<c:if test="${not(statements_configuration.biweeklyPaymentOnSpecificDayOfMonth)}">
															<c:out value="${day_of_week_name}"/>
														</c:if>
													</c:when>
													<c:otherwise>
														<c:out value="${day_of_week_name}"/>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:if>

									<tr>
										<td><fmt:message key="paymenterms.days_for_processing"/></td>
										<fmt:message key="paymenterms.accounting_process_days" var= "paymenterms_accounting_process_days">
                    		<fmt:param value="${statements_configuration.accountingProcessDays}"/>
                    </fmt:message>
										<td>${paymenterms_accounting_process_days}</td>
									</tr>
									<tr>
										<td><fmt:message key="paymenterms.what_payment_methods"/></td>
										<td>
											<c:out value="${paymentMethodsList}"/>

											<fmt:message key="paymenterms.max_number_days" var= "paymenterms_max_number_days">
                      	<fmt:param value="${payment_method_max_days}"/>
                      </fmt:message>
											<small class="meta">(${paymenterms_max_number_days})</small>
										</td>
									</tr>
									<tr>
										<td><fmt:message key="global.auto_pay"/></td>
										<td><c:out value="${(mmw.autoPayEnabled) ? 'Enabled' : 'Disabled'}"/></td>
									</tr>
									<tr>
										<td><strong><fmt:message key="global.payment_terms"/></strong></td>
										<td>
											<strong><c:out value="${mmw.paymentTermsDays}"/> <fmt:message key="global.days"/></strong>
										</td>
									</tr>
									</tbody>
								</table>

								<p>
									<small>
										<fmt:message key="paymenterms.payment_calculation"/>
									</small>
								</p>

								<button type="button" id="cta-open-wizard" class="button"><fmt:message key="paymenterms.edit"/></button>
							</c:when>

							<c:when test="${mmw.paymentTermsOverride or has_accounts}">
								<button type="button" id="cta-open-wizard" class="button"><fmt:message key="paymenterms.configure"/></button>
							</c:when>

							<c:otherwise>
								<button type="button" class="button" disabled="disabled"
										title="<fmt:message key="paymenterms.must_have_bank_account"/>"><fmt:message key="paymenterms.configure"/>
								</button>
							</c:otherwise>
						</c:choose>
					</div>

					<br/><br/>

					<div class="page-header">
						<h4><fmt:message key="paymenterms.pricing_display_options"/></h4>
					</div>

					<div id="assigment-pricing">
						<div class="accordion" id="accordion1">
							<div class="accordion-heading">
								<div class="alert alert-info">
									<p>
										<fmt:message key="paymenterms.mission_statement"/> <strong><a data-toggle="collapse" data-parent="#accordion1" href="#collapseOne"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
									</p>
								</div>
							</div>
							<div class="learn-more-box">
								<div id="collapseOne" class="accordion-body collapse">
									<div id="addressBox">
										<fmt:message key="paymenterms.transaction_fees_and_subscription_agreements"/>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="form-horizontal">
						<c:choose>
							<c:when test="${is_subscription==false}">
								<div class="control-group">
									<label class="control-label">The Worker You Hire Pays</label>
									<div class="controls">
										<input type="radio" attribute="assignment_pricing_type" name="assignment_pricing_type" value="1" <c:if test="${assignment_pricing_type==1}">checked="checked"</c:if> />
										<fmt:message key="paymenterms.transaction_fees_details"/> (<a id="assignment_resource_funds_example" href=""><fmt:message key="paymenterms.example"/></a>)
									</div>
								</div>

								<div class="control-group">
									<label class="control-label"><fmt:message key="global.you_pay"/></label>
									<div class="controls">
										<input type="radio" attribute="assignment_pricing_type"  name="assignment_pricing_type" value="2" <c:if test="${assignment_pricing_type==2}">checked="checked"</c:if> />
										<fmt:message key="paymenterms.transaction_fees_added"/> (<a id="assignment_client_funds_example" href=""><fmt:message key="paymenterms.example"/></a>)</br>
									</div>
								</div>

								<div class="control-group">
									<label class="control-label"><fmt:message key="paymenterms.show_calculator"/></label>
									<div class="controls">
										<input type="radio"  name="assignment_pricing_type" value="0" <c:if test="${assignment_pricing_type==0}">checked="checked"</c:if> />
										<fmt:message key="paymenterms.allow_switching_betweeen_payments"/> (<a id="assignment_calculator_example" href=""><fmt:message key="paymenterms.example"/></a>)
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="control-group">
									<input type="hidden" name="assignment_pricing_type" value="0" />
									<input type="hidden" name="is_subscription" value="true" />
									<span class="alert alert-warning"><fmt:message key="paymenterms.on_subscription"/></span>
								</div>
							</c:otherwise>
						</c:choose>
						<div class="control-group">
							<div class="controls">
								<span class="help-block"><i class="icon-info-sign"></i>
									<fmt:message key="paymenterms.worker_can_see_net_money"/>
								</span>
							</div>
							</br>
						</div>

						<div class="page-header">
							<h4><fmt:message key="global.invoice_settings"/></h4>
						</div>

						<div class="form-horizontal" id="email_invoices_settings_form">
							<div class="control-group">
								<label class="control-label"><fmt:message key="global.assignment_invoice"/></label>
								<div class="controls">
									<form:checkbox id="auto_send_invoice_email" path="autoSendInvoiceEmail"/>
									<span><fmt:message key="paymenterms.auto_send_email_to_default_address_for_assignment_invoices"/></span>
								</div>
							</div>

							<div class="control-group">
								<label class="control-label"><fmt:message key="paymenterms.default_assignment_invoice_email"/></label>
								<div class="controls">
									<input class="input-xlarge" type="text" id="invoice_sent_to_email" name="invoice_sent_to_email" value="${invoice_sent_to_email}"/>
									<span class="help-block"><i class="icon-info-sign"></i>
										<fmt:message key="paymenterms.assignment_invoices_sent_to_this_email_address"/>
									</span>
								</div>
							</div>

							<div class="control-group">
								<label class="control-label"><fmt:message key="paymenterms.subscription_invoice"/></label>
								<div class="controls">
									<input type="checkbox" id="auto_send_subscription_invoice_email" <c:if test="${not empty subscription_invoice_sent_to_email}">checked="checked"</c:if>  />
									<span><fmt:message key="paymenterms.auto_send_email_to_default_address_for_subscription_invoices"/></span>
								</div>
							</div>

							<div class="control-group">
								<label class="control-label"><fmt:message key="paymenterms.default_subscription_invoice_email"/></label>
								<div class="controls">
									<input type="text" id="subscription_invoice_sent_to_email" name="subscription_invoice_sent_to_email" value="${subscription_invoice_sent_to_email}"/>
									<span class="help-block">
										<i class="icon-info-sign"></i>
										<fmt:message key="paymenterms.subscription_invoices_sent_to_this_email_address"/>
									</span>
								</div>
							</div>

							<sec:authorize access="hasFeature('reserveFunds')">
								<div class="page-header">
									<h4><fmt:message key="paymenterms.reserve_funds_management"/> <span class="ml label label-warning"><fmt:message key="global.new"/>!</span></h4>
								</div>
								<div class="alert dn">
									<button type="button" class="close" data-dismiss="alert">&times;</button>
									<fmt:message key="paymenterms.will_not_be_able_to_bulk_edit"/>
								</div>

								<div class="control-group">
									<label class="control-label"><fmt:message key="global.projects"/></label>
									<label class="controls">
										<form:checkbox id="reserve_funds" path="reserveFundsEnabledFlag"/>
										<span><fmt:message key="paymenterms.enable_reserve_funds_management"/></span>
										<span class="help-block">
											<i class="icon-info-sign"></i>
											<fmt:message key="paymenterms.reserve_cash_against_projects"/>
										</span>
									</label>
								</div>
							</sec:authorize>

							<div class="wm-action-container">
								<button type="button" class="button" id="cta-submit-form"><fmt:message key="global.save_changes"/></button>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<div class="dn">
		<div id="pricing-resource-example" class="clearfix">
			<img src="${mediaPrefix}/images/assignments/assignment-resource-funds.png"/>
		</div>

		<div id="pricing-client-example">
			<img src="${mediaPrefix}/images/assignments/assignment-client-funds.png"/>
		</div>

		<div id="pricing-calc-example">
			<img src="${mediaPrefix}/images/assignments/assignment-calculator.png"/>
		</div>
	</div>

</wm:app>
