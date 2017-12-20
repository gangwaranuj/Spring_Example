var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.customField = function () {

	return function () {
		FastClick.attach(document.body);

		$('#custom_fields_form').on('submit', function () {
			trackEvent('mobile','custom fields', 'save');
		});

		var $buyerCustomFieldPartial = $('.buyer-custom-fields-partial');

		if ($buyerCustomFieldPartial.length) {
			var goToLinks = $buyerCustomFieldPartial.find('a.link-cf');
			_.each(goToLinks, function (goToLink) {
				var $goToLink = $(goToLink);
				var customFieldValue = $goToLink.siblings('#bigCf').val();
				if (/(https?:\/\/|www\.)\S+/i.test(customFieldValue)) {
					var linksFoundInText = customFieldValue.replace(/.*?:\/\//g, "");
					$goToLink.show().attr('href','http://' + linksFoundInText);
				}
			});
		}
	};
};

