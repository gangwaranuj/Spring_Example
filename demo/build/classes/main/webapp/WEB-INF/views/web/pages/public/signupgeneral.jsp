<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="public.signup" var="public_sign_up"/>
<wm:public
	pagetitle="${public_sign_up}"
	bodyclass="page-public"
	webpackScript="public"
>

	<script>
		var config = {
			type: 'signup'
		}
	</script>

	<div id="signupgeneral" class="container">
		<div>
			<form:form action="/signup/creatework" method="POST" modelAttribute="signupForm" id="signupnup_form">
				<wm-csrf:csrfToken />
				<form:hidden path="registrationType"/>
				<form:hidden path="pictureUrl"/>
				<div class="heading">
					<c:if test="${ not empty messages.errors[0] }">
						<p class="normal" style="color:red;"><c:out value="${messages.errors[0]}" /></p>
					</c:if>
				</div>
				<div class="span7">
					<div class="forms">
						<div class="control-group">
							<c:if test="${not empty signupForm.pictureUrl}">
								<div><img src="<c:out value="${wmfmt:stripXSS(signupForm.pictureUrl)}" />" /></div>
							</c:if>
							<div class="controls">
								<form:errors path="firstName" cssClass="alert alert-error inlineError span5"/>
								<fmt:message key="global.first_name" var="first_name"/>
								<form:input path="firstName" maxlength="50" placeholder="${first_name}" id="firstName" cssClass="span5"/>
							</div>
						</div>
						<div class="control-group">
							<div class="controls">
								<form:errors path="lastName" cssClass="alert alert-error inlineError span5"/>
								<fmt:message key="global.last_name" var="last_name"/>
								<form:input path="lastName" maxlength="50" placeholder="${last_name}" id="lastName" cssClass="span5"/>
							</div>
						</div>
						<div class="control-group">
							<div class="controls">
								<form:errors path="companyName" cssClass="alert alert-error inlineError span5"/>
								<fmt:message key="global.company_name" var="company_name"/>
								<form:input path="companyName" placeholder="${company_name}" maxlength="200" id="companyName" cssClass="span5"/>
							</div>
						</div>
						<div class="control-group">
							<div class="controls">
								<form:errors path="userEmail" cssClass="alert alert-error inlineError span5"/>
								<fmt:message key="global.email" var="email"/>
								<form:input type="email" path="userEmail" placeholder="${email}" maxlength="50" id="userEmail" cssClass="span5"/>
								<span class="help-block-client"><fmt:message key="public.use_this_address"/></span>
							</div>
						</div>
						<div id="addressHolder">
							<jsp:include page="/WEB-INF/views/web/partials/public/address.jsp">
								<jsp:param name="addressForm" value="signup" />
							</jsp:include>
						</div>
						<div>
							<form:errors path="industryId" cssClass="alert alert-error inlineError span5"/>
							<form:select path="industryId" id="industryId">
								<fmt:message key="public.select_your_industry" var="select_your_industry"/>
								<form:option value="" label="${select_your_industry}"/>
								<form:options items="${industryList}" itemValue="id" itemLabel="name"/>
							</form:select>
						</div>
						<div>
							<form:select path="workPhoneInternationalCode" id="workPhoneInternationalCode" multiple="false" cssStyle="width:156px">
								<fmt:message key="public.country_code" var="country_code"/>
								<form:option value="" label="${country_code}"/>
								<fmt:message key="public.phone_number" var="phone_number"/>
								<form:options items="${callingCodesList}" placeholder="${phone_number}" itemValue="id" itemLabel="name"/>
							</form:select>
						</div>
						<div>
							<fmt:message key="public.phone_number" var="phone_number"/>
							<form:input type="tel" path="workPhone" maxlength="14" id="workPhone" placeholder="${phone_number}" cssClass="span3" alt="phone-us"/>
							<form:errors path="workPhone" cssClass="alert alert-error inlineError span5"/>
						</div>
						<div class="control-group">
							<div class="controls">
								<form:errors path="password" cssClass="alert alert-error inlineError span5" htmlEscape="false"/>
								<fmt:message key="public.secure_password" var="secure_password"/>
								<form:password path="password" placeholder="${secure_password}" maxlength="16" id="password" cssClass="span3"/>
							</div>
						</div>
						<div class="control-group">
								<form:errors path="termsAgree" cssClass="alert alert-error inlineError span5"/>
								<form:label path="termsAgree">
									<form:checkbox path="termsAgree" id="termsAgree" value="1"/>
									<small><fmt:message key="public.terms_agree"/></small>
								</form:label>
						</div>
						<div>
							<button type="submit" class="button -primary"><fmt:message key="public.create_my_account"/></button>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</wm:public>
