<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Profile - ${facade.companyName}" bodyclass="page-profile-company" breadcrumbSection="Profile" breadcrumbSectionURI="/profile" breadcrumbPage="Overview" webpackScript="companypage" isBootstrapDisabled="true">
	<script>
		var config = {
			companyNumber: '${facade.companyNumber}'
		}
	</script>
	<div id="wm-company-page-app"></div>
</wm:app>
