<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="alert" style="text-align: center;">
	<fmt:message key="masquerade.you_are_masquerading_as" /><sec:authentication property="principal.username" />
	<a href='/admin/usermanagement/masquerade/stop'><strong class="strong"><fmt:message key="masquerade.stop_masquerading" /></strong></a>
</div>
