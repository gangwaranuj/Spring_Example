var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.certifications = wm.pages.admin.certifications || {};

wm.pages.admin.certifications.editcertifications = function (industryId, vendorId) {
	function vendor_list() {
		$.getJSON('/profile-edit/certificationslist', { industry: industryId, provider: vendorId }, update_provider);
	}

	/**
	 * Update the vendor select box.
	 *
	 * @param data
	 */
	function update_provider(data) {
		$('#select_provider').show();

		if (data.length == 0) {
			$('#select_provider .controls').html('No providers are available in this industry.');
		} else {
			var select = '<select name="provider" id="provider">';
			var options = '<option value=""> - Vendor - </option>';
			for (var i = 0; i < data['all'].length; i++) {
				options += '<option value="' + data['all'][i].id + '">' + data['all'][i].name + '</option>';
			}
			select += options + '</select>';
			$('#select_provider .controls').html(select);
			$('#provider').val(data['vendor']['id']);
		}
	}

	return function() {
		vendor_list();
	};
};