<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="public.signup" var="public_sign_up"/>
<wm:public
	pagetitle="${public_sign_up}"
	bodyclass="signup"
>

	<div class="findwork-background">
		<div class="findwork-overlay">
			<div id="signup-form" class="findwork-signup-container">

				<form:form action="/signup/worker" method="POST" modelAttribute="signupForm" id="signupnup_form" cssClass="form-inline findwork-form" autocomplete="off">
					<wm-csrf:csrfToken />

					<c:choose>
						<c:when test="${param.isApply == 1}">
							<h3><fmt:message key="public.apply_for"/></h3>
							<fmt:message key="public.apply" var="action"/>
						</c:when>
						<c:otherwise>
							<h3><fmt:message key="public.signup_for"/></h3>
							<fmt:message key="public.signup" var="action"/>
						</c:otherwise>
					</c:choose>

					<div id="nameDiv" class="control-group findwork-control dn">
						<div class="controls">
							<p id="name"></p>
						</div>
					</div>

					<form:hidden path="industryId" id="industryId" value="${defaultIndustryId}"/>
					<form:hidden path="registrationType"/>
					<form:hidden path="longitude" maxlength="255" id="longitude"/>
					<form:hidden path="latitude" maxlength="255" id="latitude"/>


					<div class="control-group">
						<form:errors path="firstName" cssClass="findwork-alert"/>
						<div class="controls">
							<fmt:message key="global.first_name" var="first_name"/>
							<form:input path="firstName" maxlength="50" id="firstName" cssErrorClass="findwork-alert-input" placeholder="${first_name}"/>
						</div>
					</div>


					<div class="control-group">
						<form:errors path="lastName" cssClass="findwork-alert"/>
						<div class="controls">
							<fmt:message key="global.last_name" var="last_name"/>
							<form:input path="lastName" maxlength="50" id="lastName" cssErrorClass="findwork-alert-input" placeholder="${last_name}"/>
						</div>
					</div>

					<div class="control-group">
						<form:errors path="userEmail"  cssClass="findwork-alert"/>
						<div class="controls">
							<fmt:message key="global.email_address" var="email_address"/>
							<form:input type="email" path="userEmail" maxlength="50" id="userEmail" cssErrorClass="findwork-alert-input" placeholder="${email_address}"/>
						</div>
					</div>

					<div class="control-group">
						<form:errors path="companyName" cssClass="findwork-alert"/>
						<div class="controls">
							<fmt:message key="public.company_name" var="company_name"/>
							<form:input path="companyName" maxlength="50" id="companyName" cssErrorClass="findwork-alert-input" placeholder="${company_name}"/>
						</div>
					</div>

					<div class="control-group">
						<form:errors path="workPhone" cssClass="findwork-alert"/>
						<div class="controls">
							<fmt:message key="public.work_phone" var="work_phone"/>
							<form:input path="workPhone" maxlength="50" id="workPhone" cssErrorClass="findwork-alert-input" placeholder="${work_phone}"/>
						</div>
					</div>

					<div class="control-group">
						<jsp:include page="/WEB-INF/views/web/partials/public/address.jsp">
							<jsp:param name="addressForm" value="findwork" />
							<jsp:param name="invitation" value="true" />
						</jsp:include>
					</div>

					<div class="control-group">
						<form:errors path="password" cssClass="findwork-alert" htmlEscape="false"/>

						<div class="controls">
							<fmt:message key="public.password_requirements" var="password_requirements"/>
							<form:password path="password" maxlength="16" cssClass="blarg" id="password" cssErrorClass="findwork-alert-input" placeholder="${password_requirements}"/>
						</div>
					</div>

					<div class="control-group">
						<div class="controls terms-agree">
							<form:errors path="termsAgree" cssClass="findwork-alert-tos"/>
							<form:label path="termsAgree">
								<form:checkbox path="termsAgree" id="termsAgree" value="1" cssClass="pull-left"/>
								<span class="span5 findwork-terms"><fmt:message key="public.terms_agree"/></span>
							</form:label>
						</div>
					</div>

					<div class="control-group tac">
						<div class="controls">
							<button type="submit" class="findwork-submit" id="findwork-submit">${action}</button>
						</div>
					</div>

				</form:form>
			</div>
		</div>
	</div>

</wm:public>
