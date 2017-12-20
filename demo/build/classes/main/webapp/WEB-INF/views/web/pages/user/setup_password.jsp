<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Confirm Account" bodyclass="page-public">
	<div class="sidebar-card">
		<h2 class="sidebar-card--title">New Account Setup</h2>

		<form:form modelAttribute="passwordSetupForm">

			<p>Your Work Market account with <strong><c:out value="${company.effectiveName}"/></strong> is now registered.</p>
			<p><spring:message code="mysettings.password.rule"/></p>

			<c:import url="/WEB-INF/views/web/partials/message.jsp" />

			<div>
				<label>Password</label>
				<div>
				<form:password path="passwordNew" id="password_new" maxlength="64" />
				</div>
			</div>
			<div>
				<label>Confirm Password</label>
				<div>
					<form:password path="passwordNewConfirm" id="password_new_confirm" maxlength="64" />
				</div>
			</div>
			<div>
				<ul class="inputs-list unstyled">
					<li>
						<label>
							<form:checkbox path="termsAgree" value="1" />
							<span>I have read and agree to the <a href="/tos">Terms of Use Agreement</a></span>
						</label>
					</li>
				</ul>
			</div>

			<div class="wm-action-container">
				<button type="submit" class="button -primary">Submit</button>
			</div>
		</form:form>
	</div>

</wm:public>
