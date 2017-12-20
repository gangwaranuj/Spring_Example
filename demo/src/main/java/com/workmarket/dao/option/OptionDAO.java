package com.workmarket.dao.option;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.option.Option;

/**
 * Author: rocio
 */
public interface OptionDAO extends DAOInterface<Option> {

	<T extends Option> Option findOptionByNameAndValue(Class<T> clazz, String name, String value, Long entityId);

	<T extends Option> Option findOptionByName(Class<T> clazz, String name, Long entityId);
}
