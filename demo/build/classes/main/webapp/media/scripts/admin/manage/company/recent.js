var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.recent = function () {
	return function () {
		$('#select-all').click(function() {
			$('#users_list input[type=checkbox]').prop('checked', $(this).is(':checked'))
		});

		$('#send-message-action').colorbox({
			href: function() {
				var params = {
					'user_ids': $.makeArray($('#users_list tbody input[type=checkbox]:checked').map(function(item, i) { return $(this).val() })),
					'user_names': $.makeArray($('#users_list tbody input[type=checkbox]:checked').map(function(item, i) { return $(this).parent().next().text() }))
				};
				return '/admin/manage/users/message?' + $.param(params);
			},
			title:'Send Message to Users',
			transition:'none',
			innerWidth:500,
			onComplete: function() {
				$('#message-form').ajaxForm({
					dataType: 'json',
					success: function(data) {
						$('#message-form a.disabled').removeClass('disabled');
						if (data.successful) {
							wm.funcs.notify({
								message: data.message
							});
							$.colorbox.close();
						} else {
							_.each(data.errors, function (theMessage) {
								wm.funcs.notify({
									message: theMessage,
									type: 'danger'
								});
							});

							$.colorbox.close();
						}
					}
				});
				$('#message-form .submit').click(function() {
					$(this).closest('form').trigger('submit');
				});
				$('#message-form .cancel').click(function() {
					$.colorbox.close();
				});
			}
		});

		var cellRenderer = function(template) {
			return function(row) {
				return $(template).tmpl({
					data: row.aData[row.iDataColumn],
					meta: meta[row.iDataRow]
				}).html();
			};
		};

		var meta;
		var table = $('#users_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'iDisplayLength': 100,
			'bFilter': true,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			"aaSorting": [[ 6, "desc" ]],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [0,5]},
				{'fnRender': cellRenderer('#user-checkbox-tmpl'), 'aTargets': [0]},
				{'fnRender': cellRenderer('#user-name-tmpl'), 'aTargets': [1]},
				{'fnRender': cellRenderer('#user-company-tmpl'), 'aTargets': [2]},
				{'fnRender': cellRenderer('#user-email-tmpl'), 'aTargets': [3]},
				{'fnRender': cellRenderer('#user-lane-tmpl'), 'aTargets': [5]}
			],
			'sAjaxSource': '/admin/manage/users/recent.json',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				$.each($('#recent_user_filter_form').serializeArray(), function(i, item) {
					aoData.push(item);
				});
				$.getJSON( sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});

		$('#recent_user_filter_form input, #recent_user_filter_form select').change(function() {
			table.fnDraw();
		});
	}
};