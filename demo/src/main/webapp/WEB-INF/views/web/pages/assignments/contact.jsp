<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Search Workers" bodyclass="page-assignment-contact" webpackScript="search">

	<c:set var="addressFilter">
		<c:choose>
			<c:when test="${not empty work and not empty work.location}">
				<c:out value="${work.location.address.city}"/>, <c:out value="${work.location.address.state}"/> <c:out value="${work.location.address.zip}"/>
			</c:when>
			<c:otherwise>
				<c:out value="${params.address}"/>
			</c:otherwise>
		</c:choose>
	</c:set>

	<script>
		var config = {
			mode: 'assignment',
			userNumber: ${currentUser.userNumber},
			existingWorkers: ${worker_numbers},
			declinedWorkers: ${declined_worker_numbers},
			appliedWorkers: ${applied_worker_numbers},
			existingVendorNumbers: ${existingVendorNumbers},
			declinedVendorNumbers: ${declinedVendorNumbers},
			pricing_type: '${work.pricing.type}',
			work_number: ${work.workNumber},
			companyName: '${wmfmt:escapeJavaScript(work.company.getName())}',
			currentUserCompanyName: '${wmfmt:escapeJavaScript(currentUser.companyName)}',
			paymentTime: '${work.configuration.paymentTermsDays}',
			pricingType: '${work.pricing.type}',
			disablePricingNegotation: ${work.configuration.disablePriceNegotiation},
			work: ${work_encoded},
			assignToFirstWorker: ${work.configuration.assignToFirstResource || isAssignToFirstToAcceptVendor},
			isDispatch: ${isDispatch},
			addressFilter: '${addressFilter}',
			addressZip: '${work.location.address.zip}',
			industries: {
				id: ${work.industry.id},
				filter_on: true
			},
			labels: {
				industries: {
					id: ${work.industry.id},
					name: '${work.industry.name}'
				}
			},
			searchType: 'workers',
			isBundle: ${isBundle},
			isInstantWorkerPool: ${instant_worker_pool},
			disableDeepLinking: true
		};
	</script>

	<c:if test="${companyIsLocked}">
		<div class="alert alert-danger tac">
			<strong>Your account is past due.</strong> As a result, your account is locked. You can only create and send assignments to internal users.
		</div>
	</c:if>

	<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

	<div class="search-filter-ui">
		<div class="search-filter-banner">
			<h1>Find Talent</h1>
		</div>
		<div class="clearfix"></div>
		<div class="search-header">
			<div class="count-overview">
				<c:if test="${work.status.code == (workStatusTypes['SENT'] || workStatusTypes['DRAFT'])}">
					<small class="pull-left">
						<c:if test="${!isDispatch}">
							<a id="contact-edit-assignment" data-assignmentId="${work.workNumber}" href="<c:url value="/assignments/edit/${work.workNumber}"/>">&laquo; Edit</a>
							<span> || </span>
						</c:if>
						<a href="<c:url value="/assignments/details/${work.workNumber}"/>"> Details &raquo;</a>
					</small>
				</c:if>

				Showing <span class="search_result_start_index">1</span>-<span class="search_result_end_index">10</span> of
				<span class="search_result_count">0</span><span id="search_industries"></span>.
				<strong><a id="clear_facets" class="submit" href="javascript:void(0);">Clear this search</a></strong>
				<span class="tooltipped tooltipped-n" aria-label="Upon clearing we'll keep the location set so you can find workers near this assignments location.">
					<i class="wm-icon-question-filled"></i>
				</span>
			</div>
			<div class="clearfix"></div>
		</div>
		<input type="hidden" name="sortby" id="sortby"/>
		<div class="search-filter-bucket"></div>
		<div class="search-filter-full-results">
			<%@ include file="/WEB-INF/views/web/partials/search/view.jsp" %>
		</div>
	</div>
</wm:app>
