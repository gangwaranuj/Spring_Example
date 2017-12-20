<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Request">
	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<ul class="tabs">
			<li <c:if test="${current_type == 'screening'}">class="active"</c:if>>
				<a href="<c:url value="/admin/screening"/>">Screened Users</a>
			</li>
			<li <c:if test="${current_type == 'drug_queue'}">class="active"</c:if>>
				<a href="<c:url value="/admin/manage/screenings/drug/queue"/>">Drug Test Queue</a>
			</li>
			<li <c:if test="${current_type == 'bkgrnd_queue'}">class="active"</c:if>>
				<a href="<c:url value="/admin/manage/screenings/bkgrnd/queue"/>">Background Check Queue</a>
			</li>
		</ul>

		<h2>Drug Test Request</h2>

		<form action="<c:url value="/admin/manage/screenings/drug/request"/>" method="post" id="screeningForm">
			<wm-csrf:csrfToken />

		<fieldset>
			<div class="clearfix">
				<label for="user_number" class="required">User Number:</label>
				<div class="input">
					<input type="text" name="user_number" id="user_number" maxlength="11" autocomplete="off" value="<c:out value="${param.user_number}" />" />
					<span class="help-block">Work Market user number to whom this drug test will be attributed to.</span>
				</div>
			</div>

			<c:import url="/WEB-INF/views/web/partials/screening/screening_form.jsp">
				<c:param name="editable_name" value="${true}" />
			</c:import>
		</fieldset>

		<div class="wm-action-container">
			<a class="button" href="<c:url value="/admin/screening"/>">Cancel</a>
			<button class="button">Submit</button>
		</div>

		</form>
	</div>
</wm:admin>
