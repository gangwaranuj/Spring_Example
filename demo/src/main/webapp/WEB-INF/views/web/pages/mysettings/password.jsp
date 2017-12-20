<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wm:app pagetitle="Password Change" bodyclass="accountSettings">

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="myprofile.password" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3><fmt:message key="password.password_change" /></h3>
				</div>

				<form:form modelAttribute="passwordChangeForm" action="/mysettings/password" method="post" acceptCharset="utf-8" class="form-horizontal">
					<wm-csrf:csrfToken/>
					<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

					<p class="br"><spring:message code="mysettings.password.rule"/></p>

					<fieldset>
						<div class="clearfix control-group">
							<form:label path="currentPassword" class="control-label"><fmt:message key="password.current_password" />:</form:label>
							<div class="input controls">
								<form:password path="currentPassword" maxlength="255"/>
							</div>
						</div>

						<div class="clearfix control-group">
							<form:label path="newPassword" class="control-label"><fmt:message key="password.new_password" />:</form:label>
							<div class="input controls">
								<form:password path="newPassword" maxlength="255"/>
							</div>
						</div>

						<div class="clearfix control-group">
							<form:label path="confirmNewPassword" class="control-label"><fmt:message key="password.confirm_new_password" />:</form:label>
							<div class="input controls">
								<form:password path="confirmNewPassword" maxlength="255"/>
							</div>
						</div>
					</fieldset>

					<div class="wm-action-container">
						<button class="button"><fmt:message key="password.update_password"/></button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
	<!-- / row sidebar left -->
</wm:app>
