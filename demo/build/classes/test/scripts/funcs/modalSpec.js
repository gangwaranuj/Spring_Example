// commented out until handlebars is full amd
//define(['wmModal', 'handlebars'], function (wmModal) {
//	'use strict';
//
//	describe('wmModal', function () {
//		var minmumRequiredParameters = {
//			title: 'Work Market Modal'
//		}, modal;
//
//		afterEach(function () {
//			if (modal) {
//				modal.destroy();
//			}
//		});
//
//		it('is defined', function () {
//			expect(wmModal).toBeDefined();
//		});
//
//		it('can be initialized', function () {
//			modal = wmModal();
//			expect(modal).toBeDefined();
//		});
//
//		it('returns an API when all required parameters are provided', function () {
//			modal = wmModal(minmumRequiredParameters);
//			expect(modal).toEqual(jasmine.any(Object));
//		});
//
//		it('returns false when all required parameters are not provided', function () {
//			modal = wmModal();
//			expect(modal).toBeFalsy();
//		});
//
//		it('requires a title', function () {
//			modal = wmModal(_.omit(minmumRequiredParameters, 'title'));
//			expect(modal).toBeFalsy();
//		});
//
//		it('shows immediately when "autorun" is true', function () {
//			wmModal(_.defaults({ activeClass: '-active', autorun: true }, minmumRequiredParameters));
//			expect($('.wm-modal')).toHaveClass('-active');
//		});
//
//		it('does not show immediately by default', function () {
//			wmModal(_.defaults({ activeClass: '-active' }, minmumRequiredParameters));
//			expect($('.wm-modal')).not.toHaveClass('-active');
//		});
//
//		it('inserts the modal into the DOM', function () {
//			wmModal(minmumRequiredParameters);
//			expect($('.wm-modal')).toBeInDOM();
//		});
//
//		it('appends the modal to "root"', function () {
//			modal = wmModal(_.defaults({ root: 'body' }, minmumRequiredParameters));
//			expect($('body')).toContainElement('.wm-modal');
//		});
//
//		it('listens for the click event', function () {
//			modal = wmModal(minmumRequiredParameters);
//			expect($('.wm-modal')).toHandle('click');
//		});
//
//		describe('show', function () {
//			beforeEach(function () {
//				modal = wmModal(_.defaults({ activeClass: '-active' }, minmumRequiredParameters));
//				spyOn(modal, 'show').and.callThrough();
//				modal.show();
//			});
//
//			afterEach(function () {
//				modal.destroy();
//				modal = undefined;
//			});
//
//			it('can be called', function () {
//				expect(modal.show).toHaveBeenCalled();
//			});
//
//			it('adds the active class to the modal', function () {
//				expect($('.wm-modal')).toHaveClass('-active');
//			});
//
//			it('makes the modal visible', function () {
//				expect($('.wm-modal')).toBeVisible();
//			});
//
//			it('returns the API', function () {
//				expect(modal.show()).toBe(modal);
//			});
//		});
//
//		describe('hide', function () {
//			beforeEach(function () {
//				modal = wmModal(_.defaults({ activeClass: '-active', autorun: true }, minmumRequiredParameters));
//				spyOn(modal, 'hide').and.callThrough();
//				modal.hide();
//			});
//
//			afterEach(function () {
//				modal.destroy();
//				modal = undefined;
//			});
//
//			it('can be called', function () {
//				expect(modal.hide).toHaveBeenCalled();
//			});
//
//			it('removes the active class to the modal', function () {
//				expect($('.wm-modal')).not.toHaveClass('-active');
//			});
//
//			// TODO: load in CSS fixtures
//			xit('makes the modal invisible', function () {
//				expect($('.wm-modal')).not.toBeVisible();
//			});
//
//			it('returns the API', function () {
//				expect(modal.hide()).toBe(modal);
//			});
//		});
//
//		describe('toggle', function () {
//			beforeEach(function () {
//				modal = wmModal(_.defaults({ activeClass: '-active' }, minmumRequiredParameters));
//				spyOn(modal, 'toggle').and.callThrough();
//				modal.toggle();
//			});
//
//			afterEach(function () {
//				modal.destroy();
//				modal = undefined;
//			});
//
//			it('can be called', function () {
//				expect(modal.toggle).toHaveBeenCalled();
//			});
//
//			it('adds the active class to the modal on odd runs', function () {
//				expect($('.wm-modal')).toHaveClass('-active');
//				expect(modal.toggle.calls.count()).toEqual(1);
//			});
//
//			it('removes the active class to the modal on even runs', function () {
//				modal.toggle();
//				expect($('.wm-modal')).not.toHaveClass('-active');
//				expect(modal.toggle.calls.count()).toEqual(2);
//			});
//
//			it('makes the modal visible on odd runs', function () {
//				expect($('.wm-modal')).toBeVisible();
//				expect(modal.toggle.calls.count()).toEqual(1);
//			});
//
//			// TODO: load in CSS fixtures
//			xit('makes the modal invisible on even runs', function () {
//				expect($('.wm-modal')).not.toBeVisible();
//				expect(modal.toggle.calls.count()).toEqual(2);
//			});
//
//			it('returns the API', function () {
//				expect(modal.hide()).toBe(modal);
//			});
//		});
//
//		describe('destroy', function () {
//			var $modal, destroy;
//
//			beforeEach(function () {
//				modal = wmModal(minmumRequiredParameters);
//				spyOn(modal, 'destroy').and.callThrough();
//				$modal = $('.wm-modal');
//				destroy = modal.destroy();
//			});
//
//			afterEach(function () {
//				$modal = destroy = undefined;
//			});
//
//			it('can be called', function () {
//				expect(modal.destroy).toHaveBeenCalled();
//			});
//
//			it('removes the modal from the DOM', function () {
//				expect($modal).not.toBeInDOM();
//			});
//
//			it('removes the click event from the modal', function () {
//				expect($modal).not.toHandle('click');
//			});
//
//			it('returns the API', function () {
//				expect(destroy).toBe(modal);
//			});
//		});
//	});
//});
