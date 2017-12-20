<%@ tag description="Avatar" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="src" required="false" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="hash" required="false" %>

<div class="wm-avatar ${not empty type ? '-' : ''}${not empty type ? type : ''} ${classlist}">
	<c:choose>
		<c:when test="${not empty src}">
			<img src="${src}" />
		</c:when>
		<c:otherwise>
			<canvas width="100" height="100" data-jdenticon-hash="${not empty hash ? hash : ''}${not empty hash ? hash : ''}"></canvas>
		</c:otherwise>
	</c:choose>
	<i class="wm-icon-wm-filled"></i>
</div>
