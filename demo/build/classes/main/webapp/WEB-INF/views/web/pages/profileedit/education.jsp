<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Education" bodyclass="accountSettings">

<div class="row_sidebar_left">
	<div class="sidebar">
		<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
	</div>

	<div class="content">
		<div class="inner-container">
		<div class="page-header">
			<h3>Education</h3>
		</div>

		<p>Import your education history from LinkedIn. This information is shown on your profile and is available to Work Market users searching for workers.</p>

		<c:import url="/WEB-INF/views/web/partials/message.jsp" />

		<c:if test="${not empty educationHistory}">
			<c:forEach var="h" items="${educationHistory}">
			<ul class="unstyled">
				<li><strong class="strong"><c:out value="${h.schoolName}"/></strong></li>
				<li><c:if test="${h.degree}"><c:out value="${h.degree}"/>,</c:if></li>
				<li><c:if test="${h.fieldOfStudy}"><c:out value="${h.fieldOfStudy}"/></c:if></li>
				<li>
				<c:choose>
				<c:when test="${h.dateFromMonth}">
					<c:out value="${wmfmt:monthName(h.dateFromMonth)}" />
					<c:out value="${h.dateFromYear}"/>
				</c:when>
				<c:otherwise>
					<c:out value="${h.dateFromYear}"/>
				</c:otherwise>
				</c:choose>
				&ndash;
				<c:choose>
				<c:when test="${empty h.dateToYear}">
					Present
				</c:when>
				<c:when test="${not empty h.dateToMonth}">
					<c:out value="${wmfmt:monthName(h.dateToMonth)}" />
					<c:out value="${h.dateToYear}"/>
				</c:when>
				<c:otherwise>
					<c:out value="${h.dateToYear}"/>
				</c:otherwise>
				</c:choose>
				</li>
			</ul>
			</c:forEach>
		</c:if>
		<p class="linkedin">
			<c:url value="/oauth/linkedin" var="oauthUri">
				<c:param name="import" value="1" />
				<c:param name="internal_callback" value="/profile-edit/education" />
			</c:url>
			<a href="${oauthUri}" class="db" style="padding-left: 20px; background: transparent url('${mediaPrefix}/images/icons/linkedin.png') no-repeat; height: 16px;">${empty linkedin ? 'Import' : 'Reimport'} your LinkedIn profile</a>
		</p>
	</div>
	</div>
</div>
</wm:app>
