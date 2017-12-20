package com.workmarket.domains.work.service;

import com.google.common.collect.Lists;
import com.workmarket.service.business.dto.WorkSubStatusTypeDTO;
import com.workmarket.service.exception.account.InvalidPricingException;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class LabelValidationServiceImpl implements LabelValidationService {

	private static final Log logger = LogFactory.getLog(WorkValidationServiceImpl.class);

	@Override
	public List<ConstraintViolation> validateLabel(WorkSubStatusTypeDTO dto, MessageBundleHelper messageHelper) {
		Assert.notNull(dto);

		List<ConstraintViolation> violations = Lists.newArrayList();
		if (StringUtils.isBlank(dto.getDescription())) {
			ConstraintViolation v = new ConstraintViolation()
				.setProperty("description")
				.setError("mmw.manage.label.description.empty")
				.setWhy(messageHelper.getMessage("mmw.manage.label.description.empty"));
			violations.add(v);
		}
		if (dto.getDescription().length() > 35) {
			ConstraintViolation v = new ConstraintViolation()
				.setProperty("description")
				.setError("mmw.manage.label.description.length")
				.setWhy(messageHelper.getMessage("mmw.manage.label.description.length"));
			violations.add(v);
		}

		log(violations);
		return violations;
	}

	private void log(List<ConstraintViolation> constraints) {
		for (ConstraintViolation c : constraints) {
			logger.debug(String.format("Constraint violation found: %s", c.toString()));
		}
	}
}
