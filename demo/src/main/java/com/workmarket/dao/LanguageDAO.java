package com.workmarket.dao;

import java.util.List;

import com.workmarket.domains.model.Language;

public interface LanguageDAO extends DAOInterface<Language>{
	

	public List<Language> findLanguages();
	
}