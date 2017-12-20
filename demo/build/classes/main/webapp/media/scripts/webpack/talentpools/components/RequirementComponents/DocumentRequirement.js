import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { WMFormRow, WMMenuItem, WMRaisedButton, WMSelectField } from '@workmarket/front-end-components';
import * as actions from '../../actions';

const DocumentRequirement = ({
	handleChange,
	applyRequirement,
	requirementComponentData
	}) => {
	const {
		document,
		documents
		} = requirementComponentData;
	const documentList = documents.map(documentMap => (
		<WMMenuItem
			key={ documentMap.id }
			value={ documentMap.id }
			primaryText={ documentMap.name }
		/>
	));
	return (
		<WMFormRow id="requirements-document" >
			<div style={ { display: 'flex', flexDirection: 'column' } } >
				<WMSelectField
					onChange={ (event, index, value) => handleChange(value) }
					fullWidth
					name="document"
					hintText="Select a document"
					value={ document }
				>
					{ documentList }
				</WMSelectField>
				<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
					<WMRaisedButton
						secondary
						label="Add this requirement"
						style={ { margin: '1em 0 0 1em' } }
						disabled={ !document }
						onClick={ () => {
							const data = {
								name: documents.find(documentItem => (documentItem.id === document)).name,
								$type: 'DocumentRequirement',
								requirable: documents.find(documentItem =>
									(documentItem.id === document)),
								$humanTypeName: 'Document'
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
			dispatch(actions.changeRequirementField('document', value));
		},
		applyRequirement: (data) => {
			dispatch(actions.addRequirement(data));
		}
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(DocumentRequirement);

DocumentRequirement.propTypes = {
	handleChange: PropTypes.func.isRequired,
	applyRequirement: PropTypes.func.isRequired,
	requirementComponentData: PropTypes.shape({
		document: PropTypes.string,
		documents: PropTypes.array.isRequired
	})
};
