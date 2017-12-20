import $ from 'jquery';
import 'jquery.backstretch';
import _ from 'underscore';
import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import Application from '../core';
import getCSRFToken from '../funcs/getCSRFToken';
import ajaxSendInit from '../funcs/ajaxSendInit';
import workFeed from '../workfeed/main';
import qq from '../funcs/fileUploader';
import wmModal from '../funcs/wmModal';
import configureStore from './configureStore';
import WMHome from './components/WMHome';
import WMWorkerBanner from './components/WMWorkerBanner';

Application.init({ name: 'home', features: config }, () => {});

const uri = config.backgroundImageUri.replace('http:', '');
$('body').backstretch(uri);
$('a.nav-home').css('background-image', `url('${mediaPrefix}/images/home-icon.png')`);

const loadAssignmentCreationModal = async() => {
	const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../assignments/creation_modal');
	return module.default;
};
const loadIntroJs = async() => {
	const module = await import(/* webpackChunkName: "IntroJs" */ '../config/introjs');
	return module.default;
};
const loadLanguageConflictModal = async () => {
	const module = await import(/* webpackChunkName: "languageConflictModal" */ './language_conflict_modal');
	return module.default;
};

$('#modal-background-changer').on('click', (e) => {
	e.preventDefault();

	$.ajax({
		type: 'GET',
		url: '/home/change_background',
		context: this,
		success (response) {
			if (!_.isEmpty(response)) {
				const modal = wmModal({
					autorun: true,
					title: 'Customize your background',
					destroyOnClose: true,
					content: response
				});

				const uploader = new qq.FileUploader({ // eslint-disable-line no-unused-vars
					element: $('#background-changer')[0],
					action: '/home/upload_background_image',
					allowedExtensions: ['jpg', 'jpeg', 'gif', 'png', 'bmp'],
					CSRFToken: getCSRFToken(),
					sizeLimit: 150 * 1024 * 1024, // 150MB
					multiple: false,
					template: $('#qq-uploader-tmpl').html(),
					onSubmit () {
						$('#background-messages').addClass('dn');
						$('#spinner').show();
					},
					onComplete (id, fileName, data) {
						$('#spinner').hide();
						if (data.successful) {
							$('.backstretch').remove();
							$('body').backstretch(data.uri.replace('http:', ''));
							modal.destroy();
						} else {
							const messages = $('#background-messages');
							messages.html(`<span>${data.errors.join('<br/>')}</span>`);
							messages.removeClass('dn');
						}
					}
				});

				$('.thumbnail')
					.not('.qq-upload-button')
					.on('click', (ev) => {
						ev.preventDefault();
						const $selection = $(ev.currentTarget);
						const $image = $selection.find('img:first');
						ajaxSendInit();
						$.post('/home/change_background', { assetId: $image.attr('id') }, () => {
							$('.backstretch').remove();
							$('body').backstretch($image.attr('src'));
						});
					});
			}
		}
	});
});

const homeContainer = document.getElementById('home__container');

const renderHomeApp = (isBuyer) => {
	let homeApp;
	if (isBuyer) {
		const store = configureStore();
		homeApp = (
			<Provider store={ store }>
				<WMHome showProgressBar={ config.renderOnboardingProgress } />
			</Provider>
		);
	} else {
		homeApp = <WMWorkerBanner invitationsCount={ config.groupInvitationsCount } />;
	}
	render(homeApp, homeContainer);
};

if (config.isWorker || config.isDispatcher) {
	workFeed.create(config);
} else if (config.isBuyer) {
	loadIntroJs().then((IntroJs) => {
		const intro = IntroJs('intro-welcome-tour');
		intro.setOptions({
			steps: [
				{
					intro: `<h4>Welcome to Work Market, ${config.firstName}</h4><p>Work Market is the #1 freelancer management solution. Use it to find and manage freelancers, contractors and consultants.</p>`
				},
				{
					intro:
						"<h4>Let's get started!</h4><p>From your home page you have access to all of the Work Market features from the four center tiles.</p>"
				},
				{
					element: document.querySelector('#nav-drawer--slider'),
					intro: '<h4>Navigation</h4><p>You can also navigate via this orange button.</p>',
					position: 'right'
				},
				{
					element: document.querySelector('#find-people'),
					intro:
						"<h4>Find talent</h4><p>Browse local talent that meets your company’s requirements with our <a href='/search' target='_blank'>Worker Search Engine.</p>",
					position: 'right'
				}
			]
		});
		intro.watchOnce();
	});

	if (window.location.search.indexOf('launchAssignmentModal') > -1) {
		loadAssignmentCreationModal().then(CreationModal => new CreationModal());
	}
}

if (window.workmarket.locale !== window.workmarket.preferredLocale) {
	loadLanguageConflictModal().then((modal) => {
		modal(window.workmarket.locale, window.workmarket.preferredLocale);
	});
}

renderHomeApp(config.isBuyer);
