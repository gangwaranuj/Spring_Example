/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Map } from 'immutable';
import { connect } from 'react-redux';
import {
	WMWizard,
	WMWizardSlide
} from '@workmarket/front-end-patterns';
import {
	WMMessageBanner,
	WMRaisedButton,
	WMFlatButton,
	WMHeading,
	WMSelectField,
	WMMenuItem,
	WMTextField,
	WMRadioButtonGroup,
	WMRadioButton,
	WMCheckbox,
	WMUploader,
	WMText,
	WMDivider,
	WMControlledModal,
	WMValidatingTextField
} from '@workmarket/front-end-components';
import Application from '../../../core';
import * as actions from '../../actions';
import PaymentTerms from './PaymentTerms';
import TermsOfAgreement from './TermsOfAgreement';
import CodeOfConduct from './CodeOfConduct';
import AssignmentApproval from './AssignmentApproval';
import RateAssignments from './RateAssignments';
import AssignmentPrintout from './AssignmentPrintout';
import styles from './styles';

class WMAssignmentPreferencesView extends Component {
	componentDidMount () {
		this.props.disabledAssignmentPreferences();
		this.props.getAssignmentPreferences();
	}

	handlePaymentTerms = (event, index, value) => {
		this.props.onChange('paymentTermsDays', value);
	}

	handleTermsOfAgreement = (event, termsOfAgreement) => {
		this.props.onChange('termsOfAgreement', termsOfAgreement);
	}

	handleCodeOfConduct = (event, codeOfConduct) => {
		this.props.onChange('codeOfConduct', codeOfConduct);
	}

	handleLogoOption = (event, value) => {
		this.props.onChange('printSettingsLogoOption', value);
	}

	handlePrintoutTypeChange = (event, value) => {
		this.props.onChange('useWorkMarketPrintout', value);
	}

