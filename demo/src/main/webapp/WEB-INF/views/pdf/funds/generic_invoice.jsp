<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<jsp:useBean id="now" class="java.util.Date"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html>
<head>
	<style type="text/css">
		body { font-family: sans-serif; font-size: 10pt; }

		p { margin: 0; }

		td { vertical-align: top; }

		.items td { border-left: 0.1mm solid #000000; border-right: 0.1mm solid #000000; }

		table thead td { background-color: #EEEEEE; border: 0.1mm solid #000000; }

		.items td.blanktotal { background-color: #FFFFFF; border: 0 none #000000; border-top: 0.1mm solid #000000; border-right: 0.1mm solid #000000; }

		.items td.totals { text-align: right; border: 0.1mm solid #000000; }

		.main { margin-bottom: 20px; }

		#invoice-header { content: running(header); }

		#invoice-footer { content: running(footer); }

		@page {

		@top-center { content: element(header); }
		@bottom-center { content: element(footer); }
			}

		.print-top {
			border-bottom: 1px solid #444;
			height: 60px;
			margin-bottom: 5px;
			padding-bottom: 5px;
		}

		.print-bottom {
			margin-top: 5px;
			border-top: 1px solid #444;
			padding-top: 3px;
		}

		.print-headers {
			font-weight: bold;
			background-color: #efefef;
			font-size: 15px;
			padding: 3px 0 3px 3px;
		}

		.print-body {
			padding: 5px;
			margin-bottom: 5px;
		}

		.print-body p { margin-bottom: 12px; }

	</style>
</head>

<body>


<div id="invoice-header" class="print-top" style="">
	<table width="100%">
		<tr>
			<td width="50%"><img src="${pageContext.request.scheme}://${pageContext.request.localName}:${pageContext.request.localPort}/media/images/logo.png" /></td>
			<td width="50%" style="text-align: right;">
				<div>Date: ${wmfmt:formatDate("M/d/yyyy", now)}</div>
			</td>
		</tr>
	</table>
</div>


<div class="main">

	<div style="border-bottom: 1px solid #000; padding-bottom: 2mm; margin-bottom: 2mm;">
		<table>
			<tr>
				<td width="85" style="font-size: 12pt;">Customer:</td>
				<td style="font-size: 12pt;"><c:out value="${currentUser.companyName}" /> - <c:out value="${form.first_name}" /> <c:out value="${form.last_name}" /></td>
			</tr>
		</table>
	</div>

	<table>
		<tr>
			<td width="85">Name:</td>
			<td><c:out value="${form.first_name}" /> <c:out value="${form.last_name}" /></td>
		</tr>
		<tr>
			<td>Address:</td>
			<td>
				<c:out value="${form.address1}" /><c:if test="${not empty form.address2}"> <c:out value="${form.address2}" /></c:if><br/>
				<c:out value="${form.city}" />, <c:out value="${form.state}" /> <c:out value="${form.postalCode}" /> <c:out value="${form.country}" />
			</td>
		</tr>
		<tr>
			<td>Phone:</td>
			<td><c:out value="${form.phone}" /></td>
		</tr>
	</table>

	<br/><br/><br/>

	<table class="items" width="100%" style="font-size: 9pt; border-collapse: collapse;" cellpadding="8">
		<thead>
		<tr>
			<td width="10%" style="text-align: center;">Qty</td>
			<td width="60%">Description</td>
			<td width="15%" style="text-align: right;">Unit Price</td>
			<td width="15%" style="text-align: right;">Total</td>
		</tr>
		</thead>
		<tbody>
		<tr>
			<td align="center">1</td>
			<td>Work deposit on account to be drawn down as work is issued</td>
			<td align="right"><fmt:formatNumber value="${form.amount}" currencySymbol="$" type="currency"/></td>
			<td align="right"><fmt:formatNumber value="${form.amount}" currencySymbol="$" type="currency"/></td>
		</tr>
		<tr>
			<td class="blanktotal" colspan="2" rowspan="3"></td>
			<td class="totals">Subtotal:</td>
			<td class="totals"><fmt:formatNumber value="${form.amount}" currencySymbol="$" type="currency"/></td>
		</tr>
		<tr>
			<td class="totals">Tax:</td>
			<td class="totals"></td>
		</tr>
		<tr>
			<td class="totals"><b>TOTAL:</b></td>
			<td class="totals"><b><fmt:formatNumber value="${form.amount}" currencySymbol="$" type="currency"/></b></td>
		</tr>
		</tbody>
	</table>

	<br/><br/><br/>

	<span style="font-size: 14pt;">Terms: Due on Receipt</span>

</div>


<div id="invoice-footer" style="">
	<div class="print-body">
		<div class="print-bottom">
			<div style="padding-bottom: 2mm;">Comments: Work Market ID <c:out value="${currentUser.companyId}" /> </div>
			<div style="padding-bottom: 2mm;">Invoices can be paid via credit card, wire transfer, direct deposit, and
				check. <a href="http://www.workmarket.com/funds/add">Add funds now</a></div>
			<div style="padding-bottom: 4mm;">
				Purchase Order #: <c:out value="${form.po_number}" />
			</div>
			<div style="border-top: 1px solid #000; font-size: 9pt; padding-top: 3mm;">
				<table width="100%">
					<tr>
						<td style="width: 75mm;">
							Send payment to:<br/>
							Work Market, Inc.<br/>
							Attn: ID <c:out value="${currentUser.companyId}" /> <br/>
							Reference: Lockbox #7875<br/>
							PO Box 7247<br/>
							Philadelphia, PA 19170-7875
						</td>
						<td></td>
						<td style="width: 75mm;">
							Overnight payments only to:<br/>
							First Data/Remitco<br/>
							Attn: Work Market/LBX # 7875<br/>
							Ref: <c:out value="${currentUser.companyId}" /><br/>
							400 White Clay Center Drive<br/>
							Newark, DE 19711
						</td>
						<td></td>
						<td>
							<strong style="font-size: 11pt;">Work Market, Inc.</strong><br/>
							www.workmarket.com<br/>
							T: 212-229-9675<br/>
							F: 212-647-9675
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>

</body>
</html>
