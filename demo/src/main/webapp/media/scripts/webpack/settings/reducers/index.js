import { combineReducers } from 'redux';
import { Map } from 'immutable';
import settings, { initialSettingsState } from './settingsReducer';
import profile, { initialCompanyProfileState } from './profileReducer';
import funds, { initialFundsState } from './fundsReducer';
import creditCardFunds, { initialCreditCardFundsState } from './addFundsViaCreditCardReducer';
import addEmployee, { initialAddEmployeeState } from './addEmployeeReducer';
import bulkUploadEmployees, { initialBulkEmployeesState } from './bulkUploadEmployeesReducer';
import assignmentPrintoutUpload, { initialAssignmentPrintoutState } from './assignmentPrintoutReducer';
import assignmentPreferences, { initialAssignmentPreferencesState } from './assignmentPreferencesReducer';
import firstAssignment, { initialFirstAssignmentState } from './firstAssignmentReducer';
import firstAssignmentTemplate, { initialFirstAssignmentTemplateState } from './firstAssignmentTemplateReducer';
import tax, { initialTaxState } from './taxReducer';
import employeeList, { initialEmployeeListState } from './employeesReducer';

const initialState = Map({});
initialState.settings = initialSettingsState;
initialState.profle = initialCompanyProfileState;
initialState.funds = initialFundsState;
initialState.creditCardFunds = initialCreditCardFundsState;
initialState.addEmployee = initialAddEmployeeState;
initialState.bulkUploadEmployees = initialBulkEmployeesState;
initialState.assignmentPrintoutUpload = initialAssignmentPrintoutState;
initialState.assignmentPreferences = initialAssignmentPreferencesState;
initialState.firstAssignment = initialFirstAssignmentState;
initialState.firstAssignmentTemplate = initialFirstAssignmentTemplateState;
initialState.tax = initialTaxState;
initialState.employeeList = initialEmployeeListState;

export { initialState };

export default combineReducers({
	settings,
	profile,
	funds,
	creditCardFunds,
	addEmployee,
	bulkUploadEmployees,
	tax,
	employeeList,
	assignmentPrintoutUpload,
	assignmentPreferences,
	firstAssignment,
	firstAssignmentTemplate
});
