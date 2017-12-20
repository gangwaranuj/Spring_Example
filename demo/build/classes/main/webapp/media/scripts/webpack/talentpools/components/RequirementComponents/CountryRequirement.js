import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const CountryRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		country,
		countries
		} = requirementComponentData;
	const countryList = countries.map(countryMap => (
		<WMMenuItem
			key={ countryMap.id }
			value={ countryMap.id }
			primaryText={ countryMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-country" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="country"
					hintText="Select a country"
					value={ country }
				>
					{ countryList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !country }
						onClick={ () => {
							const data = {
								name: countries.find(countryItem => (countryItem.id === country)).name,
								$type: 'CountryRequirement',
								requirable: countries.find(countryItem => (countryItem.id === country)),
								$humanTypeName: 'Country'
							};
							applyRequirement(data);
						} }
					/>
				</div>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = ({ requirementsData }) => ({
	requirementComponentData: requirementsData.toJS()
});

const mapDispatchToProps = (dispatch) => {
	return {
		handleChange: (value) => {
			dispatch(actions.changeRequirementField('country', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(CountryRequirement);

CountryRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		country: PropTypes.string,
		countries: PropTypes.array.isRequired
	})
};
