package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.WorkUploadError;
import com.workmarket.thrift.work.uploader.WorkUploadErrorType;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public abstract class ParseUtils {

	public static WorkRowParseError createErrorRow(String value, String errorMessage,
			WorkRowParseErrorType errorType, WorkUploadColumn column) {

		WorkRowParseError error = new WorkRowParseError();
		error.setMessage(errorMessage);
		error.setErrorType(errorType);
		error.setData(value);
		error.setColumn(column);
		return error;
	}

	/**
	 * Verify that the selected mapping matches the provided data.
	 * We're looking to make sure that the # of columns provided matches
	 * the # of columns configured in the mapping.
	 * Additionally if there are saved column names and columns are provided,
	 * check that these match.
	 * @param mapping
	 * @param assns
	 * @return
	 */
	public static List<WorkUploadError> verifyMapping(FieldMappingGroup mapping, List<String[]> assns, boolean headersProvided) {
		List<WorkUploadError> errors = Lists.newArrayList();

		String[] firstRow = assns.get(0);
		if (firstRow.length != mapping.getMappingsSize()) {
			String why = (firstRow.length > mapping.getMappingsSize()) ?
				"More data provided than mapped columns" :
				"Mapping expects more data";
			errors.add(
				new WorkUploadError()
					.setViolation(new ConstraintViolation().setWhy(why))
					.setErrorType(WorkUploadErrorType.MAPPING_COLUMN_LENGTH_MISMATCH)
			);
		}

		if (headersProvided) {
			@SuppressWarnings("unchecked")
			List<String> mappingColumnNames = CollectionUtilities.newListPropertyProjection(mapping.getMappings(), "columnName");
			List<String> providedColumnNames = Lists.newArrayList(firstRow);

			if (!CollectionUtils.isEqualCollection(mappingColumnNames, providedColumnNames)) {
				errors.add(
					new WorkUploadError()
						.setViolation(new ConstraintViolation().setWhy("Provided column names do not match mapping"))
						.setErrorType(WorkUploadErrorType.MAPPING_COLUMN_NAMING_MISMATCH)
				);
			}
		}

		return errors;
	}
}
