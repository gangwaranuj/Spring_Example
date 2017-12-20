package com.workmarket.dao.directory;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.directory.Email;

public interface EmailDAO extends PaginatableDAOInterface<Email> {

	Email findById(Long id);
}
