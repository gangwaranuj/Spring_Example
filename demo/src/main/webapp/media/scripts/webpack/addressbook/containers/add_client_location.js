import { connect } from 'react-redux';
import Component from '../components/add_client_location';
import LocationActions from '../actions/location';
import ContactActions from '../actions/contact';
import SecondaryContactActions from '../actions/secondary_contact';

const mapStateToProps = ({ location, projectId }) => {
	return { location, projectId };
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateLocationId: value =>
			dispatch(LocationActions.updateLocationId(Number.isNaN(+value) ? value : +value)),
		updateLocationName: value => dispatch(LocationActions.updateLocationName(value)),
		updateLocationNumber: value => dispatch(LocationActions.updateLocationNumber(value)),
		updateLocationMode: value => dispatch(LocationActions.updateLocationMode(value)),
		updateLocationAddressLine1: value =>
			dispatch(LocationActions.updateLocationAddressLine1(value)),
		updateLocationAddressLine2: value =>
			dispatch(LocationActions.updateLocationAddressLine2(value)),
		updateLocationCity: value => dispatch(LocationActions.updateLocationCity(value)),
		updateLocationState: value => dispatch(LocationActions.updateLocationState(value)),
		updateLocationZip: value => dispatch(LocationActions.updateLocationZip(value)),
		updateLocationLatitude: value => dispatch(LocationActions.updateLocationLatitude(value)),
		updateLocationLongitude: value => dispatch(LocationActions.updateLocationLongitude(value)),
		updateLocationCountry: value => dispatch(LocationActions.updateLocationCountry(value)),
		updateLocationType:
			value => dispatch(LocationActions.updateLocationType(value)),
		updateLocationInstructions:
			value => dispatch(LocationActions.updateLocationInstructions(value)),
		updateContactId: value => dispatch(ContactActions.updateContactId(value)),
		updateContactFirstName: value => dispatch(ContactActions.updateContactFirstName(value)),
		updateContactLastName: value => dispatch(ContactActions.updateContactLastName(value)),
		updateContactEmail: value => dispatch(ContactActions.updateContactEmail(value)),
		updateContactWorkPhone: value => dispatch(ContactActions.updateContactWorkPhone(value)),
		updateContactWorkPhoneExtension: value =>
			dispatch(ContactActions.updateContactWorkPhoneExtension(value)),
		updateContactMobilePhone: value => dispatch(ContactActions.updateContactMobilePhone(value)),
		updateSecondaryContactId: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactId(value)),
		updateSecondaryContactFirstName: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactFirstName(value)),
		updateSecondaryContactLastName: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactLastName(value)),
		updateSecondaryContactEmail: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactEmail(value)),
		updateSecondaryContactWorkPhone: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactWorkPhone(value)),
		updateSecondaryContactWorkPhoneExtension: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactWorkPhoneExtension(value)),
		updateSecondaryContactMobilePhone: value =>
			dispatch(SecondaryContactActions.updateSecondaryContactMobilePhone(value)),
		updateClientCompanyId: value => dispatch(LocationActions.updateClientCompanyId(value)),
		updateProjectId: value => dispatch(LocationActions.updateProjectId(value)),
		clearLocationFields: () => dispatch(LocationActions.clearLocationFields())
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(Component);
