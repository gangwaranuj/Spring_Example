import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow, WMSelectField, WMMenuItem } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const TalentPoolMembershipsRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
}) => {
	const {
		talentPoolMembership,
		talentPoolMemberships
		} = requirementComponentData;
	const talentPoolMembershipList = talentPoolMemberships
		.map(talentPoolMembershipMap => (
			<WMMenuItem
				key={ talentPoolMembershipMap.id }
				value={ talentPoolMembershipMap.id }
				primaryText={ talentPoolMembershipMap.name }
			/>
	));
	return (
		<WMFormRow id="requirements-talentPoolMemberships" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="talentPool"
					hintText="Select the talent pool you require membership in"
					value={ talentPoolMembership }
				>
					{ talentPoolMembershipList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						disabled={ !talentPoolMembership }
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						onClick={ () => {
							const data = {
								name: talentPoolMemberships.find(item =>
										(item.id === talentPoolMembership)).name,
								$type: 'GroupMembershipRequirement',
								requirable: talentPoolMemberships.find(item =>
										(item.id === talentPoolMembership)),
								$humanTypeName: 'Group Membership'
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
			dispatch(actions.changeRequirementField('talentPoolMembership', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TalentPoolMembershipsRequirement);

TalentPoolMembershipsRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		talentPoolMembership: PropTypes.number,
		talentPoolMemberships: PropTypes.array.isRequired
	})
};
