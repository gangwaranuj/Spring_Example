<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<style type="text/css">
		.main {
			margin-bottom: 20px;
		}

		.dotted {
			border-style: dotted;
		}

		.no-break div { page-break-inside: avoid;}

		.fl { float: left; }

		.fr { float: right; }

		.border { border: 1px solid #ababab; }

		.tar { text-align: right; }

		.tac { text-align: center; }

		.red { color: #f00; }

		.br { margin-bottom: 10px; }

		.strong { font-weight: bold; }

		object { display: block; }

			/* GLOBAL STYLES */
		body {
			font-family: Arial, Helvetica, sans-serif;
			font-size: 12px;
		}

		.print-top {
			border-bottom: 1px solid #444;
			height: 60px;
			margin-bottom: 5px;
			padding: 20px 0 5px;
		}

		.print-left { width: 71%; }

		.print-right { width: 28%; }

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
			overflow: hidden;
		}

		.print-body p { margin-bottom: 12px; }

		.callouts { font-size: 15px; }

		.clearfloat { clear: both; }

		.sectiontitle {
			font-size: 13px;
			font-weight: bold;
		}

			/* ADDITIONAL STYLES */
		.logo {
			height: 50px;
			margin-top: 5px;
		}

		.inkbox {
			border-bottom: 1px solid #444;
			width: 65%;
			height: 12px;
			margin: 5px 0;
		}

		.inline {
			float: left;
			display: inline;
			margin-left: 5px;
		}

		.time-tracking-entry {
			margin-top: 3px;
			border-top: 1px solid #ddd;
		}

		.notes-body { padding: 10px; }

		.notes-box { border-bottom: 0.5px solid #e6e6e6; }

		.sigboxes {
			border-top: 1px solid #444;
		}

		.sigbox {
			width: 28%;
			margin-right: 25px;
			float: left;
		}

		.sig-label {
			border-top: 1px solid #444;
			clear: left;
		}

		.sig-value {
			float: left;
			clear: left;
			font-style: italic;
			font-size: 20px;
			padding-top: 30px;
		}

		.signature-image {
			margin-top: -40px;
		}

		.top-left { width: 337px; }

		.top-right { width: 337px; }

		.avatar {
			border: 1px solid #BBBBBB;
			min-width: 48px;
			min-height: 48px;
			padding: 5px;
		}

		#resource-header { position: running(resource-header); }

		#resource-header-2 { position: running(resource-header-2); }

		#resource-footer { position: running(resource-footer); }

		#customer-header { position: running(customer-header); }

		#instructions-header { position: running(instructions-header); }

		#badge-header { position: running(badge-header); }

		#footer { position: running(footer); }

		@page :first {
			@top-right { content: element(resource-header);}
			@bottom-left {
				content: "Thank you. Your business is appreciated.";
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
			@bottom-right {
				content: "Page " counter(page) " of " counter(pages);
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
		}

		@page {
			@top-right { content: element(resource-header-2);}
			padding: 50px 0 0;
		}

		@page CustomerCopyPage {
			@top-right { content: element(customer-header); }
			@bottom-left {
				content: "Thank you. Your business is appreciated.";
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
			@bottom-right {
				content: "Page " counter(page) " of " counter(pages);
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
		}

		@page InstructionsPage {
			@top-right { content: element(instructions-header); }
			@bottom-left {
				content: "Thank you. Your business is appreciated.";
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
			@bottom-right {
				content: "Page " counter(page) " of " counter(pages);
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
		}

		@page PrintBadgePage {
			@top-right { content: element(badge-header); }
			@bottom-left {
				content: "Thank you. Your business is appreciated.";
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
			@bottom-right {
				content: "Page " counter(page) " of " counter(pages);
				font-family: Arial, Helvetica, sans-serif;
				font-size: 12px;
				border-top-style: solid;
				border-top-width: thin;
				border-top-color: #000000;
			}
		}

		#resource-copy-page { page: ResourceCopyPage; }

		#customer-copy-page { page: CustomerCopyPage; }

		#resource-instructions-page { page: InstructionsPage; }

		#print-badge-page { page: PrintBadgePage; }

		.work-resolution {
			min-height: 7em;
			word-wrap: break-word;
		}

	</style>
</head>
<body>

<div id="resource-header" style="">
	<div class="print-top">
		<div class="logo fl">
			<c:choose>
				<c:when test="${not empty work.company.avatarLarge and work.configuration.useCompanyLogoFlag}">
					<img height="50" src="<c:out value="${wmfmt:stripXSS(work.company.avatarLarge.uri)}"/>"/>
				</c:when>
				<c:when test="${not work.configuration.hideWorkMarketLogoFlag}">
					<img height="50" src="${mediaPrefix}/images/logo.png"/>
				</c:when>
			</c:choose>
		</div>
		<c:if test="${work.configuration.enablePrintoutSignature}">
			<div class="fr">
				<div class="callouts tar strong">RESOURCE COPY &mdash; CUSTOMER SIGNATURE REQUIRED</div>
				<div class="tar">Fax back if requested. Keep for your records</div>
				<div class="callouts tar">Assignment ID: <c:out value="${work.workNumber}"/></div>
			</div>
		</c:if>
		<div class="clearfloat"></div>
	</div>
</div>

<div id="resource-header-2" style="">
	<div class="print-top">
		<div class="logo fl">
			<c:choose>
				<c:when test="${not empty work.company.avatarLarge and work.configuration.useCompanyLogoFlag}">
					<img height="50" src="<c:out value="${wmfmt:stripXSS(work.company.avatarLarge.uri)}"/>"/>
				</c:when>
				<c:when test="${not work.configuration.hideWorkMarketLogoFlag}">
					<img height="50" src="${mediaPrefix}/images/logo.png"/>
				</c:when>
			</c:choose>
		</div>
		<div class="clearfloat"></div>
	</div>
</div>

<div id="customer-header" style="">
	<div class="print-top">
		<div class="logo fl">
			<c:choose>
				<c:when test="${not empty work.company.avatarLarge and work.configuration.useCompanyLogoFlag}">
					<img height="50" src="<c:out value="${wmfmt:stripXSS(work.company.avatarLarge.uri)}"/>"/>
				</c:when>
				<c:when test="${not work.configuration.hideWorkMarketLogoFlag}">
					<img height="50" src="${mediaPrefix}/images/logo.png"/>
				</c:when>
			</c:choose>
		</div>
		<div class="fr">
			<div class="callouts tar strong">CUSTOMER COPY</div>
			<div class="callouts tar">Assignment ID: <c:out value="${work.workNumber}"/></div>
		</div>
		<div class="clearfloat"></div>
	</div>
</div>


<div id="instructions-header" style="">
	<div class="print-top">
		<div class="fr">
			<div class="callouts tar strong">RESOURCE INSTRUCTIONS</div>
			<div class="callouts tar">Assignment ID: <c:out value="${work.workNumber}"/></div>
		</div>
		<div class="clearfloat"></div>
	</div>
</div>

<div id="badge-header" style="">
	<div class="print-top">
		<div class="fr">
			<div class="callouts tar strong">ON-SITE INSTRUCTIONS</div>
			<div class="callouts tar">Assignment ID: <c:out value="${work.workNumber}"/></div>
		</div>
		<div class="clearfloat"></div>
	</div>
</div>


<!-- PAGE 1 // MAIN WRAPPER DIV -->
<!-- id="resource-copy-page" -->
<div class="main">

	<!-- PRINT LEFT // ASSIGNMENT DETAILS -->
	<div class="print-left fl clearfloat">

		<div class="print-headers">Title</div>
		<div class="print-body border">
			<c:out value="${work.title}"/>
		</div>

		<div class="print-headers">Description</div>
		<div class="print-body border">
			<c:out value="${wmfmt:tidy(work.description)}" escapeXml="false"/>
		</div>


		<c:if test="${not empty work.customFieldGroups && work.customFieldGroups[0].printable}">
			<div class="print-headers no-break">Custom Information</div>
			<div class="print-body border no-break">
				<ul>
				<c:forEach var="mainItem" items="${work.customFieldGroups}">
					<c:forEach var="item" items="${mainItem.fields}">
						<c:if test="${item.showOnPrintout}">
							<li style="margin-bottom: 10px; padding-bottom: 10px;"><c:out value="${item.name}"/>: <c:out value="${item.value}"/></li>
						</c:if>
					</c:forEach>
				</c:forEach>
				</ul>
			</div>
		</c:if>

		<c:if test="${work.configuration.standardTermsEndUserFlag}">
			<div class="no-break">
				<div class="print-headers no-break">General Terms - End User</div>
				<div class="print-body border no-break">
					<c:out value="${wmfmt:tidy(wmfmt:nl2br(work.configuration.standardTermsEndUser))}" escapeXml="false"/>
				</div>
			</div>
		</c:if>
	</div>
	<!-- END PRINT LEFT -->

	<!-- ASSIGNMENT DATE / ARRIVAL TIME / ADDITIONAL INFO -->
	<div class="print-right fr">
		<div class="print-headers">Assignment Date</div>
		<div class="print-body border">
			<div>
				<c:choose>
					<c:when test="${work.schedule.range}">
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma', work.schedule.from, work.timeZone)}"/> to<br/>
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.through, work.timeZone)}"/>
					</c:when>
					<c:otherwise>
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
					</c:otherwise>
				</c:choose>
			</div>
			<c:choose>
				<c:when test="${work.configuration.checkinRequiredFlag}">
					<div class="strong red">
						CHECK IN REQUIRED
					</div>
				</c:when>
				<c:when test="${work.checkinCallRequired}">
					<div class="strong red">
						For this assignment, please call <c:out value="${work.checkinContactName}"/> at <c:out
							value="${work.checkinContactPhone}"/>
					</div>
				</c:when>
			</c:choose>

			<br/>

			<c:choose>
				<c:when test="${not empty work.activeResource.timeTrackingLog}">
					<div class="sectiontitle">CHECK IN / OUT TIMES</div>
					<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-log.jsp"/>
				</c:when>
				<c:otherwise>
					<div class="sectiontitle">ARRIVAL TIME</div>
					<div class="inkbox inline"></div>
					<div class="inline">AM/PM</div>

					<div class="sectiontitle clearfloat">DEPARTURE TIME</div>
					<div class="inkbox inline"></div>
					<div class="inline">AM/PM</div>
				</c:otherwise>
			</c:choose>
			<div class="clearfloat"></div>
		</div>

		<div class="print-headers">Contact Information</div>
		<div class="print-body border">
			<c:if test="${not empty work.locationContact}">
				<div class="sectiontitle">Customer Contact</div>
				<div>
					<c:out value="${work.locationContact.name.firstName}"/>
					<c:out value="${work.locationContact.name.lastName}"/>
					<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
						<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
							<c:if test="${not empty phone.phone}">
								<c:out value="${wmfmt:phone(phone.phone)}"/>
								<c:if test="${not empty phone.extension}">
									x <c:out value="${phone.extension}"/>
								</c:if>
								<br/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
				<br/>
			</c:if>

			<c:if test="${not empty work.supportContact}">
				<div class="sectiontitle">Support Contact</div>
				<div>
					<c:out value="${work.supportContact.name.firstName}"/> <c:out
						value="${work.supportContact.name.lastName}"/><br/>
					<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
						<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
							<c:if test="${not empty phone.phone}">
								<c:out value="${wmfmt:phone(phone.phone)}"/>
								<c:if test="${not empty phone.extension}">
									x <c:out value="${phone.extension}"/>
								</c:if>
								<br/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</c:if>
		</div>

		<div class="print-headers">Assignment Location</div>
		<div class="print-body border">
			<c:choose>
				<c:when test="${not empty work.location}">

					<c:if test="${not empty work.location.name}">
						<c:if test="${not isResource or work.status.code ne workStatusTypes.SENT}">
							<strong><c:out value="${work.location.name}"/></strong><br/>
						</c:if>
					</c:if>

					<c:if test="${not empty work.location.number}">
						<c:if test="${not isResource}">
							ID: <c:out value="${work.location.number}"/><br/>
						</c:if>
					</c:if>

					<c:if test="${not empty work.location.address}">
						<c:out value="${workResponse.work.location.address.addressLine1}"/><br/>
						<c:if test="${not empty workResponse.work.location.address.addressLine2}">
							<c:out value="${workResponse.work.location.address.addressLine2}"/><br/>
						</c:if>
						<c:out value="${workResponse.work.location.address.city}, "/>
						<c:out value="${workResponse.work.location.address.state} "/>
						<c:out value="${workResponse.work.location.address.zip}"/><br/>
						<c:out value="${workResponse.work.location.address.country}"/>

					</c:if>
				</c:when>
				<c:otherwise>
					This job is <strong class="strong">virtual / off-site</strong>.
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="clearfloat"></div>

	<!-- APPROVAL AND SIGNATURES AND NOTES -->
	<div class="print-headers no-break">Notes</div>
	<div class="print-body border notes-body no-break">
		<div class="notes-box">&nbsp;</div> <%-- create an empty lined box for handwritten notes--%>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
	</div>

	<c:if test="${not empty work.resolution}">
		<div class="print-headers no-break">Summary of Work</div>
		<div class="print-body border work-resolution">
			<c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/>
		</div>
	</c:if>

	<c:if test="${work.configuration.enablePrintoutSignature}">
		<div class="no-break">
			<div class="print-headers">Approval</div>
			<div class="print-body border">
				<c:choose>
					<c:when test="${not empty work.company.customSignatureLine}">
						<div><c:out value="${work.company.customSignatureLine}"/></div>
					</c:when>
					<c:otherwise>
						<div>By signing below, you acknowledge your agreement with the satisfactory completion of the
							assignment details listed above. Additionally, you verify the accuracy of the arrival and
							departure time(s) entered on this form.
						</div>
					</c:otherwise>
				</c:choose>

				<div class="sigbox">
					<div class="sig-value">
						<c:if test="${not empty signatureSignerName}">
							<c:out value="${signatureSignerName}"/>
						</c:if>
					</div>
					<div class="sig-label">Customer Name (Printed)</div>
				</div>

				<div class="sigbox">
					<div class="sig-value">
						<c:if test="${not empty signatureImageUrl}">
							<img class="signature-image" height="50px" src="<c:out value="${wmfmt:stripXSS(signatureImageUrl)}"/>"/>
						</c:if>
					</div>
					<div class="sig-label">Customer Signature</div>
				</div>

				<div class="sigbox">
					<div class="sig-value">
						<c:if test="${not empty signatureDate}"><c:out value="${wmfmt:formatCalendar('MM/dd/yyyy', signatureDate)}"/></c:if>
					</div>
					<div class="sig-label">Date</div>
				</div>
			</div>
		</div>
	</c:if>

	<div class="clearfloat"></div>
</div>
<!-- end main -->


<!-- PAGE 2 // MAIN WRAPPER DIV -->
<div class="main" id="customer-copy-page">


	<!-- PRINT LEFT // ASSIGNMENT DETAILS -->
	<div class="print-left fl clearfloat">
		<div class="print-headers">Title</div>
		<div class="print-body border">
			<c:out value="${work.title}"/>
		</div>

		<div class="print-headers">Description</div>
		<div class="print-body border">
			<c:out value="${wmfmt:tidy(work.description)}" escapeXml="false"/>
		</div>

		<c:if test="${not empty work.customFieldGroups && work.customFieldGroups[0].printable}">
			<div class="print-headers">Custom Information</div>
			<div class="print-body border">
				<ul>
					<c:forEach var="item" items="${work.customFieldGroups[0].fields}">
						<c:if test="${item.showOnPrintout}">
							<li style="margin-bottom: 10px; padding-bottom: 10px;"><c:out value="${item.name}"/>: <c:out value="${item.value}"/></li>
						</c:if>
					</c:forEach>
				</ul>
			</div>
		</c:if>

	</div>
	<!-- END PRINT LEFT -->

	<!-- ASSIGNMENT DATE / ARRIVAL TIME / ADDITIONAL INFO -->
	<div class="print-right fr">
		<div class="print-headers">Assignment Date</div>
		<div class="print-body border">
			<div>
				<c:choose>
					<c:when test="${work.schedule.range}">
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma', work.schedule.from, work.timeZone)}"/> to<br/>
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.through, work.timeZone)}"/>
					</c:when>
					<c:otherwise>
						<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
					</c:otherwise>
				</c:choose>
			</div>
			<c:if test="${work.configuration.checkinRequiredFlag}">
				<div class="strong red">
					CHECK IN REQUIRED
				</div>
			</c:if>

			<br/>

			<c:choose>
				<c:when test="${not empty work.activeResource.timeTrackingLog}">
					<div class="sectiontitle">CHECK IN / OUT TIMES</div>
					<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-log.jsp"/>
				</c:when>
				<c:otherwise>
					<div class="sectiontitle">ARRIVAL TIME</div>
					<div class="inkbox inline"></div>
					<div class="inline">AM/PM</div>

					<div class="sectiontitle clearfloat">DEPARTURE TIME</div>
					<div class="inkbox inline"></div>
					<div class="inline">AM/PM</div>
				</c:otherwise>
			</c:choose>
			<div class="clearfloat"></div>
		</div>

		<div class="print-headers">Contact Information</div>
		<div class="print-body border">
			<c:if test="${not empty work.locationContact}">
				<div class="sectiontitle">Customer Contact</div>
				<div>
					<c:out value="${work.locationContact.name.firstName}"/> <c:out
						value="${work.locationContact.name.lastName}"/><br/>
					<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
						<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
							<c:if test="${not empty phone.phone}">
								<c:out value="${wmfmt:phone(phone.phone)}"/>
								<c:if test="${not empty phone.extension}">
									x <c:out value="${phone.extension}"/>
								</c:if>
								<br/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
				<br/>
			</c:if>

			<c:if test="${not empty work.supportContact}">
				<div class="sectiontitle">Support Contact</div>
				<div>
					<c:out value="${work.supportContact.name.firstName}"/> <c:out
						value="${work.supportContact.name.lastName}"/><br/>
					<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
						<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
							<c:if test="${not empty phone.phone}">
								<c:out value="${wmfmt:phone(phone.phone)}"/>
								<c:if test="${not empty phone.extension}">
									x <c:out value="${phone.extension}"/>
								</c:if>
								<br/>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</c:if>
		</div>

		<div class="print-headers">Assignment Location</div>
		<div class="print-body border">
			<c:choose>
				<c:when test="${not empty work.location}">
					<c:if test="${not empty work.location.name}">
						<c:if test="${not isResource or work.status.code ne workStatusTypes.SENT}">
							<strong><c:out value="${work.location.name}"/></strong><br/>
						</c:if>
					</c:if>
					<c:if test="${not empty work.location.number}">
						<c:if test="${not isResource}">
							ID: <c:out value="${work.location.number}"/><br/>
						</c:if>
					</c:if>

					<c:if test="${not empty work.location.address}">
						<c:out value="${workResponse.work.location.address.addressLine1}"/><br/>
						<c:if test="${not empty workResponse.work.location.address.addressLine2}">
							<c:out value="${workResponse.work.location.address.addressLine2}"/><br/>
						</c:if>
						<c:out value="${workResponse.work.location.address.city}, "/>
 						<c:out value="${workResponse.work.location.address.state} "/>
						<c:out value="${workResponse.work.location.address.zip}"/><br/>
						<c:out value="${workResponse.work.location.address.country}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					This job is <strong class="strong">virtual / off-site</strong>.
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="clearfloat"></div>

	<!-- APPROVAL AND SIGNATURES -->
	<div class="print-headers no-break">Notes</div>
	<div class="print-body border notes-body no-break">
		<div class="notes-box">&nbsp;</div> <%-- create an empty lined box for handwritten notes--%>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
		<div class="notes-box">&nbsp;</div>
	</div>

	<c:if test="${not empty work.resolution}">
		<div class="print-headers no-break">Summary of Work</div>
		<div class="print-body border work-resolution">
			<c:out value="${wmfmt:tidy(work.resolution)}" escapeXml="false"/>
		</div>
	</c:if>

<c:if test="${work.configuration.enablePrintoutSignature}">
	<div class="no-break">
		<div class="print-headers">Approval</div>
		<div class="print-body border">
			<c:choose>
				<c:when test="${not empty work.company.customSignatureLine}">
					<div><c:out value="${work.company.customSignatureLine}"/></div>
				</c:when>
				<c:otherwise>
					<div>By signing below, you acknowledge your agreement with the satisfactory completion of the
						assignment details listed above. Additionally, you verify the accuracy of the arrival and
						departure time(s) entered on this form.
					</div>
				</c:otherwise>
			</c:choose>
			<div class="sigbox">
				<div class="sig-value">
					<c:if test="${not empty signatureSignerName}">
						<c:out value="${signatureSignerName}"/>
					</c:if>
				</div>
				<div class="sig-label">Customer Name (Printed)</div>
			</div>

			<div class="sigbox">
				<div class="sig-value">
					<c:if test="${not empty signatureImageUrl}">
						<img class="signature-image" height="50px" src="<c:out value="${wmfmt:stripXSS(signatureImageUrl)}"/>"/>
					</c:if>
				</div>
				<div class="sig-label">Customer Signature</div>
			</div>

			<div class="sigbox">
				<div class="sig-value">
					<c:if test="${not empty signatureDate}"><c:out value="${wmfmt:formatCalendar('MM/dd/yyyy', signatureDate)}"/></c:if>
				</div>
				<div class="sig-label">Date</div>
			</div>
		</div>
	</div>
</c:if>

</div>
<!-- end main -->


<!-- PAGE 3 // MAIN WRAPPER DIV -->
<div class="main" id="resource-instructions-page">

<!-- PRINT LEFT // ASSIGN. DETAILS / ADDL INFO -->

<div class="print-left fl">

	<!-- INSTRUCTIONS -->

	<div class="print-headers">Title</div>
	<div class="print-body border">
		<c:out value="${work.title}"/>
	</div>

	<c:if test="${not empty work.supportContact}">
		<div class="clearfloat print-body border">
			<div class="red tac">
				All spend limit requests must be documented and approved.<br/>
				Questions, change of scope or spend limit requests should be directed to:<br/>
				<c:out value="${work.supportContact.name.firstName}"/> <c:out
					value="${work.supportContact.name.lastName}"/>,
				<c:out value="${work.supportContact.email}"/>,
				<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
					<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
						<c:if test="${not empty phone.phone}">
							<c:out value="${wmfmt:phone(phone.phone)}"/>
							<c:if test="${not empty phone.extension}">
								x <c:out value="${phone.extension}"/>
							</c:if>
							<br/>
						</c:if>
					</c:forEach>
				</c:if>
			</div>

		</div>
	</c:if>

	<div class="print-headers">Description</div>
	<div class="print-body border">
		<c:out value="${wmfmt:tidy(work.description)}" escapeXml="false"/>
	</div>

	<c:if test="${not empty work.location.instructions}">
		<div class="print-headers">Travel Instructions</div>
		<div class="print-body border">
			<c:out value="${work.location.instructions}"/>
		</div>
	</c:if>
	<c:if test="${not empty work.instructions}">
		<div class="print-headers">Instructions</div>
		<div class="print-body border">
			<c:out value="${wmfmt:tidy(work.instructions)}" escapeXml="false"/>
		</div>
	</c:if>

	<div class="print-headers">Completion Details</div>

	<c:if test="${not empty work.deliverableRequirementGroupDTO}">
		<div class="print-body border">
			<c:set var="deliverableCount" value="0"/>
			<c:forEach var="deliverableRequirementDTO" items="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
				<c:set var="deliverableCount" value="${deliverableCount + deliverableRequirementDTO.numberOfFiles}"/>
			</c:forEach>
			<c:if test="${not empty work.deliverableRequirementGroupDTO.instructions}">
				<strong>Instructions</strong>
				<br/>
				<c:out escapeXml="false" value="${wmfmt:tidy(work.deliverableRequirementGroupDTO.instructions)}"/>
			</c:if>
			<br/>
			<br/>
			<c:choose>
				<c:when test="${work.deliverableRequirementGroupDTO.hoursToComplete > 0}">
					<strong>Deadline</strong>
					<br/>
					Deadline to submit attachments is <strong><c:out value="${work.deliverableRequirementGroupDTO.hoursToComplete}"/> </strong> hours after assignment start.
					<br/>
					<br/>
				</c:when>
				<c:otherwise>
					No deadline for attachments.
				</c:otherwise>
			</c:choose>
			<strong>Deliverables</strong>
			<br/>
			You are required to include <span class="strong"><c:out value="${deliverableCount}"/></span>
			attachment(s) for this assignment:<br/>
			<c:forEach var="deliverableRequirementDTO" items="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
				<div>- <c:out value="${deliverableRequirementDTO.numberOfFiles}"/> <c:out value="${wmfn:translateDeliverableTypeToName(deliverableRequirementDTO.type)}"/></div>
			</c:forEach>
			<br/>
		</div>
	</c:if>

	<c:if test="${not empty work.desiredSkills}">
		<div class="print-headers">Skills &amp; Specialities</div>
		<div class="print-body border">
			<c:out value="${work.desiredSkills}"/>
		</div>
	</c:if>

	<c:if test="${(not empty work.customFieldGroups and work.customFieldGroups[0].printable)}">
		<div class="print-headers">Custom Information</div>
		<div class="print-body border">
			<ul>
				<c:if test="${not empty work.customFieldGroups}">
					<c:forEach var="item" items="${work.customFieldGroups[0].fields}">
						<c:if test="${item.showOnPrintout || item.visibleToResource}">
							<li style="margin-bottom: 10px; padding-bottom: 10px;"><c:out value="${item.name}"/>: <c:out
									value="${item.value}"/></li>
						</c:if>
					</c:forEach>
				</c:if>
			</ul>
			<br/>
		</div>
	</c:if>

	<c:if test="${not empty work.partGroup}">
		<div class="print-headers">Parts &amp; Logistics</div>
		<div class="print-body border">
			<c:choose>
				<c:when test="${work.partGroup.isSuppliedByWorker()}">
					Parts will be supplied by the worker.
				</c:when>
				<c:otherwise>
					Parts will be supplied by the client.
					<br/>

					<c:choose>
						<c:when test="${fn:toUpperCase(work.partGroup.shippingDestinationType) == 'PICKUP'}">
							The part will need to be picked up at the following location prior to arriving onsite.<br/>
							<c:out escapeXml="false" value="${wmfmt:formatAddressDTO(work.partGroup.shipToLocation)}" />
						</c:when>
						<c:when test="${fn:toUpperCase(work.partGroup.shippingDestinationType) == 'WORKER'}">
							The parts will be shipped to the address specified on your profile.
							<c:if test="${not empty work.activeResource and not empty work.activeResource.user.profile.address}">
								<br/>
								<c:out escapeXml="false" value="${wmfmt:formatAddressLong(work.activeResource.user.profile.address)}" />
							</c:if>
						</c:when>
						<c:otherwise>
							The parts will be on location.
						</c:otherwise>
					</c:choose>

				</c:otherwise>
			</c:choose>

			<c:if test="${work.partGroup.isReturnRequired()}">
				<br/><br/>
				<c:choose>
					<c:when test="${not empty work.partGroup.returnToLocation}">
						Parts must be returned to the following location:<br/>
						<c:out escapeXml="false" value="${wmfmt:formatAddressDTO(work.partGroup.returnToLocation)}" />
					</c:when>
					<c:otherwise>
						Any parts must be returned to the manager on duty at the work location.
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:forEach var="part" items="${work.partGroup.parts}">
				<br/><br/>
				Part name: <span class="strong"><c:out value="${part.trackingNumber}"/></span><br/>
				Tracking number: <span class="strong"><c:out value="${part.trackingNumber}"/></span><br/>
				Shipping provider: <span class="strong"><c:out value="${part.shippingProvider}"/></span><br/>
				Part value: <span class="strong"><c:out value="${part.partValue}"/></span><br/>
				Needs to be returned?: <span class="strong"><c:out value="${part.isReturn() ? 'Yes' : 'No' }"/></span>
			</c:forEach>
		</div>
	</c:if>

	<div class="print-headers">Code of Conduct</div>
	<div class="print-body border">
		<c:choose>
			<c:when test="${not empty work.configuration.standardInstructions}">
				<p>
					<c:out value="${work.configuration.standardInstructions}"/>
				</p>
			</c:when>
			<c:otherwise>
				<p>
					Marketplace Guidelines for Workers
						Workers: you are the lifeblood of our community. You help power our marketplace and provide incredible value to companies looking for reliable
						and skilled on-demand talent. Avenues to worker success are reflected in your personal scorecards based on your quality of work, professionalism,
						communication, timeliness, and overall client satisfaction.
						Workers who faithfully perform their freelance duties will thrive in the Work Market community. The three most important tenets of worker
						behavior are professionalism, integrity and respect.
						Professionalism - Acting in a professional manner, thoroughly following assignment instructions and representing yourself in the best
						possible light will go a long way in helping you establish credibility on Work Market.
						Integrity - Simply put, do the right thing. Accurately report the work you’ve performed, be truthful with clients and return
						supplies and/or equipment.
						Respect - Follow the golden rule. Always speak to clients and workers respectfully, use formal channels
						(Work Market escalation team) in the event of disagreements and exercise patience until a resolution can be determined.
						Workers who breach our expectations of behavior will be subject to discipline and/or suspension depending on the severity of the
						incident. View our Work Community Guideline Violations for a list of behavioral penalties and consequences for both workers and companies.
					Marketplace Guidelines for Companies
						Companies: you are as important to the Work Market community as our workers are, and vital to ensuring marketplace equilibrium.
						Companies who act professionally and respectfully will excel in the Work Market community. You will be able to:
								- source skilled, on-demand talent
								- realize substantial cost savings
								- accelerate business growth through a reliable freelancer network
						Companies that thoughtfully craft and load assignments into the marketplace will be best positioned for success. This includes providing a
						detailed scope of work to minimize worker confusion and manage expectations. You should also seek to be responsive to workers, especially in
						the case where workers are attempting to communicate with you for assignment clarification or feedback.
						The three most important tenets of your behavior are timeliness of assignment approval,
						timeliness of assignment payment and frequency of assignment cancellations.
						Assignment Approval - Promptly approve assignments if they are properly submitted so you can maintain a superior company profile, attract high
						quality contractors and ensure workers are paid in a timely fashion.
						Assignment Payment - Abide by the mutually agreed upon payment terms so you can maintain a solid reputation in the marketplace and ensure workers
						continue to apply for your assignments. In the event of possible late payments, notify your workers as soon as possible.
						Assignment Cancellation - Judiciously use the assignment cancellation feature so you don’t risk hurting your company profile and damaging trust
						with your workers. Remember that cancellations directly affect your company ratings.
						Companies who breach our expectations of behavior will be subject to discipline and/or suspension depending on the severity of the incident. View
						our Work Market Community Guideline Violations for a list of behavioral penalties and consequences for both companies and workers.
				</p>
			</c:otherwise>
		</c:choose>
	</div>

	<c:if test="${work.configuration.standardTermsFlag}">
		<div class="print-headers">Terms of Agreement</div>
		<div class="print-body border">
			<p>
				<c:out value="${wmfmt:escapeHtml(work.configuration.standardTerms)}" escapeXml="false"/>
			</p>
		</div>
	</c:if>
	<div class="clearfloat"></div>
</div>

<!-- end print left fl -->

<!-- PRINT RIGHT// DETAILS / IVR-->

<div class="print-right fr">
	<div class="print-headers">Assignment Date</div>
	<div class="print-body border">
		<div>
			<c:choose>
				<c:when test="${work.schedule.range}">
					<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma', work.schedule.from, work.timeZone)}"/> to<br/>
					<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.through, work.timeZone)}"/>
				</c:when>
				<c:otherwise>
					<c:out value="${wmfmt:formatMillisWithTimeZone('MMMM d, yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${work.configuration.checkinRequiredFlag}">
			<div class="strong red">
				CHECK IN REQUIRED
			</div>
		</c:if>

		<br/>

		<c:choose>
			<c:when test="${not empty work.activeResource.timeTrackingLog}">
				<div class="sectiontitle">CHECK IN / OUT TIMES</div>
				<c:import url="/WEB-INF/views/mobile/partials/assignments/details/timetracking-log.jsp"/>
			</c:when>
			<c:otherwise>
				<div class="sectiontitle">ARRIVAL TIME</div>
				<div class="inkbox inline"></div>
				<div class="inline">AM/PM</div>

				<div class="sectiontitle clearfloat">DEPARTURE TIME</div>
				<div class="inkbox inline"></div>
				<div class="inline">AM/PM</div>
			</c:otherwise>
		</c:choose>
		<div class="clearfloat"></div>
	</div>

	<div class="print-headers">Contact Information</div>
	<div class="print-body border">
		<c:if test="${not empty work.locationContact}">
			<div class="sectiontitle">Customer Contact</div>
			<div>
				<c:out value="${work.locationContact.name.firstName}"/> <c:out
					value="${work.locationContact.name.lastName}"/><br/>
				<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
					<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
						<c:if test="${not empty phone.phone}">
							<c:out value="${wmfmt:phone(phone.phone)}"/>
							<c:if test="${not empty phone.extension}">
								x <c:out value="${phone.extension}"/>
							</c:if>
							<br/>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
			<br/>
		</c:if>

		<c:if test="${not empty work.supportContact}">
			<div class="sectiontitle">Support Contact</div>
			<div>
				<c:out value="${work.supportContact.name.firstName}"/> <c:out
					value="${work.supportContact.name.lastName}"/><br/>
				<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
					<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
						<c:if test="${not empty phone.phone}">
							<c:out value="${wmfmt:phone(phone.phone)}"/>
							<c:if test="${not empty phone.extension}">
								x <c:out value="${phone.extension}"/>
							</c:if>
							<br/>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
		</c:if>
	</div>

	<div class="print-headers">Assignment Location</div>
	<div class="print-body border">
		<c:choose>
			<c:when test="${not empty work.location}">
				<c:if test="${not empty work.location.name}">
					<c:if test="${not isResource or work.status.code ne workStatusTypes.SENT}">
						<strong><c:out value="${work.location.name}"/></strong><br/>
					</c:if>
				</c:if>
				<c:if test="${not empty work.location.number}">
					<c:if test="${not isResource}">
						ID: <c:out value="${work.location.number}"/><br/>
					</c:if>
				</c:if>
				<c:if test="${not empty work.location.address}">
					<c:out value="${workResponse.work.location.address.addressLine1}"/><br/>
					<c:if test="${not empty workResponse.work.location.address.addressLine2}">
						<c:out value="${workResponse.work.location.address.addressLine2}"/><br/>
					</c:if>
					<c:out value="${workResponse.work.location.address.city}, "/>
					<c:out value="${workResponse.work.location.address.state} "/>
					<c:out value="${workResponse.work.location.address.zip}"/><br/>
					<c:out value="${workResponse.work.location.address.country}"/>
				</c:if>
			</c:when>
			<c:otherwise>
				This job is <strong class="strong">virtual / off-site</strong>.
			</c:otherwise>
		</c:choose>
	</div>
	</div>
	<div class="clearfloat"></div>

<!-- end body border -->
</div>

<!-- PAGE 4 // Worker Badge //MAIN WRAPPER DIV -->
<c:if test="${!work.checkinCallRequired || (work.configuration.badgeIncludedOnPrintout && isActiveResource)}">
<div class="main" id="print-badge-page">

	<div class="print-left fl">
		<c:if test="${!work.checkinCallRequired}">
			<div class="fl" style="width: 100%">
				<div class="print-headers">Check-in Options</div>
				<div class="print-body border">
					<div class="br">Call IVR : 646-606-2562 your User ID is:
						<strong><c:out value="${work.activeResource.user.userNumber}"/></strong></div>
					<div class="br">Assignment ID:
						<strong><c:out value="${work.workNumber}"/></strong></div>
					<div class="br">Mobile site: log in to
						<strong>workmarket.com/mobile</strong> to check-in to your assignment</div>
				</div>
			</div>
			<div class="fl" style="width: 100%">
				<div class="print-body border">
					<div class="strong">Status Codes:</div>
					<div>1 - check in</div>
					<div>2 - check out</div>
					<div>3 - confirm this assignment</div>
				</div>
			</div>
		</c:if>

		<c:if test="${isActiveResource && work.configuration.badgeIncludedOnPrintout}">
			<div class="fl no-break" style="width: 100%">
				<div class="print-headers">Print Badge</div>
				<div class="print-body border">
					<div class="main">
						<p>Use this badge to take with you and show on site for your assignment.</p>
						<div>
							<img class="small" style="width: 30px; height: 20px;" src="${mediaPrefix}/images/scissors.png"/>
						</div>
						<div class="small rounded" id="badge" style="border: 2px dashed #000; width: 340px; height: 200px; padding: 5px;">
							<table width="100%">
								<tr>
									<td width="70" valign="top">
										<c:choose>
											<c:when test="${not empty work.activeResource.user.avatarSmall}">
												<img src="<c:out value="${wmfmt:stripXSS(work.activeResource.user.avatarSmall.uri)}" />" class="avatar"/>
											</c:when>
											<c:otherwise>
												<img src="${mediaPrefix}/images/no_picture.png" class="avatar"/>
											</c:otherwise>
										</c:choose>
									</td>
									<td valign="top">
										<strong><c:out value="${work.activeResource.user.name.firstName}"/> <c:out
												value="${work.activeResource.user.name.lastName}"/></strong><br/>
									</td>

									<td valign="top">
										<div class="logo fr">
											<c:if test="${not empty work.company.avatarLarge and work.configuration.useCompanyLogoFlag}">
												<img height="50" src="<c:out value="${wmfmt:stripXSS(work.company.avatarLarge.uri)}"/>"/>
											</c:if>
										</div>
									</td>
								</tr>
							</table>
							<table width="100%" class="br">
								<tr>
									<td>
										<div class="xsmall" style="text-align: bottom;">
											On behalf of: <strong>
											<c:choose>
												<c:when test="${work.configuration.badgeShowClientName}">
													<c:out value="${work.clientCompany.name}"/>
												</c:when>
												<c:otherwise>
													<c:out value="${work.company.name}"/>
												</c:otherwise>
											</c:choose>
										</strong>
											<br/>
											Valid:
											<c:choose>
												<c:when test="${work.schedule.range}">
													<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma', work.schedule.from, work.timeZone)}"/> to<br/>
													<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma z', work.schedule.through, work.timeZone)}"/>
												</c:when>
												<c:otherwise>
													<c:out value="${wmfmt:formatMillisWithTimeZone('M/dd/yyyy h:mma z', work.schedule.from, work.timeZone)}"/>
												</c:otherwise>
											</c:choose>
											<br/>
											For: <c:out value="${work.title}"/> (<c:out value="${work.workNumber}"/>)
										</div>
									</td>
								</tr>
							</table>
							<table width="100%" class="br">
								<tr>
									<c:if test="${not empty work.locationContact}">
										<td width="50%" valign="top">
											Onsite Contact:<br/>
											<c:out value="${work.locationContact.name.firstName}"/> <c:out
												value="${work.locationContact.name.lastName}"/><br/>
											<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
												<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
													<c:if test="${not empty phone.phone}">
														<c:out value="${wmfmt:phone(phone.phone)}"/>
														<c:if test="${not empty phone.extension}">
															x <c:out value="${phone.extension}"/>
														</c:if>
														<br/>
													</c:if>
												</c:forEach>
											</c:if>
										</td>
									</c:if>
									<td width="50%" valign="top">

									</td>
								</tr>
							</table>

						</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
</div> <!-- end print badge body border -->
</c:if>

<!-- end main -->

</body>
</html>
