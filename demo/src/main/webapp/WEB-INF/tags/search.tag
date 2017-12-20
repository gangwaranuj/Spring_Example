<%@ tag description="Search" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="id" required="false" %>

<div class="search ${classlist}">
	<div class="search--submit">Search</div>
	<input id="${id}" name="${id}" class="search--input" type="text" placeholder="${not empty placeholder ? placeholder : 'Search my assignments'}" />
</div>
