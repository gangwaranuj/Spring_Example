<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Photo Crop" bodyclass="accountSettings" webpackScript="profileedit">

	<link href="${mediaPrefix}/imgareaselect.css" rel="stylesheet" type="text/css"/>

	<script>
		var config = {
			type: 'photocrop',
			_x1: ${wmfmt:escapeJavaScript(startCoords.x1)},
			_y1: ${wmfmt:escapeJavaScript(startCoords.x2)},
			_x2: ${wmfmt:escapeJavaScript(startCoords.y2)},
			_y2: ${wmfmt:escapeJavaScript(startCoords.y2)},
			_width: ${wmfmt:escapeJavaScript(originalImageWidth)},
			_height: ${wmfmt:escapeJavaScript(originalImageHeight)}
		}
	</script>

	<div class="row_sidebar_left photocrop">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="page-header">
				<h2>Profile Photo</h2>
			</div>

			<c:import url="/WEB-INF/views/web/partials/message.jsp" />

			<ul>
				<li>Add your photo to distinguish your profile. We suggest you use a professional photo.</li>
				<li>Use a photo that represents you and you want others to see.</li>
				<li>You can change your photo as often as you'd like &ndash; if you work with multiple companies, Work Market will approve any photo changes.</li>
				<li>Inappropriate photos will be flagged and your profile may be suspended.</li>
			</ul>

			<div class="well">
				<h4>Set your profile image</h4>

				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="photo_upload_messages" />
				</c:import>

				<ol>
					<li>Click and drag the square on your photo below to your desired size and location</li>
					<li>Once you are satisfied, click on the Save Profile Image button</li>
				</ol>
				<div id="profile-pic-crop">
					<img src="<c:out value="${wmfmt:stripXSS(avatarOriginal)}" />" alt="Photo" id="original_photo" />
				</div>

				<form action="/profile-edit/photocrop" method="post">
					<wm-csrf:csrfToken />
					<input type="hidden" name="x1" id="x1" />
					<input type="hidden" name="y1" id="y1" />
					<input type="hidden" name="x2" id="x2" />
					<input type="hidden" name="y2" id="y2" />
					<input type="hidden" name="w" id="w" />
					<input type="hidden" name="h" id="h" />

					<button type="button" class="button" id="submitCrop">Save Profile Image</button>
				</form>
			</div>
		</div>
	</div>
</wm:app>
