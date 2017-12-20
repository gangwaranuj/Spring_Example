/**
 * Created by steve on 4/19/14.
 */

var wm = wm || {};
wm.location = wm.location || {};

wm.location.latitude = undefined;
wm.location.longitude = undefined;
wm.location.trackingOn = false;
wm.location.callback = undefined;

wm.location.locationUpdated = function (position) {
	if (!wm.location.trackingOn) { return; }

	wm.location.latitude = position.coords.latitude;
	wm.location.longitude = position.coords.longitude;

	wm.location.callback(wm.location.latitude, wm.location.longitude);
};

wm.location.locationUpdateFailed = function (position) {
	if (!wm.location.trackingOn) { return; }

	wm.location.latitude = undefined;
	wm.location.longitude = undefined;
//	callback(latitude, longitude); // don't send update yet
};

wm.location.trackLocation = function (callbackFn) {
	wm.location.trackingOn = true;
	wm.location.callback = callbackFn;

	if (navigator.geolocation) { // check isNative eventually
		navigator.geolocation.getCurrentPosition(wm.location.locationUpdated, wm.location.locationUpdateFailed);
	}
};