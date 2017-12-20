<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="wrapper grey">
	<div class="container">
		<div class="alert alert-success" style="margin-top: 50px;">
			<fmt:message key="public.unfollowed_assignment" var="unfollowed_assignment">
				<fmt:param value="${assignmentTitle}"/>
			</fmt:message>
			<h4 class="tac" style="color:#000;">${unfollowed_assignment}</h4>
			<br /><br />
			<p><a href="/assignments/details/<c:out value="${assignmentId}"/>"><fmt:message key="public.view_assignment_details"/></a></p>
			<p><a href="/mysettings/notifications/"><fmt:message key="public.update_notification"/></a></p>
		</div>
	</div>
</div>
