<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<wm:admin pagetitle="Features" bodyclass="manage-company" webpackScript="admin">

	<script>
		var config = {
			mode: 'features'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>

		<div class="content">
			<h1 class="name"><c:out value="${requestScope.company.name}"/></h1>

			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/tabs.jsp" />

			<div class="page-header">
				<h3>Plans</h3>
			</div>
			<ul class="unstyled venues" data-company-id="${company.id}">
				<c:forEach items="${venues}" var="venue">
					<c:if test="${venue.systemPlan}">
						<li>
							<label>
								<input
									type="checkbox"
									name="venues"
									value="${venue}"
									<c:if test="${fn:contains(admittedVenues, venue)}"> checked</c:if>
									<sec:authorize access="!hasAnyRole('ROLE_WM_ADMIN')"> disabled</sec:authorize>
								/>
									${venue.displayName}
								<span class="help-block">${venue.description}</span>
							</label>
						</li>
					</c:if>
				</c:forEach>
			</ul>

			<div class="page-header" style="${hasInternalBetaFeatures ? 'display:block' :  'display:none'}">
				<h3>Internal Beta Features</h3>
			</div>
			<ul class="unstyled venues" data-company-id="${company.id}">
				<c:forEach items="${venues}" var="venue">
					<c:if test="${venue.internalBetaFeature}">
						<li>
							<label>
								<input
									type="checkbox"
									name="venues"
									value="${venue}"
									<c:if test="${fn:contains(admittedVenues, venue)}"> checked</c:if>
									<sec:authorize access="!hasAnyRole('ROLE_WM_ADMIN')"> disabled</sec:authorize>
								/>
									${venue.displayName}
								<span class="help-block">${venue.description}</span>
							</label>
						</li>
					</c:if>
				</c:forEach>
			</ul>

			<div class="page-header" style="${hasOpenSignUpBetaFeatures ? 'display:block' :  'display:none'}">
				<h3>Open Signup Beta Features</h3>
			</div>
			<ul class="unstyled venues" data-company-id="${company.id}">
				<c:forEach items="${venues}" var="venue">
					<c:if test="${venue.openSignUpBetaFeature}">
						<li>
							<label>
								<input
									type="checkbox"
									name="venues"
									value="${venue}"
									<c:if test="${fn:contains(admittedVenues, venue)}"> checked</c:if>
									<sec:authorize access="!hasAnyRole('ROLE_WM_ADMIN')"> disabled</sec:authorize>
								/>
									${venue.displayName}
								<span class="help-block">${venue.description}</span>
							</label>
						</li>
					</c:if>
				</c:forEach>
			</ul>

			<div class="page-header">
				<h3>Premium Features</h3>
			</div>
			<ul class="unstyled venues" data-company-id="${company.id}">
				<c:forEach items="${venues}" var="venue">
					<c:if test="${venue.feature}">
						<li>
							<label>
								<input
									type="checkbox"
									name="venues"
									value="${venue}"
									<c:if test="${fn:contains(admittedVenues, venue)}"> checked</c:if>
									<sec:authorize access="!hasAnyRole('ROLE_WM_ADMIN')"> disabled</sec:authorize>
								/>
									${venue.displayName}
								<span class="help-block">${venue.description}</span>
							</label>
						</li>
					</c:if>
				</c:forEach>
			</ul>

		</div>
	</div>

</wm:admin>
