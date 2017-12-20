package com.workmarket.service.business.upload.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.exception.WorkRowParseError;
import com.workmarket.thrift.work.exception.WorkRowParseErrorType;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.service.business.upload.parser.ParseUtils.createErrorRow;

@Component
public class BaseParser {
	@Autowired UserDAO userDAO;
	@Autowired private MessageBundleHelper messageHelper;

	protected void addRouting(Work work, Map<String,String> types, List<WorkRowParseError> errors) {
		work.setResources(null);
		final String value = WorkUploadColumn.get(types, WorkUploadColumn.USER_NUMBER);

		if (StringUtils.isBlank(value)) {
			return;
		}

		Set<String> userNumbers = Sets.newLinkedHashSet(Arrays.asList(value.trim().split("[\\s,]+")));

		if (userNumbers.size() > Constants.UPLOAD_SEND_RESOURCES_LIMIT) {
			String errorMessage = messageHelper.getMessage("cart_push.max_resources_exceeded", Constants.UPLOAD_SEND_RESOURCES_LIMIT);
			errors.add(createErrorRow(value, errorMessage, WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.USER_NUMBER));
		} else {
			Map<String,Long> userMap = userDAO.findActiveUserIdsByUserNumbers(userNumbers);
			Set<String> invalidUserNumbers = Sets.newLinkedHashSet();
			List<Resource> resources = Lists.newLinkedList();

			for (String userNumber : userNumbers) {
				Long userId = userMap.get(userNumber);

				// if user is not found (or user is inactive)
				if (userId == null) {
					invalidUserNumbers.add(userNumber);
				} else {
					Resource resource = new Resource();
					resource.setId(userId);
					resources.add(resource);
				}
			}

			if (invalidUserNumbers.isEmpty()) {
				work.setResources(resources);
			} else {
				errors.add(createErrorRow(value, "The following Worker IDs are invalid: " + StringUtils.join(invalidUserNumbers, ", "), WorkRowParseErrorType.INVALID_DATA, WorkUploadColumn.USER_NUMBER));
			}
		}
	}
}
