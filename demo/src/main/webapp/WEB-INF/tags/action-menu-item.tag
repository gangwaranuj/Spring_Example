<%@ tag description="Action Menu Item" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="value" required="false" %>
<%@ attribute name="divider" required="false" %>
<%@ attribute name="disabled" required="false" %>

<c:choose>
	<c:when test="${shouldShowMDL}">
		<li
			class="mdl-menu__item ${divider ? 'mdl-menu__item--full-bleed-divider' : ''}"
			${disabled ? 'disabled' : ''}
		><jsp:doBody /></li>
	</c:when>
	<c:otherwise>
		<option value="${value}"><jsp:doBody /></option>
	</c:otherwise>
</c:choose>
