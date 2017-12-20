import Application from '../core';
import $ from 'jquery';
import template from './templates/modal.hbs';
import dialogPolyfill from 'dialog-polyfill';

export default (options) => {
	const shouldShowMDL = (Application.Data && Application.Data.shouldShowMDL) || false;

	let settings = Object.assign({
		slides: null,
		sidebar: false,
		fullHeight: false,
		autorun: false,
		root: 'body',
		customHandlers: [],
		closeSelector: '[data-modal-close]',
		slideForwardSelector : '[data-modal-slide="next"]',
		slideBackSelector    : '[data-modal-slide="prev"]',
		activeClass: '-active',
		classList: '',
		template,
		destroyOnClose: false,
		showProgress: false,
		shouldShowMDL,
		showCloseIcon: true
	}, typeof options === 'object' ? options : {});

	const api = {
		show: function () {
			if (shouldShowMDL) {
				settings.modal[0].showModal();
			} else {
				settings.modal.addClass(settings.activeClass);
			}
			disableScroll();
			return this;
		},
		hide: function () {
			if (shouldShowMDL) {
				settings.modal[0].close();
			} else {
				settings.modal.removeClass(settings.activeClass);
			}
			enableScroll();
			return this;
		},
		toggle: function () {
			if (shouldShowMDL) {
				if (settings.modal.hasClass(settings.activeClass)) {
					settings.modal[0].close();
				} else {
					settings.modal[0].showModal();
				}
			} else {
				settings.modal.toggleClass(settings.activeClass);
			}

			if (settings.modal.hasClass(settings.activeClass)) {
				disableScroll();
			} else {
				enableScroll();
			}
			return this;
		},
		destroy: function () {
			if (settings.modal) {
				// fire close event
				let closeEvent = document.createEvent('Event');
				closeEvent.initEvent('modal-destroy', true, true);
				settings.modal[0].dispatchEvent(closeEvent);

				settings.modal.remove();
				settings.modal.off();
				settings.modal = undefined;

				enableScroll();
			}
			return this;
		},
		slideForward: function slideForward() {
			if (!shouldShowMDL) {
				var currentSlide = settings.modal.find('.wm-modal--slide').filter('.-active');
				if (currentSlide.length > 0) {
					var nextSlide = currentSlide.next();
					if (nextSlide.length > 0) {
						currentSlide.removeClass('-active');
						nextSlide.addClass('-active');
					}
				}
			}
		},
		slideBack: function slideBack() {
			if (!shouldShowMDL) {
				var currentSlide = settings.modal.find('.wm-modal--slide').filter('.-active');
				if (currentSlide.length > 0) {
					var previousSlide = currentSlide.prev();
					if (previousSlide.length > 0) {
						currentSlide.removeClass('-active');
						previousSlide.addClass('-active');
					}
				}
			}
		}
	};

	var initialize = function () {
		var hasSlides = settings.slides && Array.isArray(settings.slides),
			hasOneValidSlide = isValid(settings),
			hasAllValidSlides = hasSlides && settings.slides.every(isValid),
			slide;

		if (hasOneValidSlide || hasAllValidSlides) {
			if (!hasSlides) {
				let { title, content, controls, fixedScroll, showCloseIcon } = settings;
				settings.slides = [{ title, content, controls, fixedScroll, showCloseIcon }];
			}

			if (!settings.slides.some(slide => slide.isActive)) {
				settings.slides[0].isActive = true;
			}

			settings.modal = $(settings.template(settings));

			if (shouldShowMDL && !settings.modal[0].showModal) {
				dialogPolyfill.registerDialog(settings.modal[0]);
			}

			// close events
			settings.modal.on('click', settings.closeSelector, ({ target }) => settings.destroyOnClose ? this.destroy() : this.hide());
			$(document).on('keyup', ({ keyCode }) => {
				if (keyCode === 27) {
					settings.destroyOnClose ? this.destroy() : this.hide();
				}
			});

			// slide events
			settings.modal.on('click', settings.slideForwardSelector, this.slideForward);
			settings.modal.on('click', settings.slideBackSelector, this.slideBack);

			settings.customHandlers.forEach(({ event, selector, callback }) => {
				if (event === 'scroll') {
					settings.modal.find(selector).on(event, callback);
				} else {
					settings.modal.on(event, selector, callback);
				}
			});

			settings.modal[0].modal = this;

			// Add the modal to the top of the body... it needs to come first because
			// it styles #outer-container, which needs to be after the modal
			$(settings.root).prepend(settings.modal);

			sizeControls();

			if (settings.autorun) {
				this.show();
			}

			return this;
		} else {
			return false;
		}

		function isValid(slide) {
			return !!slide.title;
		}
	};

	function disableScroll() {
		document.body.style.overflow = 'hidden';
	}

	function enableScroll() {
		document.body.style.overflow = 'initial';
	}

	function sizeControls() {
		settings.modal.find('.wm-modal--slide').each(function () {
			let controls = $(this).find('.wm-modal--control').toArray();
			if (settings.sidebar) {
				controls.forEach((control, i) => {
					if (i === 0) {
						control.classList.add('-sidebar-tertiary-control');
					} else {
						control.classList.add('-sidebar-main-control');
					}
				});
			} else {
				controls.forEach(control =>  control.style.width = `${100 / controls.length}%`);
			}
		});
	}

	return initialize.call(api);
};
