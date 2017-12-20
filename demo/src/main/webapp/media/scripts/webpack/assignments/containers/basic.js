'use strict';

import { connect } from 'react-redux';
import BasicComponent from '../components/basic';
import Actions from '../actions/basic';

const mapStateToProps = ({ title, description, industryId, skills, supportContactId, projectId, uniqueExternalId, ownerId, configuration, instructionsPrivate, instructions }) => {
	return { title, description, industryId, skills, supportContactId, projectId, uniqueExternalId, ownerId, configuration, instructionsPrivate, instructions };
};

const mapDispatchToProps = (dispatch) => {
	return {
		setValue: ({ name, value }) => {
			switch (name) {
				case 'id':
					dispatch(Actions.updateId(value));
					break;
				case 'title':
					dispatch(Actions.updateTitle(value));
					break;
				case 'description':
					dispatch(Actions.updateDescription(value));
					break;
				case 'skills':
					dispatch(Actions.updateSkills(value));
					break;
				case 'industryId':
					dispatch(Actions.updateIndustryId(value));
					break;
				case 'ownerId':
					dispatch(Actions.updateOwner(value));
					break;
				case 'projectId':
					dispatch(Actions.updateSupportContactId(value));
					break;
				case 'supportContactId':
					dispatch(Actions.updateSupportContactId(value));
					break;
				case 'instructions':
					dispatch(Actions.updateInstructions(value));
					break;
				case 'instructionsPrivate':
					dispatch(Actions.togglePrivateInstructions());
					break;
				case 'uniqueExternalId':
					dispatch(Actions.updateUniqueExternalId(value));
					break;
				default:
					break;
			}
		}
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(BasicComponent);

