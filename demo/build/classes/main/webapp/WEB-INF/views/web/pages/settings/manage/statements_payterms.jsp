<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="frm" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<form:form modelAttribute="termsForm" action="/settings/manage/statements_payterms_save" method="post" id="payment_terms" cssClass="form-stacked">
<wm-csrf:csrfToken />
<div class="messages"></div>

<div id="wizard_step_type">
	<div class="page-header alert alert-info">
		<p><fmt:message key="statements_payterms.details"/>
			<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/210052747-What-are-payment-terms-and-how-can-I-edit-them" target="_blank"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
		</p>
	</div>
	<c:choose>
		<c:when test="${requestScope.num_assignments_payment_pending>0 and requestScope.termsForm.statementsEnabled}">
			<div class="alert">
				<fmt:message key="statements_payterms.cannot_change_settings_to_invoices"/>
			</div>
		</c:when>
		<c:otherwise>
			<c:if test="${requestScope.num_assignments_payment_pending>0}">
				<div class="alert">
					<fmt:message key="statements_payterms.cannot_change_settings_to_statements"/>
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>

	<div class="page-header">
		<h5><fmt:message key="statements_payterms.select_invoices_or_automated_statement"/></h5>
	</div>
	<div class="clearfix">
		<label>
			<form:radiobutton path="paymentType" value="invoice" cssClass="cta-payment-type" disabled="${requestScope.termsForm.statementsEnabled and requestScope.num_assignments_payment_pending>0}"/>
			<fmt:message key="statements_payterms.company_pays_invoices" var="statements_payterms_company_pays_invoices">
      	<fmt:param value="${requestScope.companyName}"/>
      </fmt:message>
      ${statements_payterms_company_pays_invoices}<span><i class="icon-question-sign tooltipped tooltipped-n" aria-label="<fmt:message key="statements_payterms.invoices_allow_you"/>"></i></span>
		</label>
	</div>

	<br/>

	<div class="clearfix">
		<label>
			<form:radiobutton path="paymentType" value="statement" cssClass="cta-payment-type" disabled="${requestScope.num_assignments_payment_pending>0}" />
			<fmt:message key="statements_payterms.company_prefers_automated_statements" var="statements_payterms_company_prefers_automated_statements">
      	<fmt:param value="${requestScope.companyName}"/>
      </fmt:message>
			${statements_payterms_company_prefers_automated_statements}<span><i class="icon-question-sign tooltipped tooltipped-n" aria-label="<fmt:message key="statements_payterms.use_statements_if"/>"></i></span>
		</label>
	</div>
</div>

