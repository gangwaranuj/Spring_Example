<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<li class="${met_requirement}">
	<i class="wm-icon-<c:out value="${met_requirement == 'yes' ? 'checkmark' : 'x'}"/>"></i>
	<c:if test="${met_requirement == 'yes'}">
		Document:<br>
		<ul class="document_stats">
		<li>you uploaded:
			<span class="tooltipped tooltipped-n" aria-label="${criterion.name}">
				<strong>${wmfmt:truncate(criterion.name, 40, 40, '...')}</strong>
			</span>
			(<a href="#"
				class="file-remover"
				data-referencedid="${criterion.url}"
				data-requiredid="${criterion.requirable.id}">remove</a>)
		</li>
		<li>original document:
			<span class="tooltipped tooltipped-n" aria-label="${criterion.requirable.name}">
				<strong>${wmfmt:truncate(criterion.requirable.name, 35, 35, '...')}</strong>
			</span>
		</li>
		<c:if test="${criterion.expires}">
			<li>expiration date: <strong>${wmfmt:formatCalendar('MM/dd/yyyy', criterion.expirationDate)}</strong></li>
		</c:if>
	</ul>
	</c:if>
	<c:if test="${met_requirement == 'no'}">
		<p>Document: <strong>${wmfmt:truncate(criterion.requirable.name, 35, 35, '...')}</strong>
			(<a href="${criterion.url}">download</a>)
		<div
			id="file-uploader-${criterion.requirable.id}"
			data-expiration-required="${criterion.expires}"
			data-assetid="${criterion.requirable.id}"
			class="file-uploader inline">
		</div>
	</c:if>
</li>


<script id="document_datepicker" type="text/x-jquery-tmpl">
<div>
	<input type="text" class="expiration-datepicker datepicker" name="expiration_date_placeholder" placeholder='expiration date'>
</div>
</script>

<script id="qq-uploader-inline-tmpl" type="text/x-jquery-tmpl">
	<div class="qq-uploader inline">
		<a href="javascript:void(0);" class="qq-upload-button button"><span>Upload File</span></a>
		<div class="qq-upload-drop-area"><span>Drop document here to upload</span></div>
		<ul class="ustyled qq-upload-list"></ul>
	</div>
	<br/>
</script>
