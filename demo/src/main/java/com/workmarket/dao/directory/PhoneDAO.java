package com.workmarket.dao.directory;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.directory.Phone;

public interface PhoneDAO extends PaginatableDAOInterface<Phone> {

	Phone findById(Long id);
}
