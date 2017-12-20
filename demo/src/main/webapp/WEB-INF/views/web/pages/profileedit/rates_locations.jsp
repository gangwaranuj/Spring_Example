<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Rates & Location Preferences" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'rates_locations'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="myprofile.rates_locations" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content tabbable">
			<div class="inner-container">
				<div class="page-header">
					<h3>Rates &amp; Location Preferences</h3>
				</div>
				<ul class="wm-tabs">
					<li class="wm-tab -active" data-content="#rates">Rates</li>
					<li class="wm-tab" data-content="#locations">Location Preferences</li>
				</ul>

				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<form:form modelAttribute="ratesLocationsForm" path="/profile-edit/rates_locations" method="post" acceptCharset="utf-8" cssClass="form-stacked">
					<wm-csrf:csrfToken />
					<div id="rates" class="wm-tab--content -active">
						<p>Optionally include your typical hourly rates for work. This information will be displayed on your profile and may be used by clients to determine your suitability for assignments.</p>
						<div class="row">
							<div class="span6">
								<div class="clearfix">
									<form:label for="min_onsite_hourly_rate" path="minOnsiteHourlyRate">On-Site Hourly Rate</form:label>
									<span class="help-block">This is your typical hourly rate for work performed on-site, at a client location. Work of this kind may require travel, where you provide transportation, along with proper attire.</span>
									<br/>
									<div class="input">
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="minOnsiteHourlyRate" id="min_onsite_hourly_rate" cssClass="span2 tar"/>
										</div>
									</div>
								</div>
								<br/>
								<div class="clearfix">
									<form:label for="min_offsite_hourly_rate" path="minOffsiteHourlyRate">Virtual Hourly Rate</form:label>
									<span class="help-block">This is your typical hourly rate for work performed remotely, at your home, business or location of your choosing.</span>
									<br/>
									<div class="input">
										<div class="input-prepend">
											<span class="add-on">$</span>
											<form:input path="minOffsiteHourlyRate" id="min_offsite_hourly_rate" cssClass="span2 tar"/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div id="locations" class="wm-tab--content">
						<h4>Location Preferences</h4>
						<p>Select location types where you prefer to take on assignments. You must have at least one location type selected.</p>
						<fieldset>
							<div class="clearfix">
								<label>Locations</label>
								<div class="input">
									<ul class="inputs-list">
										<c:forEach var="locationType" items="${locationTypes}" varStatus="status">
											<li>
												<label for="location_types_${status.count}">
													<form:checkbox id="location_types_${status.count}" path="currentLocationTypes['${locationType.id}']"/>
													<c:out value="${locationType.description}"/>
												</label>
											</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</fieldset>

						<h4>Geographic Coverage</h4>
						<p>Set up your maximum preferred travel distance from the zip/postal code on your profile. The default is 60 miles.</p>

						<fieldset>
							<div class="clearfix">
								<label>Maximum Travel Distance</label>
								<div class="input">
									<form:input path="maxTravelDistance" id="max_travel_distance" cssClass="span2"/> miles from my postal code
								</div>
							</div>
						</fieldset>

						<h4>Excluded Postal Codes</h4>
						<p>Enter postal codes you do not service, one at a time. For example, if you live on Long Island in New York, you may want to exclude Connecticut postal codes that are within a 60 mile radius on a map.</p>

						<jsp:include page="/WEB-INF/views/web/partials/general/notices_js.jsp">
							<jsp:param name="containerId" value="postal_code_messages"/>
						</jsp:include>

						<fieldset>
							<div class="clearfix">
								<label>Exclude Postal Codes</label>
								<div class="input">
									<input type="text" id="exclude_postal_code" name="exclude_postal_code" value="" maxlength="7" class="span3"/>
									<a class="button submit" href="javascript:void(0);" id="add_postal_code">Add</a>
									<ul id="exclude_postal_code_list" class="unstyled">
										<c:forEach var="item" items="${excludedPostalCodes}">
											<li class="remove" id="postal_code_<c:out value="${item}"/>">
												<c:out value="${item}"/> - <a href="javascript:void(0);" data-value="<c:out value="${item}"/>">Remove</a>
											</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</fieldset>
					</div>
					<div class="wm-action-container">
						<button class="button">Save Changes</button>
					</div>
				</form:form>
			</div>
		</div>
	</div>

</wm:app>
