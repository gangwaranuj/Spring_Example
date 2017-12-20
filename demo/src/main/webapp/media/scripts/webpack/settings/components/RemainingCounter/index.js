import PropTypes from 'prop-types';
import React from 'react';

const RemainingCounter = ({ maxCharacters, value }) => {
	const textAreaCharacters = value.length || 0;
	const charLeft = maxCharacters - textAreaCharacters;
	let numberStyle = {};

	if (charLeft < 0) {
		numberStyle = { color: 'red' };
	}

	return (
		<p style={ numberStyle }>
			{ charLeft }
		</p>
	);
};

export default RemainingCounter;

RemainingCounter.propTypes = {
	maxCharacters: PropTypes.number.isRequired,
	value: PropTypes.string.isRequired
};
