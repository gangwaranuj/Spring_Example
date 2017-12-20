import { connect } from 'react-redux';
import {
	WMImportEmployees
} from '@workmarket/front-end-patterns';
import * as actions from '../../actions';

const mapStateToProps = state => ({
	files: state.bulkUploadEmployees
});

const mapDispatchToProps = dispatch => ({
	uploadFile: (file) => {
		dispatch(actions.onBulkEmployeeUpload(file));
	},
	cancelUpload: (id) => {
		dispatch(actions.cancelBulkUpload(id));
	}
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(WMImportEmployees);
