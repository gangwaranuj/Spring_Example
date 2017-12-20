<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="SSO Integrations" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="SSO Integrations" webpackScript="settings">

	<script>
		var config = {
			mode: 'sso'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/mmw/sso" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}"/>
				</c:import>

				<div class="page-header">
					<div class="fr">
						<small class="meta"><a href="/mmw/integration">Back to Integrations</a></small>
					</div>
					<h3>Single Sign On</h3>
				</div>

				<div>
					<p>SSO integration with Work Market ...</p>
					<form:form class="form form-horizontal" action="/mmw/sso" method="POST"
							   modelAttribute="ssoConfigForm">
						<wm-csrf:csrfToken/>
						<div class="well">
							<fieldset id="sso_fields">
								<div class="clearfix control-group">
									<label class="control-label" for="idpMetadata">IDP Metadata</label>
									<div class="input controls">
										<form:textarea rows="5" cols="30" cssClass="span6" path="idpMetadata" id="idpMetadata"/>
									</div>
								</div>
								<div class="clearfix control-group">
									<label class="control-label" for="entityId">Entity ID</label>
									<div class="input controls">
										<form:input maxlength="255" type="text" cssClass="span6" id="entityId"
													path="entityId"/>
									</div>
								</div>
								<div class="clearfix control-group">
									<label class="control-label" for="entityId">Role assigned to all new users</label>
									<div class="input controls">
										<form:select path="defaultRoleId" id="default_role_dropdown" items="${ssoConfigForm.roles}">

										</form:select>
									</div>
								</div>
								<div class="clearfix control-group">
									<button id='get-sp-metadata' class="button">GET WM METADATA</button>
								</div>
							</fieldset>
						</div>
						<div class="well">
							<div class="wm-action-container">
								<button type="submit" class="button">Save Changes</button>
							</div>
							</fieldset>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
	<div id="spMetadata" class="hide"><c:out value="${ssoConfigForm.spMetadata}" /></div>
</wm:app>