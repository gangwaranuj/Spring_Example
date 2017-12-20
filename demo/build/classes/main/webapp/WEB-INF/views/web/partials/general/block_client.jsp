<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri = "http://www.springframework.org/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="dn">
	<div id="block_client_dialog_container">
		<p><s:message code="blockclient.warning"/></p>
		<p><fmt:message key="block_client.ok_to_block" /></p>

		<form id="block_client_form" action="/user/block_client/" method="post">
			<wm-csrf:csrfToken />
			<div class="wm-action-container">
				<button type="submit" id="block-client-button" class="button"><fmt:message key="global.ok" /></button>
			</div>
		</form>
	</div>
</div>
