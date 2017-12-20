package com.workmarket.dao;

import com.workmarket.domains.model.TwilioSourceNumber;

import java.util.List;

public interface TwilioSourceNumberDAO  extends DAOInterface<TwilioSourceNumber> {

	List<String> getAllSourceNumbers();

}
