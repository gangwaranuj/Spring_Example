<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Worker Services" bodyclass="page-worker-services" webpackScript="workerservices" breadcrumbSection="Worker Services" breadcrumbSectionURI="/workerservices" breadcrumbPage="Overview">

	<div class="worker-services-bucket"></div>

	<jsp:include page="/WEB-INF/views/web/partials/general/analytics/retargeting.jsp" />

	<script>
		var config = {
			email: '${currentUser.email}'
		}
	</script>

</wm:app>
