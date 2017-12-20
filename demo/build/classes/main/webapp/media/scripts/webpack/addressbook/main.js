import AddressBookMainView from './main_view';
import AddClientLocationContainer from './containers/add_client_location';
import AddClientLocationReducer from './reducers/add_client_location';
import ProjectIdReducer from './reducers/project';
import ClientCompanyView from './client_company_view';

class MainView {
	constructor () {
		new AddressBookMainView(); // eslint-disable-line no-new
		new ClientCompanyView(); // eslint-disable-line no-new
	}
}

export default {
	MainView,
	AddClientLocationContainer,
	AddClientLocationReducer,
	ProjectIdReducer
};
