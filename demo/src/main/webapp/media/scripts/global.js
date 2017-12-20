(function($) {
	$.escapeHTML = function(html) {
		return $('<div/>').text(html).html();
	};

	$.unescapeHTML = function(html) {
		return $('<div/>').html(html).text();
	};

	$.nl2br = function(html) {
		// http://phpjs.org/functions/nl2br/
		return (html + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1<br />$2');
	};

	$.escapeHTMLAndnl2br = function(html) {
		return $.nl2br($.escapeHTML(html));
	};

	$.unescapeAndParseJSON = function(json) {
		return $.parseJSON($.unescapeHTML(json));
	};

	$.fn.trackForm = function(options) {
		var gaName = options['name'] || 'form';
		return this.each(function() {
			$(this).on('change', ':input:not(:submit)', function (e) {
				if ($(this).is(":radio")) {
					trackEvent(gaName, $(this).attr("name"), $(this).val());
				} else if ($(this).is(":checkbox")) {
					var onOff = ($(this).is(":checked") ? "x" : "o");
					trackEvent(gaName, $(this).attr("name"), onOff + " - " + $(this).val());
				} else if($(e.target).is('select')) {
					trackEvent(gaName, $(this).attr("name"), $(e.target).find("option:selected").text());
				} else if($(e.target).is(':password')) {
					trackEvent(gaName, $(this).attr("name")); // name only here
				} else{
					trackEvent(gaName, $(this).attr("name"), $(this).val());
				}
			});
		});
	};

	jQuery.fn.placeholder = function (options) {
		settings = jQuery.extend({
			onClass:false,
			offClass:'placeholder',
			placeholderSupport:(function () {
				return 'placeholder' in document.createElement('input');
			})()
		}, options);
		return this.each(function () {
			var input = this;
			if (!settings['placeholderSupport']) {
				$(input).data('defaultValue', $(input).attr('placeholder'));
				if (settings['offClass'] && $(input).val() == $(input).data('defaultValue')) {
					$(input).addClass(settings['offClass']);
				}
			}
			$(input).bind('focus',
				function () {
					if (!settings['placeholderSupport'] && ($(this).val() == $(this).data('defaultValue'))) {
						if (!settings['offClass'] || $(this).hasClass(settings['offClass'])) {
							$(this).val('');
						}
					}
					if (settings['onClass']) $(this).addClass(settings['onClass']);
					if (settings['offClass']) $(this).removeClass(settings['offClass']);
				}).bind('blur', function () {
					if (!settings['placeholderSupport'] && !$(this).val().length) {
						$(this).val($(this).data('defaultValue'));
						if (settings['offClass']) $(this).addClass(settings['offClass']);
					}
					if (settings['onClass']) $(this).removeClass(settings['onClass']);
				});
			$(input).closest('form').bind('submit', function () {
				if (!settings['offClass'] || $(this).hasClass(settings['offClass'])) {
					$(input).val('');
				}
			});
			if (!settings['placeholderSupport']) {
				$(input).blur();
			}
		});
	};
})(jQuery);

function trackEvent() {
	if (typeof ga !== 'undefined') {
		var args = Array.prototype.slice.call(arguments);
		args.unshift('send', 'event');
		ga.apply(null, args);
	}
}

function trackView() {
	if (typeof ga !== 'undefined') {
		var args = Array.prototype.slice.call(arguments);
		args.unshift('send', 'pageview');
		ga.apply(null, args);
	}
}