<div class="dn" id="wizard_step_freq">
	<h5><fmt:message key="statements_payterms.step_one_of_three"/></h5>

	<p><fmt:message key="statements_payterms.payment_process_questions"/></p>
	<div class="question clear">

		<div class="page-header">
			<fmt:message key="statements_payterms.how_often_company_receive_statements" var="statements_payterms_how_often_company_receive_statements">
        <fmt:param value="${requestScope.companyName}"/>
      </fmt:message>
      <h5>${statements_payterms_how_often_company_receive_statements}</h5>
     </div>
		<ul class="tac" >
			<li class="frequency">
				<label class="weekly <c:if test="${requestScope.termsForm.frequency eq 7}">active</c:if>">
					<form:radiobutton path="frequency" value="7"/><fmt:message key="global.weekly"/>
				</label>
				<label class="biweekly <c:if test="${requestScope.termsForm.frequency eq 14}">active</c:if>">
					<form:radiobutton path="frequency" value="14"/><fmt:message key="global.biweekly"/>
				</label>
				<label class="monthly <c:if test="${requestScope.termsForm.frequency eq 30}">active</c:if>">
					<form:radiobutton path="frequency" value="30"/><fmt:message key="global.monthly"/>
				</label>
			</li>
		</ul>

		<div id="pay_schedule">
			<br />
			<div class="page-header">
				<fmt:message key="statements_payterms.process_statements_for_company" var="statements_payterms_process_statements_for_company">
        	<fmt:param value="${requestScope.companyName}"/>
        </fmt:message>
				<h5>${statements_payterms_process_statements_for_company}</h5>
			</div>

			<div id="freq_weekly">
				<form:select  id="weekdays" path="weekday">
					<form:option value="">- <fmt:message key="statements_payterms.select_day_of_week"/> -</form:option>
					<form:options items="${requestScope.weekdays}"/>
				</form:select>
			</div>
			<div id="freq_biweekly">
				<div style="margin-bottom: 10px;">
					<label style="font-weight: normal;">
						<form:radiobutton path="biweeklyCycle" value="dayOfWeek"/> <fmt:message key="statements_payterms.specific_day_of_week"/>

						<form:select id="biweekly_weekdays" path="biweeklyWeekdays">
							<form:option value="">- <fmt:message key="statements_payterms.select_day_of_week"/> -</form:option>
							<form:options items="${requestScope.weekdays}"/>
						</form:select>
					</label>
				</div>
				<div>
					<c:out value="dayOfWeek"/>
					<c:out value="daysEachMonth"/>
					<label style="font-weight: normal;">
						<form:radiobutton path="biweeklyCycle" value="daysEachMonth"/> <fmt:message key="statements_payterms.certain_days_each_month"/>

						<form:select id="biweekly_set" path="biweeklySet">
							<form:option value="">- <fmt:message key="statements_payterms.select_set_of_days"/> -</form:option>
							<form:options items="${requestScope.biweeklySet}"/>
						</form:select>
					</label>
				</div>
			</div>
			<div id="freq_monthly">
				<form:select id="monthdays" path="monthDays">
					<form:option value="">- <fmt:message key="statements_payterms.select_day_of_month"/> -</form:option>
					<c:forEach var="index" begin="1" end="30" step="1">
						<form:option value="${index}" label="${index}"/>
					</c:forEach>
				</form:select>
			</div>
		</div>
		<br/>

		<c:if test="${not empty(requestScope.statements_configuration)}">
			<div class="alert-message">
				<p>
					<fmt:message key="statements_payterms.cycle_start"/> <strong>
					<frm:formatDate value="${requestScope.statements_configuration.startDatePaymentCycle.time}" pattern="MM/dd/yyyy"/></strong>
					<fmt:message key="statements_payterms.will_receive_first_statement_email"/> <strong>
					<frm:formatDate value="${requestScope.statements_configuration.nextStatementDate.time}" pattern="MM/dd/yyyy" /></strong>.
				</p>
			</div>
		</c:if>

		<p><fmt:message key="statements_payterms.statement_email_details"/></p>
			<span class="help-block">
				<fmt:message key="statements_payterms.statement_cycle_details"/>
			</span>
	</div>
</div>

<div class="dn" id="wizard_step_duration">
	<div class="alert alert-info">
		<h5><fmt:message key="statements_payterms.what_payment_terms"/></h5>
		<p><fmt:message key="statements_payterms.how_long"/> <strong><fmt:message key="statements_payterms.approved"/></strong> <fmt:message key="statements_payterms.can_configure_more_options"/></p>
	</div>

	<div class="alert">
		<fmt:message key="statements_payterms.durations_available_to_company" var="statements_payterms_durations_available_to_company">
    	<fmt:param value="${requestScope.companyName}"/>
    </fmt:message>
		${statements_payterms_durations_available_to_company}
	</div>
	<table id="durations">
		<tr>
			<th><fmt:message key="global.default"/></th>
			<th><fmt:message key="global.duration"/></th>
			<th><fmt:message key="global.actions"/></th>
		</tr>
		<c:forEach items="${paymentTermsDurations}" var="duration">
			<tr>
				<td class="text-center">
					<form:radiobutton path="paymentTermsDays" value="${duration.numDays}" />
					<input type="hidden" name="paymentTermDurations[]" value="${duration.numDays}" />
				</td>
				<td>
					<c:choose>
						<c:when test="${duration.numDays eq 0}">
							<fmt:message key="global.immediate"/>
						</c:when>
						<c:when test="${duration.numDays eq 1}">
            	<fmt:message key="global.one_day"/>
            </c:when>
						<c:when test="${duration.numDays eq 7}">
							<fmt:message key="statements_payterms.seven_days_best_practice"/>
						</c:when>
						<c:otherwise>
							<fmt:message key="global.multiple_days" var="global_multiple_days">
	              <fmt:param value="${duration.numDays}"/>
	            </fmt:message>
	            ${global_multiple_days}
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="javascript:void(0)" class="delete-duration tooltipped tooltipped-n" aria-label="<fmt:message key="global.delete"/>"><i class="wm-icon-trash icon-large muted" data-action="trash"></i></a>
				</td>
			</tr>
		</c:forEach>
	</table>

	<div class="clearfix">
		<button id="cta-add-option" type="button" class="button"><fmt:message key="global.add_option"/></button>
	</div>
	<div id="add-option-form-placeholder" />
