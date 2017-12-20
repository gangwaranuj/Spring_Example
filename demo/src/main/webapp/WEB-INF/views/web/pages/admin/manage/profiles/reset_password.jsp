<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <jsp:include page="../../../../partials/message.jsp" />
	<a href="${requestScope.url}" style="word-wrap: break-word"><c:out value="${requestScope.url}"/></a>
</div>