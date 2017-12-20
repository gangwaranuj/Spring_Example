package com.workmarket.dao.random;

import com.workmarket.dao.exception.random.IdentifierGenerationException;

public interface AbstractRandomIdentifierDAO {
	public int getNumberLength();
	public String generateUniqueNumber() throws IdentifierGenerationException;
}
