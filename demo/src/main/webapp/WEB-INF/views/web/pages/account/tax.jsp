<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wm:app pagetitle="Tax Information" bodyclass="accountSettings" webpackScript="tax">

	<script>
		var config = ${contextJson};
	</script>

	<div class="row_wide_sidebar_left">

		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div id="tax-messages"></div>
				<div class="page-header-link span3 pull-right"></div>

				<div class="page-header">
					<h3><fmt:message key="account.tax_information" /></h3>
				</div>

				<form action="/" id="tax-form" method="POST"></form>
				<form action="/" id="tax-sign-form" method="POST"></form>
				<div id="tax-view-form"></div>
			</div>
		</div>
	</div>

	<c:import url="/WEB-INF/views/web/partials/account/tax/edit.jsp"/>
	<c:import url="/WEB-INF/views/web/partials/account/tax/sign.jsp"/>
	<c:import url="/WEB-INF/views/web/partials/account/tax/view.jsp"/>
</wm:app>
