<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />

<wm:app pagetitle="Invite: ${assessment.name}" bodyclass="page-test-search" webpackScript="search">

	<script>
		var config = {
			mode: 'assessment',
			assessment_id: ${assessment.id},
			isSurvey: ${isSurvey},
			searchType: '${preferences}'
		};
	</script>

	<div class="count-overview">
		<span class="pull-left"><a href="<c:url value="/lms/view/details/${assessment.id}"/>">&laquo; Test Details</a></span>
		${isSurvey ? 'Survey' : 'Test'}: <a href="<c:url value="/lms/view/details/${assessment.id}"/>"><strong><c:out value="${assessment.name}"/></strong></a> showing <span class="search_result_start_index">1</span>-<span class="search_result_end_index">10</span> of
		<span class="search_result_count">0</span> workers in <span id="search_industries">all industries</span>.
		<strong><a id="clear_facets" class="submit" href="javascript:void(0);">Clear this search</a></strong>
	</div>


	<div id="facets" class="sidebar search-facets">
		<form action="/search/retrieve" id="filter_form">
			<input type="hidden" name="search_type" value="PEOPLE_SEARCH_ASSESSMENT_INVITE" />
			<input type="hidden" name="assessmentId" value=${assessment.id} />
			<input type="hidden" name="sortby" id="sortby"/>

			<c:url value="/WEB-INF/views/web/partials/search/user/facets.jsp" var="inviteFacets">
				<c:param name="isSurvey" value="${isSurvey}"/>
				<c:param name="isAssessment" value="${not empty assessment.id}" />
			</c:url>
			<c:import url="${inviteFacets}"/>
		</form>
	</div>

	<%@ include file="/WEB-INF/views/web/partials/search/view.jsp" %>
</wm:app>
