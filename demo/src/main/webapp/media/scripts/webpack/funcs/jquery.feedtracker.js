/*
 * Feedtracker -
 * 1. Captures first query request.
 * 2. Logs it (iff WorkFeed is scrolled into view)
 * 3. Logs any subsequent queries, indicating change from first.
 */

jQuery.fn.extend({
	feedTracker: function (options) {
		'use strict';

		var that = this,
			keywordField = options.keywordField,
			fields = options.queryFields,
			firstQuery = {},
			feedInView = false,
			captiveFirstFeedRequest = null,
			alreadyFiredFirstFeedRequest = false;


		var searchChanges = function(newQuery) {
			var changes = [];
			var changeDescription = 'default';


			// _.each(fields, function (description, key) {
			Object.keys(fields).forEach(function (description, key) {
				// Wasn't in first query, added in this one
				if (newQuery[key] && (!firstQuery.hasOwnProperty(key) || !firstQuery[key])) {
					changes.push(description + 'Added');
				} else if (firstQuery[key] && (!newQuery.hasOwnProperty(key) || !newQuery[key])) {
					// Fields that were removed in the new query
					changes.push(description + 'Removed');
				} else if (firstQuery[key] != newQuery[key]) {
					// Fields that were modified in the new query
					changes.push(description + 'Modified');
				}
			});

			if (changes.length > 0) {
				changes.sort();
				changeDescription = changes.join('+');
			}

			return changeDescription;
		};


		// Start - Google Site Search Analytics Tracking Code

		var track = function (newQuery) {

			if (!newQuery.hasOwnProperty(keywordField) || !newQuery[keywordField] ) {
				newQuery[keywordField] = 'NO_KEYWORD_PROVIDED';
			}

			if ($.isEmptyObject(firstQuery)) {
				firstQuery = newQuery;
				newQuery.changed = 'default';
			} else {
				newQuery.changed = searchChanges(newQuery);
			}

			var url = '/feed/firehose?' + $.param(newQuery);

			// first request?:  hang onto it (until feedInView)
			if (alreadyFiredFirstFeedRequest === false && feedInView === false) {
				captureFeedRequest(url);
			} else {
				trackFeedRequest(url);
			}
		};

		var captureFeedRequest = function (url) {
			captiveFirstFeedRequest = url;
		};

		var trackFeedRequest = function (url) {
			analytics.page({
				path: url
			});
		};

		var trackCapturedFeedRequest = function () {
			if (captiveFirstFeedRequest !== null && alreadyFiredFirstFeedRequest === false) {
				trackFeedRequest(captiveFirstFeedRequest);
				captiveFirstFeedRequest = null;
				alreadyFiredFirstFeedRequest = true;
			}
		};

		var feedScrolledIntoView = function() {
			feedInView = true;

			if (alreadyFiredFirstFeedRequest === false) {
				trackCapturedFeedRequest();
			};
		};

		var isFeedInView = function(feedContainerElem) {
			var $window = $(window);
			var $feedContainerElem = $(feedContainerElem);

			var docViewTop = $window.scrollTop();
			var docViewBottom = docViewTop + $window.height();

			var feedContainerTop = $feedContainerElem.offset().top;
			var feedContainerBottom = feedContainerTop + $feedContainerElem.height();

			return (
				docViewTop >= (feedContainerTop - 200) ||   // Saw Feed Top:  Scrolled up enough to halfway obscure the 4 boxes
				docViewBottom >= (feedContainerBottom - 10) // Saw Feed Bottom: Scrolled up enough to see last assignment
															//                  or reveal "Sorry, no assignments"
			);
		};

		const _debounce  = function(func, wait, immediate) {
			var timeout, args, context, timestamp, result;

			var later = function() {
				var last = _now() - timestamp;

				if (last < wait && last >= 0) {
					timeout = setTimeout(later, wait - last);
				} else {
					timeout = null;
					if (!immediate) {
						result = func.apply(context, args);
						if (!timeout) context = args = null;
					}
				}
			};

			return function() {
				context = this;
				args = arguments;
				timestamp = _now();
				var callNow = immediate && !timeout;
				if (!timeout) timeout = setTimeout(later, wait);
				if (callNow) {
					result = func.apply(context, args);
					context = args = null;
				}

				return result;
			};
		};

		const _now = Date.now || function() {
			return new Date().getTime();
		};

		// Scroll trigger which lets us wait to track the first feed request until Worker scrolls the feed into view.
		$(window).scroll(_debounce(function(){
			if (isFeedInView(that)) {
				feedScrolledIntoView();
			}
		}, 150));

		// Query trigger which lets us intercept feed requests
		$(window).on('feedQuery', function(event, query) {
			track(query);
		});

	}

});
