<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Review">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<h3>Insurance</h3>
		<form action="/insurance_list_filters" method="post" id="insurance_list_filter_form" accept-charset="utf-8">
			<wm-csrf:csrfToken />
			<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
				<c:param name="name" value="status" />
				<c:param name="id" value="insurance_list_filter" />
			</c:import>
		</form>

	<div id="table_insurance">
		<table id="insurance_list" class="zebra-striped">
			<thead>
				<tr>
					<th width="8%">Status</th>
					<th width="8%">Added</th>
					<th width="15%">Added By</th>
					<th>Name</th>
					<th>Industry</th>
					<th>Last Activity</th>
					<th width="21%">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
	<div class="table_insurance_msg"></div>

	<hr/>

	<h3>User Insurance</h3>
	
		<form action="/userinsurance_list_filters" method="post" id="userinsurance_list_filter_form" accept-charset="utf-8">
			<wm-csrf:csrfToken />
			<c:import url="/WEB-INF/views/web/partials/general/dropdowns/verification_statuses.jsp">
				<c:param name="name" value="status" />
				<c:param name="id" value="userinsurance_list_filter" />
			</c:import>
		</form>

	<div id="table_userinsurance">
		<table id="userinsurance_list" class="zebra-striped">
			<thead>
				<tr>
					<th width="8%">Status</th>
					<th width="8%">Added</th>
					<th width="15%">User</th>
					<th>Provider</th>
					<th>Type</th>
					<th>#<br />Coverage</th>
					<th width="8%">Issue Date</th>
					<th width="8%">Exp Date</th>
					<th width="8%">Last Activity</th>
					<th width="14%">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
	<div class="table_userinsurance_msg"></div>

</div>

