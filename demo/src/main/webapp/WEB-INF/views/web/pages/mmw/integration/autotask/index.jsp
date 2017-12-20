<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Third-Party Integrations" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Third Party Integrations" webpackScript="settings">

	<script>
		var config = {
			mode: 'autotask'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/mmw/integrations" scope="request"/>
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
					<h3>Autotask</h3>
				</div>

				<c:choose>
					<c:when test="${autotaskUserForm.hasApiUser}">
						<div>
							<p>Autotask integration with Work Market allows an AutoTask customer to create assignments in Work Market from the AutoTask interface, and then pass updated information from Work Market back to the AutoTask system. By eliminating duplicate data entry, this helps you save time and money and run your business more efficiently.</p>
							<form:form class="form form-horizontal" action="/mmw/integration/autotask" method="POST" modelAttribute="autotaskUserForm">
								<wm-csrf:csrfToken />
								<div class="well">
									<div class="page-header">
										<h4>Authentication</h4>
									</div>

									<fieldset id="autotask_user_fields">
										<div class="clearfix control-group">
											<label class="control-label" for="userName">Autotask Username</label>
											<div class="input controls">
												<form:input type="text" maxlength="255" cssClass="span6" path="autotaskUser.userName" id="userName"/>
											</div>
										</div>
										<div class="clearfix control-group">
											<label class="control-label" for="userPass">Autotask Password</label>
											<div class="input controls">
												<form:input maxlength="255" type="password" cssClass="span6" id="userPass" path="autotaskUser.password"/>
											</div>
										</div>
										<c:if test="${not empty autotaskUserForm.autotaskUser.zoneUrl}">
										<div class="clearfix control-group">
											<label class="control-label" for="userPass">&nbsp;</label>
											<div class="input controls">
												<nobr>
													<label for="updatePassword"><form:checkbox id="updatePassword" path="autotaskUser.updatePassword" value="true"/> Update Current Password</label>
												</nobr>
											</div>
										</div>
										</c:if>
										<c:if test="${not empty autotaskUserForm.autotaskUser && not empty autotaskUserForm.autotaskUser.zoneUrl}">
										<div class="clearfix control-group">
											<p><strong>Autotask Zone URL</strong></p>
											<p><span style="background-color: #f1f1f1; padding: 4px"><c:out value="${autotaskUserForm.autotaskUser.zoneUrl}" /></span></p>
										</div>
										</c:if>
								</div>

								<div class="well">
									<div class="page-header">
										<h4>Notes Sync</h4>
									</div>
									<ul class="inputs-list">
										<li>
											<label>
												<c:choose>
													<c:when test="${autotaskUserForm.notesEnabled}">
														<input type="checkbox" checked="checked" name="notesEnabled" id="notesEnabled">
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="notesEnabled" id="notesEnabled">
													</c:otherwise>
												</c:choose>
												Send notes from Work Market assignments to Autotask tickets
											</label>
										</li>
										<li>
											<label>
												<c:choose>
													<c:when test="${autotaskUserForm.notesInternal}">
														<input type="checkbox" checked="checked" name="notesInternal" id="notesInternal">
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="notesInternal" id="notesInternal">
													</c:otherwise>
												</c:choose>
												Send notes on Autotask for Internal Only
											</label>
										</li>
										<li>
											<label>
												<c:choose>
													<c:when test="${autotaskUserForm.attachmentsInternal}">
														<input type="checkbox" checked="checked" name="attachmentsInternal" id="attachmentsInternal">
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="attachmentsInternal" id="attachmentsInternal">
													</c:otherwise>
												</c:choose>
												Send Attachments from Work Market assignments Internal Autotask users Only
											</label>
										</li>
									</ul>
									</fieldset>
								</div>

								<div class="well">
									<div class="page-header">
										<h4>Required Fields / UDF Mapping</h4>
									</div>
									<p>The following two fields are required to sync data from Autotask to Work Market.
										You will need to add these fields as UDFs or map to existing fields on your Autotask ticket configuration.</p>


									<fieldset id="autotask_custom_fields">
										<div class="clearfix control-group">
											<label class="control-label">WM Status</label>
											<div class="input controls">
												<form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.status'].customFieldValue"/>
											</div>
										</div>

										<div class="clearfix control-group">
											<label class="control-label">WM Work Id</label>
											<div class="input controls">
												<form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.work.id'].customFieldValue"/>
											</div>
										</div>
								</div>

								<div class="well">
									<div class="page-header">
										<h4>Optional Fields / UDF Mapping</h4>
									</div>
									<p>To enable syncing data from Work Market to Autotask for a specific field, check the box next to the field name.
										Optionally change the Autotask name to map the Work Market field to one of your existing Autotask fields.</p>


									<table class="group-list">
										<thead>
										<tr>
											<th>Enabled</th>
											<th>Work Market Field</th>
											<th>Autotask Field Name</th>
										</tr>
										</thead>
										<tbody>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.id'].enabled" /></td>
											<td>WM Resource Id</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.id'].customFieldValue" disabled="${preferenceMap['wm.resource.id'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.first.name'].enabled" /></td>
											<td>WM Resource FirstName</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.first.name'].customFieldValue" disabled="${preferenceMap['wm.resource.first.name'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.last.name'].enabled" /></td>
											<td>WM Resource LastName</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.last.name'].customFieldValue" disabled="${preferenceMap['wm.resource.last.name'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.email'].enabled" /></td>
											<td>WM Resource Email</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.email'].customFieldValue" disabled="${preferenceMap['wm.resource.email'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.phone'].enabled" /></td>
											<td>WM Resource Phone</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.phone'].customFieldValue" disabled="${preferenceMap['wm.resource.phone'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resource.phone.mobile'].enabled" /></td>
											<td>WM Resource Phone 2</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resource.phone.mobile'].customFieldValue" disabled="${preferenceMap['wm.resource.phone.mobile'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.checkedin.on'].enabled" /></td>
											<td>WM Checked In On</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.checkedin.on'].customFieldValue" disabled="${preferenceMap['wm.checkedin.on'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.checkedout.on'].enabled" /></td>
											<td>WM Checked Out On</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.checkedout.on'].customFieldValue" disabled="${preferenceMap['wm.checkedout.on'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.resolution'].enabled" /></td>
											<td>WM Resolution</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.resolution'].customFieldValue" disabled="${preferenceMap['wm.resolution'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.max.spend.limit'].enabled" /></td>
											<td>WM Max Spend Limit</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.max.spend.limit'].customFieldValue" disabled="${preferenceMap['wm.max.spend.limit'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.actual.spend.limit'].enabled" /></td>
											<td>WM Actual Spend Limit</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.actual.spend.limit'].customFieldValue" disabled="${preferenceMap['wm.actual.spend.limit'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.hours.worked'].enabled" /></td>
											<td>WM Hours Worked</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.hours.worked'].customFieldValue" disabled="${preferenceMap['wm.hours.worked'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.total.cost'].enabled" /></td>
											<td>WM Total Cost</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.total.cost'].customFieldValue" disabled="${preferenceMap['wm.total.cost'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.spend.limit'].enabled" /></td>
											<td>WM Spend Limit</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.spend.limit'].customFieldValue" disabled="${preferenceMap['wm.spend.limit'].enabled}"/></td>
										</tr>
										<tr>
											<td><form:checkbox path="preferenceMap['wm.additional.expenses'].enabled" /></td>
											<td>WM Additional Expenses</td>
											<td><form:input maxlength="50" cssClass="span4" path="preferenceMap['wm.additional.expenses'].customFieldValue" disabled="${preferenceMap['wm.additional.expenses'].enabled}"/></td>
										</tr>
										</tbody>
									</table>

									<div class="wm-action-container">
										<a class="button" href="#" id="clear_credentials">Clear</a>
										<button type="submit" class="button">Save Changes</button>
									</div>
									</fieldset>
								</div>
							</form:form>
						</div>
					</c:when>
					<c:otherwise>You must generate a Work Market API access key before using Autotask integration. <a href="/mmw/api">Configure API access</a></c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>

</wm:app>
