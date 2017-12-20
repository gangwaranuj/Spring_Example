<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="AddFundsWireForm" class="inner-container">
	<div class="messages"></div>
	<div class="alert alert-info">
		<strong>Step 2: Wire Instructions</strong>
		<p>
			In order to send funds to Work Market, Inc. via wire transfer, from an account designated by you, you will need
			to give your bank or financial institution specific instructions.
			Funds must be transferred in US dollars and the transfers may take up to 1 business day. Wire transfers
			received by 4pm ET will be processed on that business day.
		</p>
	</div>

	<sf:form modelAttribute="addFundsWireForm" action="/funds/addwire" method="POST">
		<wm-csrf:csrfToken />

		<img style="margin-left: 65px;" src="${mediaPrefix}/images/wire-instructions.png"/>

		<div class="alert alert-info">
			<p>
				You must include your company's name and WM Company ID in your wiring instructions.<br/>
				<strong>Your WM Company ID:</strong> <c:out value="${currentUser.companyId}" />
			</p>
		</div>
	</sf:form>

	<div class="wm-action-container">
		<button type="button" class="button" id="btn-addcheck-back">Back</button>
	</div>
</div>



