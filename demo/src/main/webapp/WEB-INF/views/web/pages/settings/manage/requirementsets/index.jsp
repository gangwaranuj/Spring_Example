<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<sec:authorize access="hasRole('PERMISSION_ACCESSMMW')" var="hasMmwSidebar"/>

<fmt:message key="requirement_sets.requirement_sets" var="requirement_sets"/>
<wm:app
	pagetitle="${requirement_sets}"
	bodyclass="page-requirement-sets"
	webpackScript="requirementsets"
>

	<sec:authorize access="hasFeature('mandatoryRequirement')">
		<c:set var="isMandatoryRequirement" value="true" scope="page"/>
	</sec:authorize>
	<sec:authorize access="!hasFeature('mandatoryRequirement')">
		<c:set var="isMandatoryRequirement" value="false" scope="page"/>
	</sec:authorize>

	<script>
		var config = {
			isMandatoryRequirement: ${isMandatoryRequirement}
		}
	</script>

	<div class="row_wide_sidebar_left">
		<c:if test="${hasMmwSidebar}">
			<div class="sidebar">
				<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
			</div>
		</c:if>

		<div class="content">
			<div class="inner-container">
				<div class="page-header clear">
					<a href="#new" class="button pull-right -primary"><fmt:message key="requirement_sets.new_requirement_set"/></a>
					<h3>${requirement_sets}</h3>
				</div>
				<p><fmt:message key="requirement_sets.filter_worker_for_assignments"/></p>
				<div id="requirement-set-form" class="dn"></div>
				<div id="requirement-sets"></div>
			</div>
			<input type="hidden" id="creator-name" name="creator-name" value="${currentUser.fullName}" />
		</div>

	</div>

	<script id="document-expires-form-tmpl" type="text/html"></script>
	<script src="//maps.google.com/maps/api/js?key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>
</wm:app>
