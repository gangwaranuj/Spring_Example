<%@ tag description="Settings Switch" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="classlist" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="checked" required="false" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="attributes" required="false" %>
<%@ attribute name="on" required="false" %>
<%@ attribute name="off" required="false" %>
<%@ attribute name="label" required="false" %>

<c:choose>
	<c:when test="${shouldShowMDL}">
		<label class="settings-switch ${classlist} mdl-switch mdl-js-switch mdl-js-ripple-effect" for="${id}" ${attributes}>
			<input type="checkbox" name="${name}" id="${id}" class="mdl-switch__input" value="${value}" ${checked ? 'checked' : ''}>
			<span class="mdl-switch__label">${label}</span>
		</label>
	</c:when>
	<c:otherwise>
		<label class="settings-switch ${classlist}" id="${id}" ${attributes}>
			<input type="checkbox" name="${name}" value="${value}" ${checked ? 'checked' : ''} />
			<div class="settings-switch--skin" data-on="${not empty on ? on : 'On'}" data-off="${not empty off ? off : 'Off'}">
				<div class="settings-switch--slider"></div>
			</div>
		</label>
	</c:otherwise>
</c:choose>
