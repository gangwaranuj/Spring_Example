<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.admin.accounting.achfunding" scope="request" />

<wm:admin pagetitle="Withdrawals">

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<form action="/admin/accounting/update_transaction_status" id="report_filters" method="post">
			<wm-csrf:csrfToken />
			<input type="hidden" name="returnTo" value="${pageUri}" />
			<input type="hidden" name="updateStatus" value="" id="update_status" />

			<table id="transaction_list" class="table table-striped">
				<thead>
				<tr>
					<th><input type="checkbox" name="select_all" id="select_all"/></th>
					<th>Company Id</th>
					<th>Company Name</th>
					<c:choose>
						<c:when test="${type eq 'GCC'}">
							<th>GCC ID</th>
						</c:when>
					</c:choose>
					<th>Name on Account</th>
					<c:choose>
						<c:when test="${type eq 'ACH'}">
							<th>Bank Name</th>
							<th>Institution Number</th>
							<th>Account Number</th>
						</c:when>
						<c:when test="${type eq 'PPA'}">
							<th>Account Email</th>
						</c:when>
					</c:choose>
					<th>Country</th>
					<th>Amount</th>
					<th>Status</th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="value" items="${transactions}">
					<tr>
						<td><input type="checkbox" name="transactionIds[]" value="${value.id}" /></td>
						<td><c:out value="${value.bankAccount.company.id}"/></td>
						<td><c:out value="${value.bankAccount.company.name}"/></td>
						<c:choose>
							<c:when test="${type eq 'GCC'}">
								<td><c:out value="${value.bankAccount.accountNumber}" /></td>
							</c:when>
						</c:choose>
						<td><c:out value="${value.bankAccount.nameOnAccount}"/></td>
						<c:choose>
							<c:when test="${type eq 'ACH'}">
								<td><c:out value="${value.bankAccount.bankName}"/></td>
								<td><c:out value="${value.bankAccount.routingNumber}"/></td>
								<td data-id='<c:out value="${value.bankAccount.id}" />'>xxxxxxxxx</td>
							</c:when>
							<c:when test="${type eq 'PPA'}">
								<td><c:out value="${value.bankAccount.emailAddress}"/></td>
							</c:when>
						</c:choose>
						<td><c:out value="${value.bankAccount.country.name}"/></td>
						<td><fmt:formatNumber value="${value.amount}" currencySymbol="$" type="currency"/></td>
						<td><c:out value="${value.bankAccountTransactionStatus.code}"/></td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			<div class="wm-action-container">
				<a class="button" id="approve-outlet">Approve</a>
				<a class="button" id="reject-outlet">Reject</a>
			</div>
		</form>
	</div>

</wm:admin>