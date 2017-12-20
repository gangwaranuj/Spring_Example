package com.workmarket.service.option;

import com.workmarket.domains.model.option.Option;

public interface OptionsService<T> {

	/* This exists to avoid having to fetch model.work when working with thrift.work.. thanks Thrift! */
	boolean hasOptionByEntityId(Long entityId, String name, String value);

	boolean hasOption(T augmentable, String name, String value);

	void setOption(T augmentable, String name, String value);

	Option getOption(T augmentable, String name);
}