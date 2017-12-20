<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<form:form action="/admin/manage/users/message" commandName="messageForm" id="message-form">
<form:hidden path="userIds" />

<fieldset>
	<div class="clearfix">
		<label for="To">User Names</label>
		<div class="text">
			<c:out value="${wmfn:joinHuman(userNames, ', ', 'and')}" />
		</div>
	</div>
	<div class="clearfix">
		<label for="title">Subject</label>
		<div class="input">
			<form:input path="title" />
		</div>
	</div>
	<div class="clearfix">
		<label for="message">Message</label>
		<div class="input" >
			<form:textarea path="message" style="width: 300px; height: 300px;" />
		</div>
	</div>
</fieldset>

<div class="wm-action-container">
	<button type="button" class="button">Cancel</button>
	<button type="submit" class="button">Send</button>
</div>

</form:form>

