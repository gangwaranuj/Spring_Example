import fetch from 'isomorphic-fetch';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Map } from 'immutable';
import {
	WMTabs,
	WMTab
} from '@workmarket/front-end-components';
import * as actions from '../../actions';
import WMAddEmployeeForm from './WMAddEmployeeForm';
import WMImportEmployees from './WMImportEmployees';
import styles from './styles';

class WMAddEmployees extends Component {
	constructor () {
		super();

		this.state = {
			phoneInternationalCodes: [],
			industries: []
		};
	}

	componentDidMount () {
		this.getIndustries();
		this.getPhoneInternationalCodes();
	}

	getIndustries = (url = '') => {
		return fetch(`${url}/industries-list`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(industries => this.setState({ industries }));
	}

	getPhoneInternationalCodes = (url = '') => {
		return fetch(`${url}/v2/constants/country_codes?fields=id,name`, { credentials: 'same-origin' })
			.then(res => res.json())
			.then(phoneInternationalCodes => this.setState({
				phoneInternationalCodes: phoneInternationalCodes.results
			}));
	}

	render () {
		return (
			<WMTabs
				tabItemContainerStyle={ styles.tabs }
				inkBarStyle={ styles.inkBar }
			>
				<WMTab
					label="Add Individually"
					style={ styles.tab }
				>
					<WMAddEmployeeForm
						phoneInternationalCodes={ this.state.phoneInternationalCodes }
						industries={ this.state.industries }
						disabled={ this.props.settings.get('submitting') }
					/>
				</WMTab>
				<WMTab
					label="Import"
					style={ styles.tab }
				>
					<WMImportEmployees
						sampleCSVUrl="/download/sample_employees.csv"
					/>
				</WMTab>
			</WMTabs>
		);
	}
}

WMAddEmployees.propTypes = {
	settings: PropTypes.instanceOf(Map).isRequired
};

const mapStateToProps = state => ({
	settings: state.settings
});

const mapDispatchToProps = dispatch => ({
	onChange: (name, value) => dispatch(actions.changeAddEmployeeField(name, value))
});

export { WMAddEmployees as UnconnectedComponent };
export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMAddEmployees);
