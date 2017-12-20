<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<a href="javascript:void(0);" name="partslogisticsanchor"></a>
<div id="assignment-parts-logistics" class="inner-container">

	<div class="page-header">
		<h4>Parts &amp; Logistics</h4>
	</div>

	<div class="parts-body">
		<div class="control-group">
			<div class="controls">
				<label>
					<form:checkbox path="requiresParts" />
					This assignment requires parts logistics
					<span class="tooltipped tooltipped-n" aria-label="Identify where parts are coming from and tracking information.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</label>
			</div>
		</div>

		<div class="supply-parts-radio <c:if test="${not form.requiresParts}">dn</c:if>">
			<div class="control-group">
				<label class="control-label">Parts Supplier</label>
				<div class="controls">
					<label>
						<input type="radio" name="partGroup.suppliedByWorker" value="false" <c:if test="${not form.partGroup.suppliedByWorker}">checked</c:if> />
						Parts are being shipped
					</label>
					<label>
						<input type="radio" name="partGroup.suppliedByWorker" value="true"  <c:if test="${form.partGroup.suppliedByWorker}">checked</c:if> />
						Worker supplies parts
					</label>
				</div>
			</div>
		</div>

		<form:hidden path="partGroup.uuid" />

		<div class="ship-to-location-container  <c:if test="${not form.requiresParts or form.partGroup.suppliedByWorker}">dn</c:if>">

			<div class="control-group compound-field">
				<label for="ship-to-tracking-number-input" class="control-label">Tracking</label>
				<div class="controls">
					<input type="text" id="ship-to-tracking-number-input" placeholder="Enter a tracking number"/>
				</div>
				<div class="controls">
					<input type="text" class="part-name-input" placeholder="Enter a part name"/>
					<a href="#" class="tracking-number-add button">Add</a>
				</div>
			</div>

			<jsp:include page="/WEB-INF/views/web/partials/parts/part-table.jsp" />

			<div class="control-group">
				<label for="shipping-destination-type" class="control-label">Shipping Destination</label>
				<div class="controls">
					<form:select path="partGroup.shippingDestinationType" id="shipping-destination-type" items="${shipping_destinations}" />
				</div>
			</div>

			<div class="ship-to-location <c:if test="${form.partGroup.shippingDestinationType != 'PICKUP'}">dn</c:if>">
				<fieldset>
					<form:hidden path="partGroup.shipToLocation.id" />

					<div class="control-group">
						<label for="ship-to-location-name" class="control-label">Location Name</label>
						<div class="controls">
							<form:input path="partGroup.shipToLocation.name" type="text" id="ship-to-location-name" maxlength="255" placeholder="Location Name" />
						</div>
					</div>

					<div class="control-group compound-field">
						<label for="ship-to-location-address1" class="control-label required">Address</label>
						<div class="controls">
							<form:input path="partGroup.shipToLocation.address1" id="ship-to-location-address1" type="text" maxlength="255" placeholder="Address Line 1" />
						</div>
						<div class="controls">
							<form:input path="partGroup.shipToLocation.address2" id="ship-to-location-address2" type="text" maxlength="255" placeholder="Address Line 2" />
						</div>
					</div>

					<div class="control-group parts-city field-with-placeholder">
						<label for="ship-to-location-city" class="control-label required">City</label>
						<div class="controls">
							<form:input path="partGroup.shipToLocation.city" id="ship-to-location-city" type="text" maxlength="50" placeholder="City" />
						</div>
					</div>

					<div class="control-group parts-state field-with-placeholder">
						<label for="ship-to-location-state" class="control-label required">State/Province</label>
						<div class="controls">
							<form:select path="partGroup.shipToLocation.state" id="ship-to-location-state">
								<option value="">- Select -</option>
								<c:forEach var="country" items="${states}">
									<optgroup label="${country.key}">
										<c:forEach var="state" items="${country.value}">
											<form:option value="${state.value}" label="${state.key}"/>
										</c:forEach>
									</optgroup>
								</c:forEach>
							</form:select>
						</div>
					</div>

					<div class="control-group parts-postal-code field-with-placeholder">
						<label for="ship-to-location-postalcode" class="control-label required">Postal Code</label>
						<div class="controls">
							<form:input path="partGroup.shipToLocation.postalCode" id="ship-to-location-postalcode" type="text" maxlength="7" placeholder="Postal Code" />
						</div>
					</div>

					<div class="control-group parts-location-type field-with-placeholder">
						<label for="ship-to-location-type" class="control-label">Location Type</label>
						<div class="controls">
							<form:select path="partGroup.shipToLocation.location_type" id="ship-to-location-type" items="${location_types}" />
						</div>
					</div>
				</fieldset>
			</div>
		</div>

		<div class="worker-parts-notice <c:if test="${not form.partGroup.suppliedByWorker}">dn</c:if>">
			<div class="control-group">
				<div class="controls">
					The worker will be responsible for parts.
					<span class="help-block">
						If desired, a budget increase can be used to reimburse the
						worker for parts expenses.
					</span>
				</div>
			</div>
		</div>

		<div class="return-location-container <c:if test="${not form.requiresParts}">dn</c:if>">
			<div class="control-group">
				<div class="controls">
					<label>
						<form:checkbox path="partGroup.returnRequired" id="returnRequired" />
						Worker must return original parts
					</label>
				</div>
			</div>

			<div class="return-location <c:if test="${not form.partGroup.returnRequired}">dn</c:if>">

				<fieldset>
					<div class="control-group compound-field part-input">
						<label for="return-tracking-number-input" class="control-label">Tracking</label>
						<div class="controls">
							<input type="text" id="return-tracking-number-input" class="tracking-number-input" placeholder="Enter a tracking number"/>
						</div>
						<div class="controls">
							<input type="text" class="part-name-input" placeholder="Enter a part name"/>
							<a href="#" class="tracking-number-add button">Add</a>
						</div>
					</div>

					<jsp:include page="/WEB-INF/views/web/partials/parts/part-table.jsp" />

					<legend>Return Parts</legend>
					<form:hidden path="partGroup.returnToLocation.id" />

					<div class="control-group">
						<label for="return-location-name" class="control-label">Location Name</label>
						<div class="controls">
							<form:input path="partGroup.returnToLocation.name" id="return-location-name" type="text" maxlength="255" placeholder="Location Name" />
						</div>
					</div>

					<div class="control-group compound-field">
						<label for="return-location-address1" class="control-label required">Address</label>
						<div class="controls">
							<form:input path="partGroup.returnToLocation.address1" id="return-location-address1" type="text" maxlength="255" placeholder="Address Line 1" />
						</div>
						<div class="controls">
							<form:input path="partGroup.returnToLocation.address2" id="return-location-address2" type="text" maxlength="255" placeholder="Address Line 2" />
						</div>
					</div>

					<div class="control-group parts-city field-with-placeholder">
						<label for="return-location-city" class="control-label required">City</label>
						<div class="controls">
							<form:input path="partGroup.returnToLocation.city" id="return-location-city" type="text" maxlength="50" placeholder="City" />
						</div>
					</div>

					<div class="control-group parts-state field-with-placeholder">
						<label for="return-location-state" class="control-label required">State</label>
						<div class="controls">
							<form:select path="partGroup.returnToLocation.state" id="return-location-state">
								<option value="">- Select -</option>
								<c:forEach var="country" items="${states}">
									<optgroup label="${country.key}">
										<c:forEach var="state" items="${country.value}">
											<form:option value="${state.value}" label="${state.key}"/>
										</c:forEach>
									</optgroup>
								</c:forEach>
							</form:select>
						</div>
					</div>

					<div class="control-group parts-postal-code field-with-placeholder">
						<label for="return-location-postalcode" class="control-label required">Postal Code</label>
						<div class="controls">
							<form:input path="partGroup.returnToLocation.postalCode" id="return-location-postalcode" cssClass="postal-code" type="text" maxlength="7" placeholder="Postal Code" />
						</div>
					</div>

					<div class="control-group parts-location-type field-with-placeholder">
						<label for="return-location-type" class="control-label">Location Type</label>
						<div class="controls">
							<form:select path="partGroup.returnToLocation.location_type" id="return-location-type" items="${location_types}" />
						</div>
					</div>

				</fieldset>
			</div>
		</div>
	</div>

</div>
