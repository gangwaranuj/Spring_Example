package com.workmarket.service.option;

import com.workmarket.dao.option.OptionDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.domains.model.option.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: rocio
 */
@Service
public class CompanyOptionsService implements OptionsService<Company> {

	@Autowired OptionDAO optionDAO;

	@Override
	public boolean hasOptionByEntityId(Long entityId, String name, String value) {
		Option option = optionDAO.findOptionByNameAndValue(CompanyOption.class, name, value, entityId);
		return option != null;
	}

	@Override
	public boolean hasOption(Company augmentable, String name, String value) {
		Option option = optionDAO.findOptionByNameAndValue(CompanyOption.class, name, value, augmentable.getId());
		return option != null;
	}

	@Override
	public void setOption(Company augmentable, String name, String value) {
		Option option = getOption(augmentable, name);

		if (option == null) {
			option = new CompanyOption(name, value, augmentable.getId());
			optionDAO.saveOrUpdate(option);
		} else {
			option.setValue(value);
		}
	}

	@Override
	public Option getOption(Company augmentable, String name) {
		return optionDAO.findOptionByName(CompanyOption.class, name, augmentable.getId());
	}
}
