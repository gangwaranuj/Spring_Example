<%@ page import="com.workmarket.configuration.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.onboarding" var="global_onboarding"/>
<wm:app
	pagetitle="${global_onboarding}"
	bodyclass="page-onboarding"
	isOnboarding="true"
	webpackScript="onboarding"
>
	<wm:logo />

	<script>
		var config = {
			profileId: ${profileId}
		};
	</script>
</wm:app>
