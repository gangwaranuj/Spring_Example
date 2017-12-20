package com.workmarket.dao.banking;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.banking.BankRouting;

import java.util.List;

public interface BankRoutingDAO extends DAOInterface<BankRouting> {
	BankRouting get(String primaryKey);
	BankRouting get(String routingNumber, String countryId);

	@Deprecated
	List<BankRouting> suggest(String prefix);

	List<BankRouting> suggestInCountry(String text, String countryId);

}
