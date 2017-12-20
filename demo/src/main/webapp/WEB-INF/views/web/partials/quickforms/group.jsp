<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/quickforms/group" method="post" id="create_quickform" accept-charset="utf-8">
	<wm-csrf:csrfToken />
	<jsp:include page="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<jsp:param name="containerId" value="quickform_message" />
	</jsp:include>

	<p>Enter a talent pool name and short description. Users you invite, add via search, or bring in via a campaign can be added to this talent pool.</p>

	<fieldset>
		<div class="clearfix">
			<label class="required" for="group_name">Talent Pool Name</label>
			<div class="input">
				<input type="text" id="group_name" name="name" value="" class="span7" placeholder="" />
			</div>
		</div>

		<div class="clearfix">
			<label class="required" for="group_description">Description</label>
			<div class="input">
				<textarea id="group_description" name="description" class="span7"></textarea>
			</div>
		</div>
	</fieldset>

	<div class="wm-action-container">
		<button type="submit" class="button">Create Talent Pool</button>
	</div>
</form>
