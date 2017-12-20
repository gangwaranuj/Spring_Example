import $ from 'jquery';
import Application from '../core';
import UserProfilePopup from './user_profile_popup_view';
import MainView from './main_view';
import WorkerMapView from './resources_map_view';
import '../config/wysiwyg';
import '../dependencies/jquery.bootstrap-dropdown';
import '../dependencies/jquery.bootstrap-collapse';
import '../dependencies/jquery.bootstrap-tab';
import '../config/datepicker';

const loadIntroJs = async() => {
	const module = await import(/* webpackChunkName: "IntroJs" */ '../config/introjs');
	return module.default;
};

const features = config;
const name = 'assignment';
Application.init({ features, name }, () => {});

new UserProfilePopup({
	el: '.main.container'
});

new MainView({
	assignToFirstResource: config.assignToFirstResource,
	auth: config.authEncoded,
	companyId: config.companyId,
	companyName: config.companyName,
	currentUserCompanyName: config.currentUserCompanyName,
	deliverablesConstants: config.deliverablesConstants,
	hasWorkPastDue: config.hasWorkPastDue,
	isAdmin: config.isAdmin,
	isBadRating: config.isBadRating,
	isDispatcher: config.dispatcher,
	isNotSentOrDraft: config.isNotSentOrDraft,
	isPaymentLate: config.isPaymentLate,
	isSuppliedByWorker: config.isSuppliedByWorker,
	millisOffset: parseInt(config.assignmentTzMillisOffset, 10),
	model: config.workEncoded,
	partsConstants: config.partsConstants,
	paymentTime: config.paymentTime,
	pendingApprovalWorkPercentage: config.pendingApprovalWorkPercentage,
	pastDueWorkPercentage: config.pastDueWorkPercentage,
	rating: config.rating,
	showDocuments: config.showDocuments,
	visibilitySettings: config.visibilitySettings,
	isInternal: config.isInternal,
	showAssignButton: config.showAssignButton,
	isSent: config.isSent,
	showActions: config.showActions,
	showVendorActions: config.showVendorActions,
	showBundleActions: config.showBundleActions,
	hasInvitedAtLeastOneVendor: config.hasInvitedAtLeastOneVendor,
	bundleTitle: config.bundleTitle,
	bundleId: config.bundleId,
	hasScheduleConflicts: config.hasScheduleConflicts
});

setTimeout(function () { $('.alert-success').fadeOut(1500); }, 8500);

$('#workers-map-link').on('click', function () {
	var $mapTitle = $('.map-title');
	$('.sidebar').addClass('dn');
	$('#map-canvas').addClass('mapview');
	$('.content').addClass('mapview');
	$('.page-header').addClass('mapview');
	if ($mapTitle.size() === 0) {
		$('#outer-container').prepend('<div class="map-title"></div>');
	}

	$mapTitle.addClass('mapview');
	$('.site-footer').addClass('dn');

	new WorkerMapView({
		workNumber: config.workNumber,
		latitude: config.latitude,
		longitude: config.longitude
	});
}).siblings().on('click', function () {
	$('#map-canvas').removeClass('mapview');
	$('.map-title').removeClass('mapview');
	$('.sidebar').removeClass('dn');
	$('.site-footer').removeClass('dn');
});

if (config.isBuyer) {
	loadIntroJs().then((IntroJs) => {
		const intro = IntroJs('intro-assignment-details-tour');
		intro.setOptions({
			steps: [
				{
					element: document.querySelector('.intro-summary'),
					intro: '<h4>Your assignment page</h4><p>Here, you\'ll find the most important information about your assignments: date, time, price, and internal owner.</p><p>You\'ll also see your "Buyer Metrics," which tells your workers a little about your company.</p>',
					position: 'left'
				},
				{
					element: document.querySelector('#overview'),
					intro: '<h4>Manage your assignments</h4><p>The Assignment tab displays all of the details in your scope of work.</p>',
					position: 'right'
				},
				{
					element: document.querySelector('.intro-notes-tab'),
					intro: '<h4>Message workers via notes</h4><p>Use the Notes tab to send messages to your assigned worker. Every note is transmitted via email, text and push notifications (for those who have them set up).</p>You can also create private notes, visible only to your company\'s team, for internal tracking purposes.<p></p>',
					position: 'right'
				},
				{
					element: document.querySelector('.intro-workers-tab'),
					intro: '<h4>Manage your workers</h4><p>Use the Workers tab to review proprietary information about the different workers you sent your assignment to.</p><p>This is where you\'ll review applicant information to determine who is most qualified worker for your assignment.</p>',
					position: 'right'
				},
				{
					element: document.querySelector('.intro-summary .dropdown-toggle'),
					intro: '<h4>Modify your assignment</h4><p>Once your assignment has been assigned, you can still make limited changes to it via the gear icon drop-down.<p></p>You\'ll be able to:</p><ul><li>Increase the budget</li><li>Add an expense reimbursement</li><li>Reschedule work</li><li>Add a bonus or edit pricing</li></ul>',
					position: 'left'
				}
			]
		});
		intro.watchOnce();
	});
}

// todo: discover root cause and remove this hack
let el = document.querySelector('.search--input');
if (el) {
	el.addEventListener('paste', function (event) {
		if (typeof event !== 'undefined' && typeof event.clipboardData !== 'undefined' && typeof event.clipboardData.items !== 'undefined') {
			if (event.clipboardData.items.length) {
				event.preventDefault();
				el.value = event.clipboardData.getData('text/plain');
			}
		}
	});
}
