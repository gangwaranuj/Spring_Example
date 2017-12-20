<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Profile Photo" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'photo'
		}
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Profile Photo</h3>
				</div>

				<c:import url="/WEB-INF/views/web/partials/message.jsp" />
				<ul>
					<li>Add your photo to make your profile more personal. File size must be less than 2MB. We suggest you use a professional photo.</li>
					<li>Use a photo that represents you and you want others to see.</li>
					<li>You can change your photo as often as you'd like.</li>
					<li>Inappropriate photos will be flagged and your profile may be suspended.</li>
				</ul>
				<div class="well">

					<h4>Set your profile image</h4>

					<form action="/profile-edit/photoupload" class="form-stacked" enctype="multipart/form-data" method="post">
						<wm-csrf:csrfToken />

						<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
							<c:param name="containerId" value="photo_upload_messages" />
						</c:import>

						<div class="row">
							<div class="span4">
								<img src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(avatar.uri))}" default="${mediaPrefix}/images/no_picture.png" />" alt="Photo" class="avatar_thumbnail" />
								<div class="<c:if test="${empty avatar.uri}">dn</c:if>">
									<a class="button" href="/profile-edit/photodelete">Remove</a>
									<a class="button" href="/profile-edit/photocrop">Crop</a>
								</div>
							</div>
							<div class="span7">
								<c:choose>
									<c:when test="${not empty avatarUri}">
										<p>Your current profile photo is displayed to the left. You can change your photo at any time. If you work for other companies, your new photo may require approval from Work Market.</p>
									</c:when>
									<c:otherwise>
										<p>Select a photo and upload it to Work Market using the Upload Photo button below.</p>
										<p>You'll have the opportunity to crop and size your photo to fit.</p>
										<p>We suggest you use a square photo and file size of at least 700kb but no more than 2 MB. Ideal pixel size is 600 x 600.</p>
									</c:otherwise>
								</c:choose>

								<div id="file-uploader">
									<noscript>
										<input type="file" name="qqfile" id="qqfile" />
									</noscript>
								</div>
							</div>
						</div>

					</form>
				</div>
			</div>
		</div>

		<c:import url="/WEB-INF/views/web/partials/filemanager/add_to_filemanager.jsp" />
	</div>

	<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop photo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button">Upload <c:if test="${not empty avatar}">a new</c:if> photo</a>
			<ul class="unstyled qq-upload-list"></ul>
		</div>
	</script>

</wm:app>
