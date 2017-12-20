<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="clearfix">
	<label for="industry" class="strong">Primary Industry</label>
	<div class="input">
		<form:select path="industry" id="industry" cssClass="span4" items="${industries}"/>
	</div>
</div>