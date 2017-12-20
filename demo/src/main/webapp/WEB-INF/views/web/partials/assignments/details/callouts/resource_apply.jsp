<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="eligible" value="${eligibility.eligible}"/>
<c:set var="hidePricing" value="${currentUser.companyHidesPricing and not is_in_work_company}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>


<form:form modelAttribute="applyForm" action='/assignments/apply/${work.workNumber}' id="apply-form" method="POST" class="form-horizontal">
<wm-csrf:csrfToken />

<c:if test="${!hidePricing}">
	<c:if test="${not workResponse.workBundle and !(workResponse.viewingResource.user.laneType.value eq laneTypes['LANE_1'])}">
	<div class="resource-apply-toggle pointer">
		<strong><i class="toggler"></i>&nbsp; Optional - Propose Alternate
		<c:if test="${not is_employee}">Price and/or </c:if>Date</strong>
	</div>
	</c:if>
	<div ref="configuration" class="clearfix dn ml">
		<%-- Price Negotiation --%>
		<c:if test="${not is_employee}">
		<fieldset>
			<label class="checkbox inline">
				<c:choose>
					<c:when test="${work.configuration.disablePriceNegotiation}">
						<input type="checkbox" name="price_negotiation" id="price_negotiation" disabled="disabled"/>
						<font color="gray"><strong>The client has chosen to disable price counteroffers</strong></font>
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="price_negotiation" id="price_negotiation"/>
						Propose a new <strong>price</strong>
					</c:otherwise>
				</c:choose>
			</label>

			<c:if test="${not work.configuration.disablePriceNegotiation}">
				<div id="price_negotiation_config" class="pricing_configuration dn">

					<form:hidden path="pricing" />

					<table>
						<c:if test="${work.pricing.id ne pricingStrategyTypes.INTERNAL}">
							<tr>
								<th></th>
								<th>Current</th>
								<th>Proposed</th>
							</tr>
						</c:if>

						<c:choose>
							<c:when test="${work.pricing.id eq pricingStrategyTypes.INTERNAL}">
								<c:if test="${mode ne 'negotiate'}">
									<tr>
										<td>Assignment Budget</td>
										<td>Internal Assignment<td>
									</tr>
								</c:if>
							</c:when>

							<c:when test="${work.pricing.id eq pricingStrategyTypes.FLAT}">
								<tr>
									<td>Assignment Budget</td>
								<td><fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/></td>
										<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="flat_price" cssClass="span2 tar" />
										</div>
									</td>
								</tr>
							</c:when>

							<c:when test="${work.pricing.id eq pricingStrategyTypes.PER_HOUR}">
								<tr>
									<td>Rate <small class="meta">(per hour)</small></td>
									<td><fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="per_hour_price" cssClass="span2 tar" />
										</div>
									</td>
								</tr>
								<tr>
									<td>Hours</td>
									<td><c:out value="${work.pricing.maxNumberOfHours}"/></td>
									<td><form:input path="max_number_of_hours" cssClass="span2 tar" /></td>
								</tr>
								<tr>
									<td>Assignment Budget</td>
									<td><fmt:formatNumber value="${maxSpendOfAssignment['PER_HOUR']}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<input type="text" readonly="readonly" name="spend_max" class="span2 tar" />
										</div>
									</td>
								</tr>
							</c:when>

							<c:when test="${work.pricing.id eq pricingStrategyTypes.PER_UNIT}">
								<tr>
									<td>Rate <small class="meta">(per unit)</small></td>
									<td><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
												<form:input path="per_unit_price" cssClass="span2 tar" />
										</div>
									</td>
								</tr>
								<tr>
									<td>Units</td>
									<td><c:out value="${work.pricing.maxNumberOfUnits}"/></td>
									<td><form:input path="max_number_of_units" cssClass="span2 tar" /><td>
								</tr>
								<tr>
									<td>Assignment Budget</td>
									<td><fmt:formatNumber value="${maxSpendOfAssignment['PER_UNIT']}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<input type="text" readonly="readonly" name="spend_max" class="span2 tar" />
										</div>
									</td>
								</tr>
							</c:when>

							<c:when test="${work.pricing.id eq pricingStrategyTypes.BLENDED_PER_HOUR}">
								<tr>
									<td>Initial Rate</td>
									<td><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="initial_per_hour_price" cssClass="span2 tar" />
										</div>
									</td>
								</tr>
								<tr>
									<td>Initial Hours</td>
									<td><c:out value="${work.pricing.initialNumberOfHours}"/></td>
									<td><form:input path="initial_number_of_hours" cssClass="span2 tar" /></td>
								</tr>
								<tr>
									<td>Secondary Rate</td>
									<td><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="additional_per_hour_price" cssClass="span2 tar" />
										</div>
									</td>
								</tr>
								<tr>
									<td>Secondary Hours</td>
									<td><c:out value="${work.pricing.maxBlendedNumberOfHours}"/></td>
									<td><form:input path="max_blended_number_of_hours" cssClass="span2 tar" /></td>
								</tr>
								<tr>
									<td>Assignment Budget</td>
									<td><fmt:formatNumber value="${maxSpendOfAssignment['BLENDED_PER_HOUR']}" currencySymbol="$" type="currency"/></td>
									<td>
										<div class="input-prepend">
											<span class="add-on">$</span>
											<input type="text" readonly="readonly" name="spend_max" class="span2 tar" />
										</div>
									</td>
								</tr>
							</c:when>
						</c:choose>

						<tr id="offer-expenses" >
							<td>Additional Expenses</td>
							<td></td>
							<td>
								<div class="input-prepend">
									<span class="add-on">$</span>
									<form:input path="additional_expenses" id="${mode}additional_expenses" cssClass="span2 tar" />
								</div>
							</td>
						</tr>
					</table>
				</div>
			</c:if>
		</fieldset>
		</c:if>

		<%-- Schedule Negotiation --%>
		<fieldset>
			<label class="checkbox inline">
				<input type="checkbox" name="schedule_negotiation" id="schedule_negotiation"/>
				Propose a new <strong>date</strong> or <strong>time</strong>
			</label>

			<div id="schedule_negotiation_config" class="dn">
				<table>
					<tr>
						<td>Current ${work.schedule.range ? 'window' : 'time'}</td>
						<td>
							<c:choose>
								<c:when test="${not empty work.schedule.from}">
									<c:choose>
										<c:when test="${work.schedule.range}">
											<c:choose>
												<c:when test="${work.schedule.through - work.schedule.from < 24 * 60 * 60 * 1000}">
													<c:set var="fromFmt" value="EEEE, MMM d, yyyy h:mma" />
													<c:set var="throughFmt" value="h:mma z" />
												</c:when>
												<c:otherwise>
													<c:set var="fromFmt" value="EEEE, MMM d, yyyy h:mma" />
													<c:set var="throughFmt" value="EEEE, MMM d, yyyy h:mma z" />
												</c:otherwise>
											</c:choose>
											${wmfmt:formatMillisWithTimeZone(fromFmt, work.schedule.from, work.timeZone)} to
											${wmfmt:formatMillisWithTimeZone(throughFmt, work.schedule.through, work.timeZone)}
										</c:when>

										<c:otherwise>
											${wmfmt:formatMillisWithTimeZone("EEEE, MMM d, yyyy h:mma z", work.schedule.from, work.timeZone)}
										</c:otherwise>

									</c:choose>
								</c:when>

								<c:otherwise>Not set.</c:otherwise>
							</c:choose>
						</td>
					</tr>

					<tr>
						<td>
							<div class="inline-inputs">
								<label class="radio normal">
									<input type="radio" id="new_time" name="reschedule_option" value="time" <c:if test="${not work.schedule.range}">checked="checked"</c:if> />
									Propose a new <strong>time</strong>
								</label>
							</div>
						</td>
						<td>
							<div class="inline-inputs">
								<form:input path="from" cssClass="span2" placeholder="Select Date" />
								<form:input path="fromtime" cssClass="span2" placeholder="Select Time"/>
							</div>
						</td>
					</tr>

					<tr>
						<td>
							<div class="inline-inputs">
								<label class="radio normal">
									<input type="radio" id="new_window" name="reschedule_option" value="window" <c:if test="${work.schedule.range}">checked="checked"</c:if> />
									Propose a new <strong>time window</strong>
								</label>
							</div>
						</td>
						<td>
							<div class="inline-inputs">
								<span class="to-date <c:if test="${not work.schedule.range}">dn</c:if>">to</br>
									<form:input path="to" cssClass="span2" placeholder="Select Date"/>
									<form:input path="totime" cssClass="span2" placeholder="Select Time"/>
								</span>
							</div>
						</td>
					</tr>

				</table>
			</div>
		</fieldset>

		<%-- Expiration Configuration --%>
		<fieldset>
			<label class="checkbox inline">
				<input type="checkbox" name="offer_expiration" id="offer_expiration"/>
				Set an offer expiration date
			</label>
			<div id="offer_expiration_config" class="dn">
				<div class="control-group">
					<label class="control-label">Expiration Date</label>
					<div class="input controls">
						<form:input path="expires_on" cssClass="span2" placeholder="Select Date" />
						<form:input path="expires_on_time" cssClass="span2" placeholder="Select Time"/>
					</div>
				</div>
			</div>
		</fieldset>
	</div>

	<fieldset class="form-stacked no-top-padding">
		<div class="clearfix">
			<label><strong>Include a message with your application</strong> <small class="meta">(recommended)</small></label>
			<div class="input">
				<form:textarea path="note" cssClass="input-block-level" rows="3" />
			</div>
		</div>
	</fieldset>
