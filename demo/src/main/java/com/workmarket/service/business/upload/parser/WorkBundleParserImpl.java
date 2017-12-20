package com.workmarket.service.business.upload.parser;

import ch.lambdaj.function.matcher.Predicate;
import com.google.common.collect.Lists;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

/**
 * User: micah
 * Date: 11/7/13
 * Time: 11:04 AM
 */
@Component
public class WorkBundleParserImpl extends BaseParser implements WorkBundleParser {
	@Override
	public void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData) {
		Assert.notNull(response);
		Assert.notNull(buildData);

		Map<String,String> types = buildData.getTypes();
		List<WorkRowParseError> errors = Lists.newArrayList();

		// check basic validation
		boolean existingBundleIdPresent = WorkUploadColumn.containsAll(types, WorkUploadColumn.EXISTING_BUNDLE_ID);
		boolean newBundleNamePresent = WorkUploadColumn.containsAll(types, WorkUploadColumn.NEW_BUNDLE_NAME);
		boolean newBundleDescriptionPresent = WorkUploadColumn.containsAll(types, WorkUploadColumn.NEW_BUNDLE_DESCRIPTION);
		String existingBundleId = WorkUploadColumn.get(types, WorkUploadColumn.EXISTING_BUNDLE_ID);
		String newBundleName = WorkUploadColumn.get(types, WorkUploadColumn.NEW_BUNDLE_NAME);
		String newBundleDescription = WorkUploadColumn.get(types, WorkUploadColumn.NEW_BUNDLE_DESCRIPTION);

		boolean valid = true;

		if (existingBundleIdPresent && (newBundleNamePresent || newBundleDescriptionPresent)) {
			// can't have existing bundle id and new bundle columns on the same line
			StringBuilder err = new StringBuilder();
			err.append("Cannot mix existing bundle id and new bundle information. ");
			err.append("Found existing bundle id: " + existingBundleId + " and ");
			if (newBundleNamePresent) {
				err.append("new bundle name: " + newBundleName + ((newBundleDescriptionPresent) ? " and " : "."));
			}
			if (newBundleDescriptionPresent) {
				err.append("new bundle description: " + newBundleDescription + ".");
			}
			errors.add(ParseUtils.createErrorRow(existingBundleId, err.toString(), WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.EXISTING_BUNDLE_ID));
			valid = false;
		}
		else if (newBundleNamePresent && !newBundleDescriptionPresent) {
			// must have both bundle name and bundle description for new bundles
			errors.add(ParseUtils.createErrorRow(newBundleName,
				"Found new bundle name: " + newBundleName + ", but missing required description.",
				WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.NEW_BUNDLE_DESCRIPTION)
			);
			valid = false;
		}
		else if (!newBundleNamePresent && newBundleDescriptionPresent) {
			// must have both bundle name and bundle description for new bundles
			errors.add(ParseUtils.createErrorRow(newBundleDescription,
				"Found new description: " + newBundleDescription+ ", but missing required name.",
				WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.NEW_BUNDLE_NAME)
			);
			valid = false;
		} else if (!existingBundleIdPresent && !newBundleNamePresent && !newBundleDescriptionPresent) {
			// no bundle info in this row
			return;
		}

		// check build data for bundle data
		if (valid && newBundleNamePresent) {
			final String title = types.get(WorkUploadColumn.NEW_BUNDLE_NAME.getUploadColumnName());
			final String description = types.get(WorkUploadColumn.NEW_BUNDLE_DESCRIPTION.getUploadColumnName());
			// check bundles for matching title and description
			List<WorkBundle> bundleMatch = filter(new Predicate<WorkBundle>() {
				@Override public boolean apply(WorkBundle workBundle) {
					return (title.equals(workBundle.getTitle()) && description.equals(workBundle.getDescription()));
				}
			}, buildData.getBundles());

			WorkBundle workBundle;
			if (CollectionUtils.isNotEmpty(bundleMatch)) {
				// set the found WorkBundle in the response
				workBundle = bundleMatch.get(0);
			} else {
				// create a new WorkBundle, set the title and description, add to the response and to the list
				workBundle = new WorkBundle();
				workBundle.setTitle(title);
				workBundle.setDescription(description);
				List<Work> workList = Lists.newArrayList();
				response.addNewBundle(workBundle, workList);

				// add routing to the first thrift work if present
				addRouting(response.getWork(), types, errors);
			}
			response.addNewBundle(workBundle, response.getWork());
			response.setWorkBundle(workBundle);
		}

		response.addToRowParseErrors(errors);
	}
}
