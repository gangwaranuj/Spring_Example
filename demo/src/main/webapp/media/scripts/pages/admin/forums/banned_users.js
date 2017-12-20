var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.forums = wm.pages.admin.forums || {};

wm.pages.admin.forums.bannedUsers = function () {
	return function () {

		var actionTemplate = _.template($('#tmpl-action').html());

		var meta, datatable_obj = $('#bannedUsersTable').dataTable({
			aaSorting: [[2, 'desc']],
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'bSort': true,
			'aoColumnDefs': [
				{
					'bSortable': false,
					'fnRender': renderAction,
					'aTargets': [3]
				}
			],
			'sAjaxSource': '/admin/forums/banned_users',
			'fnServerData': function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		function renderAction(row) {
			var userId = row.aData[row.iDataColumn];
			return actionTemplate({userId: userId});
		}

		$('#bannedUsersTable').on('click', '.admin-forums-unban', function(e) {
			e.preventDefault();
			var id = $(this).attr('id').replace('unban','');
			var sure = confirm('Are you sure you want to unban this user?');
			if (!sure) {
				return;
			}
			$.ajax({
				url: '/admin/forums/unban/' + id,
				type: 'POST',
				dataType: 'json',
				success: function () {
					$(this).parentsUntil($('tbody')).remove();
					datatable_obj.fnDraw();
				}
			});
		});

		$('#user_fullname').autocomplete({
			minLength: 0,
			source: '/admin/usermanagement/suggest_users',
			focus: function (event, ui) {
				$('#user_fullname').val(ui.item.value);
				return false;
			},
			select: function (event, ui) {
				$('#user_id').val(ui.item.id);
				$('#user_fullname').val(ui.item.value);

				render_user_id();
				return false;
			},
			search: function (event, ui) {
				$('#user_id').val('');

				$('#selected_user').text('');
				$('#selected_user').hide();
			}
		});

		function render_user_id() {
			if ($('#user_id').val()) {
				$('#selected_user').text('(User ID: ' + $('#user_id').val() + ')');
				$('#selected_user').show();
			}
		}

		render_user_id();

	};
};
