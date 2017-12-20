<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<div id="concern_resolve_messages" class="alert-message dn" data-alert="alert">
	<a class="close">×</a>
	<div class="content"></div>
</div>

<form action="/admin/concerns/resolve/${id}" method="post" id="concern-resolve-form" class="form-horizontal">
	<wm-csrf:csrfToken />
	<input type="hidden" name="creator_user_number" value="<c:out value="${param.creator_user_number}" />" />

	<fieldset class="controls-group">
		<label for="comment" class="required control-label">Comment</label>
		<div class="controls">
			<textarea name="comment" id="comment"></textarea>
		</div>
	</fieldset>

	<div class="wm-action-container">
		<button type="submit" class="button">Resolve</button>
	</div>
</form>
