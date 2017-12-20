<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Nacha">

	<c:set var="isInboundEnabled" value="${inboundAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isOutboundEnabled" value="${outboundAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isNonUsaOutboundEnabled" value="${nonUsaOutboundAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isACHVerifyEnabled" value="${achverifyAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isPaypalEnabled" value="${paypalAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isGCCEnabled" value="${gccAvailable ? '' : 'disabled=disabled'}"/>
	<c:set var="isTINEnabled" value="${tinAvailable ? '' : 'disabled=disabled'}"/>

	<c:set var="isCurrentTypeInbound" value="${current_type == 'inbound' ? '-active' : ''}"/>
	<c:set var="isCurrentTypeOutbound" value="${current_type == 'outbound' ? '-active' : ''}"/>
	<c:set var="isCurrentTypeNonUsaOutbound" value="${current_type == 'nonUsaOutbound' ? '-active' : ''}"/>
	<c:set var="isCurrentTypeACHVerify" value="${current_type == 'achverify' ? '-active' : ''}"/>
	<c:set var="isCurrentTypePaypal" value="${current_type == 'paypal' ? '-active' : ''}"/>
	<c:set var="isCurrentTypeGCC" value="${current_type == 'gcc' ? '-active' : ''}"/>
	<c:set var="isCurrentTypeTIN" value="${current_type == 'tin' ? '-active' : ''}"/>

	<c:import url="/breadcrumb">
		<c:param name="pageId" value="adminAccountingNacha" />
		<c:param name="admin" value="true" />
	</c:import>

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form action="/admin/accounting/initiate_nacha" method="post" class="form-inline">
		<wm-csrf:csrfToken />
		<h3>Initiate Processing</h3>

		<label>
			<input type="radio" name="type" value="inbound" ${isInboundEnabled} />
			Inbound
		</label>

		<label>
			<input type="radio" name="type" value="outbound" ${isOutboundEnabled} />
			Outbound (US)
		</label>

		<label>
			<input type="radio" name="type" value="nonUsaOutbound" ${isNonUsaOutboundEnabled} />
			Outbound (Non-US)
		</label>

		<label>
			<input type="radio" name="type" value="achverify" ${isACHVerifyEnabled} />
			ACH Verify
		</label>

		<label>
			<input type="radio" name="type" value="paypal" ${isPaypalEnabled} />
			PayPal
		</label>

		<label>
			<input type="radio" name="type" value="gcc" ${isGCCEnabled} />
			GCC
		</label>

		<label>
			<input type="radio" name="type" value="tin" ${isTINEnabled} />
			TIN
		</label>

		<div class="wm-action-container">
			<button type="submit" class="button">Request</button>
		</div>
	</form>

	<ul class="wm-tabs">
		<li class="wm-tab ${isCurrentTypeInbound}">
			<a href="/admin/accounting/nacha?type=inbound">Inbound</a>
		</li>
		<li class="wm-tab ${isCurrentTypeOutbound}">
			<a href="/admin/accounting/nacha?type=outbound">Outbound (US)</a>
		</li>
		<li class="wm-tab ${isCurrentTypeNonUsaOutbound}">
			<a href="/admin/accounting/nacha?type=nonUsaOutbound">Outbound (Non-US)</a>
		</li>
		<li class="wm-tab ${isCurrentTypeACHVerify}">
			<a href="/admin/accounting/nacha?type=achverify">ACH Verify</a>
		</li>
		<li class="wm-tab ${isCurrentTypePaypal}">
			<a href="/admin/accounting/nacha?type=paypal">PayPal</a>
		</li>
		<li class="wm-tab ${isCurrentTypeGCC}">
			<a href="/admin/accounting/nacha?type=gcc">GCC</a>
		</li>
		<li class="wm-tab ${isCurrentTypeTIN}">
			<a href="/admin/accounting/nacha?type=tin">TIN</a>
		</li>
	</ul>

	<c:set var="batchOrConfirmation" value="${current_type == 'tin' ? 'confirmation' : 'batch'}"/>

	<table id="data_list" class="table table-striped">
	<c:choose>
		<c:when test="${current_type == 'tin'}">
			<thead>
				<tr>
					<th>Type</th>
					<th>Requestor</th>
					<th>Request Date</th>
					<th>Tracking No</th>
					<th>Status</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="value" items="${requests}">
				<tr>
					<td>TIN</td>
					<td><c:out value="${value.requestor.firstName}"/> <c:out value="${value.requestor.lastName}"/></td>
					<td><fmt:formatDate value="${value.requestDate.time}" type="both" dateStyle="medium" timeStyle="medium" timeZone="${currentUser.timeZoneId}" /></td>
					<td>
						<c:choose>
							<c:when test="${not empty value.confirmationNumber}">
								<c:out value="${value.confirmationNumber}"/>
							</c:when>
							<c:otherwise>
								<a href="javascript:void(0);" onclick="javascript:add_batch_number(<c:out value="${value.id}"/>); return false;">Add</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td><c:out value="${wmfmt:toPrettyName(value.deleted ? 'Cancelled' : value.verificationStatus)}"/></td>
					<td>
						<c:if test="${!value.deleted}">
							<c:if test="${value.verificationStatus == 'PENDING' || value.verificationStatus == 'VERIFIED'}">
								<a href="<c:url value="/admin/accounting/download_tin_file/${value.id}"/>" target="_blank" class="button">Download</a>
							</c:if>
							<c:if test="${value.verificationStatus == 'PENDING'}">
								<a href="javascript:void(0);" onclick="javascript:add_tin_file(<c:out value="${value.id}"/>); return false;" class="button">Process</a>
								<a href="javascript:void(0);" onclick="javascript:cancel_tin_file(<c:out value="${value.id}"/>, '<fmt:formatDate value="${value.requestDate.time}" type="both" dateStyle="medium" timeStyle="medium" timeZone="${currentUser.timeZoneId}" />'); return false;" class="btn btn-danger">Cancel</a>
							</c:if>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</c:when>
		<c:otherwise>
			<thead>
				<tr>
					<th>Type</th>
					<th>Requestor</th>
					<th>Request Date</th>
					<th>Notes</th>
					<c:choose>
						<c:when test="${current_type == 'paypal'}">
							<th>Transaction ID</th>
						</c:when>
						<c:when test="${current_type == 'gcc'}">
							<th>Transaction #</th>
						</c:when>
						<c:otherwise>
							<th>Confirm No.</th>
						</c:otherwise>
					</c:choose>
					<th>Status</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="value" items="${requests}">
				<tr>
					<td><c:out value="${value.bankingIntegrationGenerationRequestType.code}"/></td>
					<td><c:out value="${value.requestor.firstName}"/> <c:out value="${value.requestor.lastName}"/></td>
					<td><fmt:formatDate value="${value.requestDate.time}" type="both" dateStyle="medium" timeStyle="medium" timeZone="${currentUser.timeZoneId}" /></td>
					<td><c:out value="${value.notes}"/></td>
					<td>
						<c:choose>
							<c:when test="${!empty value.batchNumber}">
								<c:out value="${value.batchNumber}"/>
							</c:when>
							<c:otherwise>
								<a href="javascript:void(0);" onclick="javascript:add_batch_number(<c:out value="${value.id}"/>); return false;">Add</a>
							</c:otherwise>
						</c:choose>
					</td>
					<td><c:out value="${value.bankingIntegrationGenerationRequestStatus.code}"/></td>
					<td>
						<c:if test="${value.bankingIntegrationGenerationRequestStatus.code == 'submitted' && value.bankingIntegrationGenerationRequestType.code != 'tin'}">
							<a href="<c:url value="/admin/accounting/deleteRequest/${value.id}"/>" class="btn btn-danger">Cancel</a>
						</c:if>
						<c:if test="${value.bankingIntegrationGenerationRequestStatus.code == 'complete' && value.bankingIntegrationGenerationRequestType.code == 'inbound'}">
							<a href="<c:url value="/admin/accounting/settle/${value.id}"/>" class="button">Settle</a>
						</c:if>
						<c:if test="${value.bankingIntegrationGenerationRequestStatus.code == 'complete' && !empty value.assets}">
							<c:forEach var="asset" items="${value.assets}" end="0">
								<a href="<c:url value="${asset.downloadableUri}"/>" class="button">Download</a>
							</c:forEach>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</c:otherwise>
	</c:choose>
	</table>