</div>

<div class="dn" id="wizard_step_days">
	<div class="question clear">
		<h5><fmt:message key="statements_payterms.step_two_of_three"/></h5>
		<div class="page-header">
			<fmt:message key="statements_payterms.durations_available_to_company" var="statements_payterms_durations_available_to_company">
    		<fmt:param value="${requestScope.companyName}"/>
    	</fmt:message>
			<h5>${statements_payterms_durations_available_to_company}</h5>
		</div>
		<ul class="tac">
			<li class="delay">
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 0}">active</c:if>">0<br/><fmt:message key="global.day"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 1}">active</c:if>">1<br/><fmt:message key="global.day"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 2}">active</c:if>">2<br/><fmt:message key="global.days"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 3}">active</c:if>">3<br/><fmt:message key="global.days"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 4}">active</c:if>">4<br/><fmt:message key="global.days"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 5}">active</c:if>">5<br/><fmt:message key="global.days"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 6}">active</c:if>">6<br/><fmt:message key="global.days"/></a>
				<a class="option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 7}">active</c:if>">7<br/><fmt:message key="global.days"/></a>
				<a id="fifteenDays" class=" dn option <c:if test="${requestScope.statements_configuration.accountingProcessDays eq 15}">active</c:if>">15<br/><fmt:message key="global.days"/></a>

				<form:select id="delay" path="delay" cssClass="dn">
					<c:forEach var="index" begin="0" end="7">
						<form:option value="${index}" label="${index}"/>
					</c:forEach>
					<form:option value="15" label="15"/>
				</form:select>
			</li>
		</ul>
		<span class="help-block"><fmt:message key="statements_payterms.num_days_to_review"/></span>
	</div>
</div>

<div class="dn" id="wizard_step_method">
	<h5><fmt:message key="statements_payterms.step_three_of_three"/></h5>
	<div class="page-header">
		<h5><fmt:message key="statements_payterms.method_of_payment"/><br/>
			<small class="meta"><fmt:message key="statements_payterms.select__all_that_apply"/></small>
		</h5>
	</div>
	<ul>
		<li class="methods">
			<label for="wire" class="wire">
				<form:checkbox id="wire" path="wireTransferPaymentMethodEnabled"/> <fmt:message key="global.wire_transfer"/> <small class="meta">(<fmt:message key="statements_payterms.requires_three_days"/>)</small>
			</label>
			<label for="direct" class="direct">
				<form:checkbox id="direct" path="achPaymentMethodEnabled"/> <fmt:message key="global.direct_deposit"/> <small class="meta">(<fmt:message key="statements_payterms.requires_three_days"/>)</small>
			</label>
			<label class="cc" for="cc">
				<form:checkbox id="cc" path="creditCardPaymentMethodEnabled"/> <fmt:message key="global.credit_card"/> <small class="meta">(<fmt:message key="statements_payterms.instant_processing_fee"/>)</small>
			</label>
			<label class="check" for="check">
				<form:checkbox id="check" path="checkPaymentMethodEnabled"/> <fmt:message key="global.check"/> <small class="meta">(<fmt:message key="statements_payterms.allow_days"/>)</small>
			</label>
			<label class="prefund" for="prefund">
				<form:checkbox id="prefund" path="prefundPaymentMethodEnabled"/> <fmt:message key="statements_payterms.fund_prior_to_statement"/> <small class="meta">(<fmt:message key="statements_payterms.select_for_days_to_process"/>)</small>
			</label>
		</li>
	</ul>
