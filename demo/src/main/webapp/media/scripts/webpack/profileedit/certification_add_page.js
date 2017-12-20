import $ from 'jquery';
import '../config/datepicker';

export default function () {
	var current_industryval = '';

	$('select#industry').on('change', function () {
		$('#select_provider').html('Updating list...');
		updateIndustry();
	});

	$('#certificationsForm').on('change', 'select#provider', function () {
		if ($('select#provider').val() === 'other') {
			$('#custom_provider_line').show();
		} else {
			$('#custom_provider_line').hide();
		}
	});

	$('#issueDate').datepicker({dateFormat: 'mm/dd/yy'});
	$('#expirationDate').datepicker({dateFormat: 'mm/dd/yy', minDate: new Date()});

	function updateIndustry() {
		var industryval = $('select#industry').val();

		// Check the option "- Select -" case
		if (industryval == "") {
			$('#custom_provider_line').hide();
			$('#select_provider').html('Select an industry to display list of available vendors.');
			return;
		}

		// See if we need to fire off an ajax call to look up licenses for state.
		if (current_industryval != industryval) {
			current_industryval = industryval;
			$('#select_provider').html('Updating list');
			// Do the AJAX call
			$.ajax({
				url: '/profile-edit/certificationslist',
				global: false,
				type: "GET",
				data: ({industry : industryval}),
				dataType: "json",
				success: function (data){
					updateProvider(data);
				}
			});
		}
	}

	function updateProvider(data) {
		var select = $('<select>').attr('name', 'provider').attr('id', 'provider');

		$('<option>').attr('value', null).text('- Select -').appendTo(select);
		$('<option>').attr('value', 'other').text('Add New Company').appendTo(select);

		var companies = $('<optgroup>').attr('label', 'Companies');

		if (data.length > 0) {
			$('#custom_provider_line').hide();
			$.each(data, function(i, item) {
				$('<option>').attr('value', item.id).text(item.name).appendTo(companies);
			});
		}

		select.append(companies);

		$('#select_provider').html(select);

		if (data.length == 0) {
			$('#select_provider option[value="other"]').prop('selected', true);
			$('#custom_provider_line').show();
		}
	}
}
