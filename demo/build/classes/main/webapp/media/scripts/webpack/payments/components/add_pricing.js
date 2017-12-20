import PropTypes from 'prop-types';
import React from 'react';
import { WMRadioButtonGroup, WMRadioButton, WMCheckbox } from '@workmarket/front-end-components';
import { WMCurrencyField } from '@workmarket/front-end-patterns';
import wmSelect from '../../funcs/wmSelect';
import wmMaskInput from '../../funcs/wmMaskInput';
import calculateAssignmentPrice from '../calculate_assignment_price';
import styles from './styles';

export default class AddPricingComponent extends React.Component {
	constructor (props) {
		super(props);
		this.state = {
			fee: 0.1,
			spendingLimit: 0.0,
			aplLimit: 0.0,
			companyPrice: 0,
			workerPrice: 0,
			offlinePaymentEnabled: false,
			subscribed: false
		};
	}

	componentDidMount () {
		this.getPaymentConfig();
		this.getAvailableFunds();

		const root = this.node;
		wmMaskInput({ root }, 'usd');

		this.paymentTypeSelect = wmSelect({ selector: '[name="payments-type"]', root }, {
			onChange: value => this.props.updatePricingType(value)
		})[0].selectize;

		let defaultTerms = {};
		this.paymentTermsSelect = wmSelect({ selector: '[name="payments-terms"]', root }, {
			valueField: 'numDays',
			allowEmptyOption: false,
			searchField: ['numDays', 'default'],
			sortField: 'numDays',
			labelField: 'numDays',
			preload: true,
			openOnFocus: true,
			render: {
				item: item => `<div>${item.numDays === '0' ? 'Paid Immediately' : `${item.numDays} Days`}</div>`,
				option: item => `<div>${item.numDays === '0' ? 'Paid Immediately' : `${item.numDays} Days`}</div>`
			},
			onLoad: (value) => {
				defaultTerms = value.filter(terms => terms.default === true);
				if (!this.paymentTermsSelect.items[0]) {
					const paymentTermsDays = this.props.paymentTermsDays === 0
					? this.props.paymentTermsDays : this.props.paymentTermsDays || defaultTerms[0].numDays;
					this.paymentTermsSelect.setValue(paymentTermsDays);
				}
			},
			onChange: (value) => {
				if (value === '') {
					this.paymentTermsSelect.setValue(defaultTerms[0].numDays);
				} else {
					this.props.updatePaymentTermsDays(value);
				}
			},
			load: (query, callback) => fetch('/employer/v2/payment_terms?fields=numDays,default', { credentials: 'same-origin' })
				.then(res => res.json())
				.then((res) => {
					callback(res.results);
				})
		})[0].selectize;
	}

	componentWillReceiveProps (nextProps) {
		this.setState(calculateAssignmentPrice(nextProps, this.state.fee));
		this.paymentTypeSelect.setValue(nextProps.type, true);
		this.paymentTermsSelect.setValue(nextProps.paymentTermsDays, true);
	}

	componentDidUpdate () {
		let isModuleValid = false;
		if (this.props.type !== 'INTERNAL') {
			this.initialize();
		}

		isModuleValid = this.checkValidation(this.props.type, this.state);
		this.props.setModuleValidation(isModuleValid, this.props.id);
	}

	getPaymentConfig () {
		fetch('/employer/v2/payment_configuration', { credentials: 'same-origin' })
				.then(res => res.json())
		.then((res) => {
			const results = res.results[0];
			const fee = results.subscribed ? 0 : results.workFeePercentage / 100;
			const assignmentPricingType = results.assignmentPricingType;
			const subscribed = results.subscribed;

			if (!this.props.mode) {
				const mode = assignmentPricingType === 2 ? 'pay' : 'spend';
				this.props.updatePricingMode(mode);
			}

			this.setState({ fee, subscribed, offlinePaymentEnabled: results.offlinePaymentEnabled });
		});
	}

