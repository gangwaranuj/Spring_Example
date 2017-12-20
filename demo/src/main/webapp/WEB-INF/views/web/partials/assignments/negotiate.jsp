<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

<p>Assignments remain available to other workers during the counteroffer process.</p>

<form action='/assignments/negotiate/${work.workNumber}' id='negotiate-work' class='pricing_configuration form-horizontal' method="POST">
	<wm-csrf:csrfToken />

	<%--Request diff budget--%>
	<c:if test="${not is_employee}">
		<label>
			<c:choose>
				<c:when test="${work.configuration.disablePriceNegotiation}">
					<input type="checkbox" name="price_negotiation" id="price_negotiation" disabled="disabled"/>
					<strong>The client has chosen to disable price counteroffers</strong>
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="price_negotiation" id="price_negotiation" checked="checked"/>
					Request a different assignment budget
				</c:otherwise>
			</c:choose>
		</label>

		<c:if test="${not work.configuration.disablePriceNegotiation}">
			<div id="price_negotiation_config">
				<input type="hidden" name='pricing' value="${work.pricing.id}"/>
				<c:choose>
				<c:when test="${work.pricing.id == PricingStrategyType.INTERNAL}">
					<c:if test="${mode != 'negotiate'}">
						<div class="control-group">
							<label class="control-label">Assignment Budget</label>

							<div class="controls">
								Internal Assignment
							</div>
						</div>
					</c:if>
				</c:when>

				<c:when test="${work.pricing.id == PricingStrategyType.FLAT}">
					<div class="control-group">
						<label>&nbsp;</label>
						<div class="controls">
							<div class="span3"><strong>Current</strong></div>
							<div class="span3"><strong>Requested</strong></div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Assignment Budget</label>
						<div class="controls">
							<div class="span3">
								<fmt:formatNumber value="${work.pricing.flatPrice}" currencySymbol="$" type="currency"/>
							</div>
							<div class="span3">
								<input type="text" name='flat_price' id="${mode}flat_price" class='span2'/>
							</div>
						</div>
					</div>
				</c:when>

				<c:when test="${work.pricing.id == PricingStrategyType.PER_HOUR}">
					<div class="control-group">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<div class="span3"><strong>Current</strong></div>
							<div class="span3"><strong>Requested</strong></div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Rate</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${work.pricing.perHourPrice}" currencySymbol="$" type="currency"/> /hr</div>
							<div class="span3">
								<input type="text" name='per_hour_price' id="${mode}.per_hour_price" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Hours</label>
						<div class="controls">
							<div class="span3"><c:out value="${work.pricing.maxNumberOfHours}"/></div>
							<div class="span3">
								<input type="text" name='max_number_of_hours' id="${mode}.max_number_of_hours" class="span2"/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Assignment Budget</label>
						<div class="controls">
							<div class="span3">
								<fmt:formatNumber value="${maxSpendOfAssignment['PER_HOUR']}" currencySymbol="$" type="currency"/>
							</div>
							<div class="span3">$ <span class="spend_max">&nbsp;</span></div>
						</div>
					</div>
				</c:when>

				<c:when test="${work.pricing.id == PricingStrategyType.PER_UNIT}">
					<div class="control-group">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<div class="span3"><strong>Current</strong></div>
							<div class="span3"><strong>Requested</strong></div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Rate</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${work.pricing.perUnitPrice}" currencySymbol="$" type="currency" maxFractionDigits="3"/> (per unit)</div>
							<div class="span3">
								<input type="text" name='per_unit_price' id="${mode}per_unit_price" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Units</label>
						<div class="controls">
							<div class="span3"><c:out value="${work.pricing.maxNumberOfUnits}"/></div>
							<div class="span3">
								<input type="text" name='max_number_of_units' id="${mode}max_number_of_units" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Assignment Budget</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${maxSpendOfAssignment['PER_UNIT']}" currencySymbol="$" type="currency"/></div>
							<div class="span3">$ <span class="spend_max">&nbsp;</span></div>
						</div>
					</div>
				</c:when>

				<c:when test="${work.pricing.id == PricingStrategyType.BLENDED_PER_HOUR}">
					<div class="control-group">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<div class="span3"><strong>Current</strong></div>
							<div class="span3"><strong>Requested</strong></div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Initial Rate</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${work.pricing.initialPerHourPrice}" currencySymbol="$" type="currency"/> /hr</div>
							<div class="span3">
								<input type="text" name='initial_per_hour_price' id="${mode}.initial_per_hour_price" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Initial Hours</label>
						<div class="controls">
							<div class="span3"><c:out value="${work.pricing.initialNumberOfHours}"/></div>
							<div class="span3" style="margin-top:-9px;">
								<input type="text" name='initial_number_of_hours' id="${mode}.initial_number_of_hours" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Secondary Rate</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${work.pricing.additionalPerHourPrice}" currencySymbol="$" type="currency"/> /hr</div>
							<div class="span3">
								<input type="text" name='additional_per_hour_price' id="${mode}.additional_per_hour_price" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Secondary Hours</label>
						<div class="controls">
							<div class="span3"><c:out value="${work.pricing.maxBlendedNumberOfHours}"/></div>
							<div class="span3" style="margin-top:-9px;">
								<input type="text" name='max_blended_number_of_hours' id="${mode}.max_blended_number_of_hours" class='span2'/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Assignment Budget</label>
						<div class="controls">
							<div class="span3"><fmt:formatNumber value="${maxSpendOfAssignment['BLENDED_PER_HOUR']}" currencySymbol="$" type="currency"/></div>
							<div class="span3">
								$<span class="spend_max">&nbsp;</span>
							</div>
						</div>
					</div>
				</c:when>
				</c:choose>
				<hr/>
				<%--Additional Expenses--%>
				<div id="offer-expenses" class="control-group">
				<label class="control-label">Additional expenses</label>
				<div class="controls">
					<div class="span3"></div>
					<div class="span3">
						<input type="text" name='additional_expenses' id="${mode}additional_expenses" class='span2'/>
					</div>
				</div>
			</div>
			</div>
		</c:if>
	</c:if>
	<hr/>

	<%--Date or time request--%>
	<label>
		<input type="checkbox" name="schedule_negotiation" id="schedule_negotiation"/>
		Request a different date or time
	</label>
	<div class="dn" id="schedule_negotiation_config">
		<div class="control-group">
			<label class="control-label">Current ${work.schedule.range ? 'window' : 'time'}</label>
			<div class="controls">
				<span class="input-xlarge uneditable-input">
					<c:choose>
						<c:when test="${not empty work.schedule.from}">
							<c:choose>
								<c:when test="${work.schedule.range}">
									<c:choose>
										<c:when test="${work.schedule.through - work.schedule.from < 24 * 60 * 60 * 1000}">
											<c:set var="fromFmt" value="EEEE, MMM d, yyyy h:mma"/>
											<c:set var="throughFmt" value="h:mma z"/>
										</c:when>
										<c:otherwise>
											<c:set var="fromFmt" value="EEEE, MMM d, yyyy h:mma"/>
											<c:set var="throughFmt" value="EEEE, MMM d, yyyy h:mma z"/>
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
				</span>
			</div>
		</div>

		<div class="control-group">
			<div class="controls">
				<label class="radio">
					<input type="radio" id="new_time" name="reschedule_option" value="time" <c:if test="${not work.schedule.range}">checked="checked"</c:if> />
					Request a new <strong>time</strong>
				</label>
				<label class="radio">
					<input type="radio" id="new_window" name="reschedule_option" value="window" <c:if test="${work.schedule.range}">checked="checked"</c:if> />
					Request a new <strong>time window</strong>
				</label>
			</div>
		</div>

		<div class="control-group">
			<div class="controls">
				<p>
					<input type="text" name="from" class='span2' placeholder='Select Date'/>
					<input type="text" name="fromtime" class='span2' placeholder='Select Time'/>
				</p>

				<span class="to-date" <c:if test="${not work.schedule.range}">style="display:none;"</c:if>>
					to<br/>
					<p>
						<input type="text" name="to" class='span2' placeholder='Select Date'/>
						<input type="text" name="totime" class='span2' placeholder='Select Time'/>
					</p>
				</span>
			</div>
		</div>
	</div>
	<hr/>

	<%--Set offer expiry--%>
	<label>
		<input type="checkbox" name="offer_expiration" id="offer_expiration"/>
		Set an offer expiration date
	</label>
	<div class="dn" id="offer_expiration_config">
		<div class="control-group">
			<label class="control-label">Expiration Date</label>

			<div class="controls">
				<input type="text" name="expires_on" id='expires_on' class='span2' placeholder="Select Date"/>
				<input type="text" name="expires_on_time" id='expires_on_time' class='span2' placeholder="Select Time"/>
			</div>
		</div>
	</div>
	<hr/>

	<%--Add note--%>
	<div class="control-group">
		<label class="control-label">
			Add a note
		</label>

		<div class="controls">
			<textarea name='note' id='negotiation_note' class='input-block-level' rows='3'></textarea>
			<span class="help-block">
				<strong>Please note:</strong>
				Notes entered CANNOT be used to negotiate terms of the assignment, and are NOT binding.
			</span>
		</div>
	</div>

	<%--Form buttons--%>
	<div class="wm-action-container">
		<input type="hidden" id="isform" name="isform" value="true">
		<button type="submit" class="button">Submit Request</button>
	</div>
</form>
