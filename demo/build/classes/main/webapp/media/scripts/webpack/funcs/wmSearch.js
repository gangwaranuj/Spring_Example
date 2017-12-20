import $ from 'jquery';
import _ from 'underscore';

$.fn.wmSearch = function (options) {
	let settings, $header, events;

	settings = $.extend({
		header: '#header',
		input: '.search--input',
		submit: '.search--submit',
		icon: '.icon-search',
		url: '/assignments/search?keyword=',
		activeClass: '-active',
		blackoutClass: '-dark'
	}, options);

	$header = $(settings.header);

	events = function ($this) {
		let $input = $(settings.input, $this),
			$submit = $(settings.submit, $this);

		return {

			focusSearch: function (event) {
				$this.addClass(settings.activeClass);
				$input.data('blur',true);

				if (event.data.isInHeader) {
					$header.addClass(settings.blackoutClass);
				}
			},

			blurSearch: function (event) {
				var isInputEmpty = ($(event.target).val().length === 0),
					blurIsOutOfSearch = $input.data('blur');

				if (isInputEmpty && blurIsOutOfSearch) {
					$this.removeClass(settings.activeClass);

					if (event.data.isInHeader) {
						$header.removeClass(settings.blackoutClass);
					}
				} else {
					$input.trigger('focus');
				}
			},

			keyupSearch: function (event) {
				var isInputEmpty = ($(event.target).val().length === 0);

				$submit.toggleClass(settings.activeClass, !isInputEmpty);
				if (event.keyCode === 13 && !isInputEmpty) {
					$submit.trigger('mousedown');
				}
			},

			startSelectingSearchIcon: function (event) {
				$input.data('blur',false);
			},

			finishSelectingSearchIcon: function (event) {
				$input.trigger('focus');
			},

			submitSearch: function (event) {
				if (!_.isNull(settings.url)) {
					window.location = settings.url + $input.val();
				}
			}

		};
	};

	return this.each(function () {
		var $this = $(this),
			isInHeader = ($this.closest(settings.header).length !== 0);

		$this.on('focus', settings.input, { isInHeader: isInHeader }, events($this).focusSearch);
		$this.on('blur', settings.input, { isInHeader: isInHeader }, events($this).blurSearch);
		$this.on('keyup', settings.input, { isInHeader: isInHeader }, events($this).keyupSearch);
		$this.on('mousedown', settings.icon, { isInHeader: isInHeader }, events($this).startSelectingSearchIcon);
		$this.on('mouseup', settings.icon, { isInHeader: isInHeader }, events($this).finishSelectingSearchIcon);
		$this.on('mousedown', settings.submit, { isInHeader: isInHeader }, events($this).submitSearch);
	});
};