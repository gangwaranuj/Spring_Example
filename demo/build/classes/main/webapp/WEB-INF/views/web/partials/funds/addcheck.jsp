<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="inner-container">

	<div class="alert alert-info">
		<strong>Step 2: Mailing Instructions.</strong>
		<p>Processing can take 7-10 business days after receipt of your check at our bank lockbox for the check to <b></b>be deposited into your account.
			Checks should be made payable to Work Market, Inc and include your Work Market ID on your check.</p>
	</div>

	<div class="label-left">
			<strong>Mailing Address Information:</strong><br />
			Work Market, Inc.<br />
			Reference: Lockbox #7875<br />
			PO Box 7247<br />
			Philadelphia, PA  19170-7875<br />
			<br/>

			You must include your Work Market ID on your check.
			<strong>Your Work Market ID: <c:out value="${currentUser.companyId}" /></strong><br />
	</div>

	<div class="wm-action-container">
		<button type="button" class="button" id="btn-addcheck-back">Back</button>
	</div>
</div>