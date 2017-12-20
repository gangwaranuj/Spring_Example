import $ from 'jquery';
import React, { Component, PropTypes } from 'react';
import {
	WMPaper,
	WMFontIcon,
	WMFlatButton,
} from '@workmarket/front-end-components';
import wmFullScreen from '../../../funcs/wmFullScreen';
import wmNotify from '../../../funcs/wmNotify';
import styles from './styles';

// TODO[tim-mc]: REPLACE WITH STATELESS COMPONENT
// ONCE AVAILABLE IN COMPONENT LIBRARY
class WMCreationBetaBanner extends Component {
	constructor(props) {
		super(props);

		this.submitBetaConfirmation = this.submitBetaConfirmation.bind(this);
	}
	initOptIn() {
		wmFullScreen({
			message: 'Activating the beta will enable it for all users within ' +
				'your company. You\'ll be able to switch back to the current version ' +
				'if you have any issues.',
			callToAction: 'Would you like to proceed?',
			buttons: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Enable',
					primary: true,
					classList: 'confirm-assignment-creation-beta'
				}
			]
		});

		const confirmButton = document.getElementsByClassName('confirm-assignment-creation-beta')[0];
		confirmButton.addEventListener('click', this.submitBetaConfirmation);
	}

	submitBetaConfirmation() {
		$.ajax({
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			url: '/beta_features/on',
			data: JSON.stringify('ASSIGNMENTS'),
			success: () => {
				if (this.props.homeRedirect) {
					window.location = '/?launchAssignmentModal';
				} else {
					wmNotify({ message: 'Successfully enrolled. Reload the page to use the beta.' });
				}
			},
			error: function error() {
				wmNotify({
					message: 'There was a problem enrolling. Please try again later.',
					type: 'danger'
				});
			}
		});
	}

	render() {
		const bannerText = 'We\'ve made some improvements to the assignment creation experience that we think you’ll like.';
		const icon = 'info outline';
		const buttonLabel = 'TRY THE BETA';

		return (
			<WMPaper
				style={ styles.paper }
			>
				<WMFontIcon
					className="material-icons"
					id="wm-assignment-creation-beta-banner-icon"
					color={ styles.iconColor }
					style={ styles.icon }
				>
					{ icon }
				</WMFontIcon>
				<div
					style={ styles.text }
				>
					{ bannerText }
				</div>
				<WMFlatButton
					style={ styles.button }
					label={ buttonLabel }
					labelStyle={ styles.label }
					onClick={ () => this.initOptIn() }
				/>
			</WMPaper>
		);
	}
}

export default WMCreationBetaBanner;

WMCreationBetaBanner.propTypes = {
	homeRedirect: PropTypes.bool
};
