package com.workmarket.service.option;

import com.workmarket.dao.option.OptionDAO;
import com.workmarket.domains.model.option.Option;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: rocio
 */
@Service
public class WorkOptionsService implements OptionsService<AbstractWork> {

	@Autowired OptionDAO optionDAO;
	@Autowired WorkService workService;

	@Override
	public boolean hasOptionByEntityId(Long workId, String name, String value) {
		Option option = optionDAO.findOptionByNameAndValue(WorkOption.class, name, value, workId);
		return option != null;
	}

	@Override
	public boolean hasOption(AbstractWork augmentable, String name, String value) {
		Option option = optionDAO.findOptionByNameAndValue(WorkOption.class, name, value, augmentable.getId());
		return option != null;
	}

	@Override
	public void setOption(AbstractWork augmentable, String name, String value) {
		Option option = getOption(augmentable, name);

		if (option == null) {
			option = new WorkOption(name, value, augmentable.getId());
			optionDAO.saveOrUpdate(option);
		} else {
			option.setValue(value);
		}
	}

	@Override
	public Option getOption(AbstractWork augmentable, String name) {
		return optionDAO.findOptionByName(WorkOption.class, name, augmentable.getId());
	}
}
