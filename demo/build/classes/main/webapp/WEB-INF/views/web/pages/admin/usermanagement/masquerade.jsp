<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="masquerade">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div class="content">
	<div class="row-fluid">
		<div class="span8">
			<h1>Masquerade</h1>

			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}"/>
			</c:import>

			<form action="/admin/usermanagement/masquerade/start" method="GET" class="form-horizontal">
				<input type="hidden" name="user" value="${user_id}" id="user_id">
				<div class="control-group">
					<label class="control-label" for="user_fullname">User Name</label>
					<div class="controls">
						<input type="text" name="user_fullname" value="<c:out value="${user_fullname}" />" id="user_fullname"/>
						<span class="help-block">
							<span id="selected_user" class="dn"></span>
						</span>
					</div>
				</div>
				<div class="wm-action-container">
					<button type="submit" class="button">Masquerade</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(wm.pages.admin.usermanagement.masquerade);
</script>

</wm:admin>
