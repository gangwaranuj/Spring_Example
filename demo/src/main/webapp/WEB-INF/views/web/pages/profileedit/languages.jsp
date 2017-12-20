<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Languages" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'languages'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="myprofile.languages" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Languages</h3>
				</div>

				<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

				<c:choose>
					<c:when test="${currentUser.seller || currentUser.dispatcher}">
						<p>Some Work Market assignments have language requirements. By listing your language skills here, you are more likely to appear in search results for future assignments.</p>
					</c:when>
					<c:otherwise>
						<p>By specifying which languages you know, users will know what options they have in communicating with you.</p>
					</c:otherwise>
				</c:choose>


				<c:if test="${not empty(current_languages)}">
					<h3>Current Languages</h3>

					<table class="group-list">
						<thead>
						<tr>
							<th>Language</th>
							<th>Skill Level</th>
							<th class="text-center">Delete</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="item" items="${current_languages}">
							<tr>
								<td width="33%"><c:out value="${item.language.description}"/></td>
								<td width="33%"><c:out value="${item.languageProficiencyType.description}"/></td>
								<td width="33%" class="actions">
									<a href="<c:url value="/profile-edit/language_remove?id=${item.id}"/>">
										<i class="wm-icon-trash icon-large muted"></i>
									</a>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</c:if>

				<form:form class="form-horizontal" modelAttribute="profileLanguage" action="/profile-edit/languages" method="post" accept-charset="utf-8">
					<wm-csrf:csrfToken />

					<h3>Add a New Language</h3>

					<fieldset>
						<div class="clearfix control-group">
							<form:label for="language_id" path="languageId" class="control-label">Language</form:label>
							<div class="input controls">
								<form:select id="language_id" path="languageId">
									<form:option value="" label="- Select -"/>
									<form:options items="${languages}" itemValue="id" itemLabel="description"/>
								</form:select>
							</div>
						</div>
						<div class="clearfix dn control-group" id="fluency_container">
							<form:label class="control-label" for="language_proficiency_type" path="languageProficiencyTypeCode">Fluency</form:label>
							<div class="input controls">
								<form:select id="language_proficiency_type" path="languageProficiencyTypeCode">
									<form:option value="" label="- Select -"/>
									<form:options items="${proficiency_types}" itemValue="code" itemLabel="description"/>
								</form:select>
							</div>
						</div>

						<div class="wm-action-container dn" id="button_container">
							<button type="submit" class="button">Add Language</button>
						</div>
					</fieldset>

				</form:form>
			</div>
		</div>
	</div>

</wm:app>
