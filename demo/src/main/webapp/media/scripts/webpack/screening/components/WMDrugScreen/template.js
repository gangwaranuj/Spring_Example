import PropTypes from 'prop-types';
import React from 'react';
import { Map } from 'immutable';
import {
	commonStyles,
	WMFontIcon,
	WMPaper,
	WMRaisedButton,
	WMMessageBanner
} from '@workmarket/front-end-components';
import WMPersonalInformation from '../WMPersonalInformation';
import WMPaymentInformation from '../WMPaymentInformation';
import WMAboutVendor from '../WMAboutVendor';
import WMScreeningMessage from '../WMScreeningMessage';
import styles from './styles';

const WMDrugScreen = ({
	state,
	onDisabledSubmitForm,
	onSubmitForm
}) => (
	<section id="wm-drug-screen">
		{
			state.get('isInternational') &&
				<WMScreeningMessage
					icon="person_pin_circle"
					buttonLabel="Find Work"
					link="/worker/browse"
				>
					<h2>This service is not available in your country</h2>
					<p>This drug screening service is currently only offered in
					the United States. Would you be interested in purchasing
					a drug screen it if it was offered in your location?</p>
				</WMScreeningMessage>
		}
		{
			state.get('drugTestPending') &&
				<WMScreeningMessage
					icon="tag_faces"
					buttonLabel="Back to Worker Services"
					link="/workerservices"
				>
					<h2>Your order is complete and instructions are on their way to your inbox!</h2>
					<p>If the results of your drug test are clear, meaning there are
					no infractions of any kind in your report, you will receive a notification
					of your status by email and and an icon will appear on your Work Market profile.
					If the results of your drug test include any infractions, you will have the
					opportunity to view your report and contest the results with Sterling.</p>
					<p><strong>Did you know?</strong> You will rank higher on our search and become eligible to join talent pools with a drug screen. <a href="/worker/browse">Go to our Work Feed to check out jobs.</a></p>
				</WMScreeningMessage>
		}
		{
			state.get('canOrder') &&
				<div id="wm-drug-screen__order-form">
					<div style={ styles.intro }>
						<h1 style={ styles.intro.title }>Order a Drug Screen</h1>
						<p>Many organizations require drug testing to ensure a
						safer and more productive work environment. Work Market
						handles your private information with the highest security
						standards. Data entered here that was not already part of your
						profile is provided securely to Sterling and is not saved with
						your profile going forward.</p>
						{
							state.get('drugTestFailed') &&
								<WMMessageBanner
									data-component-identifier="wm-drug-screen__failed"
									style={ styles.inlineNotification }
									status="warning"
								>
									<p>The unsuccessful status of this drug screen does not appear on your
									profile nor is it visible to employers. Help your profile stand out by
									passing the test. Feel free to resubmit your information and retake the
									test at any point!</p>
								</WMMessageBanner>

						}
						{
							state.get('drugTestPassed') &&
								<WMMessageBanner
									data-component-identifier="wm-drug-screen__passed"
									style={ styles.inlineNotification }
									status="success"
								>
									<p>Well done! You passed your drug screen. Make sure to
									help your profile stand out to employers by renewing your status every
									6 months.</p>
								</WMMessageBanner>
						}
						{
							state.get('internalError') &&
								<WMMessageBanner
									data-component-identifier="wm-drug-screen__failed"
									style={ styles.inlineNotification }
									status="error"
								>
									<p>We’re sorry, there seems to be an error. Your order has not gone
									through. Please try filling out this form again later.</p>
								</WMMessageBanner>
						}
						{
							state.get('paymentError') &&
								<WMMessageBanner
									data-component-identifier="wm-drug-screen__passed"
									style={ styles.inlineNotification }
									status="error"
								>
									<p>We’re sorry, there has been a problem processing your payment.
									For security reasons, you must re-enter all credit card information.
									Tip: try another payment method.</p>
								</WMMessageBanner>
						}
					</div>
					<div style={ styles.container }>
						<div>
							<WMPaper style={ styles.paper }>
								<WMPersonalInformation />
							</WMPaper>

							<WMPaper style={ styles.paper.last }>
								<WMPaymentInformation />

								{
									!state.get('isFormValid') &&
									state.get('submissionAttempt') &&
										<div style={ styles.formError }>
											<WMFontIcon
												id="wm-screening-form__submission-error"
												className="material-icons"
												color={ 'red' }
												style={ styles.formError.icon }
											>
												info_outline
											</WMFontIcon>
											<span> Please correct all errors noted above</span>
										</div>
								}

								<div style={ styles.price }>
									Total Amount: ${ state.get('price') }
								</div>
								<div style={ styles.formActions }>
									<p>By submitting this form, you are agreeing to the <a style={ styles.formActions.termsAction } href="https://www.workmarket.com/tos" target="_blank">Terms of Service</a>.</p>
									<a
										href=""
										onClick={ (event) => {
											event.preventDefault();
											event.stopPropagation();
											onDisabledSubmitForm(state.get('isFormValid'));
										} }
									>
										<WMRaisedButton
											label="Submit"
											backgroundColor={ commonStyles.colors.baseColors.green }
											labelColor="white"
											disabled={ !state.get('isFormValid') }
											onClick={ () => onSubmitForm(state) }
										/>
									</a>
								</div>
							</WMPaper>
						</div>
						<WMAboutVendor>
							<p>Work Market has partnered with Sterling to provide background
							screening tools for the people on our network. With your permission,
							Work Market will initiate the drug screening process that requires you to
							physically visit a drug testing facility and provide a specimen for testing.
							Sterling will provide details on testing locations near you and paperwork
							to successfully complete the drug screen.</p>
						</WMAboutVendor>
					</div>
				</div>
		}
	</section>
);

WMDrugScreen.propTypes = {
	state: PropTypes.shape({
		price: PropTypes.instanceOf(Map),
		isInternational: PropTypes.instanceOf(Map),
		drugTestPassed: PropTypes.instanceOf(Map),
		drugTestFailed: PropTypes.instanceOf(Map),
		drugTestPending: PropTypes.instanceOf(Map)
	}).isRequired,
	onDisabledSubmitForm: PropTypes.func.isRequired,
	onSubmitForm: PropTypes.func.isRequired
};

export default WMDrugScreen;
