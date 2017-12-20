/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
	commonStyles,
	WMFontIcon,
	WMCard,
	WMCardHeader,
	WMCardText
} from '@workmarket/front-end-components';
import {
	WMAddFundsViaCreditCard
} from '@workmarket/front-end-patterns';
import componentStyles from './styles';
import baseStyles from '../styles';
import content from './content';

const mediaPrefix = window.mediaPrefix;
const { green } = commonStyles.colors.baseColors;
const styles = Object.assign({}, baseStyles, componentStyles);

export const FundingOptionTile = ({
	title,
	icon,
	time,
	url,
	selected,
	expandFundingType
}) => {
	const selectFundingType = () => {
		if (url) {
			window.open(url, '_blank');
		} else {
			expandFundingType();
		}
	};

	return (
		<div
			style={ styles.fundingTile(selected) }
			onClick={ () => selectFundingType() }
			className="wm-settings__funding-tile"
		>
			<WMFontIcon
				className="material-icons"
				id={ `wm-settings-funds-${title}` }
				color={ selected ? green : '#8d9092' }
				style={ styles.tileIcon }
			>
				{ icon }
			</WMFontIcon>
			<div
				style={ styles.tileTitle(selected) }
				data-component-identifier="funding-option-tile__title"
			>
				{ title }
			</div>
			<div
				style={ styles.tileTime }
			>
				<WMFontIcon
					className="material-icons"
					id={ `wm-settings-funds-${title}-time` }
					color="#8d9092"
					style={ styles.timeIcon }
				>
					timer
				</WMFontIcon>
				<div
					style={ styles.timeText }
					data-component-identifier="funding-option-tile__time"
				>
					{ time }
				</div>
			</div>
		</div>
	);
};

FundingOptionTile.propTypes = {
	title: PropTypes.string.isRequired,
	icon: PropTypes.string.isRequired,
	time: PropTypes.string.isRequired,
	url: PropTypes.string,
	selected: PropTypes.bool,
	expandFundingType: PropTypes.func.isRequired
};

class WMSettingsFunds extends Component {
	constructor (props) {
		super(props);
		this.state = {
			expanded: false,
			currentView: null
		};
	}

	handleExpandChange = (expanded) => {
		this.setState({ expanded });
	}

	handleFundingChoice = (type) => {
		this.setState({
			currentView: type
		});
	}

	handleSubmit = () => {
		this.props.onSubmitForm(this.props.info.creditCardFunds);
	}

	render () {
		const { expanded } = this.state;
		const cards = content;
		const {
			info,
			onChangeField
		} = this.props;
		const {
			creditCardFunds,
			settings
		} = info;
		const creditCardFundsError = settings.get('creditCardFundsError');
		const creditCardFundsSubmitted = settings.get('creditCardFundsSubmitted');

		return (
			<WMCard
				style={ styles.card }
				onExpandChange={ this.handleExpandChange }
				expanded={ expanded }
			>
				<WMCardHeader
					title="Add Funds"
					subtitle="You will need to add funds before routing any work"
					style={ styles.cardHeader }
					textStyle={ styles.cardHeaderText }
					titleStyle={ styles.cardHeaderTitle }
					actAsExpander
					showExpandableButton
				>
					<img
						id="wm-settings-funds__icon"
						src={ `${mediaPrefix}/images/settings/fund.svg` }
						alt="Add Funds Icon"
						style={ styles.cardIcon }
					/>
				</WMCardHeader>
				<WMCardText
					style={ styles.cardText }
					expandable
				>
					<div style={ styles.cardText.icons }>
						{
							cards.map((card, index) => (
								<FundingOptionTile
									{ ...card }
									key={ index } // eslint-disable-line react/no-array-index-key
									selected={ this.state.currentView === card.title }
									expandFundingType={ () => this.handleFundingChoice(card.title) }
								/>
							))
						}
					</div>

					{
						this.state.currentView === 'Credit Card' &&
							<div style={ styles.fundingType }>
								<WMAddFundsViaCreditCard
									onSubmit={ this.handleSubmit }
									onChangeField={ onChangeField }
									fundsToAdd={ creditCardFunds.get('fundsToAdd') }
									cardNumber={ creditCardFunds.get('cardNumber') }
									nameOnCard={ creditCardFunds.get('nameOnCard') }
									expirationMonth={ creditCardFunds.get('expirationMonth') }
									expirationYear={ creditCardFunds.get('expirationYear') }
									securityCode={ creditCardFunds.get('securityCode') }
									billingAddress1={ creditCardFunds.get('billingAddress1') }
									billingAddress2={ creditCardFunds.get('billingAddress2') }
									billingCity={ creditCardFunds.get('billingCity') }
									billingProvince={ creditCardFunds.get('billingProvince') }
									billingPostalCode={ creditCardFunds.get('billingPostalCode') }
									billingState={ creditCardFunds.get('billingState') }
									billingZip={ creditCardFunds.get('billingZip') }
									billingCountry={ creditCardFunds.get('billingCountry') }
									errors={ creditCardFundsError }
									success={ creditCardFundsSubmitted }
									disabled={ settings.get('submitting') }
								/>
							</div>
					}
				</WMCardText>
			</WMCard>
		);
	}
}

WMSettingsFunds.propTypes = {
	onChangeField: PropTypes.func.isRequired,
	onSubmitForm: PropTypes.func.isRequired,
	info: PropTypes.object.isRequired // eslint-disable-line
};

export default WMSettingsFunds;
