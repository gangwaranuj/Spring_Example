import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMRaisedButton, WMFormRow } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const DrugTestRequirement = ({ applyRequirement }) => {
	return (
		<WMFormRow id="requirement__drugtest" >
			<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
				<WMRaisedButton
					secondary
					label="Add this requirement"
					style={ { margin: '1em 0 0 1em' } }
					onClick={ () => {
						const data = {
							name: 'Passed Sterling Drug Test',
							$humanTypeName: 'Drug Test',
							$type: 'DrugTestRequirement'
						};
						applyRequirement(data);
					} }
				/>
			</div>
		</WMFormRow>
	);
};

const mapStateToProps = ({ requirementsData }) => ({
	requirementComponentData: requirementsData.toObject()
});

const mapDispatchToProps = (dispatch) => {
	return {
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		},
		cancelRequirement: () => {
			dispatch(actions.cancelRequirement());
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(DrugTestRequirement);

DrugTestRequirement.propTypes = {
	applyRequirement: PropTypes.func.isRequired
};
