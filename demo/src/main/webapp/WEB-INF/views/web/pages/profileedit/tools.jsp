<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Tools" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'tools',
			skills: ${toolsJSON}
		}
	</script>


	<style type="text/css">
		.disabled {opacity: 0.65; color: #444;}
	</style>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>


		<div class="content">
			<div class="inner-container" id="skills_container">
			<div id="skills-message"></div>

			<div class="page-header">
				<h3>Tools</h3>
			</div>

			<sf:form action="/profile-edit/save_tools" method="POST" id="tools_form">
				<wm-csrf:csrfToken />

			<p>Add Tools to your profile to identify both tools that you own and can bring to an assignment or tools that you are able to use.</p>

			<div class="row">
				<div class="span6">
					<h4>Browse tools by industry:</h4>

					<p>After selecting an industry, click on a tool below to add it to your list.</p>
					<div class="clearfix">
						<select name="industry" class="span5" id="outlet-skill-industry-select">
							<option value="">- Select -</option>
							<c:forEach var="item" items="${industries}">
								<c:choose>
									<c:when test="${industry == item.id}">
										<option value="<c:out value="${item.id}" />" selected="selected"><c:out value="${item.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value="<c:out value="${item.id}" />"><c:out value="${item.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="span6">
					<div id="skill-add">
						<h4>Manually add tools:</h4>
						<p>Don't see your tool in our list? Add your own right now.</p>
						<br />
						<div class="form-inline">
							<input type="text" id="outlet-skill-autocomplete-field" maxlength="100" />
							<a href="#" id="outlet-skill-autocomplete-button" class="button">Add</a>
						</div>
						<span id="outlet-skill-autocomplete-selected" class="dn"></span>
					</div>
				</div>
			</div>
			<hr/>
			<div class="row">
				<div class="span6">
					<h5>Available Tools <small>(Scroll to view more)</small></h5>
					<div id="browse-list"></div>
				</div>
				<div class="span6">
					<h5>Current Tools</h5>
					<ul id="skill-list" class="skill-list clear"></ul>
					<div class="wm-action-container">
						<a class="button" href="/profile">Cancel</a>
						<a id="outlet-continue-step3" class="button">Save Changes</a>
					</div>
				</div>
			</div>

			</sf:form>
		</div>
		</div>
	</div>

	<script id="template-skill-list-item" type="text/x-jquery-tmpl">
		<li class="token-input-token">
			<div class="closable">
				\${skill.name} <a class="remove outlet-skill-list-item-remove"><i class="wm-icon-trash"></i></a>
			</div>
		</li>
	</script>

	<script id="template-browse-list-item" type="text/x-jquery-tmpl">
		<li><a class="outlet-browse-list-item {{if exists}}disabled{{/if}}">\${skill.name}</a></li>
	</script>

	<script id="template-browse-list-empty" type="text/x-jquery-tmpl">
		<p class="alert-message warning">We are currently building a list of tools for this industry. If you would like to help with this effort, please contact us.</p>
	</script>
</wm:app>