	getAvailableFunds () {
		fetch('/employer/v2/assignments/availableFunds', { credentials: 'same-origin' })
			.then(res => res.json())
		.then((res) => {
			const {
				spendingLimit,
				aplLimit
			} = res.results[0];
			this.setState({ spendingLimit, aplLimit });
		});
	}

	initialize () {
		const root = this.node;
		this.paymentTermsSelect = wmSelect({ selector: '[name="payments-terms"]', root }, {
			valueField: 'numDays',
			searchField: ['numDays', 'default'],
			sortField: 'numDays',
			labelField: 'numDays',
			preload: true,
			openOnFocus: true,
			render: {
				item: item => `<div>${item.numDays === '0' ? 'Paid Immediately' : `${item.numDays} Days`}</div>`,
				option: item => `<div>${item.numDays === '0' ? 'Paid Immediately' : `${item.numDays} Days`}</div>`
			},
			onLoad: (value) => {
				const defaultTerms = value.filter(terms => terms.default === true);
				if (!this.paymentTermsSelect.items[0]) {
					const paymentTermsDays = this.props.paymentTermsDays === 0
					? this.props.paymentTermsDays : this.props.paymentTermsDays || defaultTerms[0].numDays;
					this.paymentTermsSelect.setValue(paymentTermsDays);
				}
			},
			onChange: (value) => {
				this.props.updatePaymentTermsDays(value);
			},
			load: (query, callback) => fetch('/employer/v2/payment_terms?fields=numDays,default', { credentials: 'same-origin' })
				.then(res => res.json())
				.then((res) => {
					callback(res.results);
				})
		})[0].selectize;
	}

	checkValidation = (pricingType, { companyPrice, workerPrice }) => {
		const isInternalPricing = pricingType === 'INTERNAL';
		const isValid = isInternalPricing || (companyPrice > 0 && workerPrice > 0);

		return isValid;
	}

	calculateType = (type) => {
		switch (type) {
		case 'FLAT': {
			let price = Number.parseFloat(this.props.flatPrice);
			return Number.isNaN(price) ? 0 : price.toFixed(2);
		}
		case 'PER_HOUR': return (this.props.perHourPrice * this.props.maxNumberOfHours).toFixed(2);
		case 'PER_UNIT': return (this.props.perUnitPrice * this.props.maxNumberOfUnits).toFixed(2);
		case 'BLENDED_PER_HOUR': return ((this.props.initialPerHourPrice * this.props.initialNumberOfHours)
		+ (this.props.maxBlendedNumberOfHours * this.props.additionalPerHourPrice)).toFixed(2);
		default: return '';
		}
	}

