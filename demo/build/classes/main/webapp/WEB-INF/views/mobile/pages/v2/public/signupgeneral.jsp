<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.public.signupgeneral" scope="request"/>

<script src="//maps.google.com/maps/api/js?key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>

<div id="signupgeneral" class="signup-page" data-role="content">
		<div class="panels">
			<div id="login-form" class="panel public-form">
				<div class="left-public">
					<form:form action="${(signupForm.registrationType == 'managelabor') ? '/signup/creatework' : '/findwork'}" method="POST" modelAttribute="signupForm" id="signupnup_form" cssClass="form-horizontal">
						<wm-csrf:csrfToken />

						<form:hidden path="industryId" id="industryId" value="${defaultIndustryId}" />
						<form:hidden path="registrationType"/>
						<form:hidden path="pictureUrl"/>
						<div class="content">
							<div class="sign-up-header">
								<h3>Create a free account to ${(signupForm.registrationType == 'managelabor') ? 'manage' : 'find'} work</h3>
								<c:choose>
									<c:when test="${signupForm.registrationType == 'managelabor'}">
										<div class="signup-description">
											<div>Get started today, no credit card required.</div>
											<div><small>* Access a marketplace of qualified talent *</small></div>
											<div><small>* Powerful tools to manage high-volume work *</small></div>
											<div><small>* Deliver on your business goals *</small></div>
											<p><small>Looking to find work with a <a href="/signup/creatework" class="btn btn-large btn-success brs">Freelancer Account?</a></small></p>
										</div>
									</c:when>
									<c:otherwise>
										<div class="signup-description">
											<div>Make money from high-quality freelance and contract jobs tailored to your skills.</div>
											<div><small>* Receive rewarding work assignments *</small></div>
											<div><small>* Build relationships with clients *</small></div>
											<p><small>Looking to create a <a href="/signup/creatework" class="btn btn-large btn-success brs">Client Account?</a></small></p>
										</div>
									</c:otherwise>
								</c:choose>
								<p class="normal errorMessage"><c:out value="${messages.errors[0]}" /></p>
							</div>
							<div class="row">
								<div class="span7">
									<div class="control-group">
										<c:if test="${not empty signupForm.pictureUrl}">
											<div><img src="<c:out value="${wmfmt:stripXSS(signupForm.pictureUrl)}" />"></div>
										</c:if>
										<div class="controls">
											<form:errors path="firstName" cssClass="errorMessage span5"/>
											<form:input path="firstName" maxlength="50" placeholder="First Name" id="firstName" cssClass="span5"/>
										</div>
									</div>

									<div class="control-group">
										<div class="controls">
											<form:errors path="lastName" cssClass="errorMessage span5"/>
											<form:input path="lastName" placeholder="Last Name" maxlength="50" id="lastName" cssClass="span5"/>
										</div>
									</div>

									<div class="control-group">
										<div class="controls">
											<form:errors path="userEmail" cssClass="errorMessage span5"/>
											<form:input type="email" path="userEmail" placeholder="Email" maxlength="50" id="userEmail" cssClass="span5"/>
										</div>
									</div>

									<div class="control-group">
										<div class="controls">
											<form:errors path="companyName" cssClass="errorMessage span5"/>
											<form:input path="companyName" placeholder="Company Name (optional)" maxlength="200" id="companyName" cssClass="span5"/>
										</div>
									</div>

									<div id="addressHolder">
										<jsp:include page="/WEB-INF/views/mobile/partials/address.jsp">
											<jsp:param name="addressForm" value="signup"/>
										</jsp:include>
									</div>

									<div class="control-group">
										<div class="controls">
											<form:errors path="password" cssClass="errorMessage span5" htmlEscape="false"/>
											<form:password path="password" placeholder="Secure Password" maxlength="16" id="password" cssClass="span3"/>
											<small>Minimum 8 characters, with at least one number</small>
										</div>
									</div>

									<small class="tos-container">
										<form:label path="termsAgree">
											<form:checkbox path="termsAgree" id="termsAgree" value="1" cssClass="terms-checkbox" />
											I have read and agree to the <a class="termsLink" href="/tos">Terms of Use</a> and <a class="termsLink" href="/privacy">Privacy Policy</a>
										</form:label>
										<form:errors 	path="termsAgree" cssClass="errorMessage span5"/>
									</small>


									<div class="control-group">
										<div class="controls">
											<button type="submit" class="call-to-action-button">Create My Account &raquo;</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form:form>
				</div>
			</div>
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
</div>
