<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="width: 99%; height: 99%">
	<div style="position: fixed; left: 20pt; top: 25pt; width: 235pt; height: 55pt">
		<div>
			<c:out value="${wm_name}" /><br/>
			<c:out value="${wm_address.address1}" />, <c:out value="${wm_address.address2}" /><br/>
			<c:out value="${wm_address.city}" /> <c:out value="${wm_address.state}" /> <c:out value="${wm_address.postalCode}" /><br/>
			<c:out value="${wm_phone}" /><br/>
		</div>
	</div>

	<div style="position: fixed; left: 20pt; top: 120pt; width: 110pt; height: 13pt">
		<c:out value="${wm_ein}" />
	</div>

	<div style="position: fixed; left: 143pt; top: 120pt; width: 110pt; height: 13pt">
		<c:out value="${rcpt_ssn_ein}" />
	</div>


	<div style="position: fixed; left: 270pt; top: 178pt; width: 80pt; height: 13pt">
		<c:out value="${amount}" />
	</div>

	<div style="position: fixed; left: 19pt; top: 160pt; width: 230pt; height: 13pt">
		<c:out value="${rcpt_name}" />
	</div>

	<div style="position: fixed; left: 19pt; top: 202pt; width: 230pt; height: 13pt">
		<c:out value="${rcpt_address.address1}" /><br/>
	</div>

	<div style="position: fixed; left: 19pt; top: 238pt; width: 230pt; height: 13pt">
		<c:out value="${rcpt_address.city}" />, <c:out value="${rcpt_address.state}" /> <c:out value="${rcpt_address.postalCode}" />
	</div>

</div>