define([
	'funcs/wm-tabs'
], function (wmTabs) {
	'use strict';

	describe('wmTabs', function () {
		var tab, otherTab;

		beforeEach(function () {
			appendSetFixtures('<ul class="wm-tabs"><li class="wm-tab"></li><li class="wm-tab"></li><li class="wm-tab"></li></ul>');
			appendSetFixtures('<ul class="wm-tabs"><li class="test"></li><li class="test"></li><li class="test"></li></ul>');

			jasmine.addMatchers({
				toHaveSelectedTabWith: function (util, customEqualityTesters) {
					return {
						compare: function (actual, event, options) {
							var result = {};

							var originalClassList = actual.classList.toString();
							wmTabs(options);
							$(actual).trigger(event);
							var newClassList = actual.classList.toString();

							result.pass = originalClassList !== newClassList;
							if (result.pass) {
								result.message = 'Expected ' + actual + ' not to have selected tab with "' + event + '" and options ' + JSON.stringify(options);
							} else {
								result.message = 'Expected ' + actual + ' to have selected tab with "' + event + '" and options ' + JSON.stringify(options);
							}
							return result;
						}
					}
				}
			});

			tab = document.querySelector('.wm-tab');
			otherTab = document.querySelector('.test');
		});

		afterEach(function () {
			tab = otherTab = undefined;
		});

		it('is defined', function () {
			expect(wmTabs).toBeDefined();
		});

		it('can be initialized', function () {
			expect(wmTabs()).toBeDefined();
		});

		it('targets elements with a class "wm-tab" by default', function () {
			expect(tab).toHaveSelectedTabWith('click', { event: 'click' });
		});

		it('targets elements with the "selector" property', function () {
			expect(otherTab).toHaveSelectedTabWith('click', { selector: '.test', event: 'click' });
		});

		it('it triggered by a "click" event by default', function () {
			expect(tab).toHaveSelectedTabWith('click', { selector: '.wm-tab' });
		});

		it('it triggered by the "event" property', function () {
			expect(tab).toHaveSelectedTabWith('foo', { selector: '.wm-tab', event: 'foo' });
		});

		it('it adds the class "-active" by default', function () {
			wmTabs({ selector: '.wm-tab', event: 'click' });
			$(tab).trigger('click');
			expect(tab.classList).toContain('-active');
		});

		it('it adds the "activeClass" property', function () {
			wmTabs({ selector: '.wm-tab', event: 'click', activeClass: 'foo' });
			$(tab).trigger('click');
			expect(tab.classList).toContain('foo');
		});

		it('it removes the "activeClass" property from its siblings', function () {
			wmTabs({ selector: '.wm-tab', event: 'click', activeClass: 'foo' });
			$(tab).trigger('click');
			$(tab.nextSibling).trigger('click');
			expect(tab.classList).not.toContain('foo');
		});
	});
});
