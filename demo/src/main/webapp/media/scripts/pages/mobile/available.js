/**
* Created by steve on 3/11/14.
*/

_.templateSettings = {
	evaluate: /\{\{(.+?)\}\}/g,
	interpolate: /\{\{\=(.+?)\}\}/g,
	escape: /\{\{\-(.+?)\}\}/g
};

var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.available = function (status, initialPage) {
	//vars
	var isFetchingListData = false;
	var currentFeedPage = initialPage;
	var currentInvitedPage = initialPage;
	var feedHasMore = true;
	var hasMore = true;
	var currentFilter = status;
	var maxScrollY = 0;
	var $content;

	var $invited;
	var $feed;
	var $invContainer;
	var $feedContainer;


	//util
	var clear = function () {
		$('.active-filter').removeClass('active-filter');
		$('.active-list').removeClass('active-list');
	};

	var showFilter = function (filter) {
		clear();
		if (filter === 'invited') {
			$invited.addClass('active-filter');
			$invContainer.addClass('active-list');
			currentFilter = 'invited';
		} else {
			$feed.addClass('active-filter');
			$feedContainer.addClass('active-list');
			currentFilter = 'feed';
		}
	};

	var locationUpdated = function (lat, lon) {
		$.each($('.list-distance'), function (e) {
			if ($(this).children('.lat').length) {
				$(this).children('.main-value').text(
					Math.ceil(geo_distance(lat, lon,
						$(this).find('.lat').val(),
						$(this).find('.lon').val())
					)
				)
			}
		});
	};

	var getList = function (status, page) {
		if (!hasMore) { return; }

		isFetchingListData = true;

		$.get("/mobile/assignments/listjson/" + status + "/" + page, function(response, status) {
			if (response.data.rows) {
				$(function () {
					var $template = $('#assignment-template').html();
					//passing in a status, so that the empty list message can show or hide
					//a message, based on whether or not it's invited
					var assignmentz = response.data.rows;
					$invContainer.append(_.template($template)({
						assignmentz: assignmentz,
						status: status,
						page: page
					}));
					hasMore = response.data.pagination.hasMore;
					currentInvitedPage++;
					if (!assignmentz.length) {
						showFilter('feed');
					} else {
						showFilter('invited');
					}
				})
			}
			isFetchingListData = false;
		});
	};

	var getFeed = function (page, lat, lon) {
		if (!feedHasMore) { return; }
		isFetchingListData = true;

		url = "/mobile/assignments/feed/" + page + "?lat=" + lat + "&lon=" + lon;
		if (window.location.href.indexOf("?") > 0) {
			url += "&" + window.location.href.split("?")[1];
		}

		$.get(url, function (response, success) {
			if (response.data.rows) {
				$(function () {
					var $template = $('#assignment-template').html();
					//template when passed two parameters, the second is a data object
					var assignmentz = response.data.rows;
					$feedContainer.append(_.template($template)({
						assignmentz: assignmentz,
						page: page
					}));
					feedHasMore = response.data.pagination.hasMore;
					currentFeedPage++;
				});
			}
			isFetchingListData = false;
		});
	};

	return function () {
		FastClick.attach(document.body);
		wm.location.trackLocation(locationUpdated);

		$content = $('.content');

		var grabPayload = function (filter) {
			maxScrollY = 0;
			if (filter === 'invited') {
				getList('available', currentInvitedPage);
			} else {
				getFeed(currentFeedPage, lat, lon);
			}
		};

		//on page load, set this up
		var lat = $('#home-lat').val();
		var lon = $('#home-lon').val();

		//filters
		$invited = $('.invited');
		$feed = $('.feed');

		//lists
		$invContainer = $('.invited-container');
		$feedContainer = $('.feed-container');

		grabPayload('invited');
		grabPayload('feed');

		//filter buttons
		//$invContainer provides delegate
		$invited.on('click', function () { showFilter('invited') });
		$feed.on('click', function () { showFilter('feed') });
		$invContainer.on('click', '.feed', function () { showFilter('feed') });

		//setting up up-button functionality
		$('.up').on('click', function () {
			$('html, body').animate({scrollTop:0}, 'fast');
			return false;
		});

		$(document).on('scroll', function () {
			$('.up').toggle($(document).scrollTop() > 300);

			if (isFetchingListData) {
				return false;
			}

			if (window.scrollY > maxScrollY) {
				// scrolling past previous max point, so check if fetch needed
				maxScrollY = window.scrollY;

				if ((window.scrollY + (3 * window.innerHeight)) >= $content.height()) {
					if (currentFilter === 'feed') {
						var lat = $('#home-lat').val();
						var lon = $('#home-lon').val();
						_.once(grabPayload('feed'));
					} else {
						_.once(grabPayload('invited'));
					}
				} else {
					return false;
				}
			}
		});

		$content.one('click', '.spin', showSpinner);
	}
};
