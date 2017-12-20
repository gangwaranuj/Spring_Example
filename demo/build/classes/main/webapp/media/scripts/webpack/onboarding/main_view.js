'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import ProfileInfoView from './profile_info_view';
import LocationView from './location_view';
import IndustriesView from './industries_view';
import GetStartedView from './get_started_view';
import Optimizely from '../dependencies/optimizely';
import wmModal from '../funcs/wmModal';
import t from '@workmarket/translation';

export default Backbone.View.extend({
	el: '.page-onboarding',
	events: {
		'click [data-slide]': 'slide',
		'click [data-track]': 'trackWithOptimizely',
		'blur input': 'validateSlide',
		'blur textarea': 'validateSlide',
		'change select': 'validateSlide',
		'change input[name="industry"]': 'validateSlide',
		'change input[name="individual"]': 'validateSlide'
	},
	renderedSubViews: 0,
	subViewsHaveRendered: $.Deferred(),
	subViews: ['profileInfo', 'location', 'industries', 'getStarted'],

	initialize: function () {
		this.modal = wmModal({
			showProgress: true,
			slides: [
				{
					title: t('onboarding.profileInformation'),
					id: 'profileInfoView'
				},
				{
					title: t('onboarding.location'),
					id: 'locationView'
				},
				{
					title: t('onboarding.expertise'),
					id: 'industriesView'
				},
				{
					title: t('onboarding.done'),
					id: 'getStartedView'
				}
			]
		});

		this.render();
	},
	render: function () {
		// Initialize all our views
		this.profileInfo = new ProfileInfoView({ model: this.model.profileInfo });
		this.location = new LocationView({ model: this.model.location });
		this.industries = new IndustriesView({ model: this.model.industries });
		this.getStarted = new GetStartedView({ model: this.model.getStarted });

		// Track their rendering
		_.each(this.subViews, function (subView) {
			this.listenTo(this[subView], 'render', this.subViewHasRendered);
		}, this);

		// When all subviews have rendered, jump to the current slide
		$.when(this.subViewsHaveRendered.promise()).then(_.bind(function () {
			this.modal.show();
			this.slide(this.getCurrentSlide());
		}, this));

		this.model.location.on('change', this.validateSlide.bind(this));
		this.model.profileInfo.phones.on('change', this.validateSlide.bind(this));
		this.listenTo(this.industries, 'render', this.validateSlide.bind(this));
		return this;
	},

	subViewHasRendered: function () {
		this.renderedSubViews = this.renderedSubViews + 1;
		if (this.renderedSubViews === this.subViews.length) {
			this.subViewsHaveRendered.resolve();
		}
	},

	slide: function (slide) {
		// The only time there will not be an active slide is when the page
		// first renders, in which case slide is passed a numerical value, so
		// we'll treat that slide as "thisSlide"
		var $thisSlide = this.$('.wm-modal--slide').filter('.-active').size() ? this.$('.wm-modal--slide').filter('.-active') : this.$('.wm-modal--slide').eq(slide),
			currentSubViewName = this.subViews[$thisSlide.index()],
			currentView = this[currentSubViewName],
			currentModel = currentView.model,
			url, hasUpdated, $nextSlide, nextSubViewName,
			event = slide;

		// If slide is used as an event handler
		if (_.isObject(slide)) {
			url = this.$(slide.target).data('url');
			slide = this.$(slide.target).data('slide');
		}

		if (_.isString(slide) && slide !== 'finish') {
			// Retrieve the first slide (either forward or backward)
			$nextSlide = $thisSlide[slide]();
			nextSubViewName = this.subViews[$nextSlide.index()];
		} else if (_.isNumber(slide)) {
			// Jump directly to a specific slide
			$nextSlide = this.$('.wm-modal--slide').eq(slide);
			nextSubViewName = this.subViews[$nextSlide.index()];
		}
		if (nextSubViewName === 'industries') {
			this[nextSubViewName].updateRecommendedSkills();
		}

		// Save to the server whenever we click a continue or finish button
		if (_.contains(['next','finish'], slide)) {
			hasUpdated = currentModel.update();
			if (hasUpdated.state() !== 'rejected') {
				// Upon click of a finish button, wait for a server response before
				// leaving onboarding. Upon click of a continue button, wait for a
				// server response before continuing to the next slide.
				this.trackWithSegment(currentSubViewName, event);
				if (slide === 'finish') {
					$.when(hasUpdated).done(leaveOnboarding).fail(this.showError);
				} else if (slide === 'next') {
					$.when(hasUpdated).done(goToNextSlide.bind(this)).fail(this.showError);
				}
			}
		} else {
			// Upon click of a back button, slide back immediately.
			goToNextSlide.bind(this)();
		}

		function leaveOnboarding() {
			window.location = url;
		}

		function goToNextSlide() {
			$thisSlide.removeClass('-active');
			$nextSlide.addClass('-active');

			$('.wm-modal').animate({ scrollTop: 0 }, 'slow');

			this.validateSlide();

			// Google analytics
			analytics.page({
				path: `/onboarding/${nextSubViewName}`
			})
		}
	},

	getCurrentSlide: function () {
		var models = _.reduce(this.model, function (memo, model) {
			if (model instanceof Backbone.Model || model instanceof Backbone.Collection) {
				memo.push(model);
			}
			return memo;
		}, []);

		var currentModel = _.find(models, function (model) { return !model.isValid(); });
		return currentModel ? currentModel.step - 1 : _.last(models).step - 1;
	},

	trackWithOptimizely: function (event) {
		var oEvent = $(event.currentTarget).data('track');
		Optimizely.push(['trackEvent', oEvent]);
	},

	trackWithSegment: function (currentStep, { currentTarget }) {
		const eventProperties = { currentStep };

		switch (currentStep) {
			case 'profileInfo':
				let phone = _.findWhere(this.profileInfo.model.get('phones'));
				let callingCode = _.findWhere(this.profileInfo.phones.options.countryCodes, { id: Number(phone.code) });
				let photo = this.profileInfo.model.get('avatar');
				let legalStatus = this.profileInfo.model.get('legal');

				Object.assign(eventProperties, {
					phone_number: callingCode.name + phone.number,
					photo_added: !!photo.image,
					legal_status: !!legalStatus.individual
				});
				break;
			case 'location':
				Object.assign(eventProperties, {
					city: this.location.model.get('city'),
					state: this.location.model.get('stateShortName'),
					country: this.location.model.get('countryIso'),
					zip: this.location.model.get('postalCode'),
				});
				break;
			case 'industries':
				let selectedIndustries = this.industries.model.get('industries').filter(({ checked }) => checked);
				let industries = selectedIndustries.map(({ name }) => name);
				let removedSkills = this.industries.model.get('removedSkills') || [];
				let jobTitle = this.industries.model.get('jobTitle') || {};
				Object.assign(eventProperties, {
					industries: industries,
					selectedSkills: this.industries.model.get('skills').map(({ name }) => name),
					jobTitle: jobTitle.name,
					removedSkills: removedSkills.map(({ name }) => name)
				});
				break;
			case 'getStarted':
				Object.assign(eventProperties, { link_clicked: currentTarget.textContent });
				break;
		}

		analytics.track('Worker Onboarding', Object.assign({ action: 'slide transition' }, eventProperties));
	},

	validateSlide: function () {
		var $thisSlide = this.$('.wm-modal--slide').filter('.-active').size() ? this.$('.wm-modal--slide').filter('.-active') : this.$('.wm-modal--slide').eq(slide),
			currentSubViewName = this.subViews[$thisSlide.index()],
			currentView = this[currentSubViewName],
			currentModel = currentView.model;

			if (!currentModel.isValid()) {
				currentView.$('[data-slide="next"]').attr('disabled', 'disabled');
				this.showErrors(this.getValidationError(currentModel), currentView);
			} else {
				currentView.$('[data-slide="next"]').removeAttr('disabled');
				this.clearErrors(currentView);
			}
	},

	getValidationError: function (model) {
		var externalObjects = _.map(model.externalFields, function (field) {
				let obj = this[field];
				return (obj === undefined || obj == null) ? field : (obj.models || obj);
			}, model),
			validationErrors = _.union(model.validationError, _.pluck(_.flatten(externalObjects), 'validationError'));
		return _.compact(_.flatten(validationErrors));
	},

	showErrors: function (errors, currentView) {
		currentView.$('input, textarea, .selectize-input').removeClass('error');
		_.each(errors, function(error) {
			if (error.name === 'companyEmployees' || error.name === 'companyYearFounded') {
				currentView.$('[name="' + error.name + '"]').next('.wm-select').find('.selectize-input').addClass('error');
			} else {
				currentView.$('[name="' + error.name + '"]').addClass('error');
			}
			var message = 
				error.name === 'address' 
				? 'Please use the drop pin on the map to locate a valid street address.' 
				: t('errors.provideValidResponses');
			currentView.$('[data-slide="next"]').attr('aria-label', message);
			currentView.$('[data-slide="next"]').addClass('tooltipped');
		});
	},

	clearErrors: function (currentView) {
		currentView.$('input, textarea, .selectize-input').removeClass('error');
		currentView.$('[data-slide="next"]').removeClass('tooltipped');
		currentView.$('[data-slide="next"]').attr('aria-label', '');
	}
});
