var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.backgroundImage = function () {
	'use strict';

	return function () {

		var imageHtml = _.template($('#default-image-tmpl').html());

		$('#fileupload').fileupload({
			dataType: 'json',
			maxNumberOfFiles: 1,
			success: function (response) {
				if (response.successful) {
					$('.thumbnails').append(imageHtml(response.data));
					setToggleSelection();
				}  else {
					_.each(response.messages, function (theMessage) {
						wm.funcs.notify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});

		$('.save_default').on('click', function () {
			$.ajax({
				url: '/admin/background_image/' + getSelectedImageId(),
				type: 'POST',
				dataType: 'json',
				success: function (response) {
					if (response.successful) {
						wm.funcs.notify({ message: 'Ya did it! Ya successfully set the default image. Refresh user sessions for changes to take immediate effect.' });
					} else {
						_.each(response.messages, function (theMessage) {
							wm.funcs.notify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}
			});
		});

		$('.remove_image').on('click', function () {
			var selectedImageId = getSelectedImageId();
			$.ajax({
				url: '/admin/background_image/' + selectedImageId,
				type: 'DELETE',
				dataType: 'json',
				success: function (response) {
					if (response.successful) {
						$('#' + selectedImageId).closest('li').remove();
					} else {
						_.each(response.messages, function (theMessage) {
							wm.funcs.notify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}
			});
		});

		$('.refresh_sessions').on('click', function () {
			$.ajax({
				url: '/admin/background_image/refresh_sessions',
				type: 'POST',
				dataType: 'json',
				success: function (response) {
					if (response.successful) {
						wm.funcs.notify({ message: 'Ya did it! Sessions refreshed. New default image is now visible.' });
					} else {
						_.each(response.messages, function (theMessage) {
							wm.funcs.notify({
								message: theMessage,
								type: 'danger'
							});
						});
					}
				}
			});
		});

		function setToggleSelection () {
			$('.thumbnail').on('click', function () {
				$('.thumbnail').removeClass('selected');
				$(this).addClass('selected');
			});
		}

		setToggleSelection();

		function getSelectedImageId () {
			return parseInt($('.selected img').prop('id'));
		}
	};
};
