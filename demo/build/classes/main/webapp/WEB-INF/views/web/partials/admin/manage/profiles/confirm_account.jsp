<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>
	<jsp:include page="../../../../partials/message.jsp" />
	<div class="clearfix">
		<p>
			<strong>Confirmation link for <c:out value="${user.fullName}" />:</strong><br/> <a id="confirmationLink" href="<c:out value="${url}"/>">
			<c:out value="${url}"/></a>
		</p>
	</div>
</div>