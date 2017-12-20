<%-- NOTE: Any changes to this file should be mirrored in CreditCardReceiptPDFTemplate.vm --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title><c:out value="${title}" /></title>
	<style type="text/css">
		body {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 12px;
		}

		table {
			border-collapse: collapse;
		}

		td {
			padding: 0px;
		}

		.container {
			width: 100%;
		}

		.container tr td {
			padding: 10px 5px;
		}

		.border_bottom {
			border-bottom: 1px solid #000;
		}

		.large_text {
			font-size: 16px;
			font-weight: bold;
		}

		.orange_text {
			color: #fea029;
		}

		.cc_details td {
			padding: 10px;
		}

		.txn_details {
			width: 100%;
			margin-top: 10px;
		}

		.txn_details td, .txn_details th {
			padding: 10px;
		}

		.txn_details thead tr th {
			border: 1px solid #bababa;
			font-weight: bold;
		}

		.txn_details tbody tr td {
			border: 1px solid #bababa;
			border-top: 0px;
		}

		.txn_details tfoot tr td {
			font-weight: bold;
			font-size: 16px;
		}
	</style>
</head>
<body>
	<table class="container">
		<tr>
			<td width="70%"><img src="${mediaPrefix}/images/logo.png"/></td>
			<td align="right" valign="top" class="large_text">RECEIPT</td>
		</tr>
		<tr>
			<td class="border_bottom orange_text large_text">
				<c:out value="${title}" />
			</td>
			<td class="border_bottom">
				Thank you for using Work Market
			</td>
		</tr>
		<tr>
			<td class="border_bottom">
				<table class="cc_details">
					<tr>
						<td><strong>Transaction Date:</strong></td>
						<td><c:out value="${date}" /></td>
					</tr>
					<tr>
						<td><strong>Transaction ID:</strong></td>
						<td><c:out value="${registerTransaction.id}" /></td>
					</tr>
					<tr>
						<td><strong>Amount:</strong></td>
						<td><c:out value="${total}" /></td>
					</tr>
					<tr>
						<td><strong><c:out value="${type}" />:</strong></td>
						<td><c:out value="${companyName}" /> - <c:out value="${type}" /> Deposit</td>
					</tr>
				</table>
			</td>
			<td class="border_bottom">
				<strong>Company Information</strong><br />
				<br />
				<c:out value="${companyName}" /><br />
				<c:out value="${not empty address.address1 ? address.address1 : ''}" /><br />
				<c:if test="${not empty(address.address2)}">
					<c:out value="${address.address2}" /><br />
				</c:if>
				<c:out value="${not empty address.city ? address.city : ''}" />
				<c:out value="${not empty address.state ? address.state : ''}" />
				<c:out value="${not empty address.postalCode ? address.postalCode : ''}" /><br />
				<c:out value="${not empty address.country ? address.country : ''}" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<strong>Details</strong>
				<table class="txn_details">
					<thead>
						<tr>
							<th>Description</th>
							<th align="right">Amount</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><c:out value="${row.description}" /></td>
							<td align="right"><c:out value="${row.amount}" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td align="right">Total</td>
							<td align="right"><c:out value="${total}" /></td>
						</tr>
					</tfoot>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>