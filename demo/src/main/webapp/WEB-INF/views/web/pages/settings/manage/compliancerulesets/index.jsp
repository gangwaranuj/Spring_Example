<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="compliance_rules_sets.compliance_rules" var="compliance_rules"/>
<wm:app
	pagetitle="${compliance_rules}"
	bodyclass="accountSettings"
	webpackScript="compliancerulesets"
>
	<script>
		var config = {};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

				<div class="page-header">
					<h3>${compliance_rules}</h3>
				</div>

				<div class="alert alert-info">
					<div class="row-fluid">
						<fmt:message key="compliance_rules_sets.threshold_warning"/>
					</div>
				</div>

				<div id="compliance-rule-set-form"></div>
			</div>
		</div>
	</div>

</wm:app>
