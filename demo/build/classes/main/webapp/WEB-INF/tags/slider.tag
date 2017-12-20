<%@ tag description="Slider Input" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="min" required="false" %>
<%@ attribute name="max" required="false" %>
<%@ attribute name="step" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="units" required="false" %>
<%@ attribute name="unitsPosition" required="false" %>
<%@ attribute name="title" required="false" %>

<div class="wm-slider" data-slider-value="${value} ${units}" data-slider-unit="${units}" data-slider-units-position="${unitsPosition}">
	<c:if test="${not empty title}">
		<span class="wm-slider--title">${title}</span>
	</c:if>
	<input id="${id}" class="mdl-slider mdl-js-slider" type="range" name="${name}" value="${value}" min="${min}" max="${max}" step="${step}" data-slider/>
</div>