	render () {
		const {
			paymentTermsDays,
			termsOfAgreement,
			codeOfConduct,
			useWorkMarketPrintout,
			printSettingsEndUserTermsEnabled,
			printSettingsSignatureEnabled,
			printSettingsBadgeEnabled,
			printSettingsLogoOption,
			printout,
			onChange,
			uploadPrintout,
			cancelPrintoutUpload,
			onSubmit,
			settings,
			removePrintoutUpload,
			errors
		} = this.props;

		return (
			<div style={ styles.container }>
				<WMWizard
					activeSlide={
						settings.get('assignmentPreferencesSuccess') && 'success'
					}
					disabled={ settings.get('submitting') }
				>
					<WMWizardSlide
						id="0"
						customActions={
							<WMRaisedButton
								label="GET STARTED"
								primary
								next
								disabled={ settings.get('submitting') }
							/>
						}
					>
						<div style={ styles.wrapper }>
							<img
								src={ `${mediaPrefix}/images/settings/assignment.preferences.svg` }
								style={ styles.img }
								alt="Assignment Preferences Icon"
							/>
							<WMHeading style={ styles.title }>
								{"Let's"} set up your {"company's"} assignment preferences.
							</WMHeading>
							<WMText style={ styles.text }>
								<p>{"We'll"} ask you a few questions about your business operations to make
								using Work Market as easy as possible.</p>
								<p>This should only take about <b>5 minutes</b>, and you
								can always change your settings later.</p>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="1"
						customActions={
							<WMRaisedButton
								label="SAVE AND CONTINUE"
								primary
								next
							/>
						}
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/payment.terms.svg` }
								style={ styles.img }
								alt="Payment Terms Icon"
							/>
							<WMHeading style={ styles.title }>
								How soon after assignment approval would you like to pay your workers?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>Payment terms can be adjusted on a per-assignment basis.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<PaymentTerms />
									</WMControlledModal>
									&nbsp;|&nbsp;
									<a
										href="https://workmarket.zendesk.com/hc/en-us/"
										target="_blank"
										rel="noopener noreferrer"
										style={ styles.learnLink }
									> Learn More </a>
								</div>
								<WMSelectField
									value={ paymentTermsDays }
									onChange={ this.handlePaymentTerms }
									style={ styles.dropDown }
									fullWidth
									floatingLabelText="Payment Terms Days"
								>
									<WMMenuItem value={ 0 } primaryText="Immediate" />
									<WMMenuItem value={ 7 } primaryText="7 days (Best Practice)" />
									<WMMenuItem value={ 15 } primaryText="15 days" />
									<WMMenuItem value={ 21 } primaryText="21 days" />
									<WMMenuItem value={ 30 } primaryText="30 days" />
									<WMMenuItem value={ 40 } primaryText="40 days" />
								</WMSelectField>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="2"
						customActions={ [
							<WMFlatButton
								label="SKIP"
								next
								primary
								onClick={ () => onChange('termsOfAgreementEnabled', false) }
							/>,
							<WMRaisedButton
								label="SAVE AND CONTINUE"
								primary
								next
							/>
						] }
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/terms.of.agreement.svg` }
								style={ styles.img }
								alt="Terms of Agreement Icon"
							/>
							<WMHeading style={ styles.title }>
								Would you like to add Terms of Agreement to your Assignments?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>Terms of Agreement are displayed when a worker accepts
								or declines an assignment. Include business terms that apply to all
								assignments from your company.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<TermsOfAgreement />
									</WMControlledModal>
									&nbsp;|&nbsp;<a href="https://workmarket.zendesk.com/hc/en-us" target="_blank" rel="noopener noreferrer" style={ styles.learnLink }> Learn More </a>
								</div>
								<WMTextField
									id="text-field"
									multiLine
									hintText="Add Terms of Agreement"
									fullWidth
									style={ styles.textField }
									value={ termsOfAgreement }
									onChange={ this.handleTermsOfAgreement }
								/>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="3"
						customActions={ [
							<WMFlatButton
								label="SKIP"
								next
								primary
								onClick={ () => onChange('codeOfConductEnabled', false) }
							/>,
							<WMRaisedButton
								label="SAVE AND CONTINUE"
								primary
								next
							/>
						] }
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/code.of.conduct.svg` }
								style={ styles.img }
								alt="Code of Conduct Icon"
							/>
							<WMHeading style={ styles.title }>
								Would you like to add a Code of Conduct for your workers?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>Include details on business conduct, professionalism, attire, and behavior.
								This information displays on the assignment detail and printout.
								Limit of 200 characters.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<CodeOfConduct />
									</WMControlledModal>
									&nbsp;|&nbsp;<a href="https://workmarket.zendesk.com/hc/en-us" target="_blank" rel="noopener noreferrer" style={ styles.learnLink }> Learn More </a>
								</div>
								<WMValidatingTextField
									name="Code of Conduct Text"
									value={ codeOfConduct }
									onChange={ this.handleCodeOfConduct }
									id="text-field"
									multiLine
									hintText="Add Code of Conduct"
									fullWidth
									style={ styles.textField }
									max="200"
									limitOnMax
									errorName="number of characters. The limit is 200 characters."
									floatingLabelText="Code of Conduct"
								/>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="4"
						customActions={ [
							<WMFlatButton
								label="APPROVE AUTOMATICALLY"
								secondary
								next
								onClick={ () => { onChange('autoCloseEnabled', true); } }
							/>,
							<WMRaisedButton
								label="APPROVE MANUALLY"
								primary
								next
								onClick={ () => { onChange('autoCloseEnabled', false); } }
							/>
						] }
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/auto.approve.svg` }
								style={ styles.img }
								alt="Approval Icon"
							/>
							<WMHeading style={ styles.title }>
								How would you like to approve completed assignments?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>Assignments can be approved manually or automatically after a specified
								amount of time. Once an assignment is approved it cannot be reopened.</p>
								<p>Assignments will be automatically rated {"'Excellent'"} unless a different
								rating is submitted before the assignment is paid.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<AssignmentApproval />
									</WMControlledModal>
									&nbsp;|&nbsp;<a href="https://workmarket.zendesk.com/hc/en-us" target="_blank" rel="noopener noreferrer" style={ styles.learnLink }> Learn More </a>
								</div>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="5"
						customActions={
						[
							<WMFlatButton
								label="RATE AUTOMATICALLY"
								secondary
								next
								onClick={ () => { onChange('autoRateEnabled', true); } }
							/>,
							<WMRaisedButton
								label="RATE MANUALLY"
								primary
								next
								onClick={ () => { onChange('autoRateEnabled', false); } }
							/>
						]
						}
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/ratings.svg` }
								style={ styles.img }
								alt="Ratings Icon"
							/>
							<WMHeading style={ styles.title }>
								How would you like to rate the assignments submitted by workers?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>Workers can be rated manually after each assignment is completed, or we can
								automatically assume a job has been completed to your satisfaction unless you
								identify otherwise.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<RateAssignments />
									</WMControlledModal>
									&nbsp;|&nbsp;<a href="https://workmarket.zendesk.com/hc/en-us" target="_blank" rel="noopener noreferrer" style={ styles.learnLink }> Learn More </a>
								</div>
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="6"
						customActions={
							<WMRaisedButton
								label="SAVE AND CONTINUE"
								onClick={ onSubmit }
								disabled={ settings.get('submitting') }
								primary
							/>
						}
					>
						<div style={ styles.wrapper } >
							<img
								src={ `${mediaPrefix}/images/settings/assignment.printout.svg` }
								style={ styles.img }
								alt="Assignment Printout Icon"
							/>
							<WMHeading style={ styles.title }>
								Would you like to customize your assignment print-out?
							</WMHeading>
							<WMText style={ styles.text }>
								<p>This includes all of the assignment details, and you can add end-user terms,
								custom fields, signature requirements, and more.</p>
								<div style={ styles.links }>
									<WMControlledModal
										triggerElement={ <a style={ styles.learnLink }>See Example</a> }
									>
										<AssignmentPrintout />
									</WMControlledModal>
									&nbsp;|&nbsp;<a href="https://workmarket.zendesk.com/hc/en-us" target="_blank" rel="noopener noreferrer" style={ styles.learnLink }> Learn More </a>
								</div>
							</WMText>
							<WMText style={ styles.text }>
								<WMRadioButtonGroup
									style={ styles.radioButtons }
									name="printout-type"
									onChange={ this.handlePrintoutTypeChange }
									valueSelected={ useWorkMarketPrintout }
								>
									<WMRadioButton
										label="Customize Work Market Printout"
										value
									/>
									<WMRadioButton
										label="Upload My Company Printout"
										value={ false }
									/>
								</WMRadioButtonGroup>
								<WMDivider style={ { marginTop: '20px' } } />
								{
									useWorkMarketPrintout &&
										<div>
											{
												Application.Features.companyLogoUri &&
													<div>
														<WMHeading style={ styles.logoText }>Logo</WMHeading>
														<WMRadioButtonGroup
															style={ styles.radioButtons }
															name="logo"
															onChange={ this.handleLogoOption }
															valueSelected={ printSettingsLogoOption }
														>
															<WMRadioButton
																label="Work Market Logo"
																value="wm"
															/>
															<WMRadioButton
																label="My Logo"
																value="company"
															/>
														</WMRadioButtonGroup>
													</div>
											}
											<WMCheckbox
												label="Include End User General Terms"
												checked={ printSettingsEndUserTermsEnabled }
												onCheck={ (event, checked) => onChange('printSettingsEndUserTermsEnabled', checked) }
												style={ styles.topCheckbox }
											/>
											<WMCheckbox
												label="Include signature section"
												checked={ printSettingsSignatureEnabled }
												onCheck={ (event, checked) => onChange('printSettingsSignatureEnabled', checked) }
											/>
											<WMCheckbox
												label="Include a badge for the worker printout"
												checked={ printSettingsBadgeEnabled }
												onCheck={ (event, checked) => onChange('printSettingsBadgeEnabled', checked) }
												style={ styles.bottomCheckbox }
											/>
										</div>
								}
								{
									!useWorkMarketPrintout &&
										<div>
											<WMText style={ styles.tabText }>My company elects to use a separate
											assignment form that can either be a standard printout saved within
											the Work Market file manager or individually added to each assignment
											in the attachment section of assignment creation. This option will
											ensure the worker prints out my assignment form for my clients rather
											than the standard Work Market assignment
											form.</WMText>
											<WMUploader
												id="assignment-preferences"
												files={ printout }
												multiple={ false }
												uploadFile={ uploadPrintout }
												cancelUpload={ cancelPrintoutUpload }
												style={ styles.uploader }
												removeUpload={ removePrintoutUpload }
											/>
										</div>
								}
								{
									errors &&
										<div style={ styles.errors }>
											{
												errors.map((error, index) => (
													<WMMessageBanner
														key={ index } // eslint-disable-line react/no-array-index-key
														status="error"
													>
														{ error }
													</WMMessageBanner>
												))
											}
										</div>
								}
							</WMText>
						</div>
					</WMWizardSlide>
					<WMWizardSlide
						id="success"
						back={ false }
						customActions={ [
							<WMFlatButton
								label="MAYBE LATER"
								secondary
								href="/settings/onboarding"
							/>,
							<WMRaisedButton
								label="GET STARTED"
								primary
								href="/settings/onboarding/first-assignment"
							/>
						] }
					>
						<div style={ styles.wrapper }>
							<i className="material-icons" style={ styles.doneIcon }>done_all</i>
							<WMHeading style={ styles.title }>{"You're"} done!</WMHeading>
							<WMText style={ styles.text } textAlign="center">
								<p>Your assignment preferences are complete and {"you're "}
								now ready to set up your first assignment.</p>
							</WMText>
						</div>
					</WMWizardSlide>
				</WMWizard>
			</div>
		);
	}
}

WMAssignmentPreferencesView.propTypes = {
	paymentTermsDays: PropTypes.number.isRequired,
	termsOfAgreement: PropTypes.string.isRequired,
	codeOfConduct: PropTypes.string.isRequired,
	printSettingsEndUserTermsEnabled: PropTypes.bool.isRequired,
	printSettingsSignatureEnabled: PropTypes.bool.isRequired,
	printSettingsBadgeEnabled: PropTypes.bool.isRequired,
	printSettingsLogoOption: PropTypes.string.isRequired,
	useWorkMarketPrintout: PropTypes.bool.isRequired,
	printout: PropTypes.instanceOf(Map),
	settings: PropTypes.instanceOf(Map).isRequired,
	disabledAssignmentPreferences: PropTypes.func.isRequired,
	getAssignmentPreferences: PropTypes.func.isRequired,
	onChange: PropTypes.func.isRequired,
	uploadPrintout: PropTypes.func.isRequired,
	cancelPrintoutUpload: PropTypes.func.isRequired,
	removePrintoutUpload: PropTypes.func.isRequired,
	onSubmit: PropTypes.func.isRequired,
	errors: PropTypes.arrayOf(PropTypes.string)
};

const mapStateToProps = state => ({
	settings: state.settings,
	assignmentPreferences: state.assignmentPreferences.toJS(),
	printout: state.assignmentPrintoutUpload,
	paymentTermsDays: state.assignmentPreferences.get('paymentTermsDays'),
	termsOfAgreement: state.assignmentPreferences.get('termsOfAgreement'),
	codeOfConduct: state.assignmentPreferences.get('codeOfConduct'),
	printSettingsLogoOption: state.assignmentPreferences.get('printSettingsLogoOption'),
	printSettingsEndUserTermsEnabled: state.assignmentPreferences.get('printSettingsEndUserTermsEnabled'),
	printSettingsSignatureEnabled: state.assignmentPreferences.get('printSettingsSignatureEnabled'),
	printSettingsBadgeEnabled: state.assignmentPreferences.get('printSettingsBadgeEnabled'),
	useWorkMarketPrintout: state.assignmentPreferences.get('useWorkMarketPrintout'),
	errors: state.settings.get('assignmentPreferencesError')
});

const mapDispatchToProps = dispatch => ({
	disabledAssignmentPreferences: () => dispatch(actions.disabledAssignmentPreferences()),
	getAssignmentPreferences: () => dispatch(actions.getAssignmentPreferences()),
	onChange: (name, value) => dispatch(actions.changeAssignmentPreferencesField(name, value)),
	uploadPrintout: file => dispatch(actions.onAssignmentPrintoutUpload(file)),
	cancelPrintoutUpload: id => dispatch(actions.cancelAssignmentPrintoutUpload(id)),
	onSubmit: () => dispatch(actions.onSubmitAssignmentPreferences()),
	removePrintoutUpload: id => dispatch(actions.removeAssignmentPrintoutUpload(id))
});

export { WMAssignmentPreferencesView as UnconnectedComponent };

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMAssignmentPreferencesView);
