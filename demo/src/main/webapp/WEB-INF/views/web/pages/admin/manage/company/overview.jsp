<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Overview" bodyclass="manage-company">

	<c:set var="pageScript" value="wm.pages.admin.manage.company.overview" scope="request"/>
	<c:set var="pageScriptParams" value="'${wmfmt:escapeJavaScript(company.id)}'" scope="request"/>

	<div class="row_sidebar_left">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>

		<div class="content">
			<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp">
				<c:param name="overview" value="true"/>
			</c:import>

			<form:form action="/admin/manage/company/edit_company_info" method="post" modelAttribute="form">
				<wm-csrf:csrfToken />
				<form:input type="hidden" path="id" value="${company.id}"/>

				<div class="dn" id="edit_company_info_container">
					<fieldset>
						<div class="clearfix">
							<label for="company_name">Company Name</label>

							<div class="input">
								<form:input id="company_name" maxlength="255" size="30" path="company_name" value="${company.name}"/>
							</div>
						</div>
						<div class="clearfix">
							<label for="company_operate_as_individual_flag">Company Type</label>

							<div class="input">
								<ul class="inputs-list">
									<li><label><form:radiobutton id="company_operate_as_individual_flag" path="operating_as_individual_flag" value="1"/> Individual</label></li>
									<li><label><form:radiobutton id="company_operate_as_individual_flag" path="operating_as_individual_flag" value="0"/> Company</label></li>
								</ul>
							</div>
						</div>
					</fieldset>

					<div class="wm-action-container">
						<button class="button">Save</button>
					</div>
				</div>
			</form:form>

			<div class="row">
				<div class="span12">
					<c:choose>
						<c:when test="${not empty company.overview}">
							<p class="wordwrap"><c:out value="${company.overview}"/></p>
						</c:when>
						<c:otherwise>
							<p id="no-overview">No Company Overview provided</p>
						</c:otherwise>
					</c:choose>
					<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/money_bar.jsp"/>
					<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/work_bar.jsp"/>
					<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/resource_bar.jsp"/>
				</div>
			</div>
			<!-- /row -->

			<div class="row">
				<div class="span12">
					<h4>Comments
						<small><a id="add_comment_to_company_link">(Add Comment)</a></small>
					</h4>
					<div class="alert" style="display: none;">
						<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="close">x</a>
						<div></div>
					</div>
					<table class="table table-striped" id="add_company_comment_table" cellpadding="0" cellspacing="0" border="0">
						<thead>
							<tr>
								<th>Left By</th>
								<th>Date</th>
								<th>Comment</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="4">No comments available.</td>
							</tr>
						</tbody>
					</table>

					<h4>Attachments</h4>
					<div class="message error strong pr br dn">
						<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="db close pa">Close</a>
						<b></b>
						<div></div>
					</div>
					<table class="table table-striped" id="add_company_attachment_table" cellpadding="0" cellspacing="0" border="0">
						<thead>
							<tr>
								<th>Name</th>
								<th>Description</th>
								<th>Date Added</th>
								<th>&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="4">No attachments.</td>
							</tr>
						</tbody>
					</table>

					<div id="changelog">
						<h4>Change Log</h4>
						<table class="table table-striped">
							<thead>
							<tr>
								<th>Description</th>
								<th>Detail</th>
								<th>Actor</th>
								<th>Timestamp</th>
							</tr>
							</thead>
							<tbody>

							<c:forEach var="c" items="${changelog}">
								<tr>
									<td>
										<c:out value="${c.description}"/>
									</td>
									<td>
										<c:choose>
											<c:when test="${c.changeLogType == 'CompanyPropertyChangeLog'}">
												<strong><c:out value="Property: "/></strong>
												<c:out value="${c.propertyName}"/><br/>
												<strong><c:out value="Old Value: "/></strong>
												<c:out value="${c.oldValue}"/><br/>
												<strong><c:out value="New Value: "/></strong>
												<c:out value="${c.newValue}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'CompanyAddFeatureChangeLog'}">
												<c:out value="${c.featureName}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'CompanyRemoveFeatureChangeLog'}">
												<c:out value="${c.featureName}"/>
											</c:when>
										</c:choose>
									</td>
									<td>
										<c:if test="${c.masqueradeActor != null}">
											<c:out value="${c.masqueradeActor.email} masquerading as "/>
										</c:if>
										<c:out value="${c.actor.firstName} ${c.actor.lastName}"/>
									</td>
									<td>
										<fmt:formatDate value="${c.createdOn.time}" pattern="E, MMM d yyyy @ hh:mma"/>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>

					<jsp:include page="companyActions.jsp"/>
				</div>
			</div>
			<!-- /row -->
		</div>
	</div>

	<div class="dn">
		<div id="edit_ap_limit_popup">
			<form action="/admin/manage/company/update_ap_limit" method="post" id="edit_ap_limit_form">
				<wm-csrf:csrfToken />

				<input type="hidden" name="id" value="${company.id}"/>

				<div class="message error strong pr br dn"><a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="db close pa">Close</a><b></b>

					<div></div>
				</div>

				<fieldset>
					<div class="clearfix">
						Current AP Limit:
						<div class="input"><fmt:formatNumber value="${account_register.apLimit}" currencySymbol="$" type="currency"/></div>
					</div>
					<div class="clearfix">
						New AP Limit:
						<div class="input">

							<c:choose>
								<c:when test="${empty account_register.apLimit}">
									<input type="text" name="ap_limit" id="ap_limit" class="small" value="1000"/>
								</c:when>
								<c:otherwise>
									<input type="text" name="ap_limit" id="ap_limit" class="small" value="${account_register.apLimit}"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</fieldset>
				<div class="wm-action-container">
					<button class="button">Update</button>
				</div>

			</form>

		</div>

		<div id="add_company_comment_popup">

			<form action="/admin/manage/company/add_comment_to_company" method="post" class="form-stacked">
				<wm-csrf:csrfToken />
				<div class="message error strong pr br dn">
					<a href="javascript:void(0);" onclick="javascript:close_message(this); return false;" class="db close pa">Close</a>
				</div>

				<input type="hidden" name="id" value="${company.id}"/>

				<div class="clearfix">
					<div class="input">
						<textarea rows="5" id="comment" name="comment" class="span8"></textarea>
					</div>
				</div>

				<div class="wm-action-container">
					<button type="submit" class="button">Add Comment</button>
				</div>
			</form>
		</div>
	</div>

	<script id="add_company_comment_tmpl" type="text/x-jquery-tmpl">
		<tr valign="middle">
			<td>\${name}</td>
			<td>\${date}</td>
			<td>\${comment}</td>
			<td>
				{{if user_id == '${wmfmt:escapeJavaScript(currentUserId)}'}}
				<a class="delete_company_comment" id="deletecompanycomment_\${id}">Delete</a>
				{{else}}
				&nbsp;
				{{/if}}
			</td>
		</tr>
	</script>

	<script id="add_company_attachment_tmpl" type="text/x-jquery-tmpl">
		<tr valign="middle">
			<td><a href="\${uri}">\${name}</a></td>
			<td>\${description}</td>
			<td>\${date}</td>
			<td><a name="delete_company_attachment" id="deletecompanyattachment_\${id}">Delete</a></td>
		</tr>
	</script>

</wm:admin>