</div>

<div class="dn" id="wizard_step_autopay">
	<div class="page-header">
		<h4><fmt:message key="global.auto_pay"/></h4>
	</div>

	<p>
		<fmt:message key = "statements_payterms.enable_payment_processing"/>
	</p>

	<div class="clearfix">
		<div class="input">
			<ul class="inputs-list">
				<li>
					<label>
						<form:checkbox id="auto_pay_enabled" path="autoPayEnabled"/>
						<fmt:message key="statements_payterms.enable_auto_pay_for_company" var="statements_payterms_enable_auto_pay_for_company">
              <fmt:param value="${requestScope.companyName}"/>
            </fmt:message>
						<span>${statements_payterms_enable_auto_pay_for_company}</span>
					</label>
				</li>
			</ul>
		</div>
	</div>
</div>

<div class="dn" id="wizard_step_review">
	<div class="page-header">
		<h4>Summary
			<small class="meta"><fmt:message key="statements_payterms.can_edit_payment_after_submitting"/></small>
		</h4>
	</div>
	<table class="zebra-striped">
		<tbody>
		<tr>
			<td width="60%"><fmt message key="statements_payterms.when_will_statements_be_received"/></td>
			<td><div id="wizard_question1"></div></td>
		</tr>
		<tr>
			<td><fmt:message key="statements_payterms.what_day_will_statements_be_sent"/></td>
			<td><div id="wizard_question2"></div></td>
		</tr>
		<tr>
			<td><fmt:message key="statements_payments.how_many_days_to_process"/></td>
			<td class="amount"><div id="wizard_question3"></div></td>
		</tr>
		<tr>
			<td><fmt:message key="statements_payterms.what_payment_method"/></td>
			<td><div id="wizard_question4"></div></td>
		</tr>
		<tr>
			<td><fmt:message key="global.auto_pay"/></td>
			<td><div id="wizard_question5"></div></td>
		</tr>
		<tr>
			<td><fmt:message key="global.payment_terms"/></td>
			<td>(<fmt:message key="statements_payterms.calculated_after_submitting"/>)</td>
		</tr>
		</tbody>
	</table>
</div>

<div class="wm-action-container">
	<button type="button" class="button dn" id="cta-back"><fmt:message key="global.back"/></button>
	<button type="button" class="button" id="cta-next"><fmt:message key="global.next"/></button>
</div>
</form:form>

<script id="tmpl-add-option-form" type="text/html">
	<div>
		<p><strong><fmt:message key="statements_payterms.add_duration"/></strong></p>
		<div id="option-error" class="alert hide"></div>
		<input id="cta-option" type="text" class="span1" />
		<strong>days</strong>
		<div class="wm-action-container">
			<button id="cta-cancel-add-option" type="button" class="button"><fmt:message key="global.cancel"/></button>
			<button id="cta-save-add-option" type="button" class="button"><fmt:message key = "global.save"/></button>
		</div>
	</div>
</script>

<script id="tmpl-payment-option-row" type="text/html">
	<tr>
		<td class="text-center">
			<input type="radio" name="paymentTermsDays" value="\${duration}"/>
			<input type="hidden" name="paymentTermDurations[]" value="\${duration}" />
		</td>
		<td>\${duration} day{{if duration != 1}}s{{/if}}</td>
		<td>
			<a class="delete-duration tooltipped tooltipped-n" aria-label="<fmt:message key="global.delete"/>"><i class="wm-icon-trash icon-large muted" data-action="trash"></i></a>
		</td>
	</tr>
</script>
