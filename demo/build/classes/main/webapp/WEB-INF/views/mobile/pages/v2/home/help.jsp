<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.help" scope="request"/>

<div class="help-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Mobile Help Center" />
	</jsp:include>

	<div class="help-slides-container">
		<div id="help-navigation-list" class="grid help-navigation-list active-slide slides">
			<div class="unit whole">
				<h3>
					<div class="help-circle">
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-question.jsp"/>
					</div>
					How can we help you?
				</h3>
				<ul>
					<li>
						<a class="slide-change" href="#faq">Frequently Asked Questions &raquo;</a>
					</li>
					<li>
						<a class="slide-change" href="#contactUs">Contact Us &raquo;</a>
					</li>
				</ul>
			</div><%--unit whole--%>
		</div><%--grid--%>

		<%--section contents--%>
		<c:import url="/WEB-INF/views/mobile/partials/help/faq.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/help/contact-us.jsp"/>

		<%--FAQ--%>
		<c:import url="/WEB-INF/views/mobile/partials/help/accepting-assignments.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/help/what-is-counteroffer.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/help/how-to-attachment.jsp"/>
		<c:import url="/WEB-INF/views/mobile/partials/help/how-to-checkinout.jsp"/>

	</div><%--slides container--%>
</div><%--help-page--%>