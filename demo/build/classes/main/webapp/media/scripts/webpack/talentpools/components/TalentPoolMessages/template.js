import PropTypes from 'prop-types';
import React from 'react';
import {
	commonStyles,
	WMPaper,
	WMFormRow,
	WMTextField,
	WMRaisedButton,
	WMCard,
	WMCardHeader,
	WMCardTitle,
	WMCardText,
	WMFontIcon
} from '@workmarket/front-end-components';
import { Map } from 'immutable';
import CommonStyles from '../../styles/common';

const MessageForm = ({
	onChangeField,
	onSubmitMessageForm,
	messagesData,
	messageFormData,
	handleEditDetails
}) => {
	const messagesList = messagesData.get('messages').map(message => (
		<WMCard>
			<WMCardHeader
				title={ message.get('sender_full_name') }
				subtitle={ message.get('date') }
			/>
			<WMCardTitle
				title={ message.get('subject') }
			/>
			<WMCardText>
				{ message.get('content') }
			</WMCardText>
		</WMCard>
	));

	const inactiveTalentPoolState = (
		<div id="inactiveTalentPoolState">
			<div>
				<WMFontIcon
					className="material-icons"
					style={ { display: 'block', margin: 'auto', width: '150px', height: '150px', fontSize: '150px', color: commonStyles.colors.baseColors.lightGrey } }
				>
					message
				</WMFontIcon>
			</div>
			<div style={ CommonStyles.emptyOrInactiveText }>
				To send messages to the members of a<br />
				talent pool, it must first be activated.
				<br />
				<br />
			</div>
			<WMRaisedButton
				label="Edit Details"
				style={ { marginRight: '1em', marginTop: '1em', marginBottom: '1em', display: 'table', margin: '0 auto' } }
				backgroundColor={ commonStyles.colors.baseColors.green }
				labelColor={ commonStyles.colors.baseColors.white }
				onClick={ () => handleEditDetails(messagesData.get('groupId')) }
			/>
			<br />
		</div>
	);
	const activeTalentPoolState = (
		<form>
			<WMFormRow
				required
				id="subject"
				labelText="Subject"
			>
				<WMTextField
					fullWidth
					id="talentpools_messagesRow_title"
					data-component-identifier="wm-talent_pool-message__title"
					value={ messageFormData.get('title') }
					onChange={ (event, value) => onChangeField('title', value) }
				/>
			</WMFormRow>
			<WMFormRow
				id="talentpools_messagesRow_message"
				required
				labelText="Message"
			>
				<WMTextField
					id="message"
					name="message"
					fullWidth
					multiLine
					onChange={ event => onChangeField('message', event.target.value) }
					data-dynamite-selected="true"
					value={ messageFormData.get('message') }
					style={ { marginBottom: '1em' } }
				/>
			</WMFormRow>
			<div style={ { flexDirection: 'row-reverse', display: 'flex' } }>
				<WMRaisedButton
					primary
					label="Send message"
					disabled={ messageFormData.get('title').length === 0 || messageFormData.get('message').length === 0 }
					style={ { marginBottom: '1em' } }
					onClick={ () => { onSubmitMessageForm(messagesData.get('groupId'), messageFormData); } }
				/>
			</div>
		</form>
	);
	return (
		<WMPaper style={ { padding: '1em' } } >
			{ messagesData.get('isActive') && !messagesData.get('readOnly') && activeTalentPoolState }
			{ !messagesData.get('isActive') && !messagesData.get('readOnly') && inactiveTalentPoolState }
			{ messagesList }
		</WMPaper>
	);
};

export default MessageForm;

MessageForm.propTypes = {
	onChangeField: PropTypes.func.isRequired,
	onSubmitMessageForm: PropTypes.func.isRequired,
	handleEditDetails: PropTypes.func.isRequired,
	messagesData: PropTypes.instanceOf(Map).isRequired,
	messageFormData: PropTypes.instanceOf(Map).isRequired
};
