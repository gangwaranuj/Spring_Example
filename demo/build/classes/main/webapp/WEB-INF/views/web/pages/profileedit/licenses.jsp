<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Licenses" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'licenses',
			state: '${wmfmt:escapeJavaScript(state)}',
			licenseId: '${wmfmt:escapeJavaScript(license_id)}'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<c:choose>
						<c:when test="${not empty current_licenses}">
							<h3>Current Licenses</h3>
						</c:when>
						<c:otherwise>
							<h3>Add a License</h3>
						</c:otherwise>
					</c:choose>
				</div>

				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}" />
				</c:import>

				<c:if test="${not empty current_licenses}">
					<table class="group-list">
						<thead>
						<tr>
							<th>State</th>
							<th>License</th>
							<th>#</th>
							<th>&nbsp;</th>
						</tr>
						</thead>
						<c:forEach var="item" items="${current_licenses}">
							<tr>
								<td><c:out value="${item.state}"/></td>
								<td><c:out value="${item.name}"/></td>
								<td><c:out value="${item.number}"/></td>
								<td><a href="<c:url value="/profile-edit/licensesremove?id=${item.id}" />">Delete</a></td>
							</tr>
						</c:forEach>
					</table>
				</c:if>

				<c:if test="${not empty current_licenses}">
					<h3>Add a License</h3>
				</c:if>

				<form class="form-horizontal" id="licensesForm" action="/profile-edit/licensesave" method="post" enctype="multipart/form-data">
					<wm-csrf:csrfToken />
					<!-- Validator wants a name field, this is never used otherwise in processing this form -->
					<input type="hidden" name="name" id="name" value="DUMMY"/>
					<div class="clearfix control-group">
						<label for="state" class="control-label">State</label>
						<div class="input controls">
							<select id="state" name="state">
								<option value="">- Select -</option>
								<c:forEach var="country" items="${states}">
									<optgroup label="<c:out value='${country.key}'/>">
										<c:forEach var="state" items="${country.value}">
											<option value="<c:out value='${state.value}'/>"><c:out value='${state.key}'/></option>
										</c:forEach>
									</optgroup>
								</c:forEach>
							</select>
						</div>
					</div>
					<div id="select_license" class="clearfix dn control-group">
						<label class="control-label">License</label>
						<div class="input controls"></div>
					</div>
					<div id="license_number" class="dn br">
						<div class="clearfix control-group">
							<label for="number" class="control-label">License Number</label>
							<div class="input controls">
								<input type="text" name="number" id="number" maxlength="255" />
							</div>
						</div>
						<div class="clearfix control-group">
							<label for="issueDate" class="control-label">Issue Date</label>
							<div class="input controls">
								<input type="text" name="issueDate" id="issueDate" maxlength="10" />
							</div>
						</div>
						<div class="clearfix control-group">
							<label for="expirationDate" class="control-label">Expiration Date</label>
							<div class="input controls">
								<input type="text" name="expirationDate" id="expirationDate" maxlength="10" />
							</div>
						</div>
						<div class="clearfix br control-group">
							<label class="control-label">Attachment</label>
							<div class="input controls">
								<input type="file" id="file" name="file" />
							</div>
						</div>

						<div class="alert alert-block">
							<p><strong>IMPORTANT:</strong> If you do not include an attachment with proof of your license, we may not be able to verify your request. A scan of your license or other verifiable proof will expedite your request.</p>
						</div>

					</div>
					<div id="save_button" class="wm-action-container">
						<button type="submit" value="Save" class="button">Add License</button>
					</div>

				</form>
			</div>
		</div>
	</div>

</wm:app>
