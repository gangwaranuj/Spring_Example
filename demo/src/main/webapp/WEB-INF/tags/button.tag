<%@ tag description="Button" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="icon" required="false" %>
<%@ attribute name="disabled" required="false" %>
<%@ attribute name="tooltip" required="false" %>
<%@ attribute name="tooltipDirection" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="raised" required="false" %>
<%@ attribute name="primary" required="false" %>
<%@ attribute name="accent" required="false" %>

<c:set var="tooltipDir" value="${not empty tooltipDirection ? tooltipDirection : 'n'}" />

<c:choose>
	<c:when test="${shouldShowMDL}">
		<c:choose>
			<c:when test="${not empty href}">
				<a
					class="mdl-button mdl-js-button mdl-js-ripple-effect ${classlist} ${raised ? 'mdl-button--raised' : ''} ${primary ? 'mdl-button--primary' : ''} ${accent ? 'mdl-button--accent' : ''}"
					aria-label="${not empty tooltip ? tooltip : ''}"
					id="${id}"
					${disabled ? "disabled" : ""}
					href="${href}"
				><jsp:doBody /></a>
			</c:when>
			<c:otherwise>
				<button
					class="mdl-button mdl-js-button mdl-js-ripple-effect ${classlist} ${raised ? 'mdl-button--raised' : ''} ${primary ? 'mdl-button--primary' : ''} ${accent ? 'mdl-button--accent' : ''}"
					aria-label="${not empty tooltip ? tooltip : ''}"
					id="${id}"
					${disabled ? "disabled" : ""}
				><jsp:doBody /></button>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<button
			class="button -new ${classlist} ${not empty type ? '-' : ''}${not empty type ? type : ''} ${not empty tooltip ? 'tooltipped tooltipped-' : ''}${not empty tooltip ? tooltipDir : ''}"
			aria-label="${not empty tooltip ? tooltip : ''}"
			id="${id}"
			${disabled ? "disabled" : ""}
		>
			<c:choose>
				<c:when test="${not empty href}">
					<a class="button--content ${not empty icon ? 'wm-icon-' : ''}${not empty icon ? icon : ''}" href="${href}"><jsp:doBody /></a>
				</c:when>
				<c:otherwise>
					<div class="button--content ${not empty icon ? 'wm-icon-' : ''}${not empty icon ? icon : ''}"><jsp:doBody /></div>
				</c:otherwise>
			</c:choose>
		</button>
	</c:otherwise>
</c:choose>
