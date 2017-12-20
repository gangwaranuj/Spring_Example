import $ from 'jquery';
import _ from 'underscore';
import '../funcs/wmNotifications';

export default () => {

	let $meter = $('#progress_bar'),
		uploadProgress = $.Deferred(),
		getProgress, updateProgressBar, interval;

	updateProgressBar = (response) => {
		if (response && response.data && !_.isUndefined(response.data.uploadProgress)) {
			$meter.css('width', response.data.uploadProgress * 100 + '%');
			if (response.data.uploadProgress === 1) {
				uploadProgress.resolve();
				clearInterval(interval);
				// wait 1 second for the notification to be processed, then fetch the latest notification data and update
				setTimeout($('.header--notifications').wmNotifications(), 1000);
			}
		} else {
			uploadProgress.reject();
		}
	};

	getProgress = () => {
		$.getJSON('/uploadProgress/progress', updateProgressBar);
	};

	interval = setInterval(getProgress, 5000);
	getProgress();

	return uploadProgress.promise();
};
