import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import {
	WMWizard,
	WMWizardSlide
} from '@workmarket/front-end-patterns';
import {
	WMRaisedButton,
	WMFlatButton,
	WMHeading,
	WMText,
	WMControlledModal,
	WMValidatingTextField
} from '@workmarket/front-end-components';
import * as actions from '../../actions';
import Deliverables from './Deliverables';
import Shipments from './Shipments';
import CustomFields from './CustomFields';
import Requirements from './Requirements';
import styles from './styles';

class WMFirstAssignment extends Component {
	componentDidMount () {
		this.props.disabledFirstAssignment();
		this.props.getFirstAssignment();
	}
	handleTemplateTitle = (event, templateTitle) => {
		this.props.onChangeTemplate('name', templateTitle);
	}
	handleAssignmentTitle = (event, assignmentTitle) => {
		this.props.onChangeTemplate('title', assignmentTitle);
	}
	handleOnSubmit = () => {
		this.props.onSubmitConfig()
		.then(() => {
			return this.props.onSubmitTemplate();
		});
	}
	render () {
		const {
			templateTitle,
			assignmentTitle,
			onChange
		} = this.props;
		return (
			<div style={ styles.container }>
				<div>
					<WMWizard>
						<WMWizardSlide
							id="0"
							gettingStarted
						>
							<div style={ styles.wrapper }>
								<img
									src={ `${mediaPrefix}/images/settings/assignment.svg` }
									style={ styles.img }
									alt="First Assignment Icon"
								/>
								<WMHeading style={ styles.title }>
									{"Let's"} create your first assignment.
								</WMHeading>
								<WMText style={ styles.text }>
									<p>{"We'll"} ask you a few questions about what your assignments typically require,
											and introduce you to our suite of assignment modules. {"We'll"} use this information to
											create a template to make creating assignments a breeze. </p>
									<p>This should only take about <b>5 minutes</b>, and you
									can always change your settings later.</p>
								</WMText>
							</div>
						</WMWizardSlide>
						<WMWizardSlide
							id="2"
							customActions={
							[
								<WMFlatButton
									label="NO"
									primary
									next
									onClick={ () => { onChange('deliverablesEnabled', false); } }
								/>,
								<WMRaisedButton
									label="YES"
									primary
									next
									onClick={ () => { onChange('deliverablesEnabled', true); } }
								/>
							]
							}
						>
							<div style={ styles.wrapper } >
								<img
									src={ `${mediaPrefix}/images/settings/deliverables.svg` }
									style={ styles.img }
									alt="Deliverables Icon"
								/>
								<WMHeading style={ styles.title }>
									Do your assignments typically require deliverables?
								</WMHeading>
								<WMText style={ styles.text }>
									<p>Deliverables are submitted by the worker as part of
									the assignment completion process.</p>
									<p>Deliverables can include anything from a sign-off form, photos,
									documents, and more.</p>
									<div style={ styles.links }>
										<WMControlledModal
											triggerElement={ <a style={ styles.learnLink }>See Example</a> }
										>
											<Deliverables />
										</WMControlledModal>
										&nbsp;|&nbsp;
										<a
											href="https://workmarket.zendesk.com/hc/en-us/"
											target="_blank"
											rel="noopener noreferrer"
											style={ styles.learnLink }
										> Learn More </a>
									</div>
								</WMText>
							</div>
						</WMWizardSlide>
						<WMWizardSlide
							id="3"
							customActions={
							[
								<WMFlatButton
									label="NO"
									primary
									next
									onClick={ () => { onChange('shipmentsEnabled', false); } }
								/>,
								<WMRaisedButton
									label="YES"
									primary
									next
									onClick={ () => { onChange('shipmentsEnabled', true); } }
								/>
							]
							}
						>
							<div style={ styles.wrapper } >
								<img
									src={ `${mediaPrefix}/images/settings/shipments.svg` }
									style={ styles.img }
									alt="Shipments Icon"
								/>
								<WMHeading style={ styles.title }>
									Do your assignments involve shipping parts?
								</WMHeading>
								<WMText style={ styles.text }>
									<p>Shipments can be made to the worker or the work site, and the
									tracking numbers can be attached directly to your Work Market assignment.</p>
									<div style={ styles.links }>
										<WMControlledModal
											triggerElement={ <a style={ styles.learnLink }>See Example</a> }
										>
											<Shipments />
										</WMControlledModal>
										&nbsp;|&nbsp;
										<a
											href="https://workmarket.zendesk.com/hc/en-us/"
											target="_blank"
											rel="noopener noreferrer"
											style={ styles.learnLink }
										> Learn More </a>
									</div>
								</WMText>
							</div>
						</WMWizardSlide>
						<WMWizardSlide
							id="4"
							customActions={
							[
								<WMFlatButton
									label="MAYBE LATER"
									primary
									next
									onClick={ () => { onChange('customFieldsEnabled', false); } }
								/>,
								<WMRaisedButton
									label="YES"
									primary
									next
									onClick={ () => { onChange('customFieldsEnabled', true); } }
								/>
							]
							}
						>
							<div style={ styles.wrapper } >
								<img
									src={ `${mediaPrefix}/images/settings/custom.fields.svg` }
									style={ styles.img }
									alt="Custom Fields Icon"
								/>
								<WMHeading style={ styles.title }>
									Would you like to add custom fields to your assignments?
								</WMHeading>
								<WMText style={ styles.text }>
									<p>Create custom fields to include assignment attributes that are custom to your
									company. This can include things like internal company ID, accounting group,
									project code, etc. </p>
									<p>Assignments can be searched by custom fields and can be reported against
									for custom analysis.</p>
									<div style={ styles.links }>
										<WMControlledModal
											triggerElement={ <a style={ styles.learnLink }>See Example</a> }
										>
											<CustomFields />
										</WMControlledModal>
										&nbsp;|&nbsp;
										<a
											href="https://workmarket.zendesk.com/hc/en-us/"
											target="_blank"
											rel="noopener noreferrer"
											style={ styles.learnLink }
										> Learn More </a>
									</div>
								</WMText>
							</div>
						</WMWizardSlide>
						<WMWizardSlide
							id="5"
							customActions={
							[
								<WMFlatButton
									label="MAYBE LATER"
									primary
									next
									onClick={ () => { onChange('requirementSetsEnabled', false); } }
								/>,
								<WMRaisedButton
									label="YES"
									primary
									next
									onClick={ () => { onChange('requirementSetsEnabled', true); } }
								/>
							]
							}
						>
							<div style={ styles.wrapper } >
								<img
									src={ `${mediaPrefix}/images/settings/requirements.svg` }
									style={ styles.img }
									alt="Requirements Icon"
								/>
								<WMHeading style={ styles.title }>
									Do you have specific requirements for your contractors?
								</WMHeading>
								<WMText style={ styles.text }>
									<p>Requirement sets allow you to define a set of requirements to
									automatically vet workers for Assignments and Talent Pools.</p>
									<p>The most common requirements are background checks, satisfaction
									ratings, tests, and agreements.</p>
									<div style={ styles.links }>
										<WMControlledModal
											triggerElement={ <a style={ styles.learnLink }>See Example</a> }
										>
											<Requirements />
										</WMControlledModal>
										&nbsp;|&nbsp;
										<a
											href="https://workmarket.zendesk.com/hc/en-us/"
											target="_blank"
											rel="noopener noreferrer"
											style={ styles.learnLink }
										> Learn More </a>
									</div>
								</WMText>
							</div>
						</WMWizardSlide>
						<WMWizardSlide
							id="6"
							customActions={
							[
								<WMRaisedButton
									label="SAVE TEMPLATE AND CONTINUE"
									primary
									next
									onClick={ this.handleOnSubmit }
								/>
							]
							}
						>
							<div style={ styles.wrapper } >
								<img
									src={ `${mediaPrefix}/images/settings/templates.svg` }
									style={ styles.img }
									alt="Templates Icon"
								/>
								<WMHeading style={ styles.title }>
									Great! Your assignment template is ready.
								</WMHeading>
								<WMText style={ styles.text }>
									<p>Simply give your template a name and {"you'll"} be able to use it
									to create future assignments.</p>
									<WMValidatingTextField
										name="Template Title"
										value={ templateTitle }
										onChange={ this.handleTemplateTitle }
										id="text-field"
										multiLine
										hintText="Assignment Template Name"
										fullWidth
										style={ styles.textField }
										floatingLabelText="Assignment Template Name"
										min="3"
										errorName="template name. The minimum is three characters."
									/>
									<p>Your first assignment will be created using this template, so give
									your assignment a name to see the template in action.</p>
									<WMValidatingTextField
										name="Assignment Title"
										value={ assignmentTitle }
										onChange={ this.handleAssignmentTitle }
										id="text-field"
										multiLine
										hintText="Assignment Title"
										fullWidth
										style={ styles.textField }
										floatingLabelText="Assignment Title"
										min="3"
										errorName="assignment title. The minimum is three characters."
									/>
								</WMText>
							</div>
						</WMWizardSlide>
					</WMWizard>
				</div>
			</div>
		);
	}
}

