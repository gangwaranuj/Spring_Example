<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Indeed">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="container">
	<div class="inner-container">
		<h3>Indeed Maintenance</h3>
		<ul>
			<li>
				<a href="/admin/marketing/indeed/refresh">Refresh public feed XML</a>
			</li>
			<li>
				<a href="/indeed/xml">View XML</a>
			</li>
		</ul>
	</div>
</div>

</wm:admin>