</c:if>

<c:if test="${!eligible}">
	<p><strong>You are not eligible for this assignment. Please complete the eligibilty requirements first.</strong></p>
	<div class="well-b2">
		<c:import url='/WEB-INF/views/web/partials/assignments/details/eligibility.jsp'/>
	</div>
</c:if>
<c:choose>
	<c:when test="${hidePricing}">
		You do not have permission to apply for this assignment. <br>
		Your company's Team Agent must apply on your behalf.
	</c:when>
	<c:otherwise>
		<div ref="actions" class="wm-action-container">
			<input type="hidden" id="isform" name="isform" value="true">
			<c:if test="${is_invited_resource}">
				<a class="cancel button worker-decline" href="javascript:void(0);">Decline</a>
			</c:if>
			<c:choose>
				<c:when test="${hasScheduleConflicts}">
					<button class="conflict_apply button" <c:if test="${!eligible}">disabled="disabled"</c:if>>Apply for this ${workResponse.workBundle ? "Bundle" : "Assignment"}</button>
				</c:when>
				<c:otherwise>
					<button type="submit" class="button" <c:if test="${!eligible}">disabled="disabled"</c:if>>Apply for this ${workResponse.workBundle ? "Bundle" : "Assignment"}</button>
				</c:otherwise>
			</c:choose>
		</div>
	</c:otherwise>
</c:choose>

</form:form>
