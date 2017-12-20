<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="error-page">
	<c:import url="/WEB-INF/views/mobile/partials/nav.jsp"/>

	<div id="public-message">
		<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
	</div>
	<div class="error-content">
		<h2>Sorry, we couldn't find that page.</h2>
		<p>Here are some other links that might help: </p>
		<ul>
			<li>
				<a class="spin back-button" href="javascript: history.go(-1);">
					Go Back
				</a>
			</li>
			<li>
				<a class="spin home-button" href="/mobile">
					Go to Home
				</a>
			</li>
			<li>
				<a class="spin help-button" href="/mobile/help">
					Mobile Help Center
				</a>
			</li>
		</ul>
	</div><%--error content--%>
</div><%--error page--%>