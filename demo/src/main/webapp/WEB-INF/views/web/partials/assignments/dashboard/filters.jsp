<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div id="assignment-filter-logo">
	<div class="<c:if test="${currentUser.seller || currentUser.dispatcher}">dn</c:if>">
		<c:choose>
			<c:when test="${not empty companyAvatars}">
				<img class="image companylogo" id="companyLogo" src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(companyAvatars.transformedLargeAsset.uri))}" />"
					 alt="<c:out value="${currentUser.companyName}" />"/>
			</c:when>
			<c:otherwise>
				<div id="upload_preview" class="dn">
					<img id="upload_preview_img" height="40" src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(companyAvatars.transformedLargeAsset.uri))}" />" alt="Photo"/>
				</div>
				<div id="file-uploader">
					<noscript><input type="file" name="qqfile" id="qqfile"/></noscript>
				</div>
			</c:otherwise>
		</c:choose>
		<hr/>
	</div>

	<form action="/reports" method="get" id="list_filters">
		<div id="keyword_search" class="well_content">
			<h5 id="keyword_search_title"> Keyword Search </h5>
			<div id="tempVal" title="<c:out value="${keyword}" />"></div>
			<input type="text" name="keyword" class="span3 filter_suggest input-box" value="<c:out value="${keyword}" />" placeholder="Search my assignments" id="keyword" />
		</div>

		<div id="assignment-filter-sidebar" class="filter-sidebar">
			<h5 id="workDateRangeTitle">Date Filter</h5>
			<div class="bracket">
				<select name="workMilestone" id="date_filter">
					<c:forEach var="item" items="${dateRangeFilters}">
						<option value="<c:out value="${item.key}"/>"><c:out value="${item.value}"/></option>
					</c:forEach>
				</select>


				<select name="workDateRange" id="date_sub_filter">
					<c:forEach var="item" items="${dateRangeSubFilters}">
						<option value="<c:out value="${item.key}"/>"><c:out value="${item.value}"/></option>
					</c:forEach>
				</select>
			</div>
			<div id="custom-range-dates" class="assignments-date-ranges">
				<div class="assignments-date-range">
					<input type="text" name="schedule_from" class="span2" value="<fmt:formatDate value='${defaultScheduleFrom.time}' pattern='MM/dd/yyyy'/>" id="schedule_from" maxlength="10" placeholder="Date" />
					<sec:authorize access="hasFeature('time-filter')">
						<input type="text" name="time_from" class="span1" value="<fmt:formatDate value='${defaultTimeFrom.time}' pattern='h:mm aa' />" id="time_from" maxlength="8" placeholder="Time"/>
					</sec:authorize>
					to
				</div>
				<div class="assignments-date-range">
					<input type="text" name="schedule_through" class="span2" value="<fmt:formatDate value='${defaultScheduleThrough.time}' pattern='MM/dd/yyyy'/>" id="schedule_through" maxlength="10" placeholder="Date" />
					<sec:authorize access="hasFeature('time-filter')">
						<input type="text" name="time_through" class="span1" value="<fmt:formatDate value='${defaultTimeThrough.time}' pattern='h:mm aa' />" id="time_through" maxlength="8" placeholder="Time"/>
					</sec:authorize>
					<button type="button" id="apply_filters" class="button" style="padding: 0.4em;">Go</button>
				</div>
			</div>

			<input type="text" id="filterless" name="filterless" value="${filterless}" style="visibility:hidden;"/>

			<div id="advanced-filters">
				<h5>Advanced filters</h5>

				<sec:authorize access="hasAnyRole('PERMISSION_MANAGECOWORK', 'PERMISSION_REPORTCOWORK')">
					<select name="internal_owners" id="internal-owner-dropdown" multiple data-placeholder="Any Internal Owner">
						<option>Select</option>
						<c:forEach var="user" items="${users}">
							<option value="<c:out value="${user.key}"/>"><c:out value="${user.value}" /></option>
						</c:forEach>
					</select>
				</sec:authorize>

				<%--TODO: Alex - add check for dispatcher role here too--%>
				<c:if test="${currentUser.buyer && !currentUser.seller}">
					<select name="client_companies" id="client_company" multiple data-placeholder="All Clients">
						<c:forEach var="item" items="${clients}">
							<option value="<c:out value="${item.key}"/>"><c:out value="${item.value}"/></option>
						</c:forEach>
					</select>

					<select name="projects" id="project-dropdown" multiple data-placeholder="All Projects">
						<c:forEach var="item" items="${projects}">
							<option value="<c:out value="${item.key}"/>"><c:out value="${item.value}"/></option>
						</c:forEach>
					</select>
				</c:if>

				<div id="bundle-dashboard">
					<input name="bundles" id="bundles-dropdown" type="text" placeholder="All Bundles" />
				</div>

				<div id="assigned-resource-dashboard">
					<input name="assigned_resources" id="resources-dropdown" type="text" multiple placeholder="Any Assigned Worker" />
				</div>

				<div id="assigned-vendor-dashboard">
					<input name="assigned_vendors" id="vendors-dropdown" type="text" multiple placeholder="Any Assigned Vendor" />
				</div>

				<c:if test="${currentUser.seller}">
					<p>
						<input type="checkbox" name="assigned_to_me" value="true" id="assigned_to_me" />
						<label style="float:none;" for="assigned_to_me"><small>Assigned to me</small></label>
					</p>
				</c:if>

				<c:if test="${currentUser.dispatcher}">
					<p>
						<input type="checkbox" name="dispatched_by_me" value="true" id="dispatched_by_me" />
						<label style="float:none;" for="dispatched_by_me"><small>Dispatched by me</small></label>
					</p>
				</c:if>
				<p>
					<input type="checkbox" name="following" value="true" id="following" />
					<label style="display: inline;" for="following"><small>Followed by me</small></label>
				</p>
				<sec:authorize access="hasFeature('time-filter')">
					<p id="time">
						<input type="checkbox" name="include_time" value="true" id="include_time" />
						<label style="display: inline;" for="include_time"><small>Include time with date filter</small></label>
					</p>
				</sec:authorize>
			</div>
			<p>
				<small id="more-filters-text">
					<a href="javascript:void(0);" class="advanced-filters-toggle" name="advanced_filters_toggle">Show more filters</a>
					|
					<a href="javascript:void(0);" id="clear_filters" name="clear_filters">Clear filters</a>
				</small>
			</p>
			<hr/>
		</div>
	</form>
</div>

<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
	<div class="qq-uploader<c:if test="${currentUser.seller || currentUser.dispatcher}"> dn</c:if>">
		<div class="qq-upload-drop-area"><span>Drop logo here to upload</span></div>
		<a href="javascript:void(0);" id="upload-button" class="qq-upload-button">
			Upload your logo
		</a>
		<ul class="qq-upload-list"></ul>
	</div>
</script>
