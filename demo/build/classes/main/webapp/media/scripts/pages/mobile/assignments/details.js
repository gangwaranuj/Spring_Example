var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.details = function (hoursToComplete, workStartTime, deliverablesLength, allowMobileSignature, workNumber) {
	var self = this;
	var distance = undefined;
	var workLat = $('.addressLat').val();
	var workLon = $('.addressLon').val();

	var locationUpdated = function (lat, lon) {
		if(!_.isUndefined(workLat) && !_.isUndefined(workLon)) {

			distance = geo_distance(lat, lon, workLat, workLon);

			$distance = $('.distance');

			$distance.text(distance.toFixed(2) + " mi");
			if (distance >= 5) {
				$distance.addClass("red");
			} else if (distance >= 2) {
				$distance.addClass("orange");
			} else {
				$distance.addClass("green");
			}

			$(".latitudeField").val(lat);
			$(".longitudeField").val(lon);
			$(".distanceField").val(distance);
		}
	};

	this.updateDeliverableCountdownTimer = function (assignmentStartTime) {

		var $deliverableDeadlineTimer = $('#deliverableDeadlineTimer');
		var milliInHour = 36e5;
		var milliInMinute = 60000;
		var now = new Date();
		var convertedHoursToCompleted = hoursToComplete * milliInHour;
		var deliverableDeadline = assignmentStartTime + convertedHoursToCompleted;
		var timeRemaining = deliverableDeadline - now.getTime();

		if (timeRemaining <= 0) {
			$('.deliverables-timer-text').html('Deliverables deadline has passed. Please submit as soon as possible.');
		} else if (timeRemaining > convertedHoursToCompleted) {
			$('.deliverables-timing-box').html('Due ' + hoursToComplete + ' hours (after the assignment starts)');
		} else {
			// Deadline is still active, update timer UI
			var value = '';
			var unit = '';

			var hoursRemaining = Math.floor(timeRemaining / milliInHour);
			if (hoursRemaining > 48) {
				unit = ' days';
				value = Math.floor(hoursRemaining / 24);
			} else {
				unit = (hoursRemaining === 1) ? ' hour ' : ' hours ';
				value = hoursRemaining;
			}

			$deliverableDeadlineTimer.html(value + unit);
			var updateAssignmentTime = _.bind(this.updateDeliverableCountdownTimer, this, assignmentStartTime);
			_.delay(updateAssignmentTime, milliInMinute);
		}
	};

	return function () {
		FastClick.attach(document.body);
		wm.location.trackLocation(locationUpdated);
		if (hoursToComplete > 0) {
			self.updateDeliverableCountdownTimer(workStartTime);
			$('.deliverables-timing-box').show();
		}


		$('.check-in-form').on('submit', function () {
			trackEvent('mobile', 'checkin', '', distance);
		});
		$('#check-out-form').on('submit', function () {
			trackEvent('mobile', 'checkout', '', distance);
		});
		$('.accept-action').on('click', function () {
			trackEvent('mobile','accept');
		});
		$('.decline-action').on('click', function () {
			trackEvent('mobile','decline');
		});
		$('#location-map').on('click', function () {
			trackEvent('mobile', 'map');
		});
		$('#confirmation-form').on('submit', function () {
			trackEvent('mobile', 'confirm');
		});
		$('#print-assignment').on('click', function () {
			trackEvent('mobile','print');
		});
		$('#add-note-form').on('submit', function () {
			trackEvent('mobile','note', 'add');
		});

		if ($('#add-attachment-popup').length) {
			if (allowMobileSignature) {
				$('.asset_type').on('change', function () {
					$('.mobile-signature').toggle(_.contains(['sign_off', 'other'], $('.asset_type').val()));
				});
			}

			$('.get-signature').on('click', function () {
				window.location.href = '/mobile/assignments/signature/'+ workNumber +'?reqId=' + $('.deliverable_group_id').val() + '&pos=' + $('.position').val();
			});

			$('#upload-file').on('change', function () {
				var $uploadButton = $('.upload-button');
				var isFileSelected = !_.isEmpty($(this).val());
				$uploadButton.toggleClass('default-button', isFileSelected);
				$uploadButton.prop('disabled', !isFileSelected);
			});

			$('#add-attachment-form').one('submit', function () {
				showSpinner();
				trackEvent('mobile', 'attachment', 'add');
			});
		}
	};
};