	render () {
		return (
			<div
				ref={ node => (this.node = node) }
			>
				<div style={ styles.messageBox }>
					<span style={ styles.messageBox.icon }>$</span>
					<div>
						<h3>Available Funds</h3>
						<div style={ styles.messageBox.line }><span style={ styles.messageBox.highlight }>${Number(this.state.aplLimit).toLocaleString('en', { minimumFractionDigits: 2, maximumFractionDigits: 3 })}</span> in payment terms</div>
						<div style={ styles.messageBox.line }><span style={ styles.messageBox.highlight }>${Number(this.state.spendingLimit).toLocaleString('en', { minimumFractionDigits: 2, maximumFractionDigits: 3 })}</span> in cash (immediate terms)</div>
					</div>
				</div>

				<div className="assignment-creation--container">
					<label className="assignment-creation--label -required" htmlFor="payments-type">Payment Type</label>
					<div className="assignment-creation--field">
						<select
							className="wm-select"
							name="payments-type"
							id="payments-type"
							defaultValue={ this.props.type }
						>
							<option value="FLAT">Flat Fee</option>
							<option value="PER_HOUR">Hourly Rate</option>
							<option value="PER_UNIT">Unit Rate</option>
							<option value="BLENDED_PER_HOUR">Blended Hourly Rate</option>
							<option value="INTERNAL">Internal</option>
						</select>
					</div>
				</div>

				{this.props.type === 'FLAT' ? (
					<div className="assignment-creation--container">
						<label className="assignment-creation--label -required" htmlFor="payments-flatPrice">Flat Fee</label>
						<div className="assignment-creation--field">
							<WMCurrencyField
								name="payments-flatPrice"
								id="payments-flatPrice"
								amount={ this.props.flatPrice }
								exponent={ 2 }
								onChange={ (event, value) => this.props.updatePricingFlatPrice(value) }
							/>
						</div>
					</div>
				) : ''}

				{this.props.type === 'PER_HOUR' ? (
					<div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-perHourPrice">Per Hour Rate</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-perHourPrice"
									id="payments-perHourPrice"
									amount={ this.props.perHourPrice }
									exponent={ 2 }
									onChange={ (event, value) => this.props.updatePricingPerHourPrice(value) }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-maxNumberOfHours">Hours Allowed</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-maxNumberOfHours"
									id="payments-maxNumberOfHours"
									amount={ this.props.maxNumberOfHours }
									onChange={
										(event, value) => this.props.updatePricingMaxNumberOfHours(value)
									}
								/>
							</div>
						</div>
					</div>
				) : ''}

				{this.props.type === 'PER_UNIT' ? (
					<div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-perUnitPrice">Per Unit Rate</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-perUnitPrice"
									id="payments-perUnitPrice"
									amount={ this.props.perUnitPrice }
									exponent={ 3 }
									onChange={ (event, value) => this.props.updatePricingPerUnitPrice(value) }
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-maxNumberOfUnits">Units Allowed</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-maxNumberOfUnits"
									id="payments-maxNumberOfUnits"
									amount={ this.props.maxNumberOfUnits }
									exponent={ 2 }
									onChange={
										(event, value) => this.props.updatePricingMaxNumberOfUnits(value)
									}
								/>
							</div>
						</div>
					</div>
				) : ''}

				{this.props.type === 'BLENDED_PER_HOUR' ? (
					<div>
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-initialPerHourPrice">Initial Per Hour Rate</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-initialPerHourPrice"
									id="payments-initialPerHourPrice"
									amount={ this.props.initialPerHourPrice }
									exponent={ 2 }
									onChange={
										(event, value) => this.props.updatePricingInitialPerHourPrice(value)
									}
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-initialNumberOfHours">Initial Hours Allowed</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-initialNumberOfHours"
									id="payments-initialNumberOfHours"
									amount={ this.props.initialNumberOfHours }
									exponent={ 2 }
									onChange={
										(event, value) => this.props.updatePricingInitialNumberOfHours(value)
									}
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-additionalPerHourPrice">Additional Per Hour Rate</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-additionalPerHourPrice"
									id="payments-additionalPerHourPrice"
									amount={ this.props.additionalPerHourPrice }
									exponent={ 2 }
									onChange={
										(event, value) => this.props.updatePricingAdditionalPerHourPrice(value)
									}
								/>
							</div>
						</div>

						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-maxBlendedNumberOfHours">Additional Hours Allowed</label>
							<div className="assignment-creation--field">
								<WMCurrencyField
									name="payments-maxBlendedNumberOfHours"
									id="payments-maxBlendedNumberOfHours"
									amount={ this.props.maxBlendedNumberOfHours }
									exponent={ 2 }
									onChange={
										(event, value) => this.props.updatePricingMaxBlendedNumberOfHours(value)
									}
								/>
							</div>
						</div>
					</div>
				) : ''}
				{ this.state.subscribed && (this.props.type !== 'INTERNAL') && (
					<div style={ styles.budget } className="assignment-creation--container">
						<label className="assignment-creation--label" htmlFor="payments-totalBudget" style={ styles.label }>Total Budget:</label>
						<div className="assignment-creation--field">
							<span style={ styles.total }>
								&#36;{ this.calculateType(this.props.type) }
							</span>
						</div>
					</div>
				) }
				{this.props.type !== 'INTERNAL' ? (
					<div>
						{ this.state.fee > 0 && (
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-mode">Fees Paid by</label>
							<div className="assignment-creation--field">
								<WMRadioButtonGroup
									name="payments-mode"
									onChange={ (event, value) => this.props.updatePricingMode(value) }
									valueSelected={ this.props.mode }
									defaultSelected="spend"
								>
									<WMRadioButton
										label="Company"
										value="pay"
									/>
									<WMRadioButton
										label="Worker"
										value="spend"
									/>
								</WMRadioButtonGroup>
							</div>
						</div>
						) }
						<div className="assignment-creation--container">
							<label className="assignment-creation--label -required" htmlFor="payments-terms">Payment Terms</label>
							<div className="assignment-creation--field">
								<select
									className="wm-select"
									name="payments-terms"
									id="payments-terms"
									defaultValue={ this.props.paymentTermsDays }
								/>
							</div>
						</div>
						{ this.state.offlinePaymentEnabled && (
							<div className="assignment-creation--field">
								<WMCheckbox
									id="offline-payment"
									label="Pay Worker outside the Work Market platform"
									checked={ this.props.offlinePayment }
									onCheck={ (event, isInputChecked) =>
										this.props.updatePricingOfflinePayment(isInputChecked)
									}
								/>
								<small>
									{ 'By setting this assignment payment to "Off-Platform", you agree to take responsibility for all payment outside of the Work Market platform.' }
								</small>
							</div>
						)}
						<div className="assignment-creation--field">
							<WMCheckbox
								id="disable-negotiation"
								label="Price is non-negotiable"
								checked={ this.props.disablePriceNegotiation }
								onCheck={ (event, isInputChecked) =>
									this.props.updatePricingDisablePriceNegotiation(isInputChecked)
								}
							/>
							<small>
								If checked, price counteroffers and spend limit increase requests are disabled.
							</small>
						</div>

						{ this.state.fee > 0 && (
						<table className="assignment-creation--payments-table">
							<thead>
								<tr>
									<th>Your Cost</th>
									<th>Transaction Fee</th>
									<th>Worker Nets</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>${this.state.companyPrice}</td>
									<td>{(this.state.fee * 100).toFixed(2)}%</td>
									<td>${this.state.workerPrice}</td>
								</tr>
							</tbody>
						</table>
					) }
					</div>
				) : ''}
			</div>
		);
	}
}

