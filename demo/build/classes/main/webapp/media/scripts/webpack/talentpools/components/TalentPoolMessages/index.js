import { connect } from 'react-redux';
import * as actions from '../../actions';
import template from './template';

const mapStateToProps = ({ messagesData, messageFormData }) => ({
	messagesData,
	messageFormData
});

const mapDispatchToProps = (dispatch) => {
	return {
		onChangeField: (name, value) => {
			dispatch(actions.changeMessageField(name, value));
		},
		onSubmitMessageForm: (groupId, formData) => {
			dispatch(actions.submitMessageForm(groupId, formData.toJS()));
		},
		handleEditDetails: (id) => {
			dispatch(actions.changeTab('details', id));
		}
	};
};

const MessageForm = connect(
	mapStateToProps,
	mapDispatchToProps
)(template);

export default MessageForm;
