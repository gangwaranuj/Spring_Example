var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.accounting = wm.pages.admin.accounting || {};

wm.pages.admin.accounting.achfunding = function () {
	return function () {
		$('#transaction_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'iDisplayLength': 100,
			'aoColumnDefs': [{
				'aTargets': [ 0 ],
				'bSortable': false
			}]
		});

		$('#select_all').on('change', function () {
			$('#transaction_list tbody input[type="checkbox"]').prop('checked', $(this).prop('checked'));
		});

		$('#approve-outlet').on('click', function () {
			$('#update_status').val('approved');
			$(this).closest('form').trigger('submit');
		});

		$('#reject-outlet').on('click', function () {
			$('#update_status').val('rejected');
			$(this).closest('form').trigger('submit');
		});

		$('#transaction_list tbody').on('dblclick', function (event) {
			var target = $(event.target);
			var id = target.data('id');
			if (typeof id !== 'undefined') {
				var url = '/admin/v2/vault/bankAccounts/' + id + '/accountNumber';
				$.get(url, function (data) {
					target.text(data.results[0].value);
				}).fail(function (data) {
					alert(data.responseJSON.meta.message);
				});
			}
		});
	};
};
