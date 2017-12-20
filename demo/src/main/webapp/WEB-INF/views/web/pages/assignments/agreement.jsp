<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Agreement" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'acceptAgreements',
			versionId: '${wmfmt:escapeJavaScript(version.id)}'
		};
	</script>

	<c:forEach var="asset" items="${assets}">
		<div class="row">
			<div class="span16">${asset.content}</div>
		</div>
	</c:forEach>

	<div class="wm-action-container">
		<button class="button" data-action="cancel">Cancel</button>
		<button class="button" data-action="agree">Agree</button>
	</div>

</wm:app>
