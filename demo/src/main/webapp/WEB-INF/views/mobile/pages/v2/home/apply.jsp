<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="isApply" value="${not work.configuration.assignToFirstResource}"/>
<c:set var="action" value="${isApply ? 'Propose' : 'Counteroffer'}"/>
<c:set var="pageScript" value="wm.pages.mobile.assignments.apply" scope="request" />
<c:set var="pageScriptParams" value="${work.pricing.id}, ${PricingStrategyType['FLAT']}, ${PricingStrategyType['PER_HOUR']}, ${PricingStrategyType['PER_UNIT']}, ${PricingStrategyType['BLENDED_PER_HOUR']}" scope="request" />

<div class="wrap negotiate">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Apply / Offer" />
	</jsp:include>
	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>

		<div class="unit whole">
			<form action="/mobile/assignments/${isApply ? 'apply' : 'negotiate'}/${work.workNumber}" id="form_negotiate_assignment" method="post">
			<wm-csrf:csrfToken />
				<p>
					<c:choose>
						<c:when test="${work.configuration.disablePriceNegotiation}">
							Offer an alternate date for the assignment. Note: the client has chosen to disable price counteroffers for this assignment.
						</c:when>
						<c:otherwise>
							Offer a new price and/or date for the assignment.
						</c:otherwise>
					</c:choose>
				</p>
				<c:if test="${not is_employee}">
					<c:if test="${work.pricing.id ne PricingStrategyType['INTERNAL']}">
						<c:if test="${!work.configuration.disablePriceNegotiation}">
							<div class="grid">
								<input class="checkbox" type="checkbox" name="price_negotiation" id="price_negotiation"/>
								<label for="price_negotiation">Offer a new price</label>
								<div id="price_negotiation_container" hidden>
									<div class="pricing_configuration">
										<div class="helptext" id="help_pricing"></div>
										<div class="field">
											<input type="hidden" name="pricing" value="${work.pricing.id}"/>
											<c:choose>
												<c:when test="${work.pricing.id == PricingStrategyType['FLAT']}">
													<div class="pricing_flat inner-tag">
														<div class="nine-tenths">
															<label for="flat_price">Price Offer</label>
															<input type="text" class="one-third" name="flat_price" placeholder="$(USD)" id="flat_price"/>
															<span class="sub-header">(current: <fmt:formatNumber value="${work.pricing.flatPrice}" maxFractionDigits="2" currencySymbol="$" type="currency"/>)</span>
														</div>
													</div>
												</c:when>
												<c:when test="${work.pricing.id == PricingStrategyType['PER_HOUR']}">
													<div class="pricing_hourly inner-tag">
														<div class="nine-tenths">
															<label for="per_hour_price">Hourly Rate</label>
															<input type="text" name="per_hour_price" class="one-third" id="per_hour_price" placeholder="$(USD)"/>
															<span class="sub-header">(current: <fmt:formatNumber value="${work.pricing.perHourPrice}" maxFractionDigits="2" currencySymbol="$" type="currency"/>/hr)</span>
														</div>
														<div class="nine-tenths">
															<label for="max_number_of_hours">Maximum Hours</label>
															<input type="text" name="max_number_of_hours" id="max_number_of_hours" class="one-third" placeholder="hours"/>
															<span class="sub-header">(current: ${work.pricing.maxNumberOfHours} hours)</span>
														</div>
														<div class="nine-tenths note">
															<span>Note: Final amount is dependent on hours worked.</span>
														</div>
													</div>
												</c:when>
												<c:when test="${work.pricing.id == PricingStrategyType['PER_UNIT']}">
													<div class="pricing_units inner-tag">
														<div class="nine-tenths">
															<label for="per_unit_price">Unit Rate</label>
															<input type="text" name="per_unit_price" class="one-third" id="per_unit_price" placeholder="$(USD)"/>
															<span class="sub-header">(current: <fmt:formatNumber value="${work.pricing.perUnitPrice}" maxFractionDigits="2" currencySymbol="$" type="currency"/>/unit)</span>
														</div>
														<div class="nine-tenths">
															<label for="max_number_of_units">Maximum Units</label>
															<input type="text" name="max_number_of_units" id="max_number_of_units" class="one-third" placeholder="units"/>
															<span class="sub-header">(current: ${work.pricing.maxNumberOfUnits} units)</span>
														</div>
														<div class="nine-tenths note">
															<span>Note: Final amount is dependent on amount of units processed.</span>
														</div>
													</div>
												</c:when>
												<c:when test="${work.pricing.id == PricingStrategyType['BLENDED_PER_HOUR']}">
													<div class="pricing_blended inner-tag">
														<div class="nine-tenths">
															<label for="initial_per_hour_price">Initial Hourly Rate</label>
															<input type="text" name="initial_per_hour_price" id="initial_per_hour_price" class="one-third" placeholder="$(USD)"/>
															<span class="sub-header">(current: <fmt:formatNumber value="${work.pricing.initialPerHourPrice}" maxFractionDigits="2" currencySymbol="$" type="currency"/>/hr)</span>
														</div>
														<div class="nine-tenths">
															<label for="initial_number_of_hours">Max Initial Hours</label>
															<input type="text" name="initial_number_of_hours" id="initial_number_of_hours" class="one-third" placeholder="hours"/>
															<span class="sub-header">(current: ${work.pricing.initialNumberOfHours} hours)</span>
														</div>
														<div class="nine-tenths">
															<label for="additional_per_hour_price">Additional Hour Rate</label>
															<input type="text" name="additional_per_hour_price" id="additional_per_hour_price" class="one-third" placeholder="$(USD)"/>
															<span class="sub-header">(current: <fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" maxFractionDigits="2" currencySymbol="$" type="currency"/>/hr)</span>
														</div>
														<div class="nine-tenths">
															<label for="max_blended_number_of_hours">Max Additional Hours</label>
															<input type="text" name="max_blended_number_of_hours" id="max_blended_number_of_hours" class="one-third" placeholder="hours"/>
															<span class="sub-header">(current: ${work.pricing.maxBlendedNumberOfHours} hours)</span>
														</div>
														<div class="nine-tenths note">
															<span>Note: Final amount is dependent on hours worked.</span>
														</div>
													</div>
												</c:when>
											</c:choose>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</c:if>
				</c:if>
				<div class="grid">
					<input class="checkbox" type="checkbox" name="schedule_negotiation" id="schedule_negotiation"/>
					<label for="schedule_negotiation">Offer a new date/time</label>
					<div id="schedule_negotiation_container" class="nine-tenths" hidden>
						<input class="ratio" type="radio" name="scheduling" value="0" id="scheduling1" <c:if test="${not work.schedule.range}">checked="checked"</c:if> />
						<label for="scheduling1" class="header-label">At a specific time</label>
						<input class="ratio" type="radio" name="scheduling" value="1" id="scheduling2" <c:if test="${work.schedule.range}">checked="checked"</c:if> />
						<label for="scheduling2" class="header-label">During a time window</label>
						<div id="fixed_schedule_container" class="inner-tag" <c:if test="${work.schedule.range}">hidden</c:if>>
							<label for="from" class="header-label">From:</label>
							<div class="half">
								<input name="from" id="from" class="pickadate" placeholder="date" readonly/>
							</div>
							<div class="half">
								<input name="fromtime" id="fromtime" class="timepicker" placeholder="time" readonly/>
							</div>
						</div>
						<div id="variable_schedule_container" <c:if test="${not work.schedule.range}">hidden</c:if>>
							<div class="inner-tag">
								<label for="from2" class="header-label">From:</label>
								<div class="half">
									<input name="variable_from" id="from2" class="pickadate" placeholder="date" readonly/>
								</div>
								<div class="half">
									<input name="variable_fromtime" id="fromtime2" class="timepicker" placeholder="time" readonly/>
								</div>
							</div>
							<div class="inner-tag">
								<label for="to" class="header-label">To:</label>
								<div class="half">
									<input name="to" id="to" class="pickadate" placeholder="date" readonly/>
								</div>
								<div class="half">
									<input name="totime" id="totime" class="timepicker" placeholder="time" readonly/>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="grid">
					<input class="checkbox" type="checkbox" name="expire_negotiation" id="expire_negotiation"/>
					<label for="expire_negotiation">Set an expiration date</label>
					<div id="expire_negotiation_container" class="inner-tag nine-tenths" hidden>
						<label for="expires_on">I need an answer by:</label>
						<div class="half">
							<input name="expires_on" id="expires_on" class="pickadate" placeholder="date" readonly/>
						</div>
						<div class="half">
							<input name="expires_on_time" id="expires_on_time" class="timepicker" placeholder="time" readonly/>
						</div>
					</div>
				</div>
				<div class="grid">
					<label for="note">
						<c:choose>
							<c:when test="${isApply}">Include a message with your application for this assignment
								<small>(recommended)</small>
							</c:when>
							<c:otherwise>Note:</c:otherwise>
						</c:choose>
					</label>
					<textarea name="note" id="negotiation_note"></textarea>
				</div>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/buyer_scorecard_warning.jsp"/>
				<input type="hidden" id="isform" name="isform" value="true">
				<input id="submit-${isApply ? 'apply' : 'counteroffer'}" type="submit" name="submit" value="${isApply ? 'Apply' : 'Counteroffer'}" />
			</form>
		</div>
	</div>
</div>
