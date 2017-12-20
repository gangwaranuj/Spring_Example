/* eslint-disable react/require-default-props */
import $ from 'jquery';
import moment from 'moment';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { WMToggle, WMRadioButtonGroup, WMRadioButton, WMTimePicker } from '@workmarket/front-end-components';
import wmMaskInput from '../../funcs/wmMaskInput';

const checkValidation = (isRangeRequired, { endDate, endTime, startDate, startTime }) => {
	let isValid = false;

	if (
		startDate.length &&
		startTime.toString().length &&
		(
			!isRangeRequired ||
			(
				(isRangeRequired && endDate.length) &&
				(isRangeRequired && endTime.toString().length)
			)
		)
	) {
		isValid = true;
	}
	return isValid;
};

export default class AddScheduleComponent extends Component {
	constructor (props) {
		super(props);
		this.state = {
			startDate: '',
			startTime: new Date(),
			endDate: '',
			endTime: new Date()
		};
	}

	componentDidMount () {
		const root = this.node;
		wmMaskInput({ root });

		// Move the datepicker into the modal
		const datePicker = document.getElementById('ui-datepicker-div');
		const node = document.querySelector('.wm-modal--slide');

		node.parentNode.insertBefore(datePicker, node);

		const startDate = $('[name="schedule-startDate"]');
		const dateFormat = 'mm/dd/yy';
		startDate.datepicker({
			dateFormat,
			onSelect: value => this.updateDate({ name: 'startDate', value }),
			defaultDate: new Date()
		});
	}

	componentWillReceiveProps (props) {
		const { from, through } = props;
		if (from) {
			this.setState({
				startDate: moment(props.from, 'MM/DD/YYYY h:mma').format('MM/DD/YYYY'),
				startTime: moment(props.from, 'MM/DD/YYYY h:mma').toDate()
			});
		}
		if (through) {
			this.setState({
				endDate: moment(props.through, 'MM/DD/YYYY h:mma').format('MM/DD/YYYY'),
				endTime: moment(props.through, 'MM/DD/YYYY h:mma').toDate()
			});
		}
	}

	componentDidUpdate (prevProps) {
		let isModuleValid = false;
		let isValid = false;

		const currentTime = new Date();
		const fromTime = moment(this.props.from, 'MM/DD/YYYY h:mma').valueOf();
		const throughTime = moment(this.props.through, 'MM/DD/YYYY h:mma').valueOf();
		const getFromYear = moment(this.props.from, 'MM/DD/YYYY h:mma').year();
		const getThroughYear = moment(this.props.through, 'MM/DD/YYYY h:mma').year();
		const year = currentTime.getFullYear();

		if (this.props.range && !prevProps.range) {
			const endDate = $('[name="schedule-endDate"]');
			const dateFormat = 'mm/dd/yy';
			endDate.datepicker({
				dateFormat,
				onSelect: value => this.updateDate({ name: 'endDate', value }),
				defaultDate: new Date()
			});
		}

		isModuleValid = checkValidation(this.props.range, this.state);

		if (((throughTime >= fromTime) && (year === getThroughYear) && (year === getFromYear)
		&& isModuleValid) || (this.props.through === null && (year === getFromYear)
		&& isModuleValid)) {
			isValid = true;
		}

		this.props.setModuleValidation(isValid, this.props.id);
	}

	componentWillUnmount () {
		// Move the datepicker out of the modal
		const datePicker = document.getElementById('ui-datepicker-div');
		document.body.appendChild(datePicker);
	}

	updateDate ({ name, value }) {
		const updateType = name.replace(/^schedule-/, '');
		this.setState({ [updateType]: value }, () => {
			if ((updateType === 'startTime' || updateType === 'startDate') && (this.state.startTime && this.state.startDate)) {
				const startDate = updateType === 'startDate' ? value : this.state.startDate;
				const startTime = updateType === 'startTime' ? moment(value).format('h:mma') : moment(this.state.startTime).format('h:mma');
				this.props.updateScheduleFrom(`${startDate} ${startTime}`);
			} else if ((updateType === 'endTime' || updateType === 'endDate') && (this.state.endTime && this.state.endDate)) {
				const endDate = updateType === 'endDate' ? value : this.state.endDate;
				const endTime = updateType === 'endTime' ? moment(value).format('h:mma') : moment(this.state.startTime).format('h:mma');
				this.props.updateScheduleThrough(`${endDate} ${endTime}`);
			}
		});
	}

