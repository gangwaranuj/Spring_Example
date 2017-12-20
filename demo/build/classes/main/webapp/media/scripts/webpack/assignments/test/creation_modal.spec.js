/* global describe, it, before, after, afterEach, beforeEach */

import CreationModal from '../creation_modal';

describe('Assignment Creation Modal ::', () => {

	describe('Analytics ::', () => {
		let assignmentModal,
			analyticsSpy;

		const analyticsMessage = 'Assignment Creation Modal';

		const setupEvent = (eventType) => {
			const event = document.createEvent('Event');
			event.initEvent(eventType, true, false);

			return event;
		};

		beforeAll(() => {
			const analytics = { track: () => {} };
			global.window.mediaPrefix = '/globalsrule/';
			global.window.analytics = analytics;
			analyticsSpy = jest.fn(analytics, 'track');
		});

		beforeEach(function (done) {
			// give it a little more time to mount all the components
			this.timeout(50000);
			assignmentModal = new CreationModal();
			done();
		});

		afterEach(() => {
			analyticsSpy.mockReset();
			assignmentModal.modal.destroy();
			assignmentModal = null;
		});

		afterAll(() => {
			global.window.analytics = null;
			global.window.mediaPrefix = null;
		});

		// TODO [tim-mc] resolve issue with editor dependency to reenable this test
		xit('should fire analytics when Save as Draft button is clicked', () => {
			const draftButton = document.getElementsByClassName('-save-draft')[0];
			const draftAnalyticsProps = {
					assignmentId: '',
					action: 'Save As Draft button clicked'
			};
			const clickEvent = setupEvent('click');

			draftButton.dispatchEvent(clickEvent);
			expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, draftAnalyticsProps);
		});
		// TODO [tim-mc] resolve issue with editor dependency to reenable this test
		xit('should fire analytics when Save + Route is clicked', () => {
			const saveButton = document.getElementsByClassName('-route')[0];
			const saveAnalyticsProps = {
				assignmentId: '',
				action: 'Save + Route button clicked'
			};
			const clickEvent = setupEvent('click');

			saveButton.dispatchEvent(clickEvent);
			expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, saveAnalyticsProps);
		});
		// TODO [tim-mc] resolve issue with editor dependency to reenable this test
		xit('should fire analytics when Save as Template button is clicked', () => {
			const templateButton = document.getElementsByClassName('-save-template')[0];
			const saveTemplateAnalyticsProps = {
				action: 'Save as Template clicked'
			};
			const clickEvent = setupEvent('click');

			templateButton.dispatchEvent(clickEvent);
			expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, saveTemplateAnalyticsProps);
		});
		// TODO [tim-mc] resolve issue with editor dependency to reenable this test
		xit('should fire analytics when modal is closed', () => {
			const closeModalAnalyticsProps = {
				action: 'Modal closed'
			};
			const destroyEvent = setupEvent('modal-destroy');

			document.dispatchEvent(destroyEvent);
			expect(analyticsSpy).toHaveBeenCalledWith(analyticsMessage, closeModalAnalyticsProps);
		});
	});
});
