<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Overview & Employment" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'employment'
		}
	</script>

<div class="row_sidebar_left">
	<div class="sidebar">
		<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
	</div>

	<div class="content">
		<div class="inner-container">
		<div class="page-header">
			<h3>Overview &amp; Employment</h3>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

		<form:form class="form-horizontal" modelAttribute="profile" action="/profile-edit/employment" method="post" id="form_reference" accept-charset="utf-8">
			<wm-csrf:csrfToken/>

			<p>Provide a brief summary of your work experience. This information is shown on your profile and is available to Work Market users searching for
				workers.</p>

			<fieldset>
				<div class="clearfix control-group">
					<form:label path="overview" class="control-label">Summary</form:label>
					<div class="input controls">
						<form:textarea path="overview" id="overview" cssClass="span8" rows="7" maxlength="500"/>
					</div>
				</div>
			</fieldset>

			<div class="clearfix control-group">
				<label class="control-label">LinkedIn</label>
				<div class="text controls">
					<c:if test="${not empty requestScope.linkedin}">
						<c:forEach items="${requestScope.linkedin.linkedInPositions}" var="position">
							<ul class="unstyled">
								<li><strong><c:out value="${position.title}"/></strong></li>
								<li><c:out value="${position.company.name}"/></li>
								<li>
									<c:choose>
										<c:when test="${not empty position.startDate.month}">
											<c:out value="${wmfmt:monthName(position.startDate.month)}"/> <c:out value="${position.startDate.year}"/>
										</c:when>
										<c:otherwise><c:out value="${position.startDate.year}"/></c:otherwise>
									</c:choose>
									&nbsp;-&nbsp;
									<c:choose>
										<c:when test="${not empty position.current and position.current}">Present</c:when>
										<c:when test="${not empty  position.endDate.month}">
											<c:out value="${wmfmt:monthName(position.endDate.month)}"/> <c:out value="${position.endDate.year}"/>
										</c:when>
										<c:otherwise><c:out value="${position.endDate.year}"/></c:otherwise>
									</c:choose>
								</li>
							</ul>
						</c:forEach>
					</c:if>
					<c:set var="whatToDo" scope="page">
						<c:choose>
							<c:when test="${empty requestScope.linkedin}">Import</c:when>
							<c:otherwise>Reimport</c:otherwise>
						</c:choose>
					</c:set>
					<a href="/oauth/linkedin?import=1&internal_callback=/profile-edit/employment" class="db" style="padding-left: 20px; background: transparent url('${mediaPrefix}/images/icons/linkedin.png') no-repeat; height: 16px;"><c:out value="${pageScope.whatToDo}"/> your LinkedIn profile</a>
				</div>
			</div>

			<a name="resume"></a>

			<fieldset>
				<h4>Resume</h4>

				<p>Upload your resume to improve your search result accuracy. Please limit your upload to no more than 5MB. Supported file types are:
				</p>
				<ul><li>Microsoft Word</li>
					<li>PDF</li>
					<li>Text files</li></ul>

				<div id="resume-messages"></div>

				<ul id="resume_list">
					<c:forEach var="resume" items="${resumes}">
						<li id="resume_${pageScope.resume.id}">
							<a href="<c:out value="${resume.uri}" />"><c:out value="${resume.name}"/></a>
							-
							<a href="javascript:void(0);" data-action="remove-resume" data-asset-id='${resume.id}'>Remove</a>
						</li>
					</c:forEach>
				</ul>

				<div id="file-uploader">
					<noscript>
						<input type="file" name="file"/>
					</noscript>
				</div>
			</fieldset>

			<div class="wm-action-container">
				<button class="button">Save Changes</button>
			</div>

		</form:form>

	</div>
	</div>
</div>

<script id="resume_list_item" type="text/x-jquery-tmpl">
	<li id="resume_\${id}">
		<a href="<c:url value="asset"/>\${id}">\${filename}</a>
		-
		<a href="javascript:void(0);" data-action="remove-resume" data-asset-id='\${id}'>Remove</a>
	</li>
</script>

<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
	<div class="qq-uploader">
		<div class="qq-upload-drop-area"><span>Drop resume here to upload</span></div>
		<a href="javascript:void(0);" class="qq-upload-button button -small">Upload Resume</a>
		<ul class="qq-upload-list"></ul>
	</div>
</script>

</wm:app>
