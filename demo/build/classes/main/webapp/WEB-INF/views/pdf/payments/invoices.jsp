<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="now" class="java.util.Date" />

<html>
<head>
	<title>Test PDF</title>
	<style type="text/css">
		@page {
			margin: 12.5em 4em 4em 4em;
			<c:if test="${not empty company.getAddress().getAddress2()}">
				margin-top: 14em;
			</c:if>
			@top-center {content: element(header);}
			@bottom-center {content: element(footer);}
		}

		body {font-family: sans-serif; font-size: 10pt; margin: 0 0.1em 2em;}
		header {position: running(header);}
		footer {position: running(footer);}
		footer .thanks {text-align: center; font-weight: bold; text-size: 14px; border-top: 1px solid #000; margin-top: 4px; padding-top: 4px;}
		footer .comments {padding-bottom: 2mm; font-size:9pt;}

		td {vertical-align: top;}
		table.invoices { -fs-table-paginate: paginate; border-spacing: 0px; font-size: 9pt; width: 100%; border-collapse: collapse; margin-top:20px; }
		thead {display: table-header-group; }
		table.invoices td {padding: 8px; border-left: 0.1mm solid #000000; border-right: 0.1mm solid #000000; border-bottom: 0.1mm solid #000000;}
		table.invoices thead th {background-color: #EEEEEE; border: 0.1mm solid #000000; padding: 8px;}
		table.invoices tbody tr {page-break-inside: avoid;}
		table.small th, table.small td {font-size: 8pt; text-align: left;}
		.small {font-size: 8pt;}
		small {color: #666;}
		table.header th {text-align: left; padding-right: 10px; min-width:110px; padding-bottom:5px;}
		table.header td {text-align: left; padding-bottom: 5px;}
		.terms{display:block; page-break-before:always;}
		hr {border-width:.5px; }
		h5 {margin-bottom:5px; font-size:12px;}

	</style>
</head>
<body>

<header>
	<table width="100%" style="padding-top:20px">
		<tr>
			<td width="50%">
				<img src="${pageContext.request.scheme}://${pageContext.request.localName}:${pageContext.request.localPort}/media/images/workmarket_email_logo.png" style="opacity: 0.75;"/>
			</td>
			<td width="50%" style="text-align: right;">
				<div><h2><c:out value="${requestScope.invoices[0].companyName}" /></h2></div>
			</td>
		</tr>
	</table>
	<h3 style="color:#000000">
		<c:out value="${company.getName()}" /><br />
		ATTN: Accounts Payable<br />
		<c:out value="${company.getAddress().getAddress1()}" /><c:if test="${not empty company.getAddress().getAddress1()}"><br /></c:if>
		<c:out value="${company.getAddress().getAddress2()}" /><c:if test="${not empty company.getAddress().getAddress2()}"><br /></c:if>
		<c:out value="${company.getAddress().getCity()}" /><c:if test="${not empty company.getAddress().getCity()}">,</c:if>
		<c:out value="${company.getAddress().getState()}" />
		<c:out value="${company.getAddress().getPostalCode()}" />
	</h3>
	<hr/>
</header>

<table class="header">
	<tr>
		<th>Create Date:</th>
		<td><fmt:formatDate value="${now}" pattern="M/dd/yyyy" /></td>
	</tr>
	<tr>
		<th>Due Date:</th>
		<td>
			<c:choose>
				<c:when test="${(fn:length(invoices) eq 1) or isBundle or numStatements == 1}">
					<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', dueOn.timeInMillis, currentUser.timeZoneId)}"/>
				</c:when>
				<c:otherwise>Please see details below for individual due dates </c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<th>Total Balance:</th>
		<td><fmt:formatNumber value="${totalDue}" type="currency"  /></td>
	</tr>
	<tr>
		<th>Total Paid:</th>
		<td><fmt:formatNumber value="${totalDue - remainingBalanceDue}" type="currency"  /></td>
	</tr>
	<tr>
		<th>Remaining Balance Due: </th>
		<td><fmt:formatNumber value="${remainingBalanceDue}" type="currency" /></td>
	</tr>
</table>
<hr/>

<h4 style="padding:10px 0px 5px 0px">Invoice Details</h4>
<table class="invoices" width="100%">
	<thead>
	<tr>
		<th width="15%">Invoice ID</th>
		<th width="55%">Description</th>
		<th width="10%">Due Date</th>
		<th width="10%">Status</th>
		<th width="10%" style="text-align: right;">Balance</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach var="item" items="${invoices}">
		<c:choose>
			<c:when test="${item.bundle}">
				<c:forEach var="bundledItem" items="${item.bundledInvoices}">
					<tr>
						<td>
							<c:out value="${bundledItem.invoiceNumber}" /><br/>
							<small><c:out value="${item.invoiceNumber}" /></small>
						</td>
						<td>
							<c:out value="${bundledItem.workTitle}" /> (<c:out value="${bundledItem.workNumber}" />)<br/>
							<small>Worker Name: <c:out value="${bundledItem.workResourceName}"/></small><br/>
							<c:if test="${not empty bundledItem.workResourceCompanyName}">
								<c:choose>
									<c:when test="${bundledItem.workResourceCompanyName!='Sole Proprietor'}">
										<small>Company Name: <c:out value="${bundledItem.workResourceCompanyName}"/></small>
									</c:when>
									<c:otherwise>
										<small>Company Name: N/A</small>
									</c:otherwise>
								</c:choose>
								<br/>
							</c:if>
							<c:if test="${not empty bundledItem.workCountry or not empty bundledItem.clientCompanyName}">
								<c:if test="${not empty bundledItem.workCountry}">
									<small>Location: <c:out value="${bundledItem.formattedAddressShort}"/></small><br/>
								</c:if>
								<c:if test="${not empty bundledItem.clientCompanyName}">
									<small> Client: <c:out value="${bundledItem.clientCompanyName}"/></small><br/>
								</c:if>
							</c:if>
							<c:forEach var="f" items="${bundledItem.customFields}">
								<small><c:out value="${f.fieldName}" />: <c:out value="${f.fieldValue}" /></small><br/>
							</c:forEach>
						</td>
						<td>
							<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', bundledItem.invoiceDueDate.timeInMillis, currentUser.timeZoneId)}"/>
						</td>
						<td>
							<c:choose>
								<c:when test="${bundledItem.invoiceStatusTypeCode=='pending'}">
									Unpaid
									<c:if test="${bundledItem.invoicePastDue}">
										<br/><small style="color:#c43c35">Past Due</small>
									</c:if>
								</c:when>
								<c:when test="${bundledItem.invoiceStatusTypeCode=='void'}">
									Void
									<small>
										<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', bundledItem.invoiceVoidDate.timeInMillis, currentUser.timeZoneId)}"/>
									</small>
								</c:when>
								<c:otherwise>
									<c:out value="${bundledItem.invoiceStatusTypeCode}" /><br/>
									<small>
										<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', bundledItem.invoicePaymentDate.timeInMillis, currentUser.timeZoneId)}"/>
									</small>
								</c:otherwise>
							</c:choose>
						</td>
						<td align="right">
							<fmt:formatNumber value="${bundledItem.invoiceBalance}" type="currency" />
						</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>
						<c:out value="${item.invoiceNumber}" />
						<c:if test="${not empty item.invoiceSummaryNumber}">
							<br/><small><c:out value="${item.invoiceSummaryNumber}" /></small>
						</c:if>
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty item.workNumber}">
								<strong>Title:</strong> <c:out value="${item.workTitle}" /> (<c:out value="${item.workNumber}" />)<br/>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${item.invoiceType eq 'invoice'}">
										Statement Invoice
									</c:when>
									<c:otherwise>
										<c:out value="${item.invoiceDescription}" />
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>

						<c:if test="${not empty item.workResourceName}">
							<small>Worker Name: <c:out value="${item.workResourceName}"/></small>
						</c:if>
						<br/>
						<c:if test="${not empty item.workResourceCompanyName}">
							<c:choose>
								<c:when test="${item.workResourceCompanyName!='Sole Proprietor'}">
									<small>Company Name: <c:out value="${item.workResourceCompanyName}"/></small>
								</c:when>
								<c:otherwise>
									<small>Company Name: N/A</small>
								</c:otherwise>
							</c:choose>
							<br/>
						</c:if>
						<c:if test="${not empty item.workCountry or not empty item.clientCompanyName}">
							<c:if test="${not empty item.workCountry}">
								<small>Location: <c:out value="${item.formattedAddressShort}"/></small><br/>
							</c:if>
							<c:if test="${not empty item.clientCompanyName}">
								<small> Client:<c:out value="${item.clientCompanyName}"/></small> <br/>
							</c:if>
						</c:if>
						<c:if test="${not empty item.uniqueIdDisplayName and not empty item.uniqueIdValue}">
							<small><c:out value="${item.uniqueIdDisplayName}"/>: <c:out value="${item.uniqueIdValue}"/></small><br/>
						</c:if>
						<c:forEach var="f" items="${item.customFields}">
							<small><c:out value="${f.fieldName}" />: <c:out value="${f.fieldValue}" /></small><br/>
						</c:forEach>
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty item.invoiceSummaryNumber}">
								<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', item.invoiceSummaryDueDate.timeInMillis, currentUser.timeZoneId)}"/>
							</c:when>
							<c:otherwise>
								<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', item.invoiceDueDate.timeInMillis, currentUser.timeZoneId)}"/>
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.invoiceStatusTypeCode=='pending'}">Unpaid
								<c:if test="${item.invoicePastDue}"><br/><small style="color:#c43c35">Past Due</small></c:if>
							</c:when>
							<c:when test="${item.invoiceStatusTypeCode=='void'}">Void
								<small>
									<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', item.invoiceVoidDate.timeInMillis, currentUser.timeZoneId)}"/>
								</small>
							</c:when>
							<c:otherwise>
								<c:out value="${item.invoiceStatusTypeCode}" /><br/>
								<small>
									<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', item.invoicePaymentDate.timeInMillis, currentUser.timeZoneId)}"/>
								</small>
							</c:otherwise>
						</c:choose>
					</td>
					<td align="right">
						<c:choose>
							<c:when test="${not empty isReceivables and isReceivables}">
								<fmt:formatNumber value="${item.amountEarned}" currencySymbol="$" type="currency"/>
							</c:when>
							<c:otherwise>
								<fmt:formatNumber value="${item.invoiceBalance}" currencySymbol="$" type="currency"/>
							</c:otherwise>
						</c:choose>
						<br/>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	</tbody>
</table>

<div class="terms">
	<table class="header">
		<tr>
			<td>
				<p>
				<h4>Payment Instructions</h4>
				<strong>Terms:</strong> Payment must be received and posted by the individual due dates shown above.<br />
				Invoices may be paid via credit card, wire transfer, direct deposit, or check.
				</p>
			</td>
		</tr>
	</table>
</div>

<h5>Send wire transfer to the following:</h5>
<table class="small">
	<tr><th>Beneficiary Name</th><td>Work Market, Inc.</td></tr>
	<tr><th>Beneficiary Account</th><td>9983235230</td></tr>
	<tr><th>Beneficiary Address</th><td>240 West 37th Street, 10th Floor, New York, NY 10018</td></tr>
	<tr><th>Beneficiary Phone #</th><td>(646) 588-4641</td></tr>
	<tr><th>Beneficiary Bank Name</th><td>Citibank, N.A.</td></tr>
	<tr><th>Beneficiary Bank City and State</th><td>787 Seventh Avenue, New York, NY 10019</td></tr>
	<tr><th>ABA or Swift Code</th><td>021 0000 89 or CITIUS33</td></tr>
</table>

<h5>Send checks to the following address:</h5>
<p class="small">Work Market, Inc.<br/>
	Attn: ID <c:out value="${currentUser.companyId}" /><br/>
	Reference: Lockbox #7875<br/>
	PO Box 7247<br/>
	Philadelphia, PA 19170-7875</p>
<p><small>Please note that check processing may take up to 10 days.</small></p>

<h5>For overnight sending only:</h5>
<p class="small">
	First Data/Remitco<br/>
	Attn: Work Market/LBX # 7875<br/>
	Ref: ID <c:out value="${currentUser.companyId}" /><br/>
	400 White Clay Center Drive<br/>
	Newark, DE 19711
</p>
<p><small>
	Please include ID <c:out value="${currentUser.companyId}" /> on the memo line of your check. Note that check processing may take up to 10 days.
</small></p>

<footer>
	<div class="comments">Comments: Work Market ID <c:out value="${currentUser.companyId}" /></div>
	<div class="thanks">Thank you for choosing Work Market. We appreciate your business!</div>
	<div style="width: 100%;">
		<br/><strong style="font-size: 11pt;">Work Market, Inc.</strong>
		www.workmarket.com
		info@workmarket.com
		T: 212-229-WORK
		F: 212-647-WORK
	</div>
</footer>

</body>
</html>
