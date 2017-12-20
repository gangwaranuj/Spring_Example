<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="send_message_popup">
	<form action="/users/send_message" method="post">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="<c:out value="${user.id}"/>"/>
	<input type="hidden" name="return_to" value="<c:out value="${param.returnTo}"/>"/>

	<div class="clearfix">
		<label>Subject</label>
		<div class="input">
			<input type="text" class="span6" name="subject">
		</div>
	</div>
	<div class="clearfix">
		<label>Message</label>
		<div class="input">
			<textarea name="message" class="span6" rows="8"></textarea>
		</div>
	</div>
	<div class="wm-action-container">
		<button type="submit" class="button">Send Message</button>
	</div>

	</form>
</div>