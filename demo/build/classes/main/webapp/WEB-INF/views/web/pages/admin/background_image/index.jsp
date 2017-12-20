<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Background Image">

<c:set var="pageScript" value="wm.pages.admin.backgroundImage" scope="request"/>

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content admin">
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

	<ul class="thumbnails">
		<c:forEach var="image" items="${backgroundImages}">
			<li class="span3">
				<a href="#" class="thumbnail <c:if test="${currentDefaultImageId eq image.id}">selected</c:if>">
					<img id="${image.id}" src="<c:out value="${wmfmt:stripXSS(image.asset.uri)}" />" />
				</a>
			</li>
		</c:forEach>
	</ul>

	<input id="fileupload" type="file" name="file" data-url="/admin/background_image">
	<button class="save_default">Save selection as default</button>
	<button class="remove_image">Remove selection</button>
	<button class="refresh_sessions">Refresh all user sessions</button>

</div>

<script type="text/template" id="default-image-tmpl">
	<li class="span3">
		<a href="#" class="thumbnail">
			<img id="{{= id }}" src="{{= uri }}" />
		</a>
	</li>
</script>

</wm:admin>
