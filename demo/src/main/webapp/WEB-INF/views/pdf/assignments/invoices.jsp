<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<jsp:useBean id="now" scope="page" class="java.util.Date"/>

<html>
<head>
	<style>
		body { font-family: sans-serif; font-size: 10pt; }

		td { vertical-align: top; }

		.items td { border-left: 0.1mm solid #000000; border-right: 0.1mm solid #000000; border-bottom: 0.1mm solid #000000; }

		table thead td { background-color: #EEEEEE; border: 0.1mm solid #000000; }

		table.small th, table.small td { font-size: 8pt; text-align: left; }

		.small { font-size: 8pt; }

		small { color: #666; }

		table.header th { text-align: left; padding-right: 30px; }
	</style>
</head>
<body>

<table class="items" width="100%" style="font-size: 9pt; border-collapse: collapse;" cellpadding="8">
	<thead>
	<tr>
		<td width="15%">Approved</td>
		<td width="15%">Invoice</td>
		<td>Assignment</td>
		<td width="15%" style="text-align: right;">Amount</td>
	</tr>
	</thead>
	<tbody>

	<c:forEach var="item" items="${requestScope.items}">
		<c:choose>
			<c:when test="${item.invoiceType eq 'bundle'}">
				<c:forEach var="bundledItem" items="${item.bundledInvoices}">
					<tr>
						<td>
							<c:if test="${not empty(bundledItem.workCloseDate)}">
								<fmt:formatDate value="${bundledItem.workCloseDate.time}" pattern="M/dd/yyyy"/>
							</c:if>
						</td>
						<td>
							<c:out value="${bundledItem.invoiceNumber}"/><br/>
							<small><c:out value="${item.invoiceNumber}"/></small>
						</td>
						<td>
							<c:out value="${bundledItem.workTitle}"/> (<c:out value="${bundledItem.workNumber}"/>)<br/>
							<c:forEach var="customField" items="${bundledItem.customFields}">
								<c:out value="${customField.fieldName}"/> : <c:out value="${customField.fieldValue}"/><br/>
							</c:forEach>
						</td>
						<td align="right"><fmt:formatNumber value="${bundledItem.invoiceBalance}" currencySymbol="$" type="currency"/><br/>
							<small><c:out value="${wmfmt:capitalize(bundledItem.invoiceStatusTypeCode)}"/></small>
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>
						<c:if test="${not empty(item.workCloseDate)}">
							<fmt:formatDate value="${item.workCloseDate.time}" pattern="M/dd/yyyy"/>
						</c:if>
					</td>
					<td>
						<c:out value="${item.invoiceNumber}"/>

						<c:if test="${not empty(item.invoiceSummaryNumber)}">
							<br/>
							<small><c:out value="${item.invoiceSummaryNumber}"/></small>
						</c:if>
					</td>
					<td>
						<c:out value="${item.workTitle}"/> (<c:out value="${item.workNumber}"/>)<br/>
						<c:forEach var="customField" items="${item.customFields}">
							<c:out value="${customField.fieldName}"/>: <c:out value="${customField.fieldValue}"/><br/>
						</c:forEach>
					</td>
					<td align="right">
						<fmt:formatNumber value="${item.invoiceBalance}" currencySymbol="$" type="currency"/><br/>
						<small><c:out value="${wmfmt:capitalize(item.invoiceStatusTypeCode)}"/></small>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	</tbody>
</table>

<p><strong>Terms:</strong> Payment must be received and posted by
	<em><fmt:formatDate value="${requestScope.dueOn.time}" pattern="M/dd/yyyy" timeZone="${currentUser.timeZoneId}"/></em>.<br/>
	Invoices may be paid via credit card, wire transfer, direct deposit, or check.</p>

<h5>Send wire transfer to the following:</h5>
<table class="small">
	<tr>
		<th>Beneficiary Name</th>
		<td>Work Market, Inc.</td>
	</tr>
	<tr>
		<th>Beneficiary Account</th>
		<td>9983235230</td>
	</tr>
	<tr>
		<th>Beneficiary Address</th>
		<td>240 West 37th Street, 10th Floor, New York, NY 10018</td>
	</tr>
	<tr>
		<th>Beneficiary Phone #</th>
		<td>(646) 588-4641</td>
	</tr>
	<tr>
		<th>Beneficiary Bank Name</th>
		<td>Citibank, N.A.</td>
	</tr>
	<tr>
		<th>Beneficiary Bank City and State</th>
		<td>787 Seventh Avenue, New York, NY 10019</td>
	</tr>
	<tr>
		<th>ABA or Swift Code</th>
		<td>021 0000 89 or CITIUS33</td>
	</tr>
</table>

<h5>Send checks to the following address:</h5>

<p class="small">Work Market, Inc.<br/>
	Attn: ID <c:out value="${currentUser.companyId}"/><br/>
	Reference: Lockbox #7875<br/>
	PO Box 7247<br/>
	Philadelphia, PA 19170-7875</p>

<p>
	<small>Please note that check processing may take up to 10 days.</small>
</p>

<h5>For overnight sending only:</h5>

<p class="small">
	First Data/Remitco<br/>
	Attn: Work Market/LBX # 7875<br/>
	Ref: <c:out value="${currentUser.companyId}"/><br/>
	400 White Clay Center Drive<br/>
	Newark, DE 19711
</p>

<p>
	<small>
		Please include ID <c:out value="${currentUser.companyId}"/> on the memo line of your check. Note that check processing may take up to 10 days.
	</small>
</p>

</body>
</html>