WMFirstAssignment.propTypes = {
	disabledFirstAssignment: PropTypes.func.isRequired,
	getFirstAssignment: PropTypes.func.isRequired,
	onChange: PropTypes.func.isRequired,
	onChangeTemplate: PropTypes.func.isRequired,
	onSubmitConfig: PropTypes.func.isRequired,
	onSubmitTemplate: PropTypes.func.isRequired,
	templateTitle: PropTypes.string.isRequired,
	assignmentTitle: PropTypes.string.isRequired
};

const mapStateToProps = state => ({
	settings: state.settings,
	firstAssignment: state.firstAssignment.toJS(),
	templateTitle: state.firstAssignmentTemplate.get('name'),
	assignmentTitle: state.firstAssignmentTemplate.get('title')
});

const mapDispatchToProps = dispatch => ({
	disabledFirstAssignment: () => dispatch(actions.disabledFirstAssignment()),
	getFirstAssignment: () => dispatch(actions.getFirstAssignment()),
	onChange: (name, value) => dispatch(actions.changeFirstAssignmentField(name, value)),
	onChangeTemplate: (name, value) =>
	dispatch(actions.changeFirstAssignmentTemplateField(name, value)),
	onSubmitConfig: () => dispatch(actions.onSubmitFirstAssignment()),
	onSubmitTemplate: () => dispatch(actions.onSubmitFirstAssignmentTemplate())
});

export { WMFirstAssignment as UnconnectedComponent };

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMFirstAssignment);

