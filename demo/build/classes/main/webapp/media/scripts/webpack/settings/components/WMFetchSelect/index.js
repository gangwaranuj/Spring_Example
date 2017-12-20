import PropTypes from 'prop-types';
import React from 'react';
import { WMSelectField, WMMenuItem } from '@workmarket/front-end-components';

class WMFetchSelect extends React.Component {
	constructor (props) {
		super(props);
		this.state = { options: [] };
	}

	componentDidMount (
		urlRoot = ''
	) {
		fetch(urlRoot + this.props.fetchURL, { credentials: 'same-origin' })
		.then(res => res.json())
		.then((res) => {
			this.setState({ options: res.results });
		});
	}

	render () {
		const { options } = this.state;
		const renderOptions = options.map((option, index) => (
			<WMMenuItem
				key={ index } // eslint-disable-line react/no-array-index-key
				value={ option[0] }
				primaryText={ option[0] }
			/>
			)
		);

		return (
			<WMSelectField
				{ ...this.props }
				onChange={ (event, index, value) => this.props.onSelectChange(event, index, value) }
			>
				{ renderOptions }
			</WMSelectField>
		);
	}
}

export default WMFetchSelect;

WMFetchSelect.propTypes = {
	onSelectChange: PropTypes.func.isRequired,
	fetchURL: PropTypes.string.isRequired
};
