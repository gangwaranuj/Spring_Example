<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.welcome" scope="request"/>

<span class="landing-page">
	<div class="header">
		<img class="work-wave-icon-recent" src="${mediaPrefix}/images/nav-logo-mobile.png" />
		<button id="login" class="cta-button" data-ajax="false" href="/login">Log In</button>
	</div>

	<div class="image"></div>
	<div class="white-modal">
		<p>Find & Manage Your Onsite Freelance Workforce</p>
	</div>
	<div class="app-links">
		<a href="https://play.google.com/store/apps/details?id=com.workmarket.android">
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/android-icon.jsp"/>
		</a>
		<a href="https://itunes.apple.com/us/app/work-market/id675449493?ls=1&mt=8">
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/apple-icon.jsp"/>
		</a>

		<p>Want work? Get the app.</p>
	</div>

	<div class="bars">
		<div class="bar one"></div>
		<div class="bar two"></div>
		<div class="bar three"></div>
		<div class="bar four"></div>
	</div>

	<div class="signup-button-container">
		<button href="/signup" class="cta-button call-to-action-button" id="sign-up">Sign Up &raquo;</button>
	</div>

	<small>Need to find freelancers? Curious how it works?</small>
	<div class="learn-more">
		<a href="http://workforce-solutions.workmarket.com/mobile.html">Learn More</a>
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
</span><%--landing page--%>
