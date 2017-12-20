<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="login-page">
	<div class="grid login-header">
		<div class="header">
			<img class="work-wave-icon-recent" src="${mediaPrefix}/images/nav-logo-mobile.png" />
		</div>
	</div>

	<div class="grid">
		<div class="unit whole">
			<form action="login" method="post" id="login_form" data-ajax="false">
				<wm-csrf:csrfToken />
				<h3>Log In</h3>
				<div id="public-message" class="brs">
					<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
					<c:if test="${param.error != null}">
						<div class="alert-message error" data-alert="alert">
							<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" escapeXml="false"/>
						</div>
					</c:if>
				</div>
				<input type="email" placeholder="Email" name="userEmail" id="email" value="<c:out value="${param.login}" />" data-role="none" class="email gap public-form text text-input-wrap" />
				<input type="password" placeholder="Password" name="password" id="password" data-role="none" class="password gap public-form text text-input-wrap" autocomplete="off" />
				<div class="whole">
					<input type="checkbox" name="_spring_security_remember_me" checked id="rememberMe" data-role="none" class="checkbox-remember public-form inline" />
					<label class="checkbox-remember" for="rememberMe">Remember Me</label>
				</div>
				<button data="ajax-false" type="submit" name="login" id="login" class="submit call-to-action-button">Log In</button>
				<hr>
				<div class="public-footer">
					<div class="rocket-container">
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-rocket.jsp"/>
						<small>Designed and Engineered in NYC</small>
						<div class="rocket-footer">
							<c:if test="${empty cookie['wm-app-platform'].value}">
								<%-- Only show full site link if we are NOT using an app --%>
								<a href="/?site_preference=normal" data-ajax="false">Full Site</a> |
							</c:if>
							<a href="/signup" data-ajax="false">Sign Up</a>
						</div>
					</div>
				</div>
			</form>
		</div><%--unit whole--%>
	</div><%--grid--%>
</div><%--login-page--%>
