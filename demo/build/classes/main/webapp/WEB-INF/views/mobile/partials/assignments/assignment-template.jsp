<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<script id="assignment-template" type="text/template">
	{{ if (!assignmentz.length && page == 1) { }}
		<%--if there are no assignments in this filter--%>
		<div class="empty-list-message">
			Sorry, looks like you don't have any.
			<%--if you are in the invited feed, show the message pointing you to workfeed--%>
			{{ if (status) { }}
				<div class="pointing-to-workfeed">
					<p>Check out <a href="javascript:void(0);" class="feed">Work<span class="gray-text">Feed</span>&#8482;</a> for jobs near you!</p>
				</div><%--pointing-to-workfeed--%>
			{{ } }}
		</div><%--empty list message--%>
	{{ } }}

	{{ _.each( assignmentz, function ( assignment ) { }}
	<a class="spin" href="/mobile/assignments/details/{{= assignment.id }}">
		<div class="details-link assignment_list grid">
			<div class="info unit whole">
				<h3>{{= assignment.title_short }}</h3>
				<p>{{= assignment.company }}</p>

				{{ if ((assignment.address).length > 0) { }}
				<p>{{= assignment.city + ', ' + assignment.state + ' ' + assignment.postal_code }}</p>
				{{ } else { }}
				<p>This assignment is virtual/offsite</p>
				{{ } }}

				<%--statuses--%>
				{{ if (assignment.status === '${WorkStatusType.PAID}' && (assignment.pricing_type !== 'Internal') ) { }}
				<p>Paid on {{= assignment.paid_on}} </p>
				{{ } }}

				{{ if (assignment.status === '${WorkStatusType.PAYMENT_PENDING}') { }}
				<p>Payment scheduled for {{= assignment.due_on}} </p>
				{{ } }}

				{{ if (assignment.status === '${WorkStatusType.COMPLETE}' && (assignment.payment_terms_enabled) ) { }}
				<p>Payment {{= assignment.terms_days}} days after approval</p>
				{{ } }}

			</div><%--whole--%>

			<div class="unit whole relevant-3">
				<c:choose>
					<c:when test="${isWorkerCompany}" >
						<c:if test="${!hidePricing}">
							<div class="price">
								<span class="main-value"> {{= assignment.price }} </span>
								<span class="small db"> {{= assignment.pricing_type }} </span>
							</div>
						</c:if>
					</c:when>
					<c:otherwise>
						<div class="price">
							<span class="main-value"> {{= assignment.price }} </span>
							<span class="small db"> {{= assignment.pricing_type }} </span>
						</div>
					</c:otherwise>
				</c:choose>

				<div class="schedule">
					<span class="main-value"> {{= assignment.start_date }} </span>
							<span class="small db">
								{{= assignment.start_time }}
								{{ if ( (assignment.end_date && assignment.end_time != null) && (assignment.start_date === assignment.end_date) ) { }}
									{{= assignment.end_time }}
								{{ } }}
							</span>
				</div>
				<div class="list-distance">
					{{ if (assignment.location_offsite == false) { }}
					<input class="lat" type="hidden" value="{{= assignment.latitude }}"/>
					<input class="lon" type="hidden" value="{{= assignment.longitude }}"/>
					{{ } }}
					<span class="main-value">
						{{ if (!_.isUndefined(wm.location.latitude) && assignment.location_offsite == false) { }}
						{{= Math.ceil(geo_distance(wm.location.latitude, wm.location.longitude, assignment.latitude, assignment.longitude)) }}
						{{ } else { }}
						{{= '-' }}
						{{ } }}
					</span>
					<span class="small db">miles</span>
				</div>
			</div><%--unit whole--%>
		</div><%--grid--%>
	</a><%--details link--%>
	{{ });  }}
</script>
