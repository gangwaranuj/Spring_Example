import {
	Record,
	List,
	Iterable
} from 'immutable';

const Employee = new Record({
	id: '',
	firstName: '',
	lastName: '',
	fullName: '',
	thumbnail: '',
	isCurrentUser: false
});

class EmployeeList extends Record({
	employees: new List()
}, 'EmployeeList') {
	constructor (values) {
		const record = super();
		if (Iterable.isIterable(values)) {
			return record.set('employees', values.map(employee => new Employee(employee)));
		}
		return record;
	}
}

export default EmployeeList;
