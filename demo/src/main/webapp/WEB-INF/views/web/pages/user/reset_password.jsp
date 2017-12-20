<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Reset Password" bodyclass="reset-password" webpackScript="resetPassword" hideHeader="true" hideFooter="true">
	<div class="credentials">
		<a href="/">
			<img
				src="${mediaPrefix}/images/work-market-logo-orange.svg"
				alt="Work Market logo"
				class="credentials__logo"
			/>
		</a>
		<sf:form modelAttribute="passwordResetForm">
			<div
				id="reset-password-messages"
				class="credentials__messages credentials__messages--hidden"
			>
				<c:import url="/WEB-INF/views/web/partials/message.jsp" />
			</div>

			<div class="mdl-card mdl-shadow--2dp credentials__container">
				<div class="mdl-card__title">
					<i class="material-icons credentials__lock-icon">
						lock_outline
					</i>
					<h4 class="mdl-card__title-text credentials__heading">
						Reset Password
					</h4>
				</div>

				<div class="mdl-card__supporting-text credentials__copy">
					<wm-csrf:csrfToken />
					<p>Reset the password for <strong class="strong"><c:out value="${user.fullName}"/></strong>.</p>
					<p><spring:message code="mysettings.password.rule"/></p>

					<div>
						<div
							class="
								mdl-textfield
								mdl-js-textfield
								mdl-textfield--floating-label
								credentials__form-field
							"
						>
							<sf:password
								class="mdl-textfield__input"
								path="passwordNew"
								id="password_new"
								maxlength="64"
							/>
							<label
								class="mdl-textfield__label"
								for="password_new"
							>
								New Password
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
							<sf:password
								class="mdl-textfield__input"
								path="passwordNewConfirm"
								id="password_new_confirm"
								maxlength="64"
							/>
							<label
								class="mdl-textfield__label"
								for="password_new_confirm"
							>
								Confirm New Password
							</label>
						</div>
					</div>
				</div>
				<div class="mdl-card__actions mdl-card--border credentials__actions">
					<button
						type="submit"
						id="submit-button"
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
						Reset Password
					</button>
				</div>
			</div>
		</sf:form>

		<p class="credentials__support">Need Help? Contact Customer Support at <strong>(212) 229 - WORK</strong></p>
	</div>
</wm:public>
