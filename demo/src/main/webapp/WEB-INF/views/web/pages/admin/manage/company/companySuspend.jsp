<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<div id="company_suspend_messages" class="alert-message dn" data-alert="alert">
	<a class="close">×</a>
	<div></div>
</div>

<form action="/admin/manage/company/suspend/${id}" method="post" id="company-suspend-form">
	<wm-csrf:csrfToken />
	<input type="hidden" name="creator_user_number" value="<c:out value="${param.creator_user_number}" />" />

	<fieldset>
		<div class="clearfix">
			<label for="comment" class="required">Comment</label>
			<div class="input">
				<textarea name="comment" id="comment" class="span6"></textarea>
			</div>
		</div>
	</fieldset>

	<div class="wm-action-container">
		<button type="submit" class="button">Suspend</button>
	</div>
</form>
