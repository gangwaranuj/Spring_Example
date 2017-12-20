package com.workmarket.domains.work.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.AbstractWork;

import java.util.List;

public interface BaseWorkDAO extends DAOInterface<AbstractWork> {

	AbstractWork findById(Long id);

	AbstractWork findById(Long id, boolean loadEverything);

	AbstractWork findByWorkNumber(String workNumber);

	List<AbstractWork> findByWorkNumbers(List<String> workNumbers);

	Long findWorkId(String workNumber);
}