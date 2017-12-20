<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="public.login" var="public_login"/>
<wm:public
	pagetitle="${public_login}"
	bodyclass="login"
	webpackScript="login"
	hideHeader="true"
	hideFooter="true"
>
	<a href="/">
		<img
			src="${mediaPrefix}/images/work-market-logo-orange.svg"
			alt="Work Market logo"
			class="credentials__logo"
		/>
	</a>

	<div class="credentials">
		<div id="login-form" style="display: none;">
			<div
				id="login-messages"
				class="credentials__messages credentials__messages--hidden"
			>
				<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

				<c:if test="${not empty requestScope['javax.servlet.error.message']}">
					<div class="alert alert-error">
						${requestScope['javax.servlet.error.message']}
					</div>
				</c:if>

				<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION.message}">
					<div class="alert alert-error">
						<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" escapeXml="false"/>
					</div>
					<c:choose>
						<c:when test="${fn:contains(SPRING_SECURITY_LAST_EXCEPTION['class'].name,'LinkedInUserNotFoundException') && SPRING_SECURITY_LAST_EXCEPTION.linkedInResult != null}">
							<input type="hidden" name="linkedInId" value="${SPRING_SECURITY_LAST_EXCEPTION.linkedInResult.linkedInId}"/>
						</c:when>
						<c:when test="${fn:contains(SPRING_SECURITY_LAST_EXCEPTION['class'].name,'SocialUserNotFoundException') && SPRING_SECURITY_LAST_EXCEPTION.socialId != null}">
							<input type="hidden" name="socialId" value="${SPRING_SECURITY_LAST_EXCEPTION.socialId}"/>
						</c:when>
					</c:choose>
				</c:if>
			</div>

			<div class="mdl-card mdl-shadow--2dp credentials__container">
				<div class="mdl-card__title">
					<i class="material-icons credentials__lock-icon">
						lock_outline
					</i>
					<h4 class="mdl-card__title-text credentials__heading">
						<fmt:message key="public.login" />
					</h4>
				</div>

				<form id="page_form">
					<div class="mdl-card__supporting-text credentials__copy">
						<div>
							<div
								class="
									mdl-textfield
									mdl-js-textfield
									mdl-textfield--floating-label
									credentials__form-field
								"
							>
								<input
									class="mdl-textfield__input"
									type="text"
									name="userEmail"
									id="login-email"
									value="<c:out value="${param.login}" />"
									maxlength="80"
								/>
								<label
									class="mdl-textfield__label"
									for="login-email"
								>
									<fmt:message key="global.email_address" />
								</label>
							</div>
						</div>
						<div>
							<div
								class="
									mdl-textfield
									mdl-js-textfield
									mdl-textfield--floating-label
									credentials__form-field
								"
							>
								<input
									class="mdl-textfield__input"
									type="password"
									name="password"
									id="login-password"
									maxlength="64"
									autocomplete="off"
								/>
								<label
									class="mdl-textfield__label"
									for="login-password"
								>
									<fmt:message key="public.password" />
								</label>
							</div>
						</div>
						<c:if test="${requestScope['googleRecaptchaEnabled']}">
							<div id="recaptcha" class="g-recaptcha" data-callback="onVerifyRecaptcha" data-sitekey="${googleRecaptchaSiteKey}"></div>
						</c:if>
					</div>

					<div class="mdl-card__actions mdl-card--border credentials__actions">
						<label
							class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect credentials__remember-me"
							for="rememberMe"
						>
							<input
								id="rememberMe"
								class="mdl-checkbox__input"
								type="checkbox"
								name="_spring_security_remember_me"
							/>
							<span class="mdl-checkbox__label"><fmt:message key="public.remember_me" /></span>
						</label>
						<button
							type="submit"
							id="login_page_button"
							class="
								mdl-button
								mdl-button--primary
								mdl-js-button
								mdl-button--raised
								mdl-js-ripple-effect
								credentials__submit-button
								credentials__submit-button--enabled
							"
						>
							<fmt:message key="public.login" />
						</button>
					</div>
				</form>
			</div>

			<c:if test="${hasLocaleFeature}">
				<div class="credentials__options">
					<a id="reset-password" href="/login#reset"><fmt:message key="public.i_forgot_my_password" /></a>
					<div id="language-selector"></div>
				</div>
			</c:if>

			<c:if test="${!hasLocaleFeature}">
				<p class="credentials__support">
					<a id="reset-password" href="/login#reset"><fmt:message key="public.i_forgot_my_password" /></a>
				</p>
			</c:if>
		</div>

		<div id="reset-form" style="display: none;">
			<div
				id="reset-messages"
				class="credentials__messages credentials__messages--hidden"
			>
				<c:import url="/WEB-INF/views/web/partials/message.jsp"/>
				<c:if test="${param.error != null}">
					<div>
						<c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" escapeXml="false"/>
					</div>
				</c:if>
			</div>

			<div class="mdl-card mdl-shadow--2dp credentials__container">
				<div class="mdl-card__title">
					<i class="material-icons credentials__lock-icon">
						lock_outline
					</i>
					<h4 class="mdl-card__title-text credentials__heading">
						<fmt:message key="public.reset_your_password" />
					</h4>
				</div>

				<sf:form action="/user/send_forgot_password" method="POST" modelAttribute="forgotUserPassword">
					<div class="mdl-card__supporting-text credentials__copy">
						<wm-csrf:csrfToken />

						<div>
							<div
								class="
									mdl-textfield
									mdl-js-textfield
									mdl-textfield--floating-label
									credentials__form-field
								"
							>
								<sf:input
									class="mdl-textfield__input"
									path="userEmail"
									maxlength="50"
									id="email"
									cssErrorClass="fieldError"
								/>
								<sf:errors path="userEmail" cssClass="inlineError"/>
								<label
									class="mdl-textfield__label"
									for="email"
								>
									<fmt:message key="global.email_address" />
								</label>
							</div>
						</div>
					</div>
					<div class="mdl-card__actions mdl-card--border credentials__actions">
						<button
							type="submit"
							id="reset-submit"
							class="
								mdl-button
								mdl-button--primary
								mdl-js-button
								mdl-button--raised
								mdl-js-ripple-effect
								credentials__submit-button
								credentials__submit-button--enabled
							"
						>
							<fmt:message key="public.reset_password" />
						</button>
					</div>
				</sf:form>
			</div>

			<div class="credentials__support">
				<span><fmt:message key="public.got_your_password" /></span>
				<a href="/login"><fmt:message key="public.login" /></a>
			</div>
		</div>
	</div>
</wm:public>
