/* eslint-disable react/require-default-props */
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
	connect
} from 'react-redux';
import {
	Map,
	Record
} from 'immutable';
import {
	WMCompanyProfile
} from '@workmarket/front-end-patterns';
import {
	mapDispatchToProps,
	mapStateToProps
} from '../../connect';
import Application from '../../../core';
import WMSettingsBank from '../WMSettingsBank';
import WMSettingsFunds from '../WMSettingsFunds';
import WMSettingsTax from '../WMSettingsTax';
import WMSettingsAssignment from '../WMSettingsAssignment';
import WMSettingsFirstAssignment from '../WMSettingsFirstAssignment';
import WMSettingsEmployees from '../WMSettingsEmployees';
import {
	EmployeeList as EmployeeListModel
} from '../../reducers/models';
import styles from './styles';

const handleLogoUpload = (fileBlob) => {
	return new Promise(
		(resolve, reject) => {
			const upload = new FormData();
			const xhr = new XMLHttpRequest();
			upload.append('qqfile', fileBlob);
			xhr.open('POST', '/upload/uploadqq');
			xhr.responseType = 'json';
			xhr.setRequestHeader('X-CSRF-Token', Application.CSRFToken);
			xhr.onreadystatechange = () => {
				if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
					const {
						uuid
					} = xhr.response;
					if (uuid) {
						resolve(uuid);
					} else {
						reject('Upload failed');
					}
				}
			};

			xhr.send(upload);
		}
	);
};

export class WMCheckList extends Component {
	static propTypes = {
		getOnboardingProgress: PropTypes.func.isRequired,
		onChangeField: PropTypes.func.isRequired,
		onSubmitProfileForm: PropTypes.func.isRequired,
		getCompanyProfileInfo: PropTypes.func.isRequired,
		getEmployeeList: PropTypes.func.isRequired,
		info: PropTypes.shape({
			funds: PropTypes.instanceOf(Map),
			settings: PropTypes.instanceOf(Map),
			profile: PropTypes.instanceOf(Record),
			tax: PropTypes.instanceOf(Map),
			employeeList: PropTypes.instanceOf(EmployeeListModel)
		})
	}

	state = {
		companyProfileExpanded: false
	}

	componentDidMount () {
		const {
			getOnboardingProgress,
			getCompanyProfileInfo,
			getEmployeeList
		} = this.props;
		getOnboardingProgress();
		getCompanyProfileInfo();
		getEmployeeList();
	}

	componentWillReceiveProps (nextProps) {
		if (
			!this.props.info.settings.get('profileSubmitted') &&
			nextProps.info.settings.get('profileSubmitted')
		) {
			this.setState({ companyProfileExpanded: false });
		}
	}

	handleCompanyProfileExpand = () => {
		const {
			companyProfileExpanded
		} = this.state;
		this.setState({ companyProfileExpanded: !companyProfileExpanded });
	}

	render () {
		const {
			info: {
				profile,
				settings,
				employeeList
			},
			onChangeField,
			onSubmitProfileForm
		} = this.props;
		const {
			companyProfileExpanded
		} = this.state;
		const { firstName } = Application.UserInfo;
		const welcomeText = 'Welcome to Work Market. To help you make the most of the platform, we need a bit more information from you.';

		return (
			<div
				style={ styles.container }
			>
				<div>
					<h3>Hey {firstName}!</h3>
					<p>{ welcomeText }</p>
				</div>
				<WMCompanyProfile
					cardIconSrc={ `${mediaPrefix}/images/settings/company.profile.svg` }
					overview={ profile.overview }
					website={ profile.website }
					address1={ profile.location.get('addressLine1') }
					address2={ profile.location.get('addressLine2') }
					city={ profile.location.get('city') }
					state={ profile.location.get('state') }
					zip={ profile.location.get('zip') }
					country={ profile.location.get('country') }
					yearFounded={ profile.yearFounded }
					inVendorSearch={ profile.inVendorSearch }
					workInviteSentToUserId={ profile.workInviteSentToUserId }
					employees={ employeeList.employees }
					backgroundCheck={ profile.backgroundCheck }
					drugTest={ profile.drugTest }
					profileError={ settings.get('profileError') }
					profileSubmitted={ settings.profileSubmitted }
					locationFieldPrefix="location."
					address1Name="addressLine1"
					address2Name="addressLine2"
					changeField={ onChangeField }
					onSubmit={ onSubmitProfileForm }
					handleUploadLogo={ handleLogoUpload }
					expanded={ companyProfileExpanded }
					handleExpand={ this.handleCompanyProfileExpand }
				/>
				<WMSettingsBank />
				<WMSettingsFunds />
				<WMSettingsTax />
				<WMSettingsEmployees />
				<WMSettingsAssignment />
				<WMSettingsFirstAssignment />
			</div>
		);
	}
}

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMCheckList);
