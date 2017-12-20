var wm = wm || {};
wm.pages = wm.pages || {},
wm.pages.admin = wm.pages.admin || {},
wm.pages.admin.usermanagement = wm.pages.admin.usermanagement || {};

wm.pages.admin.usermanagement.index = function () {
	return function () {
		$('#user_fullname').autocomplete({
			minLength:0,
			source:'/admin/usermanagement/suggest_contractors',
			focus:function (event, ui) {
				$('#user_fullname').val(ui.item.value);
				return false;
			},
			select:function (event, ui) {
				$('#user_id').val(ui.item.id);
				$('#user_fullname').val(ui.item.value);

				render_user_id();
				return false;
			},
			search:function (event, ui) {
				$('#user_id').val('');

				$('#selected_user').text('');
				$('#selected_user').hide();
			}
		});

		function render_user_id() {
			if ($('#user_id').val()) {
				$('#selected_user').text('(User ID: ' + $('#user_id').val() + ')');
				$('#selected_user').show();
				$('#add-contractor').get(0).setAttribute('href', '/admin/usermanagement/edit?id=' + $('#user_id').val());
			}
		}
		render_user_id();
	};
};