<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Vendor Instructions">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<ul class="tabs">
		<li>
			<a href="<c:url value="/admin/licenses/review"/>">Licenses</a>
		</li>
		<li class="active">
			<a href="<c:url value="/admin/certifications/review"/>">Certifications</a>
		</li>
		<li>
			<a href="<c:url value="/admin/insurance/review"/>">Insurance</a>
		</li>
	</ul>

	<h4>List of certification vendors and instructions.</h4>

	<div class="row">
		<div class="span12">
			<c:forEach var="v" items="${vendors}">
				<h5><c:out value="${v.name}" /></h5>
				<p><c:out value="${v.instruction.description}" /></p>
				<hr />
			</c:forEach>
		</div>
	</div>
</div>

</wm:admin>