	render () {
		return (
			<div
				ref={ node => (this.node = node) }
			>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label -required" htmlFor="schedule-range">Schedule Options</label>
					<div className="assignment-creation--button">
						<WMRadioButtonGroup
							name="schedule-range"
							onChange={ (event, value) => {
								// necessary to force default value, it doesn't work with booleans as values
								const isRange = value === 'range';
								this.props.toggleScheduleRange(isRange);
							} }
							valueSelected={ this.props.range ? 'range' : 'specific' }
							defaultSelected={ 'specific' }
						>
							<WMRadioButton
								label="Specific Date & Time"
								value={ 'specific' }
							/>
							<WMRadioButton
								label="Set Arrival Window"
								value={ 'range' }
							/>
						</WMRadioButtonGroup>
					</div>
				</div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label -required" htmlFor="schedule-startDate">
						{ this.props.range ? 'Start ' : '' }Date
					</label>
					<div className="assignment-creation--field">
						<input
							type="text"
							id="schedule-startDate"
							name="schedule-startDate"
							value={ this.state.startDate }
							onChange={ () => this.updateDate() }
						/>
					</div>
				</div>
				<div className="assignment-creation--container">
					<label className="assignment-creation--label -required" htmlFor="schedule-startDate">
						{ this.props.range ? 'Start ' : '' }Time
					</label>
					<div className="assignment-creation--field">
						<WMTimePicker
							hintText="12hr Format"
							id="timepicker1"
							minutesStep={ 5 }
							dialogStyle={ { zIndex: '10001' } }
							value={ this.state.startTime }
							onChange={ (event, value) => this.updateDate({ name: 'startTime', value }) }
						/>
					</div>
				</div>

				{this.props.range ? (
					<div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="schedule-endDate">
								End Date
							</label>
							<div className="assignment-creation--field">
								<input
									type="text"
									id="schedule-endDate"
									name="schedule-endDate"
									defaultValue={ this.state.endDate }
								/>
							</div>
						</div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="schedule-endDate">
								End Time
							</label>
							<div className="assignment-creation--field">
								<WMTimePicker
									id="timepicker2"
									hintText="12hr Format"
									minutesStep={ 5 }
									value={ this.state.endTime }
									dialogStyle={ { zIndex: '10001' } }
									onChange={ (event, value) => this.updateDate({ name: 'endTime', value }) }
								/>
							</div>
						</div>
					</div>
				) : ''}

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="schedule-confirmation">
						Require confirmation from the worker before starting the assignment
					</label>
					<div className="assignment-creation--toggle-button">
						<WMToggle
							toggled={ this.props.confirmationRequired }
							onToggle={ this.props.toggleConfirmationRequired }
						/>

						{ this.props.confirmationRequired ? (
							<div className="assignment-creation--confirmation-time">
								<input
									type="text"
									id="schedule-confirmationLeadTime"
									name="schedule-confirmationLeadTime"
									value={ this.props.confirmationLeadTime }
									onChange={
										({ target: { value } }) => this.props.updateConfirmationLeadTime(value)
									}
								/> hour(s) before start time
							</div>
							) : '' }

					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="schedule-checkin">
						Require check-in and check-out when starting and finishing work
					</label>
					<div className="assignment-creation--toggle-button">
						<WMToggle
							toggled={ this.props.checkinRequired }
							onToggle={ this.props.toggleCheckinRequired }
						/>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="schedule-checkinCall">
						Require check-in call when starting work
					</label>
					<div className="assignment-creation--toggle-button">
						<WMToggle
							toggled={ this.props.checkinCallRequired }
							onToggle={ this.props.toggleCheckinCallRequired }
						/>
					</div>
				</div>

				{ this.props.checkinCallRequired ? (
					<div className="assignment-creation--container">
						<label className="assignment-creation--label" htmlFor="schedule-checkInContactName">
							Instruct worker to call
						</label>
						<div className="assignment-creation--field">
							<input
								type="text"
								id="schedule-checkInContactName"
								name="schedule-checkInContactName"
								value={ this.props.checkinContactName }
								onChange={
									({ target: { value } }) => this.props.updateCheckinContactName(value)
								}
								placeholder="Contact Name"
							/>
							<input
								type="tel"
								name="schedule-checkInContactPhone"
								value={ this.props.checkinContactPhone }
								onChange={
									({ target: { value } }) => this.props.updateCheckinContactPhone(value)
								}
								placeholder="(___) ___-____"
								data-mask
							/>
						</div>
					</div>
				) : '' }

				<div className="assignment-creation--container">
					<label className="assignment-creation--label" htmlFor="schedule-showNotes">
						Show notes field upon check-out for worker
					</label>
					<div className="assignment-creation--toggle-button">
						<WMToggle
							toggled={ this.props.checkoutNoteDisplayed }
							onToggle={ this.props.toggleCheckoutNoteDisplayed }
						/>
					</div>
				</div>

				{ this.props.checkoutNoteDisplayed ? (
					<div className="assignment-creation--container">
						<label className="assignment-creation--label" htmlFor="schedule-instructions">Instructions for notes</label>
						<div className="assignment-creation--field">
							<textarea
								id="schedule-instructions"
								name="schedule-instructions"
								onChange={ ({ target: { value } }) => this.props.updateCheckoutNote(value) }
								value={ this.props.checkoutNote }
							/>
						</div>
					</div>
				) : '' }
			</div>
		);
	}
}

AddScheduleComponent.propTypes = {
	range: PropTypes.bool.isRequired,
	from: PropTypes.number,
	through: PropTypes.number,
	confirmationRequired: PropTypes.bool.isRequired,
	confirmationLeadTime: PropTypes.number.isRequired,
	checkinRequired: PropTypes.bool.isRequired,
	checkinCallRequired: PropTypes.bool.isRequired,
	checkinContactName: PropTypes.string,
	checkinContactPhone: PropTypes.string,
	checkoutNoteDisplayed: PropTypes.bool.isRequired,
	checkoutNote: PropTypes.string,
	updateCheckoutNote: PropTypes.func.isRequired,
	setModuleValidation: PropTypes.func.isRequired,
	id: PropTypes.string.isRequired,
	updateScheduleFrom: PropTypes.func.isRequired,
	toggleScheduleRange: PropTypes.func.isRequired,
	updateScheduleThrough: PropTypes.func.isRequired,
	toggleConfirmationRequired: PropTypes.func.isRequired,
	updateConfirmationLeadTime: PropTypes.func.isRequired,
	toggleCheckinRequired: PropTypes.func.isRequired,
	toggleCheckinCallRequired: PropTypes.func.isRequired,
	updateCheckinContactName: PropTypes.func.isRequired,
	updateCheckinContactPhone: PropTypes.func.isRequired,
	toggleCheckoutNoteDisplayed: PropTypes.func.isRequired
};
