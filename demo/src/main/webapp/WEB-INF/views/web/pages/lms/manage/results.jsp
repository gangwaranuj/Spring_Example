<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<h2><c:out value="${assessment.assessment.name}"/></h2>

<p>
	<strong>Survey Results:</strong>
	<c:out value="${gradingResponse.requestedAttempt.user.name.firstName}"/> <c:out value="${gradingResponse.requestedAttempt.user.name.lastName}"/>,
	<c:out value="${assessment.requestedAttempt.user.company.name}"/>
	<br />

	<strong>Date Completed:</strong>
	<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy', gradingResponse.requestedAttempt.completeOn, currentUser.timeZoneId)}" />
</p>

<c:import url="/WEB-INF/views/web/partials/lms/listall.jsp" />
