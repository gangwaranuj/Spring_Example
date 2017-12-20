<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Done">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div class="content">
	<h1 class="strong">Edit Employee</h1>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<div class="alert alert-success">Employee info was updated.</div>

	<p>Return to <a href="/admin/usermanagement/edit?id=<c:out value='${id}'/>">Edit Employee</a></p>
	<p>Return to <a href="/admin/usermanagement">Employee Management</a></p>
</div>

</wm:admin>
