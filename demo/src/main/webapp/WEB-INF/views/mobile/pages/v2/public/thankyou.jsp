<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div data-role="content">
	<div class="header">
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/wm-wave.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-logo.jsp" />
	</div>

	<div class="login-form-container panel-content">
		<div id="login-form" class="public-form middle-section">
			<h3>Next Steps</h3>
			<div>
				<p>Thank you. You've completed the sign-up process and should receive a confirmation email shortly.</p>
				<p>Please check your spam folder if you don't receive an email from hi@myworkmarket.com in the next few minutes.</p>
				<p>Welcome to Work Market, we've got great work waiting for you!</p>
			</div>
		</div>
	</div>
	<div class="public-footer">
		<div class="rocket-container">
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-rocket.jsp"/>
			<small>Designed and Engineered in NYC</small>
			<div class="rocket-footer">
				<a href="/?site_preference=normal" data-ajax="false">Full Site</a> |
				<a href="/" data-ajax="false">Home</a>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).on('pageinit pageshow', 'div:jqmData(role="page"), div:jqmData(role="dialog")', function () {
		var is_uiwebview = /(iPhone|iPod|iPad).*AppleWebKit(?!.*Safari)/i.test(navigator.userAgent);
		if (is_uiwebview) {
			$(".full_site_link").hide();
		}
	});
</script>
