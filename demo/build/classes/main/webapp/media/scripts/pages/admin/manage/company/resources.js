var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.resources = function (companyId) {
	var self = this;
	var $workersList = $('#workers_list');

	var employeesTable = $('#users_list').dataTable({
		'sPaginationType':'full_numbers',
		'bLengthChange':true,
		'bFilter':false,
		'bStateSave':false,
		'bProcessing':true,
		'bServerSide':true,
		'iDisplayLength':100,
		'aaSorting':[
			[0, 'asc']
		],
		'sAjaxSource':'/admin/manage/company/resourceslist/' + companyId,
		'fnServerData':function (sSource, aoData, fnCallback) {
			aoData.push({'name':'id', 'value':companyId}, {'name':'type', 'value':'employees'});
			$.getJSON(sSource, aoData, function (json) {
				for (var i = 0, size = json.aaData.length; i < size; i++) {
					var fullName = encodeURIComponent(json.aaData[i][0]);
					json.aaData[i][0] = '<a href="/admin/manage/profiles/index/' + json.aMeta[i].id + '">' + json.aaData[i][0] + '</a>';
					json.aaData[i][3] = '<a href="/admin/usermanagement/masquerade/start?user=' + encodeURIComponent(json.aMeta[i].email)
						+ '&user_fullname=' + fullName + '">Masquerade</a>';
				}
				fnCallback(json);
			});
		}
	});

	var workersTable = $workersList.dataTable({
		'sPaginationType':'full_numbers',
		'bLengthChange':true,
		'bFilter':true,
		'bStateSave':false,
		'bProcessing':true,
		'bServerSide':true,
		'iDisplayLength':100,
		'aaSorting':[[0, 'asc']],
		'sAjaxSource':'/admin/manage/company/resourceslist/' + companyId,
		'fnServerData':function (sSource, aoData, fnCallback) {
			aoData.push({'name':'id', 'value':companyId}, {'name':'type', 'value':'contractors'});
			$.getJSON(sSource, aoData, function (json) {
				for (var i = 0, size = json.aaData.length; i < size; i++) {
					var fullName =  encodeURIComponent(json.aaData[i][0]);
					json.aaData[i][0] = '<a href="/admin/manage/profiles/index/' + json.aMeta[i].id + '">' + json.aaData[i][0] + '</a>';
					json.aaData[i][2] = '<a href="javascript:void(0);"' + 'data-lane="'+ json.aaData[i][2] + '" ' + 'data-id="' + json.aMeta[i].user_id + '" class="lane_change" >' + json.aaData[i][2] + '</a>';
					json.aaData[i][5] = '<a href="/admin/usermanagement/masquerade/start?user=' + encodeURIComponent(json.aMeta[i].email)
						+ '&user_fullname=' + fullName + '">Masquerade</a>';
				}
				fnCallback(json);
			});
		}
	});

	this.errorHandler = function(modal, response) {
		$(modal + ' div').html(response);
		$(modal).removeClass('success').addClass('error').show();
	};


	$('#form_change_lane').ajaxForm({
		dataType:'json',
		success:function (response) {
			$('#form_change_lane a.disabled').removeClass('disabled');
			if (response.successful) {
				$workersList.fnDraw();
				$.colorbox.close();
				$('#dynamic_messages div').html(response.messages[0]);
				$('#dynamic_messages').removeClass('error').addClass('alert-success').show();
			} else {
				self.errorHandler('#dynamic_messages_lane_change', response.messages[0]);
				$.colorbox.resize();
			}
		}
	});

	$workersList.on('click','.lane_change', function (e) {
		var link = $(e.currentTarget);
		var current_lane = link.data('lane');
		var user_id = link.data('id');

		$('#user_id').val(user_id);
		if (current_lane === 2) {
			$('#lane_id').val(3);
			$('#change_lane_to').html('LANE 3');
		} else {
			$('#lane_id').val(2);
			$('#change_lane_to').html('LANE 2');
		}

		$.colorbox({
			inline:true,
			href:'#change_lane_popup',
			title:'Change Lane',
			transition:'none',
			innerWidth:500
		});
	});
};
