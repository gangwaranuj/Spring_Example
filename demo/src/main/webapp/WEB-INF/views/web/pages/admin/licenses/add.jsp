<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Add">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h3>Add New License</h3>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form action="/admin/licenses/add" method="post" id="form_licensesadd" accept-charset="utf-8">
		<wm-csrf:csrfToken />

		<div class="clearfix">
			<label for="state">State</label>
			<div class="input">
				<select id="state" name="state">
					<option value="">- Select -</option>
					<c:forEach items="${states}" var="s">
						<option value="${s.id}"><c:out value="${s.name}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="clearfix">
			<label for="name">License Name</label>
			<div class="input"><input type="text" name="name" id="name" maxlength="255"/></div>
		</div>

		<div class="wm-action-container">
			<a class="button" href="<c:url value="/admin/licenses/review"/>">Cancel</a>
			<input type="submit" value="Save" class="button" />
		</div>
	</form>
</div>

</wm:admin>