AddPricingComponent.propTypes = {
	mode: PropTypes.string.isRequired,
	paymentTermsDays: PropTypes.number.isRequired,
	maxNumberOfUnits: PropTypes.number,
	maxBlendedNumberOfHours: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	maxNumberOfHours: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	initialNumberOfHours: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	initialPerHourPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	additionalPerHourPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	perHourPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	perUnitPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	flatPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
	setModuleValidation: PropTypes.func.isRequired,
	updatePricingMaxNumberOfHours: PropTypes.func.isRequired,
	updatePricingInitialNumberOfHours: PropTypes.func.isRequired,
	updatePricingInitialPerHourPrice: PropTypes.func.isRequired,
	updatePricingMaxBlendedNumberOfHours: PropTypes.func.isRequired,
	updatePricingMaxNumberOfUnits: PropTypes.func.isRequired,
	updatePricingAdditionalPerHourPrice: PropTypes.func.isRequired,
	updatePricingPerUnitPrice: PropTypes.func.isRequired,
	updatePricingPerHourPrice: PropTypes.func.isRequired,
	updatePricingFlatPrice: PropTypes.func.isRequired,
	updatePricingType: PropTypes.func.isRequired,
	updatePricingMode: PropTypes.func.isRequired,
	updatePaymentTermsDays: PropTypes.func.isRequired,
	id: PropTypes.string.isRequired,
	type: PropTypes.string.isRequired,
	offlinePayment: PropTypes.bool.isRequired,
	updatePricingOfflinePayment: PropTypes.func.isRequired,
	disablePriceNegotiation: PropTypes.string.isRequired,
	updatePricingDisablePriceNegotiation: PropTypes.func.isRequired
};
