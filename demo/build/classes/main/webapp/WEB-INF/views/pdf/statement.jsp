<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />

<html>
<head>
	<style type="text/css">

		@page {
			margin: 8em 4em 4em 4em;
			@top-center {content: element(header);}
			@bottom-center {content: element(footer);}
		}
		body {font-family: sans-serif; font-size: 10pt; margin: 0 0.1em 2em;}
		header {position: running(header);}
		footer {position: running(footer);}
		footer .thanks {text-align: center; font-weight: bold; text-size: 14px; border-top: 1px solid #000; margin-top: 4px; padding-top: 4px;}
		footer .comments {padding-bottom: 2mm; font-size:9pt;}
		td {vertical-align: top;}
		thead {display: table-header-group; font-weight:bold; }
		thead td {background-color: #EEEEEE; border: 0.1mm solid #000000;}
	    table.header th {text-align: left; padding-right: 10px; min-width:110px; padding-bottom: 5px;}
        table.header td {text-align: left; padding-bottom: 5px;}
		table.items { -fs-table-paginate: paginate; border-spacing: 0px; font-size: 9pt; width: 100%; border-collapse: collapse; }
		table.items td {padding: 8px; border-left: 0.1mm solid #000000; border-right: 0.1mm solid #000000; border-bottom: 0.1mm solid #000000;}
		table.items thead th {background-color: #EEEEEE;  padding: 8px; }
		table.items tbody tr {page-break-inside: avoid;} 
		table.small th, table.small td {font-size: 8pt; text-align: left;}
		.small {font-size: 8pt;}
		small {color: #666;}
		.terms{display:block; page-break-before:always;}
		hr {border-width:.5px; }
		h5 {margin-bottom:5px; font-size:12px;}

		table.header {
					<c:choose>
					<c:when test="${not empty company.getAddress().getAddress2()}"><c:out value="style='margin-top:6em;'" /></c:when>
					<c:otherwise>margin-top:4.5em;</c:otherwise>
					</c:choose>
				}
	</style>
</head>
<body>
<header>
	<table width="100%" style="padding-top:20px">
		<tr>
			<td width="50%"><img src="${pageContext.request.scheme}://${pageContext.request.localName}:${pageContext.request.localPort}/media/images/logo.png" /></td>
			<td width="50%" style="text-align: right;">
				<div><h2><c:out value="${requestScope.invoices[0].companyName}" /></h2></div>
			</td>
		</tr>
	</table>

	<h3 style="color:#f7961d">
		Statements Summary for <c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', requestScope.statement.periodStartDate.timeInMillis, currentUser.timeZoneId)}"/>
		to <c:out value="${wmfmt:formatMillisWithTimeZone('MMM dd, yyyy', requestScope.statement.periodEndDate.timeInMillis, currentUser.timeZoneId)}"/>
	</h3>
	<h3 style="color:#f7961d">
		<c:out value="${company.getName()}" /><br />
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
			<th> Issue Date:</th>
			<td><c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', requestScope.statement.createdOn.timeInMillis, currentUser.timeZoneId)}"/></td>
		</tr>
		<tr>
		 	<th> Due Date:</th>
			<td><c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', requestScope.statement.dueDate.timeInMillis, currentUser.timeZoneId)}"/></td>
		</tr>
		<tr>
			<th> Invoice Number: </th>
			<td><c:out value="${requestScope.statement.invoiceNumber}" /></td>
		</tr>
		<tr>
			<th> Total Balance: </th>
			<td> <fmt:formatNumber value="${requestScope.statement.balance}" pattern="$0.00" type="currency" /></td>
		</tr>
		<tr>
			<th> Total Paid: </th>
			<td> <fmt:formatNumber value="${(requestScope.statement.balance - requestScope.statement.remainingBalance)}" pattern="$0.00" type="currency" /></td>
		</tr>
		<tr>
			<th> Remaining Balance: </th>
			<td> <fmt:formatNumber value="${requestScope.statement.remainingBalance}" pattern="$0.00" type="currency" /></td>
		</tr>
	</table>
	<hr/>

<h3 style="padding:10px 0px 5px 0px">Statement Details</h3>
<table class="items" width="100%" style="font-size: 9pt; border-collapse: collapse;" cellpadding="8">
	<thead>
	<tr>
		<td width="15%">Invoice ID</td>
		<td>Assignment Description</td>
		<td width="15%">Approval Date</td>
		<td width="10%" style="text-align: right;">Balance</td>
	</tr>
	</thead>
	<tbody>

	<c:forEach var="item" items="${requestScope.invoices}">
		<tr>
			<td>
				<c:out value="${item.invoiceNumber}" />
			</td>
			<td>
				<c:out value="${item.workTitle}" /> (<c:out value="${item.workNumber}" />)<br />
				<c:forEach var="field" items="${item.customFields}">
					<small><c:out value="${field.fieldName}" />: <c:out value="${field.fieldValue}" /></small><br />
				</c:forEach>
			</td>
			<td>
				<c:if test="${not empty(item.workCloseDate)}">
					<c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', item.workCloseDate.timeInMillis, currentUser.timeZoneId)}"/>
				</c:if>
			</td>
			<td align="right"><fmt:formatNumber value="${item.invoiceBalance}" pattern="$ 0.00" currencySymbol="$" type="currency"/><br/>
				<small><c:choose>
					<c:when test="${item.invoiceStatusTypeCode=='pending'}"> Unpaid</c:when>
					<c:otherwise><c:out value="${item.invoiceStatusTypeCode}"/></c:otherwise>
				</c:choose></small>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<p class="terms">
	<h4>Payment Instructions</h4>
	<strong>Terms:</strong> Payment must be received and posted by <em><c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/yyyy', requestScope.statement.dueDate.timeInMillis, currentUser.timeZoneId)}"/></em>.<br />
	Statements may be paid via credit card, wire transfer, direct deposit, or check.
</p>

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
<p class="small">
	Work Market, Inc.<br/>
	Attn: ID <c:out value="${currentUser.companyId}" /><br/>
	Reference: Lockbox #7875<br/>
	PO Box 7247<br/>
	Philadelphia, PA 19170-7875
</p>
<p><small>
	Please include ID <c:out value="${currentUser.companyId}" /> on the memo line of your check. Note that check processing may take up to 10 days.
</small></p>

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