<script type="text/javascript">

	var insurance_list_obj, userinsurance_list_obj;
	var meta, meta2;
	var insurance_list_filters = [];
	var userinsurance_list_filters = [];

	$(document).ready(function() {

		// Run the filters on load.
		insurance_list_filters = $('#insurance_list_filter_form').serializeArray();
		userinsurance_list_filters = $('#userinsurance_list_filter_form').serializeArray();

		$('#insurance_list_filter').change(function(){
			var selected = $(this).val();
			if (selected != "") {
				apply_insurance_list_filters();
			}
			else {
				clear_insurance_list_filters();
			}
		});

		$('#userinsurance_list_filter').change(function(){
			var selected = $(this).val();
			if (selected != "") {
				apply_userinsurance_list_filters();
			}
			else {
				clear_userinsurance_list_filters();
			}
		});

		var renderActionCell = function(row) {
			return $('#action-cell-tmpl').tmpl({
				meta: meta[row.iDataRow]
			}).html();
		};

		var renderActionCell2 = function(row) {
			return $('#action2-cell-tmpl').tmpl({
				meta: meta2[row.iDataRow]
			}).html();
		};

		var renderUserInsuranceNumberCell = function(row) {
			return $('#userinsurancenumber-cell-tmpl').tmpl({
				data: row.aData[row.iDataColumn],
				meta: meta2[row.iDataRow]
			}).html();
		};

		insurance_list_obj = $('#insurance_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[1,'desc']],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [6]},
				{'fnRender': renderActionCell, 'aTargets': [6]}
			],
			'sAjaxSource': 'unverified_insurance',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				for (var i = 0, size = insurance_list_filters.length; i < size; i++) {
					aoData.push(insurance_list_filters[i]);
				};
				$.getJSON( sSource, aoData, function (json) {
					meta = json.aMeta;
					if (json.aaData.length == 0) {
						$("#table_insurance").hide();
						$('.table_insurance_msg').html('There are no records.');
					}
					else {
						$("#table_insurance").show();
						$('.table_insurance_msg').html('');
					}
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][2] = '<a href="/profile/'+json.aMeta[i]['user_number']+'">' + json.aaData[i][2] + '<\/a>';
					}
					fnCallback(json)
				});
			}
		});

		userinsurance_list_obj = $('#userinsurance_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[1,'desc']],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [4,5,9]},
				{'fnRender': renderUserInsuranceNumberCell, 'aTargets': [5]},
				{'fnRender': renderActionCell2, 'aTargets': [9]}
			],
			'sAjaxSource': 'unverified_userinsurance',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				for (var i = 0, size = userinsurance_list_filters.length; i < size; i++) {
					aoData.push(userinsurance_list_filters[i]);
				};
				$.getJSON( sSource, aoData, function (json) {
					meta2 = json.aMeta;
					if (json.aaData.length == 0) {
						$("#table_userinsurance").hide();
						$('.table_userinsurance_msg').html('There are no records.');
					}
					else {
						$("#table_userinsurance").show();
						$('.table_userinsurance_msg').html('');
					}
					for (var i = 0, size = json.aaData.length; i < size; i++) {
						json.aaData[i][2] = '<a href="/profile/'+json.aMeta[i]['user_number']+'">' + json.aaData[i][2] + '<\/a>';
					}
					fnCallback(json)
				});
			}
		});

		$('a[name=userinsurance_approve]').on('click', function(){
			var vendor = $(this).attr('id');
			
			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/approve_userinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('approved!');
					}
					else
					{
						alert('An error occurred while approving this insurance.')
					}
				}
			});
		});

		$('a[name=userinsurance_decline]').on('click', function(){
			var vendor = $(this).attr('id');

			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/decline_userinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('declined!');
					}
					else
					{
						alert('An error occurred while declining this insurance.')
					}
				}
			});
		});

		$('a[name=userinsurance_unverify]').on('click', function(){
			var vendor = $(this).attr('id');

			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/onhold_userinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('On Hold!');
					}
					else
					{
						alert('An error occurred while changing the status.')
					}
				}
			});
		});

		$('a[name=insurance_approve]').on('click', function(){
			var vendor = $(this).attr('id');

			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/approveinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('approved!');
					}
					else
					{
						alert('An error occurred while approving this insurance.')
					}
				}
			});
		});

		$('a[name=insurance_decline]').on('click', function(){
			var vendor = $(this).attr('id');

			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/declineinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('declined!');
					}
					else
					{
						alert('An error occurred while declining this insurance.')
					}
				}
			});
		});

		$('a[name=insurance_unverify]').on('click', function(){
			var vendor = $(this).attr('id');

			// Do the AJAX call
			$.ajax({
				url: "/admin/insurance/onholdinsurance",
				global: false,
				type: "GET",
				data: ({id : vendor}),
				dataType: "json",
				success: function(data){
					if (data && data.success == true)
					{
						var message_name = vendor+'_msg';
						$('#'+message_name).html('Oh Hold!');
					}
					else
					{
						alert('An error occurred while changing the status.')
					}
				}
			});
		});


	});

	function apply_insurance_list_filters() {
		insurance_list_filters = $('#insurance_list_filter_form').serializeArray();
		insurance_list_obj.fnDraw();
	}

	function clear_insurance_list_filters() {
		insurance_list_filters = [];
		insurance_list_obj.fnDraw();
	}

	function apply_userinsurance_list_filters() {
		userinsurance_list_filters = $('#userinsurance_list_filter_form').serializeArray();
		userinsurance_list_obj.fnDraw();
	}

	function clear_userinsurance_list_filters() {
		userinsurance_list_filters = [];
		userinsurance_list_obj.fnDraw();
	}

</script>

<script id="action-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="insurance_\${meta.id}_msg">
			<a name="insurance_approve" id="insurance_\${meta.id}">Approve</a><span class="separator">|</span>
			<a name="insurance_decline" id="insurance_\${meta.id}">Decline</a><span class="separator">|</span>
			<a name="insurance_unverify" id="insurance_\${meta.id}">Unverified</a>
		</div>
	</div>
</script>

<script id="action2-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="userinsurance_\${meta.id}_\${meta.user_id}_msg">
			<a href="<c:url value="/admin/insurance/edit_userinsurance?id="/>\${meta.id}&user_id=\${meta.user_id}">View Details</a>
		</div>
	</div>
</script>

<script id="userinsurancenumber-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		\${meta.number}
		{{if meta.attachment_relative_uri}}
			<a href="\${meta.attachment_relative_uri}" class="download-attachment-csr pr" title="Download Attachment"><b></b></a>
		{{/if}}
		<br />
		\${meta.coverage}
	</div>
</script>

</wm:admin>