</div>

<div class="dn">
	<div id="popup_batch_number">
		<form action="/admin/accounting/set_${batchOrConfirmation}_number" method="post" id="form_batch_number" class="form-horizontal">
			<wm-csrf:csrfToken />
			<input type="hidden" name="request_id" id="request_id" value="" />

			<div class="alert-message error message dn"><div></div></div>

			<div class="control-group">
				<label for="${batchOrConfirmation}_number" class="control-label">
					${wmfmt:upcaseFirstLetter(batchOrConfirmation)} Number:
				</label>
				<div class="controls">
					<input type="text" name="${batchOrConfirmation}_number" id="${batchOrConfirmation}_number" />
				</div>
			</div>

			<div class="wm-action-container">
				<button type="submit" class="button">Save</button>
			</div>
		</form>
	</div>
</div>


<div class="dn">
	<div id="popup_process_tin_file">
		<form action="/admin/accounting/process_tin_file" method="post" id="process_tin_file" class="form-horizontal">
			<wm-csrf:csrfToken />
			<input type="hidden" name="request_id" value="" />
			<input type="hidden" name="asset_uuid" value="" />
			<div class="error dn" id="dynamic_message" style="color:red; font-weight:bold;"><div></div></div>
			<label class="control-label">IRS TIN Matched file</label>
			<div class="controls">
				<div id="file-uploader" class="dib"></div>
			</div>
			<div class="wm-action-container">
				<button class="button cancel" id="cancel_process">Cancel</button>
				<button type="submit" class="button dn" id="process-file-button">Process</button>
			</div>
		</form>
	</div>
</div>

