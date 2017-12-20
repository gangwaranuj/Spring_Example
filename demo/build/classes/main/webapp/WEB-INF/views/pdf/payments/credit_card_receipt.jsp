<%-- NOTE: Any changes to this file should be mirrored in CreditCardReceiptPDFTemplate.vm --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Credit Card Receipt</title>
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
				Receipt for Credit Card Funding
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
						<td><c:out value="${creditCardTransaction.id}" /></td>
					</tr>
					<tr>
						<td><strong>Amount:</strong></td>
						<td><c:out value="${total}" /></td>
					</tr>
					<tr>
						<td><strong>Credit Card:</strong></td>
						<td><c:out value="${creditCardTransaction.cardName}" /></td>
					</tr>
				</table>
			</td>
			<td class="border_bottom">
				<strong>Billing Address</strong><br />
				<br />
				<c:out value="${fullName}" /><br />
				<c:out value="${not empty creditCardTransaction.address1 ? creditCardTransaction.address1 : ''}" /><br />
				<c:if test="${not empty(creditCardTransaction.address2)}">
					<c:out value="${creditCardTransaction.address2}" /><br />
				</c:if>
				<c:out value="${not empty creditCardTransaction.city ? creditCardTransaction.city : ''}" />
				<c:out value="${not empty creditCardTransaction.state ? creditCardTransaction.state : ''}" />
				<c:out value="${not empty creditCardTransaction.postalCode ? creditCardTransaction.postalCode : ''}" /><br />
				<c:out value="${not empty creditCardTransaction.country ? creditCardTransaction.country : ''}" />
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
						<c:forEach var="row" items="${rows}">
							<tr>
								<td><c:out value="${row.description}" /></td>
								<td align="right"><c:out value="${row.amount}" /></td>
							</tr>
						</c:forEach>
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
		<tr>
			<td colspan="2">NOTE:<br />
				The charge for Work Market will appear on your credit card<br />
				statement as "<a href="http://www.workmarket.com">WORK MARKET, INC</a>".</td>
		</tr>
	</table>
</body>
</html>