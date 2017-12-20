import fundsFields from './fundsFields';
import taxFields from './taxFields';

export {
	fundsFields,
	taxFields
};

const allFields = Object.assign({}, fundsFields, taxFields);

export default allFields;
