package com.workmarket.dao.callingcodes;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.CallingCode;

import java.util.List;

public interface CallingCodeDAO extends DAOInterface<CallingCode>{

	List<CallingCode> findAllActiveCallingCodes();

	CallingCode findCallingCodeById(Long id);

	CallingCode findCallingCodeByCallingCodeId(String id);

	List<String> getAllUniqueActiveCallingCodeIds();
}
