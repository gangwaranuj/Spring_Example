<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<p><img src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(not empty asset.largeThumbnailUri ? asset.largeThumbnailUri : asset.uri))}" />" alt="Photo" width="480" onload="javascript:$.colorbox.resize();" /></p>

<div class="row">
	<div class="span4">
		<p>
			<strong><c:out value="${asset.name}" /></strong> -
			<a href="/asset/download/${asset.uuid}">Download</a><br />
			${wmfmt:bytes(asset.fileByteSize)}
		</p>
		<p>
			<strong>Caption</strong><br />
			<c:out value="${asset.description}" />
		</p>
	</div>
	<div class="span4">
		<p>
			<strong><c:out value="${asset.creatorFirstName}" /> <c:out value="${asset.creatorLastName}" /></strong><br />
			<c:out value="${asset.creatorCompanyName}" />
		</p>
		<p>
			<strong>Question <c:out value="${item.position}" /></strong><br />
			<fmt:formatDate value="${asset.createdOn.time}" pattern="M/dd/yyyy" />
		</p>
	</div>
</div>
