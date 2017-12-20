<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:if test="${!isDispatch and !isGroup}">
	<div class="sidebar-card" data-id="1" id="searchType">
		<h2 class="sidebar-card--title">Search Type</h2>
		<fieldset id="search-type">
			<wm:radio name="searchType" id="search-type-people" value="workers" isChecked="${param.preferences == 'workers' || empty param.preferences}">Workers</wm:radio>
			<wm:radio name="searchType" id="search-type-vendors" value="vendors" isChecked="${param.preferences == 'vendors'}">Vendors</wm:radio>
		</fieldset>
	</div>
</c:if>

<div class="sidebar-card" data-id="2" id="keywords">
	<h2 class="sidebar-card--title">Search</h2>
	<input id="keyword" name="keyword" class="search--input" type="text" placeholder="Keywords or name...">
	<button class="sidebar-card--button search-outlet" id="keyword_go">GO</button>
</div>

<%-- assessment status - for lms details page --%>
<c:if test="${param.isAssessment}">
	<div class="sidebar-card" data-id="3" id="assessmentStatus">
		<h2 class="sidebar-card--title">${param.isSurvey ? 'Survey' : 'Test'} Status</h2>
		<wm:checkbox classList="wm-checkbox--show-all" name="assessmentstatus_showall" id="assessmentstatus_showall" value="" isChecked="true">Show All</wm:checkbox>
		<wm:checkbox name="notinvitedassessment" id="notinvitedassessment" value="" badge="-">Not Invited</wm:checkbox>
		<wm:checkbox name="invitedassessment" id="invitedassessment" value="" badge="-">Invited</wm:checkbox>
		<wm:checkbox name="passedassessment" id="passedassessment" value="" badge="-">Passed</wm:checkbox>
		<c:if test="${!param.isSurvey}">
			<wm:checkbox name="failedtest" id="failedtest" value="" badge="-">Failed</wm:checkbox>
		</c:if>
	</div>
</c:if>

<%-- group status - for manage members group detail page--%>
<c:if test="${is_group_admin}">
	<div class="sidebar-card" data-id="4" id="groupStatus">
		<h2 class="sidebar-card--title">Talent Pool Status</h2>
		<wm:checkbox classList="wm-checkbox--show-all" name="groupStatusShowall" id="groupStatusShowall" value="true">Show All</wm:checkbox>
		<wm:checkbox name="member" id="member" value="true" isChecked="true" badge="-">Member</wm:checkbox>
		<wm:checkbox name="memberoverride" id="memberoverride" value="true" badge="-">Member Override</wm:checkbox>
		<wm:checkbox name="pending" id="pending" value="true" badge="-">Pending - Meets Requirements</wm:checkbox>
		<wm:checkbox name="pendingoverride" id="pendingoverride" value="true" badge="-">Pending - Requirements Not Met</wm:checkbox>
		<wm:checkbox name="invited" id="invited" value="true" badge="-">Invited</wm:checkbox>
		<wm:checkbox name="declined" id="declined" value="true" badge="-">Declined</wm:checkbox>
	</div>
</c:if>

<div class="sidebar-card" data-id="5" id="location">
	<h2 class="sidebar-card--title">Location</h2>
	<input id="address" name="address" class="search--input" type="text" placeholder="City, State or Postal Code">

	<div class="help-block">Miles to location:</div>
	<select name="radius" id="radius" class="wm-select">
		<option value="15">15</option>
		<option value="30">30</option>
		<option value="60"  ${expanded_search_radius?"":"selected=selected"}>60</option>
		<option value="100" ${expanded_search_radius?"selected=selected":""}>100</option>
		<option value="200">200</option>
		<option value="500">500</option>
		<option value="any">Any</option>
	</select>

	<div id="countries-filters">
		<div class="help-block">Countries:
			<span class="tooltipped tooltipped-n info" id="cross_border_tooltip" style="display: none;" aria-label="
				Work Market does not allow workers to cross national borders to perform work,
				unless that work has established a legal right to work in that country,
				in order to ensure all parties in a transaction operate in accordance with all laws and regulations.">?</span>
		</div>
		<select id="countries" name="countries"></select>
	</div>
	<button class="sidebar-card--button search-outlet" id="location_go">GO</button>
</div>

<div class="sidebar-card" id="groups" data-id="6">
	<h2 class="sidebar-card--title">My Talent Pools</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="group_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
	<span class="select-container"><select id="more-groups" name="group" class="more-filters"></select></span>
</div>

<div class="sidebar-card" data-id="7" id="shared-groups" style="display: none;">
	<h2 class="sidebar-card--title">Shared Talent Pools</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="shared_groups_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
</div>