<div class="dn">
	<div id="popup_cancel_tin_file">
		<form action="/admin/accounting/cancel_tin_file" method="post" id="cancel_tin_file_form" class="form-horizontal">
			<wm-csrf:csrfToken />
			<input type="hidden" name="request_id" value="" />
			<div class="text-error error dn" id="dynamic_message"><div></div></div>
			<div>
				Cancel the TIN batch created on <span id="tin-creation-date"></span>?
			</div>
			<div class="wm-action-container">
				<button class="button cancel" id="cancel_cancel">Cancel</button>
				<button type="submit" class="button" id="cancel-file-button">Submit</button>
			</div>
		</form>
	</div>
</div>

<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
	<div class="qq-uploader">
		<div class="qq-upload-drop-area"></div>
		<a href="javascript:void(0);" class="qq-upload-button btn">Choose File</a>
		<ul class="qq-upload-list"></ul>
	</div>
</script>

<script type="text/javascript">
	var uploader;

	$(document).ready(function() {
		$('#data_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'iDisplayLength': 100,
			'aoColumnDefs': [
				{'aTargets': [0], 'bSortable': false}
			]
		});

		$('#form_batch_number').ajaxForm({
			dataType: 'json',
			beforeSubmit: function(arr, $form, options) {
				$('#form_batch_number .message').hide();
			},
			success: function(data) {
				$('#form_batch_number a.disabled').removeClass('disabled');
				if (data.successful) {
					window.location.reload();
				} else {
					// Output error messages.
					var errors = '<ul>';
					for (var i in data.errors) {
						errors+= '<li>' + data.errors[i] + '<\/li>';
					}
					errors+= '</ul>';
					$('#form_batch_number .message').removeClass('success').addClass('error').show();
					$('#form_batch_number .message div').html(errors);

					$.colorbox.resize();
				}
			}
		});
	});

	var batchOrConfirmation = "${wmfmt:escapeJavaScript(batchOrConfirmation)}";
	function add_batch_number(request_id) {
		$('#request_id').val(request_id);
		$('#batch_number').val('');
		$.colorbox({
			inline:true,
			href:'#popup_batch_number',
			title:'Add ' + batchOrConfirmation.charAt(0).toUpperCase() + batchOrConfirmation.slice(1) + ' Number',
			transition:'none'
		});
	}

	function add_tin_file(request_id) {
		$('input[name="request_id"]').val(request_id);
		$('#batch_number').val('');
		$.colorbox({
			width: 550,
			inline: true,
			href: '#popup_process_tin_file',
			title: 'Process the IRS Returned TIN Match File',
			transition: 'none',
			onComplete: function() {
				$('#cancel_process').click(function(e) {
					e.preventDefault();
					$.colorbox.close();
				});
				$('#dynamic_message').hide();
				uploader = new qq.FileUploader({
					element: document.getElementById('file-uploader'),
					action: '/upload/uploadqq',
					allowedExtensions: [<c:forEach items="${uploadTypes}" var="type">"<c:out value="${type}" />",</c:forEach>],
					CSRFToken: getCSRFToken(),
					sizeLimit: 2 * 1024 * 1024, // 2MB
					multiple: false,
					template: $('#qq-uploader-tmpl').html(),
					onSubmit: function (id, fileName) {
						$('#dynamic_message').hide();
					},
					onComplete: function (id, fileName, data) {
						$.colorbox.resize();
						if (data.successful) {
							$('#process-file-button').removeClass('dn');
							$('#process_tin_file input[name="asset_uuid"]').val(data.uuid);
							$('#process_tin_file').submit(function(e) {
								var self = this;
								e.preventDefault();
								$('#process_tin_file').ajaxSubmit({
									dataType: 'json',
									success: function(data) {
										$.colorbox.close();
										redirectWithFlash(data.redirect, (data.successful) ? "success" : "error", data.messages);
									},
									error: function(data) {
										$.colorbox.close();
										redirectWithFlash(data.redirect, "error", data.messages);
									}
								});
							});
						}
					},
					showMessage: function (message) {
						wm.funcs.notify({
							message: message,
							type: 'danger'
						});
					}
				});
				$.colorbox.resize();
			}
		});
	}

	function cancel_tin_file(request_id, request_date) {
		$('input[name="request_id"]').val(request_id);
		$('#tin-creation-date').html(request_date);
		$.colorbox({
			width: 400,
			inline:true,
			href:'#popup_cancel_tin_file',
			title:'Cancel TIN Batch',
			transition:'none',
			onComplete: function() {
				$('#cancel_cancel').click(function(e) {
					e.preventDefault();
					$.colorbox.close();
				});
				$('#cancel_tin_file_form').submit(function(e) {
					var self = this;
					e.preventDefault();
					$('#cancel_tin_file_form').ajaxSubmit({
						dataType: 'json',
						success: function(data) {
							$.colorbox.close();
							redirectWithFlash(data.redirect, (data.successful) ? "success" : "error", data.messages);
						},
						error: function(data) {
							$.colorbox.close();
							redirectWithFlash(data.redirect, "error", data.messages);
						}
					});
				});
			}
		});
	}
</script>

</wm:admin>
