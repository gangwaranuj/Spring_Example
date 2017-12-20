<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Map" bodyclass="page-dashboard-map" breadcrumbSection="Work" breadcrumbSectionURI="/assignments" breadcrumbPage="Map" webpackScript="dashboardmap">

	<script>
		var config = {
			name: 'dashboardmap'
		}
	</script>

	<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?v=3.23&key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE"></script>
	<button type="button" class="map-status-button button" value="all">All</button>
	<button type="button" class="map-status-button button" value="draft">Draft</button>
	<c:if test="${!currentUser.buyer}">
		<button type="button" class="map-status-button button" value="sent">Available</button>
	</c:if>
	<c:if test="${currentUser.buyer}">
		<button type="button" class="map-status-button button" value="sent">Sent</button>
	</c:if>
	<button type="button" class="map-status-button button" value="active">Assigned</button>
	<button type="button" class="map-status-button button" value="inprogress" >In Progress</button>
	<c:choose>
		<c:when test="${currentUser.buyer}">
			<a href="/assignments" class="pull-right button">Back to My Work</a>
		</c:when>
		<c:otherwise>
			<a href="/assignments" class="pull-right button">Back to Dashboard</a>
		</c:otherwise>
	</c:choose>
	<h3>Map</h3>

	<div>
		<input type="hidden" id="status" value="all" />
		<div class="wm-action-container">
			<input id="map-address-location" type="text" placeholder="City, State, or Zipcode" value="">
			<button type="button" class="button">
				<i class="wm-icon-location"></i> Search Assignments
			</button>
		</div>
	</div>

	<div id="map-canvas" class="map-canvas"></div>
</wm:app>
