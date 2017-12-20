<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Job Title and Skills" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'qualifications',
			skills: ${skills},
			jobTitle: '${wmfmt:escapeJavaScript(jobTitle)}',
			industry: '${industry}'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
		<div class="inner-container qualifications-container" id="skills_container">
			<wm-csrf:csrfToken />
			<div id="skills-message"></div>
			<div class="page-header">
				<h3>Job Title and Skills</h3>
			</div>

			<div class="control-group">
				<label for="job-title-autocomplete" class="control-label required">Job Title</label>
				<div class="controls">
					<input id="job-title-autocomplete" type="text" style="width: 70%;" value=""/>
				</div>
			</div>

			<div class="control-group">
				<label for="skills-autocomplete" class="control-label">Skills &amp; Specialties</label>
				<div class="controls" style="max-width: 500px;">
					<div>
						<ul class="your-skills your-skills-wrapper" name="your-skills" style="max-height:none">
							<span class="skills-list"></span>

							<div class="skills-select">
								<input id="skills-autocomplete" class="skills-autocomplete" type="text" name="skill" placeholder="Search or Select..." value="" />
							</div>
						</ul>

					</div>

				<span class="help-block">
					Provide a list of skills and specialties needed to perform the work.
				</span>

					<div class="recommended-skills">
						<p>Suggested Skills</p>
						<ul class="skills" name="skills"></ul>
					</div>

				</div>
			</div>

			<div class="control-group">
				<div class="wm-action-container qualifications-action">
					<a id="save-qualifications" class="button">Save Changes</a>
				</div>
				<div id="qualification-success" class="alert alert-success" style="display:none">Your Profile Has Been Updated</div>
				<div id="qualification-errors" class="alert alert-error" style="display:none"></div>
			</div>
		</div>
		</div>
	</div>

</wm:app>
