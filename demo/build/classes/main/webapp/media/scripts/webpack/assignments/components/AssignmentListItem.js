'use strict';
import PropTypes from 'prop-types';
import React from 'react';
import { WMCheckbox } from '@workmarket/front-end-components';

const AssignmentListItem = (props) => {

	const { item, title } = props;
	const name = title || item.name || item.description;
	const checked = item.required;

	return (
		<div className="assignment-creation--list-item">
			{ props.onToggleToClick ? (
				<WMCheckbox
					classList="assignment-creation--checkbox"
					name="survey-required"
					onCheck={ props.onToggleToClick }
					checked={ checked }
					label={ checked ? `Required ${name}` : name }
				/>
			) : <span style={ { width: '100%' } }>{ name }</span> }
			{!props.required && (
				<div>
					<i
						className="wm-icon-trash deliverables--remove"
						onClick={ props.onRemoveToClick }
					/>
				</div>
			)}
		</div>
	);
};

AssignmentListItem.PropTypes = {
	item: PropTypes.object.isRequired,
	onRemoveToClick: PropTypes.func.isRequired,
	onToggleToClick: PropTypes.func,
	required: PropTypes.boolean
};

export default AssignmentListItem;

