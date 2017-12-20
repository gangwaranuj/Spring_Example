import $ from 'jquery';
import React, { Component } from 'react';
import { WMModal, WMFlatButton, WMCard, WMFontIcon } from '@workmarket/front-end-components';
import fetch from 'isomorphic-fetch';
import Application from '../../core';
import qq from '../../funcs/fileUploader';
import wmNotify from '../../funcs/wmNotify';
import uploaderButtonTemplate from '../templates/uploaderButton.hbs';
import styles from './styles';

class EmployeeUploaderComponent extends Component {
	constructor (props) {
		super(props);
		this.state = {
			file: {},
			canBulkImportEmployees: false,
			isUploaderModalOpen: false,
			link: '/download/sample_employees.csv'
		};
	}

	componentDidMount () {
		this.getOrgStructuresToggle().then((myEntitlements) => {
			if (myEntitlements && myEntitlements.org_structures && myEntitlements.org_structures !== 'false') {
				this.setState({ link: '/download/sample_employees_with_org.csv' });
			}
		});
	}

	getOrgStructuresToggle () { // eslint-disable-line class-methods-use-this
		return fetch('/featureEntitlements', { credentials: 'same-origin' })
			.then(res => res.json())
			.then(data => data)
			.catch((err) => {
				console.error('There was an error retrieving entitlements in the creation modal.', err); // eslint-disable-line no-console
				return false;
			});
	}

	loadUploader () {
		this.uploader = new qq.FileUploader({
			element: this.employeeUploaderElement,
			action: '/upload/uploadqq',
			CSRFToken: Application.CSRFToken,
			multiple: false,
			sizeLimit: 10 * 1024 * 1024, // 10MB
			template: uploaderButtonTemplate(),
			listElement: this.nonRenderedList,
			onSubmit: () => {
				$(this.nonRenderedList).hide();
			},
			onComplete: (id, fileName, data) => {
				if (data.successful) {
					this.setState({
						canBulkImportEmployees: true,
						file: data
					});
				}
			},
			showMessage: (message) => {
				wmNotify({
					message,
					delay: '4000'
				});
			}
		});
	}

	postBulkEmployees ({ uuid }) {
		$.ajax({
			type: 'POST',
			url: `/users/import/${uuid}`,
			contentType: 'application/json',
			dataType: 'json',
			headers: {
				'X-CSRF-Token': Application.CSRFToken
			}
		})
			.success((response) => {
				if (response.successful) {
					wmNotify({
						message: 'Your file is currently being uploaded. You should get the status of your upload via notifications.',
						delay: '4000'
					});
					this.closeModal();
				}
			})
			.error(() => {
				wmNotify({
					message: 'There was an error in uploading the file. Please upload the file again.',
					delay: '4000'
				});
				this.closeModal();
			});
	}

	openModal = () => {
		this.setState({ isUploaderModalOpen: true }, () => {
			this.loadUploader();
		});
	}

	closeModal () {
		this.setState({
			file: {},
			canBulkImportEmployees: false,
			isUploaderModalOpen: false
		});
	}

	render () {
		const bulkUploadActions = [
			<WMFlatButton
				label="CANCEL"
				secondary
				onClick={ () => {
					this.closeModal();
				} }
			/>,
			<WMFlatButton
				label="UPLOAD"
				primary
				disabled={ !this.state.canBulkImportEmployees }
				onClick={ () => {
					this.postBulkEmployees(this.state.file);
				} }
			/>
		];

		const renderUploadedFile = () => {
			const { file } = this.state;
			if (file.uuid) {
				return (<div style={ styles.uploaded }>
					<WMFontIcon
						style={ styles.documentIcon }
						id="uploader-employee"
						className="material-icons"
					>
						insert_drive_file
					</WMFontIcon>
					<a alt={ `${file.uuid}` } href={ `/upload/download/${file.uuid}` }>{file.file_name}</a>
				</div>);
			}
			return (<div style={ styles.pendingUpload }>
				<WMFontIcon
					style={ styles.cloudIcon }
					id="uploader-employee"
					className="material-icons"
				>
					cloud_upload
				</WMFontIcon>
			</div>);
		};

		return (
			<div>
				<button
					style={ styles.button }
					className="button pull-right uploader--button"
					onClick={ this.openModal }
				>
					Bulk Upload
				</button>
				<WMModal
					id={ 'settings_uploader_popup' }
					open={ this.state.isUploaderModalOpen }
					title={ 'Upload Bulk Employees' }
					style={ {
						zIndex: '10001'
					} }
					modal
					actions={ bulkUploadActions }
    >

					<div id="uploader-info">
						{ 'Get started by ' }
						<a href={ this.state.link } download="sample_employees.csv">downloading the CSV template.</a>
						{ ' You\'ll need your employee\'s name and email address. Please do not remove column headers.' }
					</div>

					<WMCard style={ styles.dropUploader }>
						<div style={ styles.dropContainer }>

							{/* This list isn't rendered but is required for the qq uploader*/}
							<ul
								ref={ (c) => { this.nonRenderedList = c; } }
							/>
							{ renderUploadedFile() }
							<div
								ref={ (c) => { this.employeeUploaderElement = c; } }
							/>
						</div>
					</WMCard>

					<small style={ styles.meta }>Limit 10000 enteries per upload</small>
					<a
						style={ styles.link }
						className="fr"
						target="_blank"
						rel="noopener noreferrer"
						href="https://workmarket.zendesk.com/hc/en-us/articles/223271408"
					>
						Learn More
					</a>
				</WMModal>
			</div>
		);
	}
}

export default EmployeeUploaderComponent;
