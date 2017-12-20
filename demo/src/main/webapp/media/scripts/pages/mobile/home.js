var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};

wm.pages.mobile.home = function () {
	var invitedCount = 0;
	var feedCount = 0;

	var getFindWorkCount = function (lat, lon) {
		var latLonUrl = '';

		if (lat) {
			latLonUrl += '&lat=' + lat;
		}
		if (lon) {
			latLonUrl += '&lon=' + lon;
		}

		$.get("/mobile/assignments/findwork/count?page=1" + latLonUrl, function (data) {
			$(function () {
				feedCount = data.totalCount;
				$('#available-count').text(invitedCount + feedCount);
			});
		});
	};

	var getCounts = function () {
		$.get("/mobile/counts", function (data) {
			$(function () {
				invitedCount = data['available'].statusCount;
				$('#available-count').text(invitedCount + feedCount);
				$('#inprogress-count .count').text(data['inprogress'].statusCount);
				$('#assigned-count .count').text(data['active'].statusCount);
				$('#pending-count .count').text(data['complete'].statusCount);
				$('#invoiced-count .count').text(data['paymentPending'].statusCount);
				$('#paid-count .count').text(data['paid'].statusCount);
			});
		});
	};

	var locationUpdated = function (lat, lon) {
		getFindWorkCount(lat, lon);
	};

	getCounts();
	getFindWorkCount();

	return function () {
		wm.location.trackLocation(locationUpdated);
		FastClick.attach(document.body);
	};
};
