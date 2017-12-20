<%@ tag description="Label" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="status" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="color" required="false" %>

<div class="wm-label -${status}" id="${id}" <c:if test="${not empty color}">style="background-color: ${color};"</c:if>><jsp:doBody /></div>