<div class="sidebar-card" id="lanes" data-id="8">
	<h2 class="sidebar-card--title">Worker Type
		<small class="lane-restriction tooltipped tooltipped-n" aria-label="If you are assigning an internal assignment, dispatching an assignment or your account is locked your lanes may be limited.">
			- Why are some disabled?
		</small>
	</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="lanes_showall" id="lanes_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
</div>

<div class="sidebar-card sliders" id="ratings" data-id="9">
	<h2 class="sidebar-card--title">Quality</h2>
	<wm:slider title="Overall Satisfaction" name="slider" id="satisfactionRate" value="0" min="0" max="100" step="1" units="%" />
	<input type="hidden" name="satisfactionRate"/>
	<wm:slider title="On-Time" name="slider" id="onTimePercentage" value="0" min="0" max="100" step="1" units="%" />
	<input type="hidden" name="onTimePercentage"/>
	<wm:slider title="Deliverable On-Time" name="slider" id="deliverableOnTimePercentage" value="0" min="0" max="100" step="1" units="%" />
	<input type="hidden" name="deliverableOnTimePercentage"/>
	<hr/>
	<div id="verifications">
		<ul class="filter_items inputs-list"></ul>
	</div>
</div>

<div class="sidebar-card" id="industries" data-id="10">
	<h2 class="sidebar-card--title">Industry</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="industry_showall" id="industry_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
	<span class="select-container"><select id="more-industries" name="industry" class="more-filters"></select></span>
</div>

<div class="sidebar-card" id="assessments" data-id="11">
	<h2 class="sidebar-card--title">Tests</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="assessment_showall" id="assessment_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
	<span class="select-container"><select id="more-assessments" name="assessment" class="more-filters"></select></span>
</div>

<div class="sidebar-card" id="certifications" data-id="12">
	<h2 class="sidebar-card--title">Certifications</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="certification_showall" id="certification_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
	<span class="select-container"><select id="more-certifications" name="certification" class="more-filters"></select></span>
</div>

<div class="sidebar-card" data-id="13" id="licenses">
	<h2 class="sidebar-card--title">Licenses</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="license_showall" id="license_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
	<span class="select-container"><select id="more-licenses" name="license" class="more-filters"></select></span>
</div>

<div class="sidebar-card" id="companytypes" data-id="14">
	<h2 class="sidebar-card--title">Company Type</h2>
	<wm:checkbox classList="wm-checkbox--show-all" name="companytypes_showall" id="companytypes_showall" value="" isChecked="true">Show All</wm:checkbox>
	<ul class="filter_items inputs-list"></ul>
</div>

<div class="sidebar-card" id="hasAvatar" data-id="15">
	<h2 class="sidebar-card--title">Profile Picture</h2>
	<wm:checkbox name="avatar" id="avatar" value="true">Has Profile Picture</wm:checkbox>
</div>

<c:if test="${is_group_admin}">

	<div class="sidebar-card sliders" id="insurance">
		<h2 class="sidebar-card--title">Insurances</h2>
		<wm:checkbox name="errorsAndOmissionsToggle" id="errorsAndOmissionsToggle" value="true" />
		<wm:slider title="Errors and Omissions" name="slider" id="errorsAndOmissionsToggle" value="0" min="0" max="3000000" step="300" units="$" unitsPosition="first" />

		<wm:checkbox name="generalLiabilityToggle" id="generalLiabilityToggle" value="true" />
		<wm:slider title="General Liability" name="slider" id="generalLiabilityToggle" value="0" min="0" max="10000000" step="100000" units="$" unitsPosition="first" />

		<wm:checkbox name="workersCompToggle" id="workersCompToggle" value="true" />
		<wm:slider title="Workers Compensation" name="slider" id="workersCompToggle" value="0" min="0" max="2000000" step="1000" units="$" unitsPosition="first" />

		<wm:checkbox name="automobileToggle" id="automobileToggle" value="true" />
		<wm:slider title="Automobile" name="slider" id="automobileToggle" value="0" min="0" max="2000000" step="1000" units="$" unitsPosition="first" />

		<wm:checkbox name="contractorsCoverage" id="contractorsCoverage" value="true" />
		<wm:slider title="Contractors Insurance" name="slider" id="contractorsCoverage" value="0" min="0" max="2000000" step="1000" units="$" unitsPosition="first" />

		<wm:checkbox name="commercialGeneralLiabilityCoverage" id="commercialGeneralLiabilityCoverage" value="true" />
		<wm:slider title="Commercial General Liability" name="slider" id="commercialGeneralLiabilityCoverage" value="0" min="0" max="2000000" step="1000" units="$" unitsPosition="first" />

		<wm:checkbox name="businessLiabilityToggle" id="businessLiabilityToggle" value="true" />
		<wm:slider title="Business Liability" name="slider" id="businessLiabilityToggle" value="0" min="0" max="2000000" step="1000" units="$" unitsPosition="first" />
	</div>
</c:if>
