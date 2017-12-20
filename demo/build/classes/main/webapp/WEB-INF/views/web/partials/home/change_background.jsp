<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div id="backgrounds">
	<div id="background-messages" class="message alert alert-error dn"></div>
	<ul class="thumbnails">
		<c:forEach var="image" items="${backgroundImages}">
			<li class="span3">
				<a href="#" class="thumbnail">
					<img id="${image.asset.id}" src="<c:out value="${wmfmt:stripXSS(image.asset.uri)}" />"/>
				</a>
			</li>
		</c:forEach>

		<li class="span3">
			<span id="background-changer" class="tooltipped tooltipped-n" aria-label='<fmt:message key="change_background.change_background" />'></span>
			<div class="text-center">
				<img id="spinner" src="${mediaPrefix}/images/loading.gif" alt="Loading" style="height: 16px; width: 16px;" class="dn"/>
			</div>
		</li>
	</ul>
	<p>
		<fmt:message key="change_background.unique_photo" />. <a href="http://help.workmarket.com/customer/portal/articles/1140420" target="_blank"><fmt:message key="global.learn_more" /></a>
	</p>
</div>

<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
	<div class="qq-uploader">
		<ul class="qq-upload-list dn"></ul>
		<a href="javascript:void(0);" class="qq-upload-button thumbnail button">
			<fmt:message key="global.upload_custom" />
		</a>
		<div class="qq-upload-drop-area">
			<span><fmt:message key="change_background.drop_image" /></span>
		</div>
	</div>
</script>
