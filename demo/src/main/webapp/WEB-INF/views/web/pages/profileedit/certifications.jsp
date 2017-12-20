<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Certifications" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'certifications',
			providerId: '${wmfmt:escapeJavaScript(provider_id)}',
			certificationId: '${wmfmt:escapeJavaScript(certification_id)}'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Certifications</h3>
				</div>

				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}" />
				</c:import>

				<c:if test="${not empty current_certifications}">
					<h3>Current Certifications</h3>

					<table class="group-list">
						<thead>
						<tr>
							<th>Industry</th>
							<th>Vendor</th>
							<th>Certification</th>
							<th>#</th>
							<th>&nbsp;</th>
						</tr>
						</thead>
						<c:forEach var="item" items="${current_certifications}" varStatus="status">
							<tr class="${ ((status.count % 2) == 0) ? 'odd' : 'even' }">
								<td><c:out value="${item.industry}" /></td>
								<td><c:out value="${item.provider}" /></td>
								<td><c:out value="${item.name}" /></td>
								<td><c:out value="${item.number}" /></td>
								<td><a href="<c:url value="/profile-edit/certificationsremove?id=${item.cert_id}"/>" class="fr">Delete</a></td>
							</tr>
						</c:forEach>
					</table>
				</c:if>

				<c:if test="${not empty unverified_certifications}">
					<h3>Unverified Certifications</h3>

					<table class="group-list">
						<thead>
						<tr>
							<th>Industry</th>
							<th>Vendor</th>
							<th>Certification</th>
							<th>#</th>
							<th>&nbsp;</th>
						</tr>
						</thead>
						<c:forEach var="item" items="${unverified_certifications}" varStatus="status">
							<tr class="${ ((status.count % 2) == 0) ? 'odd' : 'even' }">
								<td><c:out value="${item.industry}" /></td>
								<td><c:out value="${item.provider}" /></td>
								<td><c:out value="${item.name}" /></td>
								<td><c:out value="${item.number}" /></td>
								<td><a href="<c:url value="/profile-edit/certificationsremove?id=${item.cert_id}"/>" class="fr">Delete</a></td>
							</tr>
						</c:forEach>
					</table>
				</c:if>

				<h3>Add a Certification</h3>

				<form class="form-horizontal" id="certificationsForm" action="/profile-edit/certificationssave" method="post" enctype="multipart/form-data">
					<wm-csrf:csrfToken />

					<fieldset>
						<div class="clearfix control-group">
							<label for="industry" class="control-label">Industry</label>
							<div class="input controls">
								<select id="industry" name="industry">
									<option value="-1">- Select -</option>
									<c:forEach items="${industry}" var="an_industry">
										<option value="${an_industry.id}"  ${(prefill_industry == an_industry.id)?'selected':''} ><c:out value="${an_industry.name}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>

						<div id="select_provider" class="clearfix control-group">
							<label class="control-label">Company</label>
							<div class="input controls">
								<p id="provider_instructions" class="dn"></p>
							</div>
						</div>

						<div id="select_certification" class="clearfix dn control-group">
							<label class="control-label required">Certification</label>
							<div class="input controls"></div>
						</div>

						<div id="certification_details" class="dn">
							<div class="clearfix dn control-group" id="certification_number">
								<label for="number" class="control-label required">Certification Number</label>
								<div class="input controls">
									<input type="text" name="number" id="number" maxlength="50" />
								</div>
							</div>
							<div class="clearfix control-group">
								<label for="issueDate" class="control-label required">Issue Date</label>
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
						</div>

						<div id="certification_attachment" class="clearfix dn control-group">
							<label for="attachment" class="control-label required">Attachment</label>
							<div class="input controls">
								<input type="file" id="attachment" name="file" />
							</div>
						</div>
					</fieldset>

					<div class="alert alert-block">
						<p><strong>IMPORTANT:</strong> If you do not include an attachment with proof of your certification, we may not be able to verify your request. A scan of your certificate or other verifiable proof will expedite your request.</p>
					</div>

					<div id="save_button" class="wm-action-container dn">
						<button type="submit" class="button">Add Certification</button>
					</div>

					<div class="alert alert-info">
						<span class="muted"><i class="icon-plus-sign icon-large"></i> Can't find your certification in our list? <a href="<c:url value="/profile-edit/certificationsadd"/>">Add your certification to our database</a></span>
					</div>

				</form>
			</div>
		</div>
	</div>

</wm:app>
