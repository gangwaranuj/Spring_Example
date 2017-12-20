<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Certifications" bodyclass="accountSettings" webpackScript="profileedit">
	<script>
		var config = {
			type: 'certification_add'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="page-header">
				<h2>Create Certification</h2>
			</div>

			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}" />
			</c:import>

			<form id="certificationsForm" action="/profile-edit/certificationsadd" method="post" enctype="multipart/form-data" class="form-horizontal">
				<wm-csrf:csrfToken />

				<p></p>

				<div class="control-group">
					<label for="industry" class="required control-label">Industry</label>
					<div class="controls">
						<select id="industry" name="industry">
							<option value="">- Select -</option>
							<c:forEach items="${industry}" var="an_industry">
									<option value="${an_industry.id}" ><c:out value="${an_industry.name}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="provider" class="required control-label">Company</label>
					<div class="controls">
						<div id="select_provider">Select an industry to display list of available companies.</div>
					</div>
				</div>
				<div class="control-group dn" id="custom_provider_line">
					<label for="customProvider" class="required control-label">Company Name</label>
					<div class="controls">
						<input type="text" name="customProvider" id="customProvider" maxlength="200" />
					</div>
				</div>
				<div class="control-group">
					<label for="name" class="required control-label">Certification</label>
					<div class="controls">
						<input type="text" name="name" id="name" maxlength="200" />
					</div>
				</div>
				<div class="control-group">
					<label for="number" class="control-label">Certification Number</label>
					<div class="controls">
						<input type="text" name="number" id="number" maxlength="50" />
					</div>
				</div>
				<div class="control-group">
					<label for="issueDate" class="required control-label">Issue Date</label>
					<div class="controls">
						<input type="text" name="issueDate" id="issueDate" maxlength="10" />
					</div>
				</div>
				<div class="control-group">
					<label for="expirationDate" class="control-label">Expiration Date</label>
					<div class="controls">
						<input type="text" name="expirationDate" id="expirationDate" maxlength="10" />
					</div>
				</div>
				<div class="control-group">
					<label for="attachment" class="control-label">Attachment</label>
					<div class="controls">
						<input type="file" id="attachment" name="file" />
					</div>
				</div>

				<div class="alert-message warning">
					<p><strong>IMPORTANT:</strong> If you do not include an attachment with proof of your certification, we may not be able to verify your request. A scan of your certificate or other verifiable proof will expedite your request.</p>
				</div>

				<div class="form-actions">
					<input type="submit" value="Save" class="button" />
				</div>

			</form>
		</div>
	</div>

</wm:app>
