<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Contact Information" bodyclass="profileedit" webpackScript="profileedit">

	<script>
		var config = {
			type: 'index'
		}
	</script>

	<c:set var="isSuperuser" value="0"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isSuperuser" value="1"/>
	</sec:authorize>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<div id="profile_edit" class="row_sidebar_left">
		<div class="sidebar">
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3><i class="icon-user"></i> Contact Information</h3>
				</div>

				<sf:form action="/profile-edit" method="POST" modelAttribute="generalProfile" id="form_profile" cssClass="form-horizontal left">
					<wm-csrf:csrfToken />

					<div class="control-group">
						<label class="control-label">First Name</label>

						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-user"></i></span>
								<sf:input type="text" path="firstName" readonly="true"/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Last Name</label>

						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-user"></i></span>
								<sf:input type="text" path="lastName" readonly="true"/>
							</div>
						</div>
					</div>


					<div class="control-group">
						<label class="control-label" for="userEmail" class="required">Email</label>

						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-envelope"></i></span>
								<sf:input type="email" path="userEmail" maxlength="255" id="userEmail" cssErrorClass="fieldError" readonly="${currentUser.ssoUser}"/>
							</div>
							<sf:errors path="userEmail" cssClass="inlineError"/>

							<c:if test="${$changed_email}">
								<div class="alert alert-error">
									<strong>You have changed your email to <em><c:out value="${changed_email}"/></em>.</strong>
									Until you confirm the new email address, please use <c:out value="${email}"/> to log in to the site.
								</div>
							</c:if>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label" for="userEmailSecondary">Secondary Email</label>

						<div class="controls">
							<div class="input-prepend">
								<span class="add-on"><i class="icon-envelope"></i></span>
								<sf:input type="email" path="userEmailSecondary" maxlength="255" id="userEmailSecondary"
							          cssErrorClass="fieldError"/>
								<sf:errors path="userEmailSecondary" cssClass="inlineError"/>
							</div>
							<span class="help-block">
								Add a secondary email to receive assignment related emails.
								This email does not have to be unique to your account.
							</span>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label" for="workPhone" class="required">Work Phone</label>

						<div class="controls">
							<sf:select path="workPhoneInternationalCode" id="workPhoneInternationalCode" multiple="false" disabled="${mboProfile.status eq 'NORMAL'}">
								<sf:option value="" label="- Country code -"/>
								<sf:options items="${callingCodesList}" itemValue="id" itemLabel="name"/>
							</sf:select>
							<div class="input-prepend">
								<span class="add-on"><i class="icon-phone"></i></span>
								<sf:input type="tel" path="workPhone" maxlength="14" id="workPhone" cssClass="span3" cssErrorClass="fieldError" alt="phone-us" readonly="${mboProfile.status eq 'NORMAL'}"/>
								<sf:errors path="workPhone" cssClass="inlineError"/>
							</div>
							<div class="input-prepend">
								<span class="add-on">ext.</span>
								<sf:input type="text" path="workPhoneExtension" maxlength="4" id="workPhoneExtension" cssClass="span1" cssErrorClass="span2 fieldError" disabled="${mboProfile.status eq 'NORMAL'}"/>
								<sf:errors path="workPhoneExtension" cssClass="inlineError"/>
							</div>
						</div>
					</div>

					<%-- TODO country flag dropdown http://www.marghoobsuleman.com/countries-dropdown-flags --%>

					<div class="control-group">
						<label class="control-label" for="mobilePhone">Mobile Phone</label>
						<div class="controls">
								<sf:select path="mobilePhoneInternationalCode" id="mobilePhoneInternationalCode" multiple="false" cssStyle="width:150px" disabled="${mboProfile.status eq 'NORMAL'}">
									<sf:option value="" label="- Country code -"/>
									<sf:options items="${callingCodesList}" itemValue="id" itemLabel="name"/>
								</sf:select>
							<div class="input-prepend">
								<span class="add-on"><i class="icon-phone"></i></span>
								<sf:input type="tel" path="mobilePhone" maxlength="14" id="mobilePhone" cssClass="span3" cssErrorClass="fieldError"
								          alt="phone-us" disabled="${mboProfile.status eq 'NORMAL'}"/>
								<sf:errors path="mobilePhone" cssClass="inlineError"/>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label for="timezone" class="control-label">Timezone</label>
						<div class="controls">
							<sf:select path="timezone" id="timezone" cssErrorClass="fieldError">
								<sf:option value="" label="- Select -"/>
								<sf:options items="${timezoneList}" itemValue="id" itemLabel="timeZoneId"/>
							</sf:select>
							<sf:errors path="timezone" cssClass="inlineError"/>
						</div>
					</div>

					<div class="control-group">
						<jsp:include page="/WEB-INF/views/web/partials/public/address.jsp"/>
					</div>

					<div class="wm-action-container">
						<button type="submit" class="button">Save Changes</button>
					</div>

				</sf:form>
			</div>
		</div>
	</div>
</wm:app>
