<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="nav wm-tabs">
	<a class="wm-tab <c:if test="${currentView eq 'invitations'}">-active</c:if>" href="/invitations">Invitations</a>
	<a class="wm-tab <c:if test="${currentView eq 'campaigns'}">-active</c:if>" href="/campaigns">Landing Pages</a>
</ul>
