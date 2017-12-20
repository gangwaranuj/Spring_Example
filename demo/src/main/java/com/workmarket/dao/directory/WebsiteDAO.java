package com.workmarket.dao.directory;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.directory.Website;

public interface WebsiteDAO extends PaginatableDAOInterface<Website> {

	Website findById(Long id);
}
