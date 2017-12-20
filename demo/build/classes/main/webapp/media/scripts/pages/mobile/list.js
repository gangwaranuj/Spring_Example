/**
 * Created by steve on 4/15/14.
 */

var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.list = function (status, initialPage) {
	var isFetchingListData = false;
	var currentInvitedPage = initialPage;
	var hasMore = true;
	var maxScrollY = 0;
	var $content;

	var locationUpdated = function (lat, lon) {
		$.each($('.list-distance'), function (e) {
			if($(this).children('.lat').length) {
				$(this).children('.main-value').text(
					Math.ceil(geo_distance(lat, lon,
						$(this).find('.lat').val(),
						$(this).find('.lon').val())
					)
				)
			}
		});
	};

	var isTheListEmpty = function () {
		//if the container has assignments
		if ($('.assignment-container').has('a').length > 0) {
			$('.assignment-container .empty-list-message').hide();
		} else {
			//since it's empty, show the message saying that
			$('.assignment-container .empty-list-message').show();
		}
	};

	var getList = function (status, page) {
		if (!hasMore) { return;}

		isFetchingListData = true;

		$.get("/mobile/assignments/listjson/" + status + "/" + page, function(response, status) {
			if (!_.isEmpty(response.data.rows)) {
				$(function() {
					var $template = $('#assignment-template').html();
					//template when passed two parameters, the second is a data object
					var assignmentz = response.data.rows;
					$('.assignment-container').append(_.template($template)({ assignmentz : assignmentz }));
					hasMore = response.data.pagination.hasMore;
					currentInvitedPage++;
					isTheListEmpty();
				});
			}

			isFetchingListData = false;
		});
	};

	return function () {
		getList(status, currentInvitedPage);
		FastClick.attach(document.body);
		wm.location.trackLocation(locationUpdated);

		$content = $('.content');

		//setting up up button functionality
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
					_.once(getList(status, currentInvitedPage));
				} else {
					return false;
				}
			}
		});

		$content.one('click', '.spin', showSpinner);
	}
};
