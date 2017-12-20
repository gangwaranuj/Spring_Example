<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
	<style type="text/css">
		body {
			font-family: sans-serif;
			font-size: 10pt;
		}

		@page {
			background: #fff url('${mediaPrefix}/images/taxes/2011_1099MISC-1.png') no-repeat 50% 0 !important;
		}

		@page instructions {
			background: #fff url('${mediaPrefix}/images/taxes/2011_1099MISC-2.png') no-repeat 50% 0 !important;
		}

		#payer {
			font-weight: bold;
			position: absolute;
			top: 40px;
			left: 30px;
		}

		#payertaxid {
			font-weight: bold;
			position: absolute;
			top: 180px;
			left: 30px;
		}

		#recipienttaxid {
			font-weight: bold;
			position: absolute;
			top: 180px;
			left: 190px;
		}

		#recipientname {
			font-weight: bold;
			position: absolute;
			top: 225px;
			left: 30px;
		}

		#recipientaddress {
			font-weight: bold;
			position: absolute;
			top: 280px;
			left: 30px;
		}

		#recipientcitystatezip {
			font-weight: bold;
			position: absolute;
			top: 325px;
			left: 30px;
		}

		#compensation {
			font-weight: bold;
			position: absolute;
			top: 247px;
			left: 400px;
		}
	</style>
</head>
<body>

<div id="payer">
	WORK MARKET INC<br />
	240 WEST 37th STREET<br />
	NEW YORK NY 10018<br />
	1-212-229-9675
</div>

<div id="payertaxid">
	27-2580820
</div>

<div id="recipienttaxid">
	<c:out value="${requestScope.taxNumber}"/>
</div>

<div id="recipientname">
	<c:out value="${requestScope.activeEntry.taxName}"/>
	<c:if test="${not empty requestScope.activeEntry.businessNameFlag && requestScope.activeEntry.businessNameFlag}">
		<br />
		<c:out value="${requestScope.activeEntry.businessName}"/>
	</c:if>
</div>

<div id="recipientaddress">
	<c:out value="${requestScope.activeEntry.address}"/>
</div>

<div id="recipientcitystatezip">
	<c:out value="${requestScope.activeEntry.city}"/>
	<c:out value="${requestScope.activeEntry.state}"/>
	<c:out value="${requestScope.activeEntry.postalCode}"/>
</div>

<div id="compensation">
	<fmt:formatNumber value="${requestScope.amount}" pattern="0.00" />
</div>

<pagebreak page-selector="instructions" />

</body>
</html>
