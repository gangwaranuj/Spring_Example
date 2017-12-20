import $ from 'jquery';
import injectTapEventPlugin from 'react-tap-event-plugin';
import React from 'react';
import PropTypes from 'prop-types';
import { render } from 'react-dom';
import { WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import { showFormMessaging } from './credentials-messaging';
import Application from '../core';
import { getQueryParams } from '../funcs/utils';

injectTapEventPlugin();

const $loginForm = $('#login-form');
const $pageForm = $('#page_form');
const $loginEmail = $('#login-email');
const $loginPassword = $('#login-password');
const $loginSubmit = $('#login_page_button');
const $loginMessages = $('#login-messages');

const $resetForm = $('#reset-form');
const $resetEmail = $('#email');
const $resetMessages = $('#reset-messages');

const showResetView = () => {
	$resetForm.show();
	$loginForm.hide();
	$resetEmail.focus();

	showFormMessaging($resetMessages);
};

const showLoginView = () => {
	$loginForm.show();
	$resetForm.hide();

	if (!$loginEmail.val().length) {
		$loginEmail.focus();
	} else if (!$loginPassword.val().length) {
		$loginPassword.focus();
	}

	showFormMessaging($loginMessages);
};

const checkHash = () => {
	if (location.hash.match(/reset/)) {
		showResetView();
	} else {
		showLoginView();
	}
};

checkHash();

$(window).on('hashchange', checkHash);

// Login Form

$loginForm.on('input', 'input', () => $resetEmail.val($loginEmail.val()));

$loginSubmit.on('click', (e) => {
	e.preventDefault();

	// 'redirectTo' is the accepted parameter in spring for targetUrl for redirecting after login is successful.
	const redirectToParam = getQueryParams().redirectTo
	if(redirectToParam) {
        const redirectParam = $("<input>")
            .attr("type", "hidden")
            .attr("name", "redirectTo").val(redirectToParam);
        $pageForm.append(redirectParam);
    }

	$pageForm.attr('method', 'post');
	$pageForm.attr('action', '/login');
	$pageForm.submit();
});

$('#reset-password').on('click', () => {
	$loginForm.hide();
	$resetForm.show();

	$resetEmail.focus();
});

const LanguageSelector = ({ currentLocale, locales, renderElm }) => {
	return (<WMSelectField
		value={ currentLocale }
		onChange={ (event, key, value) => {
			renderLanguageSelector(currentLocale, locales, renderElm);
			// For now we are updating the locale filter using this query string method
			window.location.href = `/login?lang=${value}`;
		} }
	>
		{
			locales.map((locale, idx) =>
				<WMMenuItem key={ `locale_${idx}` } value={ locale.code } primaryText={ <div style={ { display: 'flex', alignItems: 'center' } }><img style={ { marginRight: '5px' } } src={ mediaPrefix + locale.iconUrl }/> { locale.language }</div> } />
			)
		}
	</WMSelectField>);
};

LanguageSelector.propTypes = {
    currentLocale: PropTypes.string.isRequired,
    locales: PropTypes.array.isRequired,
    renderElm: PropTypes.object.isRequired
};

const renderLanguageSelector = (currentLocale, locales, renderElm) => {
	render(
		<LanguageSelector currentLocale={ currentLocale } locales={ locales } renderElm={ renderElm }/>,
		renderElm
	);
};

const languageSelectorElm = document.getElementById('language-selector');

if (languageSelectorElm) {
	renderLanguageSelector(window.workmarket.locale, window.workmarket.supportedLocales, languageSelectorElm)
}
